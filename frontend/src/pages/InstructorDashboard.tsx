import { useEffect, useState } from 'react';
import { bookingsApi, instructorApi } from '../api/endpoints';
import { useAuth } from '../auth/AuthContext';
import { Alert } from '../components/Alert';
import { DataTable } from '../components/DataTable';
import { StatusBadge } from '../components/StatusBadge';
import { useToast } from '../components/ToastProvider';
import type { BookingDto, EventDto } from '../types';
import { eventTitle, formatDate, userName } from '../utils';

export function InstructorDashboard() {
  const { user } = useAuth();
  const { showToast } = useToast();
  const [events, setEvents] = useState<EventDto[]>([]);
  const [bookings, setBookings] = useState<BookingDto[]>([]);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(true);
  const [actionKey, setActionKey] = useState('');

  const load = async () => {
    if (!user?.id) return;
    setError('');
    setLoading(true);
    try {
      const [schedule, allBookings] = await Promise.all([
        instructorApi.events(),
        bookingsApi.list().catch(() => []),
      ]);
      setEvents(schedule);
      const ids = new Set(schedule.map((item) => item.id));
      setBookings(allBookings.filter((booking) => booking.event?.id && ids.has(booking.event.id)));
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Не удалось загрузить расписание');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void load();
  }, [user?.id]);

  const createReport = async (eventId: number) => {
    setActionKey(`report-${eventId}`);
    setError('');
    setMessage('');
    try {
      await instructorApi.report(eventId, 'Отчёт создан инструктором из личного кабинета');
      setMessage('Отчёт мероприятия создан');
      showToast('Отчёт мероприятия создан', 'success');
      await load();
    } catch (err) {
      const text = err instanceof Error ? err.message : 'Не удалось создать отчёт';
      setError(text);
      showToast(text, 'error');
    } finally {
      setActionKey('');
    }
  };

  return (
    <section className="page">
      <div className="pageHeader"><h1>Кабинет инструктора</h1></div>
      <Alert>{error}</Alert>
      <Alert type="success">{message}</Alert>
      <div className="panel">
        <h2>Мои мероприятия</h2>
        {loading ? <div className="empty">Загружаем расписание...</div> : (
          <DataTable
            items={events}
            empty="Назначенных мероприятий пока нет"
            columns={[
              { key: 'id', title: '№' },
              { key: 'title', title: 'Мероприятие', render: (event) => eventTitle(event) },
              { key: 'date', title: 'Дата', render: (event) => formatDate(event.startDateTime || event.date) },
              { key: 'places', title: 'Места', render: (event) => `${event.availablePlaces ?? '-'} из ${event.totalPlaces ?? '-'}` },
              { key: 'status', title: 'Статус', render: (event) => <StatusBadge value={event.status} /> },
            ]}
            actions={(event) => (
              <button
                disabled={event.status !== 'COMPLETED' || actionKey === `report-${event.id}`}
                title={event.status !== 'COMPLETED' ? 'Отчёт можно создать после завершения мероприятия' : undefined}
                onClick={() => createReport(Number(event.id))}
              >
                {actionKey === `report-${event.id}` ? 'Создаём...' : 'Создать отчёт'}
              </button>
            )}
          />
        )}
      </div>
      <div className="panel">
        <h2>Участники мероприятий</h2>
        <DataTable
          items={bookings}
          empty="Участников по вашим мероприятиям пока нет"
          columns={[
            { key: 'id', title: 'Бронь' },
            { key: 'event', title: 'Мероприятие', render: (booking) => eventTitle(booking.event) },
            { key: 'client', title: 'Клиент', render: (booking) => userName(booking.client) },
            { key: 'participantsCount', title: 'Участников' },
            { key: 'status', title: 'Статус', render: (booking) => <StatusBadge value={booking.status} /> },
          ]}
        />
      </div>
    </section>
  );
}
