import { useEffect, useState } from 'react';
import { bookingsApi, notificationsApi, paymentsApi, reviewsApi } from '../api/endpoints';
import { Alert } from '../components/Alert';
import { DataTable } from '../components/DataTable';
import { SmartForm } from '../components/SmartForm';
import { StatusBadge } from '../components/StatusBadge';
import type { BookingDto, NotificationDto, PaymentDto, PaymentMethod } from '../types';
import { bookingTitle, enumLabel, eventTitle, formatDate, formatPrice, label } from '../utils';

export function ClientDashboard() {
  const [bookings, setBookings] = useState<BookingDto[]>([]);
  const [payments, setPayments] = useState<PaymentDto[]>([]);
  const [notifications, setNotifications] = useState<NotificationDto[]>([]);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const load = async () => {
    setError('');
    try {
      const [bookingList, paymentList, notificationList] = await Promise.all([
        bookingsApi.my(),
        paymentsApi.my().catch(() => []),
        notificationsApi.my().catch(() => []),
      ]);
      setBookings(bookingList);
      setPayments(paymentList);
      setNotifications(notificationList);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Не удалось загрузить кабинет');
    }
  };

  useEffect(() => {
    void load();
  }, []);

  const createPayment = async (bookingId: number, method: PaymentMethod) => {
    setMessage('');
    setError('');
    try {
      await paymentsApi.createMock(bookingId, method);
      setMessage('Тестовая оплата создана');
      await load();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Не удалось создать оплату');
    }
  };

  return (
    <section className="page">
      <div className="pageHeader"><h1>Кабинет клиента</h1></div>
      <Alert>{error}</Alert>
      <Alert type="success">{message}</Alert>
      <div className="alert info">Тестовая оплата. Реальная платёжная система не подключена. Статус оплаты изменяется менеджером или администратором.</div>
      <div className="panel">
        <h2>Мои бронирования</h2>
        <DataTable
          items={bookings}
          columns={[
            { key: 'id', title: '№' },
            { key: 'event', title: 'Мероприятие', render: (booking) => eventTitle(booking.event) },
            { key: 'participantsCount', title: 'Участников' },
            { key: 'totalPrice', title: 'Сумма', render: (booking) => formatPrice(booking.totalPrice) },
            { key: 'status', title: 'Статус', render: (booking) => <StatusBadge value={booking.status} /> },
          ]}
          actions={(booking) => (
            <div className="inlineActions">
              <button className="danger" disabled={booking.status === 'CANCELLED'} onClick={() => bookingsApi.cancel(Number(booking.id)).then(load).catch((err) => setError(err.message))}>Отменить бронирование</button>
              {(['CASH', 'CARD_MOCK', 'BANK_TRANSFER_MOCK'] as PaymentMethod[]).map((method) => (
                <button className="secondary" key={method} onClick={() => void createPayment(Number(booking.id), method)}>{enumLabel(method)}</button>
              ))}
            </div>
          )}
        />
      </div>
      <div className="panel">
        <h2>Мои оплаты</h2>
        <DataTable
          items={payments}
          columns={[
            { key: 'id', title: '№' },
            { key: 'booking', title: 'Бронирование', render: (payment) => bookingTitle(payment.booking) || label(payment.bookingId) },
            { key: 'amount', title: 'Сумма', render: (payment) => formatPrice(payment.amount) },
            { key: 'method', title: 'Метод', render: (payment) => enumLabel(payment.method) },
            { key: 'status', title: 'Статус', render: (payment) => <StatusBadge value={payment.status} /> },
            { key: 'mockTransactionId', title: 'Тестовая транзакция' },
          ]}
        />
      </div>
      <div className="panel">
        <h2>Уведомления</h2>
        <button className="secondary" onClick={() => notificationsApi.readAll().then(load).catch((err) => setError(err.message))}>Отметить все прочитанными</button>
        <DataTable
          items={notifications}
          columns={[
            { key: 'type', title: 'Тип', render: (item) => enumLabel(item.type) },
            { key: 'message', title: 'Сообщение' },
            { key: 'createdAt', title: 'Дата', render: (item) => formatDate(item.createdAt) },
            { key: 'read', title: 'Прочитано', render: (item) => item.read ? 'Да' : 'Нет' },
          ]}
          actions={(item) => !item.read ? <button className="secondary" onClick={() => notificationsApi.read(Number(item.id)).then(load).catch((err) => setError(err.message))}>Прочитано</button> : null}
        />
      </div>
      <div className="panel">
        <h2>Отзыв по завершённому бронированию</h2>
        <SmartForm
          submitText="Создать отзыв"
          fields={[
            { name: 'activityId', label: 'ID активности', type: 'number', required: true },
            { name: 'bookingId', label: 'ID бронирования', type: 'number' },
            { name: 'rating', label: 'Оценка', type: 'number', required: true },
            { name: 'text', label: 'Текст', type: 'textarea', required: true },
          ]}
          onSubmit={async (values) => {
            await reviewsApi.create(values);
            setMessage('Отзыв отправлен');
          }}
        />
      </div>
    </section>
  );
}

