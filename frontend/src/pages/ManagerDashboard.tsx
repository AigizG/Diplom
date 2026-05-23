import { useEffect, useMemo, useState } from 'react';
import {
  activitiesApi,
  bookingsApi,
  dashboardApi,
  documentsApi,
  employeesApi,
  equipmentApi,
  eventsApi,
  paymentsApi,
  reportsApi,
  reviewsApi,
  usersApi,
} from '../api/endpoints';
import { Alert } from '../components/Alert';
import { ConfirmModal } from '../components/ConfirmModal';
import { DataTable } from '../components/DataTable';
import { JsonCard } from '../components/JsonCard';
import { Modal } from '../components/Modal';
import { FieldConfig, SmartForm } from '../components/SmartForm';
import { StatusBadge } from '../components/StatusBadge';
import { useToast } from '../components/ToastProvider';
import type { ActivityDto, AdminDashboardDto, BookingDto, EmployeeDto, EquipmentDto, EventDto, ManagerDashboardDto, PaymentDto, ReviewDto, UserDto } from '../types';
import { activityTitle, employeeUser, enumLabel, eventTitle, formatDate, formatPrice, label, reportTitle, userName } from '../utils';

type Tab =
  | 'dashboard'
  | 'activities'
  | 'categories'
  | 'events'
  | 'bookings'
  | 'payments'
  | 'equipment'
  | 'reviews'
  | 'documents'
  | 'reports'
  | 'users'
  | 'employees';

const tabs: Array<{ key: Tab; label: string; adminOnly?: boolean }> = [
  { key: 'dashboard', label: 'Сводка' },
  { key: 'activities', label: 'Активности' },
  { key: 'categories', label: 'Категории' },
  { key: 'events', label: 'Мероприятия' },
  { key: 'bookings', label: 'Бронирования' },
  { key: 'payments', label: 'Оплаты' },
  { key: 'equipment', label: 'Снаряжение' },
  { key: 'reviews', label: 'Отзывы' },
  { key: 'documents', label: 'Документы' },
  { key: 'reports', label: 'Отчёты' },
  { key: 'users', label: 'Пользователи', adminOnly: true },
  { key: 'employees', label: 'Сотрудники', adminOnly: true },
];

const activityFields: FieldConfig[] = [
  { name: 'title', label: 'Название', required: true },
  { name: 'description', label: 'Описание', type: 'textarea', required: true },
  { name: 'categoryId', label: '№ категории', type: 'number', required: true },
  { name: 'difficultyLevel', label: 'Сложность' },
  { name: 'location', label: 'Локация' },
  { name: 'price', label: 'Цена', type: 'number', required: true },
  { name: 'status', label: 'Статус', type: 'select', options: ['ACTIVE', 'HIDDEN', 'ARCHIVED'] },
];

const eventFields: FieldConfig[] = [
  { name: 'activityId', label: '№ активности', type: 'number', required: true },
  { name: 'startDateTime', label: 'Дата и время начала', type: 'datetime-local', required: true },
  { name: 'endDateTime', label: 'Дата и время окончания', type: 'datetime-local', required: true },
  { name: 'instructorId', label: '№ инструктора', type: 'number', required: true },
  { name: 'totalPlaces', label: 'Количество мест', type: 'number', required: true },
  { name: 'status', label: 'Статус', type: 'select', options: ['PLANNED', 'COMPLETED', 'CANCELLED', 'POSTPONED'] },
];

const equipmentFields: FieldConfig[] = [
  { name: 'name', label: 'Название', required: true },
  { name: 'quantityTotal', label: 'Всего единиц', type: 'number', required: true },
  { name: 'quantityAvailable', label: 'Доступно единиц', type: 'number', required: true },
  { name: 'categoryId', label: '№ категории', type: 'number', required: true },
  { name: 'conditionStatus', label: 'Состояние', type: 'select', options: ['AVAILABLE', 'NEEDS_REPAIR', 'WRITTEN_OFF'] },
];

