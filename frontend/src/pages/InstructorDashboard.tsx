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
  const [events, setEvents] = useState<EventDto[]>([]);
  const [bookings, setBookings] = useState<BookingDto[]>([]);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(true);
  const [actionKey, setActionKey] = useState('');
  const toast = useToast();

  const load = async () => {
    if (!user?.id) return;
    setLoading(true);
    setError('');
    try {
      const [schedule, allBookings] = await Promise.all([
        instructorApi.events(),
        bookingsApi.list().catch(() => []),
      ]);
      setEvents(schedule);
      const ids = new Set(schedule.map((item) => item.id));
      setBookings(allBookings.filter((booking) => booking.event?.id && ids.has(booking.event.id)));
    } catch (err) {
      const text = err instanceof Error ? err.message : 'Не удалось загрузить расписание';
      setError(text);
      toast.error(text);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void load();
  }, [user?.id]);

  const createReport = async (event: EventDto) => {
    const key = `report-${event.id}`;
    setActionKey(key);
    setError('');
    setMessage('');
    try {
      await instructorApi.report(Number(event.id), `Отчёт по мероприятию ${eventTitle(event)} от ${formatDate(new Date().toISOString())}`);
      setMessage('Отчёт мероприятия создан');
      toast.success('Отчёт мероприятия создан');
    } catch (err) {
      const text = err instanceof Error ? err.message : 'Не удалось создать отчёт';
      setError(text);
      toast.error(text);
    } finally {
      setActionKey('');
    }
  };

  return (
    <section className="page">
      <div className="pageHeader"><h1>Кабинет инструктора</h1></div>
      <Alert>{error}</Alert>
      <Alert type="success">{message}</Alert>
      {loading && <div className="alert info">Загружаем расписание...</div>}
      <div className="panel">
        <h2>Мои мероприятия</h2>
        <DataTable
          items={events}
          columns={[
            { key: 'id', title: '№' },
            { key: 'title', title: 'Мероприятие', render: (event) => eventTitle(event) },
            { key: 'date', title: 'Дата', render: (event) => formatDate(event.startDateTime || event.date) },
            { key: 'location', title: 'Локация' },
            { key: 'status', title: 'Статус', render: (event) => <StatusBadge value={event.status} /> },
          ]}
          actions={(event) => (
            <button
              disabled={actionKey === `report-${event.id}`}
              onClick={() => void createReport(event)}
            >
              {actionKey === `report-${event.id}` ? 'Создаём...' : 'Создать отчёт'}
            </button>
          )}
        />
      </div>
      <div className="panel">
        <h2>Участники мероприятий</h2>
        <DataTable
          items={bookings}
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
