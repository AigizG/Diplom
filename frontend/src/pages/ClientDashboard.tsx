import { useEffect, useState } from 'react';
import { bookingsApi, notificationsApi, paymentsApi, reviewsApi } from '../api/endpoints';
import { Alert } from '../components/Alert';
import { DataTable } from '../components/DataTable';
import { SmartForm } from '../components/SmartForm';
import { StatusBadge } from '../components/StatusBadge';
import { useToast } from '../components/ToastProvider';
import type { BookingDto, NotificationDto, PaymentDto, PaymentMethod } from '../types';
import { enumLabel, eventTitle, formatDate, formatPrice, label } from '../utils';

export function ClientDashboard() {
  const [bookings, setBookings] = useState<BookingDto[]>([]);
  const [payments, setPayments] = useState<PaymentDto[]>([]);
  const [notifications, setNotifications] = useState<NotificationDto[]>([]);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);
  const [actionKey, setActionKey] = useState('');
  const toast = useToast();

  const load = async () => {
    setError('');
    setLoading(true);
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
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void load();
  }, []);

  const createPayment = async (bookingId: number, method: PaymentMethod) => {
    const key = `payment-${bookingId}-${method}`;
    setMessage('');
    setError('');
    setActionKey(key);
    try {
      await paymentsApi.createMock(bookingId, method);
      setMessage('Тестовая оплата создана');
      toast.success('Тестовая оплата создана');
      await load();
    } catch (err) {
      const text = err instanceof Error ? err.message : 'Не удалось создать оплату';
      setError(text);
      toast.error(text);
    } finally {
      setActionKey('');
    }
  };

  const run = async (key: string, action: () => Promise<unknown>, success: string) => {
    setMessage('');
    setError('');
    setActionKey(key);
    try {
      await action();
      setMessage(success);
      toast.success(success);
      await load();
    } catch (err) {
      const text = err instanceof Error ? err.message : 'Ошибка операции';
      setError(text);
      toast.error(text);
    } finally {
      setActionKey('');
    }
  };

  return (
    <section className="page">
      <div className="pageHeader"><h1>Кабинет клиента</h1></div>
      <Alert>{error}</Alert>
      <Alert type="success">{message}</Alert>
      {loading && <div className="alert info">Загружаем данные кабинета...</div>}
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
              <button
                className="danger"
                disabled={booking.status === 'CANCELLED' || actionKey === `cancel-${booking.id}`}
                onClick={() => void run(`cancel-${booking.id}`, () => bookingsApi.cancel(Number(booking.id)), 'Бронирование отменено')}
              >
                {actionKey === `cancel-${booking.id}` ? 'Отменяем...' : 'Отменить бронирование'}
              </button>
              {(['CASH', 'CARD_MOCK', 'BANK_TRANSFER_MOCK'] as PaymentMethod[]).map((method) => (
                <button
                  className="secondary"
                  key={method}
                  disabled={booking.status === 'CANCELLED' || actionKey === `payment-${booking.id}-${method}`}
                  onClick={() => void createPayment(Number(booking.id), method)}
                >
                  {actionKey === `payment-${booking.id}-${method}` ? 'Создаём...' : enumLabel(method)}
                </button>
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
            { key: 'bookingId', title: 'Бронирование', render: (payment) => `№ ${label(payment.bookingId)}` },
            { key: 'amount', title: 'Сумма', render: (payment) => formatPrice(payment.amount) },
            { key: 'method', title: 'Метод', render: (payment) => enumLabel(payment.method) },
            { key: 'status', title: 'Статус', render: (payment) => <StatusBadge value={payment.status} /> },
            { key: 'mockTransactionId', title: 'Тестовая транзакция' },
          ]}
        />
      </div>
      <div className="panel">
        <h2>Уведомления</h2>
        <button
          className="secondary"
          disabled={actionKey === 'read-all'}
          onClick={() => void run('read-all', () => notificationsApi.readAll(), 'Уведомления отмечены прочитанными')}
        >
          {actionKey === 'read-all' ? 'Отмечаем...' : 'Отметить все прочитанными'}
        </button>
        <DataTable
          items={notifications}
          columns={[
            { key: 'type', title: 'Тип', render: (item) => enumLabel(item.type) },
            { key: 'message', title: 'Сообщение' },
            { key: 'createdAt', title: 'Дата', render: (item) => formatDate(item.createdAt) },
            { key: 'read', title: 'Прочитано', render: (item) => item.read ? 'Да' : 'Нет' },
          ]}
          actions={(item) => !item.read ? (
            <button
              className="secondary"
              disabled={actionKey === `read-${item.id}`}
              onClick={() => void run(`read-${item.id}`, () => notificationsApi.read(Number(item.id)), 'Уведомление отмечено прочитанным')}
            >
              {actionKey === `read-${item.id}` ? 'Отмечаем...' : 'Прочитано'}
            </button>
          ) : null}
        />
      </div>
      <div className="panel">
        <h2>Отзыв по завершённому бронированию</h2>
        <SmartForm
          submitText="Создать отзыв"
          fields={[
            { name: 'bookingId', label: 'ID завершённого бронирования', type: 'number', required: true },
            { name: 'rating', label: 'Оценка', type: 'number', required: true },
            { name: 'text', label: 'Текст', type: 'textarea', required: true },
          ]}
          onSubmit={async (values) => {
            await reviewsApi.create(values);
            setMessage('Отзыв отправлен');
            toast.success('Отзыв отправлен');
            await load();
          }}
        />
      </div>
    </section>
  );
}

