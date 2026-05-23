import { CalendarDays, Clock, MapPin, Search, SlidersHorizontal, Star } from 'lucide-react';
import { FormEvent, useEffect, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { activitiesApi } from '../api/endpoints';
import { Alert } from '../components/Alert';
import { StatusBadge } from '../components/StatusBadge';
import type { ActivityCategoryDto, ActivityDto } from '../types';
import { activityTitle, enumLabel, formatPrice, label } from '../utils';

export function CatalogPage() {
  const [activities, setActivities] = useState<ActivityDto[]>([]);
  const [categories, setCategories] = useState<ActivityCategoryDto[]>([]);
  const [filters, setFilters] = useState<Record<string, string>>({});
  const [searchParams] = useSearchParams();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const load = async (params = filters) => {
    setLoading(true);
    setError('');
    try {
      const cleanParams = Object.fromEntries(Object.entries(params).filter(([, value]) => value));
      const [activityList, categoryList] = await Promise.all([
        activitiesApi.list(cleanParams),
        activitiesApi.categories().catch(() => []),
      ]);
      setActivities(activityList);
      setCategories(categoryList);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Не удалось загрузить активности');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const initial = Object.fromEntries(searchParams.entries());
    setFilters(initial);
    void load(initial);
  }, [searchParams]);

  const submit = (event: FormEvent) => {
    event.preventDefault();
    void load(filters);
  };

  return (
    <section className="page catalogPage">
      <div className="pageHeader">
        <div>
          <h1>Каталог активного отдыха</h1>
          <p>Подберите маршрут по цене, сложности, дате и месту проведения.</p>
        </div>
      </div>
      <Alert>{error}</Alert>

      <div className="catalogLayout">
        <aside className="filterPanel">
          <div className="filterTitle"><SlidersHorizontal size={18} /> Фильтры</div>
          <form className="filterStack" onSubmit={submit}>
            <label>
              <span>Категория</span>
              <select value={filters.category || ''} onChange={(event) => setFilters({ ...filters, category: event.target.value })}>
                <option value="">Все категории</option>
                {categories.map((category) => <option key={category.id} value={category.id}>{category.name}</option>)}
              </select>
            </label>
            <label><span>Поиск</span><input placeholder="Название или формат" value={filters.search || ''} onChange={(event) => setFilters({ ...filters, search: event.target.value })} /></label>
            <label><span>Локация</span><input placeholder="Город, регион, место" value={filters.location || ''} onChange={(event) => setFilters({ ...filters, location: event.target.value })} /></label>
            <label><span>Сложность</span><input placeholder="Лёгкая, средняя, высокая" value={filters.difficultyLevel || ''} onChange={(event) => setFilters({ ...filters, difficultyLevel: event.target.value })} /></label>
            <div className="rangeRow">
              <label><span>Цена от</span><input type="number" value={filters.minPrice || ''} onChange={(event) => setFilters({ ...filters, minPrice: event.target.value })} /></label>
              <label><span>Цена до</span><input type="number" value={filters.maxPrice || ''} onChange={(event) => setFilters({ ...filters, maxPrice: event.target.value })} /></label>
            </div>
            <div className="rangeRow">
              <label><span>Длительность от</span><input type="number" value={filters.minDurationHours || ''} onChange={(event) => setFilters({ ...filters, minDurationHours: event.target.value })} /></label>
              <label><span>Дата</span><input type="date" value={filters.date || ''} onChange={(event) => setFilters({ ...filters, date: event.target.value })} /></label>
            </div>
            <label>
              <span>Статус</span>
              <select value={filters.status || ''} onChange={(event) => setFilters({ ...filters, status: event.target.value })}>
                <option value="">Любой статус</option>
                <option value="ACTIVE">{enumLabel('ACTIVE')}</option>
                <option value="HIDDEN">{enumLabel('HIDDEN')}</option>
                <option value="ARCHIVED">{enumLabel('ARCHIVED')}</option>
              </select>
            </label>
            <label>
              <span>Сортировка</span>
              <select value={filters.sort || 'title,asc'} onChange={(event) => setFilters({ ...filters, sort: event.target.value })}>
                <option value="title,asc">По названию</option>
                <option value="price,asc">Сначала дешевле</option>
                <option value="price,desc">Сначала дороже</option>
                <option value="durationHours,asc">Короче по времени</option>
              </select>
            </label>
            <button><Search size={16} /> Найти</button>
            <button type="button" className="secondary" onClick={() => { setFilters({}); void load({}); }}>Сбросить фильтры</button>
          </form>
        </aside>

        <div className="catalogResults">
          <div className="resultBar">
            <strong>{loading ? 'Ищем активности...' : `Найдено: ${activities.length}`}</strong>
            <span>Цены указаны за одного участника</span>
          </div>
          {loading ? <div className="panel">Загрузка...</div> : (
            <div className="tourList">
              {activities.map((activity) => (
                <Link className="tourResultCard" to={`/activities/${activity.id}`} key={activity.id}>
                  <img src={activity.imageUrl || 'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=900&q=80'} alt="" />
                  <div className="tourResultBody">
                    <div className="cardHeader">
                      <h2>{activityTitle(activity)}</h2>
                      <StatusBadge value={activity.status} />
                    </div>
                    <p>{activity.shortDescription || activity.description || 'Описание не заполнено'}</p>
                    <div className="tourFacts">
                      <span><MapPin size={15} /> {label(activity.location)}</span>
                      <span><Clock size={15} /> {label(activity.durationHours)} ч</span>
                      <span><Star size={15} /> {activity.averageRating ? activity.averageRating.toFixed(1) : 'Нет оценок'}</span>
                      <span><CalendarDays size={15} /> {activity.nearestEvent?.startDateTime ? new Date(activity.nearestEvent.startDateTime).toLocaleDateString('ru-RU') : 'Дата уточняется'}</span>
                    </div>
                  </div>
                  <div className="tourResultAside">
                    <span>{label(activity.category)}</span>
                    <strong>{formatPrice(activity.price)}</strong>
                    <small>{label(activity.difficultyLevel)}</small>
                    <span className="fakeButton">Подробнее</span>
                  </div>
                </Link>
              ))}
              {!activities.length && <div className="empty">Активности не найдены. Измените фильтры или сбросьте условия поиска.</div>}
            </div>
          )}
        </div>
      </div>
    </section>
  );
}