export function ManagerDashboard({ adminMode = false }: { adminMode?: boolean }) {
  const visibleTabs = useMemo(() => tabs.filter((tab) => adminMode || !tab.adminOnly), [adminMode]);
  const [tab, setTab] = useState<Tab>(visibleTabs[0].key);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const [form, setForm] = useState<React.ReactNode | null>(null);
  const [activities, setActivities] = useState<ActivityDto[]>([]);
  const [categories, setCategories] = useState<Array<{ id: number; name?: string }>>([]);
  const [events, setEvents] = useState<EventDto[]>([]);
  const [bookings, setBookings] = useState<BookingDto[]>([]);
  const [payments, setPayments] = useState<PaymentDto[]>([]);
  const [equipment, setEquipment] = useState<EquipmentDto[]>([]);
  const [reviews, setReviews] = useState<ReviewDto[]>([]);
  const [users, setUsers] = useState<UserDto[]>([]);
  const [employees, setEmployees] = useState<EmployeeDto[]>([]);
  const [reports, setReports] = useState<Record<string, unknown>>({});
  const [managerDashboard, setManagerDashboard] = useState<ManagerDashboardDto | null>(null);
  const [adminDashboard, setAdminDashboard] = useState<AdminDashboardDto | null>(null);
  const [period, setPeriod] = useState({ from: '', to: '' });
  const [loading, setLoading] = useState(false);
  const [actionKey, setActionKey] = useState('');
  const [confirm, setConfirm] = useState<{ text: string; action: () => Promise<unknown>; success?: string; danger?: boolean } | null>(null);
  const toast = useToast();

  const run = async (action: () => Promise<unknown>, success = 'Готово', key = success) => {
    setError('');
    setMessage('');
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

  const confirmRun = (text: string, action: () => Promise<unknown>, success?: string, danger = true) => {
    setConfirm({ text, action, success, danger });
  };

  const load = async () => {
    setError('');
    setLoading(true);
    try {
      if (tab === 'activities') setActivities(await activitiesApi.list());
      if (tab === 'categories') setCategories(await activitiesApi.categories());
      if (tab === 'events') setEvents(await eventsApi.list());
      if (tab === 'bookings') setBookings(await bookingsApi.list());
      if (tab === 'payments') setPayments(await paymentsApi.list());
      if (tab === 'equipment') setEquipment(await equipmentApi.list());
      if (tab === 'reviews') setReviews(await reviewsApi.list());
      if (tab === 'users') setUsers(await usersApi.list());
      if (tab === 'employees') setEmployees(await employeesApi.list());
      if (tab === 'dashboard') {
        if (adminMode) setAdminDashboard(await dashboardApi.admin());
        else setManagerDashboard(await dashboardApi.manager());
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Не удалось загрузить данные');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void load();
  }, [tab]);

  const loadReports = async () => {
    setError('');
    try {
      const params = { from: period.from, to: period.to };
      const [bookingsReport, revenue, popular, workload, equipmentUsage, cancellations] = await Promise.all([
        reportsApi.bookings(params),
        reportsApi.revenue(params),
        reportsApi.popular(params),
        reportsApi.workload(params),
        reportsApi.equipment(params),
        reportsApi.cancellations(params),
      ]);
      setReports({
        bookings: bookingsReport.data,
        revenue: revenue.data,
        popular: popular.data,
        workload: workload.data,
        equipmentUsage: equipmentUsage.data,
        cancellations: cancellations.data,
      });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Не удалось загрузить отчёты');
    }
  };

  return (
    <section className="page">
      <div className="pageHeader">
        <div>
          <h1>{adminMode ? 'Администрирование' : 'Панель менеджера'}</h1>
          <p>Операционное управление мероприятиями, оплатами, документами и отчётами.</p>
        </div>
      </div>
      <div className="tabs">
        {visibleTabs.map((item) => <button className={tab === item.key ? 'active' : ''} key={item.key} onClick={() => setTab(item.key)}>{item.label}</button>)}
      </div>
      <Alert>{error}</Alert>
      <Alert type="success">{message}</Alert>
      {loading && <div className="alert info">Загружаем данные...</div>}
      {tab === 'dashboard' && (
        <WorkPanel title={adminMode ? 'Панель управления администратора' : 'Панель управления менеджера'}>
          {adminMode && adminDashboard ? (
            <div className="metricGrid">
              <Metric title="Пользователи" value={adminDashboard.users} />
              <Metric title="Клиенты" value={adminDashboard.clients} />
              <Metric title="Сотрудники" value={adminDashboard.employees} />
              <Metric title="Активности" value={adminDashboard.activities} />
              <Metric title="Бронирования" value={adminDashboard.bookings} />
              <Metric title="Тестовая выручка" value={`${adminDashboard.paidRevenue} ₽`} />
            </div>
          ) : managerDashboard ? (
            <>
              <div className="metricGrid">
                <Metric title="Новые заявки" value={managerDashboard.newBookings} />
                <Metric title="Подтверждено" value={managerDashboard.confirmedBookings} />
                <Metric title="Оплачено" value={managerDashboard.paidBookings} />
                <Metric title="Тестовая выручка" value={`${managerDashboard.paidRevenue} ₽`} />
              </div>
              <h3>Ближайшие мероприятия</h3>
              <DataTable
                items={managerDashboard.nearestEvents || []}
                columns={[
                  { key: 'activity', title: 'Активность', render: (item) => eventTitle(item) },
                  { key: 'date', title: 'Дата', render: (item) => formatDate(item.startDateTime || item.date) },
                  { key: 'places', title: 'Места', render: (item) => `${item.availablePlaces ?? '-'} из ${item.totalPlaces ?? '-'}` },
                ]}
              />
            </>
          ) : <div className="empty">Нет данных для панели управления</div>}
        </WorkPanel>
      )}
      {tab === 'activities' && (
        <WorkPanel title="Активности" action="Создать активность" onAction={() => setForm(
          <SmartForm fields={activityFields} submitText="Создать" onCancel={() => setForm(null)} onSubmit={(values) => run(() => activitiesApi.create(values), 'Активность создана').then(() => setForm(null))} />,
        )}>
          <DataTable
            items={activities}
            columns={[
              { key: 'id', title: '№' },
              { key: 'name', title: 'Название', render: (item) => activityTitle(item) },
              { key: 'category', title: 'Категория', render: (item) => label(item.category) },
              { key: 'price', title: 'Цена', render: (item) => formatPrice(item.price) },
              { key: 'status', title: 'Статус', render: (item) => <StatusBadge value={item.status} /> },
            ]}
            actions={(item) => (
              <div className="inlineActions">
                <button className="secondary" onClick={() => setForm(<SmartForm fields={activityFields} submitText="Сохранить" initialValues={item} onCancel={() => setForm(null)} onSubmit={(values) => run(() => activitiesApi.update(Number(item.id), values), 'Активность обновлена').then(() => setForm(null))} />)}>Изменить</button>
                <button className="danger" disabled={actionKey === `delete-activity-${item.id}`} onClick={() => confirmRun('Удалить активность?', () => activitiesApi.remove(Number(item.id)), 'Активность удалена')}>Удалить</button>
              </div>
            )}
          />
        </WorkPanel>
      )}
      {tab === 'categories' && (
        <WorkPanel title="Категории активностей" action="Создать категорию" onAction={() => setForm(<SmartForm fields={[{ name: 'name', label: 'Название', required: true }, { name: 'description', label: 'Описание', type: 'textarea' }]} submitText="Создать" onCancel={() => setForm(null)} onSubmit={(values) => run(() => activitiesApi.createCategory(values), 'Категория создана').then(() => setForm(null))} />)}>
          <DataTable
            items={categories}
            columns={[{ key: 'id', title: '№' }, { key: 'name', title: 'Название' }, { key: 'description', title: 'Описание' }]}
            actions={(item) => (
              <div className="inlineActions">
                <button className="secondary" onClick={() => setForm(<SmartForm fields={[{ name: 'name', label: 'Название', required: true }, { name: 'description', label: 'Описание', type: 'textarea' }]} submitText="Сохранить" initialValues={item} onCancel={() => setForm(null)} onSubmit={(values) => run(() => activitiesApi.updateCategory(Number(item.id), values), 'Категория обновлена').then(() => setForm(null))} />)}>Изменить</button>
                <button className="danger" disabled={actionKey === `delete-category-${item.id}`} onClick={() => confirmRun('Удалить категорию?', () => activitiesApi.deleteCategory(Number(item.id)), 'Категория удалена')}>Удалить</button>
              </div>
            )}
          />
        </WorkPanel>
      )}
      {tab === 'events' && (
        <WorkPanel title="Мероприятия" action="Создать мероприятие" onAction={() => setForm(<SmartForm fields={eventFields} submitText="Создать" onCancel={() => setForm(null)} onSubmit={(values) => run(() => eventsApi.create(values), 'Мероприятие создано').then(() => setForm(null))} />)}>
          <DataTable
            items={events}
            columns={[
              { key: 'id', title: '№' },
              { key: 'activity', title: 'Активность', render: (item) => eventTitle(item) },
              { key: 'date', title: 'Дата', render: (item) => formatDate(item.startDateTime || item.date) },
              { key: 'status', title: 'Статус', render: (item) => <StatusBadge value={item.status} /> },
            ]}
            actions={(item) => (
              <div className="inlineActions">
                <button className="secondary" onClick={() => setForm(<SmartForm fields={eventFields} submitText="Сохранить" initialValues={item} onCancel={() => setForm(null)} onSubmit={(values) => run(() => eventsApi.update(Number(item.id), values), 'Мероприятие обновлено').then(() => setForm(null))} />)}>Изменить</button>
                <button disabled={actionKey === `complete-event-${item.id}`} onClick={() => run(() => eventsApi.complete(Number(item.id)), 'Мероприятие завершено', `complete-event-${item.id}`)}>{actionKey === `complete-event-${item.id}` ? 'Завершаем...' : 'Завершить'}</button>
                <button className="secondary" onClick={() => setForm(<SmartForm fields={[{ name: 'startDateTime', label: 'Новая дата начала', type: 'datetime-local', required: true }, { name: 'endDateTime', label: 'Новая дата окончания', type: 'datetime-local', required: true }]} submitText='Перенести' onCancel={() => setForm(null)} onSubmit={(values) => run(() => eventsApi.postpone(Number(item.id), values), 'Мероприятие перенесено').then(() => setForm(null))} />)}>Перенести</button>
                <button className="danger" disabled={actionKey === `cancel-event-${item.id}`} onClick={() => confirmRun('Отменить мероприятие?', () => eventsApi.cancel(Number(item.id)), 'Мероприятие отменено')}>Отменить</button>
              </div>
            )}
          />
        </WorkPanel>
      )}
      {tab === 'bookings' && (
        <WorkPanel title="Бронирования">
          <DataTable
            items={bookings}
            columns={[
              { key: 'id', title: '№' },
              { key: 'client', title: 'Клиент', render: (item) => userName(item.client) },
              { key: 'event', title: 'Мероприятие', render: (item) => eventTitle(item.event) },
              { key: 'status', title: 'Статус', render: (item) => <StatusBadge value={item.status} /> },
            ]}
            actions={(item) => (
              <div className="inlineActions">
                <button disabled={actionKey === `confirm-booking-${item.id}`} onClick={() => run(() => bookingsApi.confirm(Number(item.id)), 'Бронирование подтверждено', `confirm-booking-${item.id}`)}>{actionKey === `confirm-booking-${item.id}` ? 'Подтверждаем...' : 'Подтвердить'}</button>
                <button disabled={actionKey === `complete-booking-${item.id}`} onClick={() => run(() => bookingsApi.complete(Number(item.id)), 'Бронирование завершено', `complete-booking-${item.id}`)}>{actionKey === `complete-booking-${item.id}` ? 'Завершаем...' : 'Завершить'}</button>
                <button className="secondary" disabled={actionKey === `payment-booking-${item.id}`} onClick={() => run(() => paymentsApi.createMock(Number(item.id), 'CARD_MOCK'), 'Тестовая оплата создана', `payment-booking-${item.id}`)}>{actionKey === `payment-booking-${item.id}` ? 'Создаём...' : 'Создать тестовую оплату'}</button>
                <button className="danger" onClick={() => confirmRun('Отменить бронирование?', () => bookingsApi.cancel(Number(item.id)), 'Бронирование отменено')}>Отменить</button>
              </div>
            )}
          />
        </WorkPanel>
      )}
      {tab === 'payments' && (
        <WorkPanel title="Тестовые оплаты">
          <DataTable
            items={payments}
            columns={[
              { key: 'id', title: '№' },
              { key: 'bookingId', title: 'Бронь', render: (item) => `№ ${label(item.bookingId)}` },
              { key: 'method', title: 'Метод', render: (item) => enumLabel(item.method) },
              { key: 'amount', title: 'Сумма', render: (item) => formatPrice(item.amount) },
              { key: 'status', title: 'Статус', render: (item) => <StatusBadge value={item.status} /> },
              { key: 'mockTransactionId', title: 'Тестовая транзакция' },
            ]}
            actions={(item) => (
              <div className="inlineActions">
                <button disabled={actionKey === `paid-${item.id}`} onClick={() => run(() => paymentsApi.paid(Number(item.id)), 'Оплата отмечена как оплаченная', `paid-${item.id}`)}>{actionKey === `paid-${item.id}` ? 'Сохраняем...' : 'Оплачено'}</button>
                <button className="secondary" disabled={actionKey === `partial-${item.id}`} onClick={() => run(() => paymentsApi.partial(Number(item.id)), 'Оплата отмечена как частичная', `partial-${item.id}`)}>{actionKey === `partial-${item.id}` ? 'Сохраняем...' : 'Частичная'}</button>
                <button className="secondary" disabled={actionKey === `refund-${item.id}`} onClick={() => run(() => paymentsApi.refund(Number(item.id)), 'Возврат оформлен', `refund-${item.id}`)}>{actionKey === `refund-${item.id}` ? 'Оформляем...' : 'Возврат'}</button>
                <button className="danger" onClick={() => confirmRun('Отменить оплату?', () => paymentsApi.cancel(Number(item.id)), 'Оплата отменена')}>Отменить</button>
              </div>
            )}
          />
        </WorkPanel>
      )}
      {tab === 'equipment' && (
        <WorkPanel title="Снаряжение" action="Создать снаряжение" onAction={() => setForm(<SmartForm fields={equipmentFields} submitText="Создать" onCancel={() => setForm(null)} onSubmit={(values) => run(() => equipmentApi.create(values), 'Снаряжение создано').then(() => setForm(null))} />)}>
          <DataTable
            items={equipment}
            columns={[{ key: 'id', title: '№' }, { key: 'name', title: 'Название' }, { key: 'quantityAvailable', title: 'Доступно' }, { key: 'quantityTotal', title: 'Всего' }, { key: 'conditionStatus', title: 'Состояние', render: (item) => <StatusBadge value={item.conditionStatus || item.status} /> }]}
            actions={(item) => (
              <div className="inlineActions">
                <button className="secondary" onClick={() => setForm(<SmartForm fields={equipmentFields} submitText="Сохранить" initialValues={item} onCancel={() => setForm(null)} onSubmit={(values) => run(() => equipmentApi.update(Number(item.id), values), 'Снаряжение обновлено').then(() => setForm(null))} />)}>Изменить</button>
                <button className="danger" onClick={() => confirmRun('Удалить снаряжение?', () => equipmentApi.remove(Number(item.id)), 'Снаряжение удалено')}>Удалить</button>
              </div>
            )}
          />
        </WorkPanel>
      )}
      {tab === 'reviews' && (
        <WorkPanel title="Модерация отзывов">
          <DataTable
            items={reviews}
            columns={[{ key: 'id', title: '№' }, { key: 'rating', title: 'Оценка' }, { key: 'text', title: 'Текст', render: (item) => item.text || item.comment || '-' }, { key: 'hidden', title: 'Скрыт', render: (item) => item.hidden ? 'Да' : 'Нет' }]}
            actions={(item) => (
              <div className="inlineActions">
                <button onClick={() => run(() => reviewsApi.moderate(Number(item.id)), 'Отзыв промодерирован')}>Промодерировать</button>
                <button className="secondary" onClick={() => run(() => reviewsApi.hide(Number(item.id)), 'Отзыв скрыт')}>Скрыть</button>
                <button className="danger" onClick={() => confirmRun('Удалить отзыв?', () => reviewsApi.remove(Number(item.id)), 'Отзыв удалён')}>Удалить</button>
              </div>
            )}
          />
        </WorkPanel>
      )}
      {tab === 'documents' && (
        <WorkPanel title="Документы">
          <div className="threeForms">
            <SmartForm fields={[{ name: 'bookingId', label: 'ID бронирования', type: 'number', required: true }]} submitText="Создать договор" onSubmit={(values) => run(() => documentsApi.contract(Number(values.bookingId)), 'Договор создан')} />
            <SmartForm fields={[{ name: 'eventId', label: 'ID мероприятия', type: 'number', required: true }]} submitText="Список участников" onSubmit={(values) => run(() => documentsApi.participantList(Number(values.eventId)), 'Список создан')} />
            <SmartForm fields={[{ name: 'eventId', label: 'ID мероприятия', type: 'number', required: true }]} submitText="Отчёт мероприятия" onSubmit={(values) => run(() => documentsApi.eventReport(Number(values.eventId)), 'Отчёт создан')} />
          </div>
        </WorkPanel>
      )}
      {tab === 'reports' && (
        <WorkPanel title="Отчёты">
          <div className="filters">
            <input type="date" value={period.from} onChange={(event) => setPeriod({ ...period, from: event.target.value })} />
            <input type="date" value={period.to} onChange={(event) => setPeriod({ ...period, to: event.target.value })} />
            <button disabled={actionKey === 'reports'} onClick={() => void run(loadReports, 'Отчёты загружены', 'reports')}>{actionKey === 'reports' ? 'Загружаем...' : 'Загрузить отчёты'}</button>
          </div>
          <div className="reportGrid">
            {Object.entries(reports).map(([name, rows]) => <div className="panel inset" key={name}><h3>{reportTitle(name)}</h3><JsonCard data={rows} /></div>)}
          </div>
        </WorkPanel>
      )}
      {tab === 'users' && (
        <WorkPanel title="Пользователи">
          <DataTable
            items={users}
            columns={[{ key: 'id', title: '№' }, { key: 'email', title: 'Эл. почта' }, { key: 'fullName', title: 'ФИО', render: (item) => userName(item) }, { key: 'role', title: 'Роль', render: (item) => enumLabel(item.role) }, { key: 'enabled', title: 'Активен', render: (item) => item.enabled === false ? 'Нет' : 'Да' }]}
            actions={(item) => (
              <div className="inlineActions">
                <button className="secondary" onClick={() => setForm(<SmartForm fields={[{ name: 'fullName', label: 'ФИО' }, { name: 'phone', label: 'Телефон' }, { name: 'role', label: 'Роль', type: 'select', options: ['CLIENT', 'MANAGER', 'INSTRUCTOR', 'ADMIN'] }]} submitText="Сохранить" initialValues={item} onCancel={() => setForm(null)} onSubmit={(values) => run(() => usersApi.patch(Number(item.id), values), 'Пользователь обновлён').then(() => setForm(null))} />)}>Изменить</button>
                <button className="danger" onClick={() => confirmRun('Заблокировать пользователя?', () => usersApi.block(Number(item.id)), 'Пользователь заблокирован')}>Заблокировать</button>
                <button onClick={() => run(() => usersApi.unblock(Number(item.id)), 'Пользователь разблокирован')}>Разблокировать</button>
              </div>
            )}
          />
        </WorkPanel>
      )}
      {tab === 'employees' && (
        <WorkPanel title="Сотрудники" action="Создать сотрудника" onAction={() => setForm(<SmartForm fields={[{ name: 'email', label: 'Эл. почта', type: 'email', required: true }, { name: 'password', label: 'Пароль', type: 'password', required: true }, { name: 'fullName', label: 'ФИО', required: true }, { name: 'role', label: 'Роль', type: 'select', required: true, options: ['MANAGER', 'INSTRUCTOR', 'ADMIN'] }, { name: 'phone', label: 'Телефон' }]} submitText="Создать" onCancel={() => setForm(null)} onSubmit={(values) => run(() => employeesApi.create(values), 'Сотрудник создан').then(() => setForm(null))} />)}>
          <DataTable
            items={employees}
            columns={[
              { key: 'id', title: '№' },
              { key: 'email', title: 'Эл. почта', render: (item) => employeeUser(item)?.email || '-' },
              { key: 'fullName', title: 'ФИО', render: (item) => userName(employeeUser(item)) },
              { key: 'role', title: 'Роль', render: (item) => enumLabel(employeeUser(item)?.role) },
              { key: 'active', title: 'Активен', render: (item) => item.active ? 'Да' : 'Нет' },
            ]}
            actions={(item) => (
              <div className="inlineActions">
                <button className="secondary" onClick={() => setForm(<SmartForm fields={[{ name: 'email', label: 'Эл. почта', type: 'email' }, { name: 'fullName', label: 'ФИО' }, { name: 'role', label: 'Роль', type: 'select', options: ['MANAGER', 'INSTRUCTOR', 'ADMIN'] }, { name: 'phone', label: 'Телефон' }]} submitText="Сохранить" initialValues={employeeUser(item) || {}} onCancel={() => setForm(null)} onSubmit={(values) => run(() => employeesApi.update(Number(item.id), values), 'Сотрудник обновлён').then(() => setForm(null))} />)}>Изменить</button>
                <button className="danger" onClick={() => confirmRun('Деактивировать сотрудника?', () => employeesApi.deactivate(Number(item.id)), 'Сотрудник деактивирован')}>Деактивировать</button>
              </div>
            )}
          />
        </WorkPanel>
      )}
      {form && <Modal title="Форма" onClose={() => setForm(null)}>{form}</Modal>}
      {confirm && (
        <ConfirmModal
          message={confirm.text}
          danger={confirm.danger}
          loading={Boolean(actionKey)}
          onCancel={() => setConfirm(null)}
          onConfirm={() => {
            const current = confirm;
            setConfirm(null);
            void run(current.action, current.success, current.success || current.text);
          }}
        />
      )}
    </section>
  );
}

function WorkPanel({
  title,
  action,
  onAction,
  children,
}: {
  title: string;
  action?: string;
  onAction?: () => void;
  children: React.ReactNode;
}) {
  return (
    <div className="panel">
      <div className="panelHeader">
        <h2>{title}</h2>
        {action && <button onClick={onAction}>{action}</button>}
      </div>
      {children}
    </div>
  );
}

function Metric({ title, value }: { title: string; value: React.ReactNode }) {
  return (
    <div className="metricCard">
      <span>{title}</span>
      <strong>{value}</strong>
    </div>
  );
}





