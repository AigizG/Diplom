import type { ActivityDto, BookingDto, EmployeeDto, EventDto, UserDto } from './types';

export function label(value: unknown): string {
  if (value === null || value === undefined || value === '') return '-';
  if (typeof value === 'string') return enumLabel(value);
  if (typeof value === 'number') return String(value);
  if (typeof value === 'boolean') return value ? 'Да' : 'Нет';
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

export function employeeUser(employee?: EmployeeDto | UserDto | null): UserDto | null {
  if (!employee) return null;
  return 'user' in employee && employee.user ? (employee.user as UserDto) : (employee as UserDto);
}

export function bookingTitle(booking?: BookingDto | null): string {
  return booking?.id ? `Бронирование #${booking.id}` : '-';
}

export function formatDate(value?: string): string {
  if (!value) return '-';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return date.toLocaleString('ru-RU', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
}

export function objectRows(item: Record<string, unknown>): Array<[string, string]> {
  return Object.entries(item).map(([key, value]) => [fieldLabel(key), label(value)]);
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
  CONTRACT: 'Договор',
  BOOKING_FORM: 'Форма бронирования',
  PARTICIPANT_LIST: 'Список участников',
  EVENT_REPORT: 'Отчёт по мероприятию',
  CLIENT: 'Клиент',
  MANAGER: 'Менеджер',
  INSTRUCTOR: 'Инструктор',
  ADMIN: 'Администратор',
  BOOKING_CREATED: 'Бронирование создано',
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

const fieldLabels: Record<string, string> = {
  id: '№',
  email: 'Эл. почта',
  fullName: 'ФИО',
  phone: 'Телефон',
  role: 'Роль',
  enabled: 'Активен',
  active: 'Активен',
  status: 'Статус',
  amount: 'Сумма',
  totalPrice: 'Сумма',
  paidRevenue: 'Тестовая выручка',
  createdAt: 'Создано',
  updatedAt: 'Обновлено',
  mockTransactionId: 'Тестовая транзакция',
  bookingId: 'Бронирование',
  eventId: 'Мероприятие',
  activityId: 'Активность',
  participantsCount: 'Участников',
  rating: 'Оценка',
  text: 'Текст',
  message: 'Сообщение',
  title: 'Название',
  name: 'Название',
};

export function fieldLabel(value: string): string {
  return fieldLabels[value] || value;
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
