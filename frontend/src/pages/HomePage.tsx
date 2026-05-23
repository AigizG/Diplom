import { CalendarDays, MapPin, Search, ShieldCheck, Star, WalletCards } from 'lucide-react';
import { FormEvent, useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { activitiesApi, eventsApi, reviewsApi } from '../api/endpoints';
import { StatusBadge } from '../components/StatusBadge';
import type { ActivityCategoryDto, ActivityDto, EventDto, ReviewDto } from '../types';
import { activityTitle, formatDate, label } from '../utils';

export function HomePage() {
  const navigate = useNavigate();
  const [activities, setActivities] = useState<ActivityDto[]>([]);
  const [events, setEvents] = useState<EventDto[]>([]);
  const [categories, setCategories] = useState<ActivityCategoryDto[]>([]);
  const [reviews, setReviews] = useState<ReviewDto[]>([]);
  const [search, setSearch] = useState('');

  useEffect(() => {
    void Promise.all([
      activitiesApi.list({ status: 'ACTIVE', sort: 'createdAt,desc' }).then((items) => setActivities(items.slice(0, 6))).catch(() => []),
      eventsApi.list().then((items) => setEvents(items.slice(0, 5))).catch(() => []),
      activitiesApi.categories().then((items) => setCategories(items.slice(0, 6))).catch(() => []),
      reviewsApi.list().then((items) => setReviews(items.filter((review) => review.moderated).slice(0, 3))).catch(() => []),
    ]);
  }, []);

  const submit = (event: FormEvent) => {
    event.preventDefault();
    navigate(`/activities${search ? `?search=${encodeURIComponent(search)}` : ''}`);
  };

  return (
    <section className="homePage">
      <div className="homeHero">
        <div className="homeHeroContent">
          <span className="eyebrow">Туры и активный отдых по России</span>
          <h1>Выберите маршрут, дату и забронируйте активность онлайн</h1>
          <p>Каталог программ, расписание мероприятий, тестовые оплаты и личные кабинеты для клиентов, менеджеров и инструкторов.</p>
          <form className="heroSearch" onSubmit={submit}>
            <Search size={18} />
            <input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Поиск по названию, месту или формату" />
            <button>Найти</button>
          </form>
        </div>
      </div>

      <div className="page homeSections">
        <section className="sectionBand">
          <div className="pageHeader compact">
            <div>
              <h2>Популярные активности</h2>
              <p>Карточки с ценой, сложностью и ближайшей датой.</p>
            </div>
            <Link className="linkButton" to="/activities">Выбрать активность</Link>
          </div>
          <div className="tourGrid">
            {activities.map((activity) => (
              <Link className="tourCard" to={`/activities/${activity.id}`} key={activity.id}>
                <img src={activity.imageUrl || imageFor(activity.id)} alt="" />
                <div>
                  <div className="cardHeader">
                    <h3>{activityTitle(activity)}</h3>
                    <StatusBadge value={activity.status} />
                  </div>
                  <p>{activity.shortDescription || activity.description || 'Программа активного отдыха'}</p>
                  <div className="metaLine"><MapPin size={15} /> {label(activity.location)} · {label(activity.durationHours)} ч · {label(activity.difficultyLevel)}</div>
                  <strong>{label(activity.price)} ₽</strong>
                </div>
              </Link>
            ))}
          </div>
        </section>

        <section className="sectionBand twoColumns">
          <div>
            <h2>Ближайшие даты</h2>
            <div className="eventList">
              {events.map((event) => (
                <Link className="eventRow" to={`/activities/${event.activity?.id || event.activityId}`} key={event.id}>
                  <CalendarDays size={18} />
                  <span>{activityTitle(event.activity)}</span>
                  <strong>{formatDate(event.startDateTime || event.date)}</strong>
                  <StatusBadge value={event.status} />
                </Link>
              ))}
            </div>
          </div>
          <div>
            <h2>Категории</h2>
            <div className="categoryCloud">
              {categories.map((category) => <Link to={`/activities?category=${category.id}`} key={category.id}>{category.name}</Link>)}
            </div>
          </div>
        </section>

        <section className="sectionBand benefitGrid">
          <div><ShieldCheck size={24} /><h3>Контроль ролей</h3><p>Доступы разделены между клиентами, менеджерами, инструкторами и администраторами.</p></div>
          <div><WalletCards size={24} /><h3>Тестовая оплата</h3><p>Реальные платёжные системы не используются, статусы меняются внутри системы.</p></div>
          <div><Star size={24} /><h3>Отзывы и рейтинг</h3><p>Публикуются только промодерированные отзывы клиентов.</p></div>
        </section>

        {!!reviews.length && (
          <section className="sectionBand">
            <h2>Отзывы клиентов</h2>
            <div className="cardsGrid">
              {reviews.map((review) => <div className="panel" key={review.id}><strong>{review.rating}/5</strong><p>{review.text || review.comment}</p></div>)}
            </div>
          </section>
        )}
      </div>
    </section>
  );
}

function imageFor(id: number) {
  const images = [
    'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=900&q=80',
    'https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=900&q=80',
    'https://images.unsplash.com/photo-1522163182402-834f871fd851?auto=format&fit=crop&w=900&q=80',
  ];
  return images[id % images.length];
}
