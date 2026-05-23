import { useEffect, useMemo, useState } from 'react';
import { useParams } from 'react-router-dom';
import { activitiesApi, bookingsApi } from '../api/endpoints';
import { useAuth } from '../auth/AuthContext';
import { Alert } from '../components/Alert';
import { DataTable } from '../components/DataTable';
import { StatusBadge } from '../components/StatusBadge';
import type { ActivityDetailsDto, ActivityDto, EventDto, ReviewDto } from '../types';
import { activityTitle, formatDate, formatPrice, label, splitTextList, userName } from '../utils';

export function ActivityDetailPage() {
  const { id } = useParams();
  const activityId = Number(id);
  const { user } = useAuth();
  const [details, setDetails] = useState<ActivityDetailsDto | null>(null);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(true);
  const [selectedEventId, setSelectedEventId] = useState<number | ''>('');
  const [participantsCount, setParticipantsCount] = useState(1);
  const [comment, setComment] = useState('');
  const [participantNames, setParticipantNames] = useState(['']);

  const activity = details?.activity;
  const events = details?.nearestEvents || [];
  const reviews = details?.reviews || [];
  const selectedEvent = events.find((event) => event.id === selectedEventId);
  const total = useMemo(() => Number(activity?.price || 0) * participantsCount, [activity?.price, participantsCount]);

  const load = async () => {
    setError('');
    setLoading(true);
    try {
      const data = await activitiesApi.details(activityId);
      setDetails(data);
      setSelectedEventId(data.nearestEvents[0]?.id || '');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Не удалось загрузить активность');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (activityId) void load();
  }, [activityId]);

  const book = async () => {
    if (!user) {
      setError('Войдите как клиент, чтобы создать бронирование');
      return;
    }
    if (!selectedEventId) {
      setError('Выберите дату мероприятия');
      return;
    }
    setError('');
    setSuccess('');
    try {
      const participants = Array.from({ length: participantsCount }, (_, index) => ({ fullName: participantNames[index] || `Участник ${index + 1}` }));
      await bookingsApi.create({ eventId: selectedEventId, participantsCount, comment, participants });
      setSuccess('Бронирование создано');
      await load();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Не удалось создать бронирование');
    }
  };

  if (loading) return <section className="page"><div className="panel">Загрузка...</div></section>;

  return (
    <section className="page detailPage">
      <Alert>{error}</Alert>
      <Alert type="success">{success}</Alert>
      <div className="detailHero">
        <div className="detailHeroGallery">
          <img src={activity?.imageUrl || fallbackImage(activity)} alt="" />
          <div className="detailThumbs">
            {gallery(activity).slice(0, 3).map((image) => <img key={image} src={image} alt="" />)}
          </div>
        </div>
        <div>
          <div className="pageHeader compact">
            <div>
              <h1>{activityTitle(activity)}</h1>
              <p>{activity?.shortDescription || activity?.description || 'Описание не заполнено'}</p>
            </div>
            <StatusBadge value={activity?.status} />
          </div>
          <dl className="detailsGrid">
            <div><dt>Категория</dt><dd>{label(activity?.category)}</dd></div>
            <div><dt>Сложность</dt><dd>{label(activity?.difficultyLevel)}</dd></div>
            <div><dt>Локация</dt><dd>{label(activity?.location)}</dd></div>
            <div><dt>Длительность</dt><dd>{label(activity?.durationHours)} ч</dd></div>
            <div><dt>Возраст</dt><dd>{activity?.minAge ? `${activity.minAge}+` : '-'}</dd></div>
            <div><dt>Цена</dt><dd>{formatPrice(activity?.price)}</dd></div>
            <div><dt>Рейтинг</dt><dd>{details?.averageRating ? details.averageRating.toFixed(1) : '-'}</dd></div>
            <div><dt>Отзывы</dt><dd>{details?.reviewCount || 0}</dd></div>
          </dl>
          <div className="detailCta">
            <a className="linkButton" href="#booking">Выбрать дату</a>
            <span>Мест на выбранную дату: {selectedEvent?.availablePlaces ?? '-'}</span>
          </div>
        </div>
      </div>

      <div className="detailGrid">
        <div className="panel">
          <h2>Описание</h2>
          <p>{activity?.description}</p>
          <InfoList title="Программа маршрута" items={splitTextList(activity?.routeDescription)} fallback={activity?.routeDescription} />
          <InfoList title="Что входит" items={splitTextList(activity?.includedServices)} />
          <InfoList title="Что не входит" items={splitTextList(activity?.notIncludedServices)} />
          <InfoList title="Что взять с собой" items={splitTextList(activity?.requiredEquipmentDescription)} fallback={activity?.requiredEquipmentDescription} />
          {activity?.healthRestrictions && <><h3>Ограничения</h3><p>{activity.healthRestrictions}</p></>}
        </div>

        <div className="panel bookingBox" id="booking">
          <h2>Бронирование</h2>
          <label>
            <span>Дата</span>
            <select value={selectedEventId} onChange={(event) => setSelectedEventId(Number(event.target.value))}>
              {events.map((event) => <option key={event.id} value={event.id}>{formatDate(event.startDateTime || event.date)} · мест: {event.availablePlaces ?? '-'}</option>)}
            </select>
          </label>
          <label>
            <span>Участников</span>
            <input min={1} max={selectedEvent?.availablePlaces || activity?.maxParticipants || 99} type="number" value={participantsCount} onChange={(event) => { const count = Number(event.target.value); setParticipantsCount(count); setParticipantNames((current) => Array.from({ length: count }, (_, index) => current[index] || '')); }} />
          </label>
          {Array.from({ length: participantsCount }, (_, index) => (
            <label key={index}>
              <span>{`ФИО участника ${index + 1}`}</span>
              <input value={participantNames[index] || ''} onChange={(event) => setParticipantNames((current) => Array.from({ length: participantsCount }, (_, itemIndex) => itemIndex === index ? event.target.value : current[itemIndex] || ''))} />
            </label>
          ))}
          <label>
            <span>Комментарий</span>
            <textarea value={comment} onChange={(event) => setComment(event.target.value)} />
          </label>
          <div className="totalLine"><span>Итого</span><strong>{formatPrice(total)}</strong></div>
          <button disabled={!events.length || user?.role !== 'CLIENT'} onClick={() => void book()}>Создать бронирование</button>
          {!user && <p className="muted">Для бронирования войдите как клиент. Если вы вошли под другой ролью, кнопка недоступна.</p>}
        </div>
      </div>

      <div className="panel">
        <h2>Доступные даты</h2>
        <DataTable
          items={events}
          columns={[
            { key: 'date', title: 'Дата', render: (event) => formatDate(event.startDateTime || event.date) },
            { key: 'places', title: 'Места', render: (event) => `${event.availablePlaces ?? '-'} из ${event.totalPlaces ?? '-'}` },
            { key: 'instructor', title: 'Инструктор', render: (event) => userName(event.instructor) },
            { key: 'status', title: 'Статус', render: (event) => <StatusBadge value={event.status} /> },
          ]}
        />
      </div>

      <div className="panel">
        <h2>Отзывы</h2>
        <DataTable
          items={reviews}
          columns={[
            { key: 'author', title: 'Автор', render: (review) => userName(review.author || review.client) },
            { key: 'rating', title: 'Оценка' },
            { key: 'text', title: 'Текст', render: (review) => review.text || review.comment || '-' },
          ]}
        />
      </div>
    </section>
  );
}

function InfoList({ title, items, fallback }: { title: string; items: string[]; fallback?: string }) {
  if (!items.length && !fallback) return null;
  return (
    <>
      <h3>{title}</h3>
      {items.length ? <ul className="checkList">{items.map((item) => <li key={item}>{item}</li>)}</ul> : <p>{fallback}</p>}
    </>
  );
}

function fallbackImage(activity?: ActivityDto | null) {
  return activity?.galleryImages?.split(',')[0]?.trim()
    || 'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1200&q=80';
}

function gallery(activity?: ActivityDto | null) {
  const images = activity?.galleryImages
    ?.split(',')
    .map((item) => item.trim())
    .filter(Boolean) || [];
  if (activity?.imageUrl) images.unshift(activity.imageUrl);
  return images.length ? Array.from(new Set(images)) : [
    'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=900&q=80',
    'https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=900&q=80',
    'https://images.unsplash.com/photo-1522163182402-834f871fd851?auto=format&fit=crop&w=900&q=80',
  ];
}



