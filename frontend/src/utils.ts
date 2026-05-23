import type { ActivityDto, BookingDto, EventDto, UserDto } from './types';

export function label(value: unknown): string {
  if (value === null || value === undefined || value === '') return '-';
  if (typeof value === 'string') return enumLabel(value);
  if (typeof value === 'number' || typeof value === 'boolean') return String(value);
  if (typeof value === 'object') {
    const item = value as Record<string, unknown>;
    return String(item.name || item.title || item.email || item.id || JSON.stringify(item));
  }
  return String(value);
}

export function activityTitle(activity?: ActivityDto | null): string {
  return activity?.title || activity?.name || (activity?.id ? `Активность #${activity.id}` : 'Активность');
}

export function eventTitle(event?: EventDto | null): string {
  const title = event?.title || activityTitle(event?.activity);
  const date = event?.startDateTime || event?.date;
  return date ? `${title} · ${formatDate(date)}` : title;
}

export function userName(user?: UserDto | null): string {
  return user?.fullName || user?.name || user?.email || (user?.id ? `Пользователь #${user.id}` : '-');
}

export function bookingTitle(booking?: BookingDto | null): string {
  return booking?.id ? `Бронирование #${booking.id}` : '-';
}

export function formatDate(value?: string): string {
  if (!value) return '-';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return date.toLocaleString('ru-RU');
}

export function objectRows(item: Record<string, unknown>): Array<[string, string]> {
  return Object.entries(item).map(([key, value]) => [key, label(value)]);
}

export function isManagerRole(role?: string) {
  return role === 'MANAGER' || role === 'ADMIN';
}

const enumLabels: Record<string, string> = {
  ACTIVE: 'Активна',
  HIDDEN: 'Скрыта',
  ARCHIVED: 'В архиве',
  PLANNED: 'Запланировано',
  CONFIRMED: 'Подтверждена',
  PAID: 'Оплачена',
  COMPLETED: 'Завершена',
  PENDING: 'Ожидает оплаты',
  NEW: 'Новая',
  PARTIALLY_PAID: 'Частично оплачена',
  POSTPONED: 'Перенесено',
  CANCELLED: 'Отменено',
  REFUNDED: 'Возврат',
  AVAILABLE: 'Доступно',
  NEEDS_REPAIR: 'Требует ремонта',
  WRITTEN_OFF: 'Списано',
  ASSIGNED: 'Назначено',
  RETURNED: 'Возвращено',
  CASH: 'Наличные',
  CARD_MOCK: 'Тестовая карта',
  BANK_TRANSFER_MOCK: 'Тестовый банковский перевод',
  CLIENT: 'Клиент',
  MANAGER: 'Менеджер',
  INSTRUCTOR: 'Инструктор',
  ADMIN: 'Администратор',
  BOOKING_CONFIRMED: 'Бронирование подтверждено',
  BOOKING_CANCELLED: 'Бронирование отменено',
  PAYMENT_STATUS_CHANGED: 'Статус оплаты изменён',
  EVENT_CHANGED: 'Мероприятие изменено',
  INSTRUCTOR_ASSIGNED: 'Инструктор назначен',
};

export function enumLabel(value?: string | null): string {
  if (!value) return '-';
  return enumLabels[value] || value;
}

export function reportTitle(value: string): string {
  const titles: Record<string, string> = {
    bookings: 'Бронирования',
    revenue: 'Выручка',
    popular: 'Популярные активности',
    workload: 'Загрузка инструкторов',
    equipmentUsage: 'Использование снаряжения',
    cancellations: 'Отмены',
  };
  return titles[value] || value;
}

export function splitTextList(value?: string | null): string[] {
  if (!value) return [];
  return value
    .split(/\r?\n|[,;]/)
    .map((item) => item.trim())
    .filter(Boolean);
}

export function formatPrice(value?: number | string | null): string {
  if (value === null || value === undefined || value === '') return '-';
  const number = Number(value);
  if (Number.isNaN(number)) return String(value);
  return new Intl.NumberFormat('ru-RU', { style: 'currency', currency: 'RUB', maximumFractionDigits: 0 }).format(number);
}

