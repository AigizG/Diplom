import { api, getList, getOne } from './client';
import type {
  ActivityCategoryDto,
  ActivityDetailsDto,
  ActivityDto,
  AdminDashboardDto,
  BookingDto,
  DocumentDto,
  EmployeeDto,
  EquipmentDto,
  EventDto,
  NotificationDto,
  PaymentDto,
  PaymentMethod,
  ManagerDashboardDto,
  ReportResponseDto,
  ReportRow,
  ReviewDto,
  UserDto,
} from '../types';

export const authApi = {
  login: (body: { email: string; password: string }) => api.post('/api/auth/login', body),
  register: (body: Record<string, unknown>) => api.post('/api/auth/register', body),
  me: () => getOne<UserDto>('/api/users/me'),
};

export const usersApi = {
  list: () => getList<UserDto>('/api/users'),
  get: (id: number) => getOne<UserDto>(`/api/users/${id}`),
  patch: (id: number, body: Record<string, unknown>) => api.patch(`/api/users/${id}`, body),
  block: (id: number) => api.patch(`/api/users/${id}/block`),
  unblock: (id: number) => api.patch(`/api/users/${id}/unblock`),
};

export const activitiesApi = {
  list: (params?: Record<string, unknown>) => getList<ActivityDto>('/api/activities', params),
  get: (id: number) => getOne<ActivityDto>(`/api/activities/${id}`),
  details: (id: number) => getOne<ActivityDetailsDto>(`/api/activities/${id}/details`),
  create: (body: Record<string, unknown>) => api.post('/api/activities', body),
  update: (id: number, body: Record<string, unknown>) => api.put(`/api/activities/${id}`, body),
  remove: (id: number) => api.delete(`/api/activities/${id}`),
  categories: () => getList<ActivityCategoryDto>('/api/activity-categories'),
  createCategory: (body: Record<string, unknown>) => api.post('/api/activity-categories', body),
  updateCategory: (id: number, body: Record<string, unknown>) => api.put(`/api/activity-categories/${id}`, body),
  deleteCategory: (id: number) => api.delete(`/api/activity-categories/${id}`),
};

export const eventsApi = {
  list: (params?: Record<string, unknown>) => getList<EventDto>('/api/events', params),
  get: (id: number) => getOne<EventDto>(`/api/events/${id}`),
  create: (body: Record<string, unknown>) => api.post('/api/events', body),
  update: (id: number, body: Record<string, unknown>) => api.put(`/api/events/${id}`, body),
  cancel: (id: number) => api.patch(`/api/events/${id}/cancel`),
  complete: (id: number) => api.patch(`/api/events/${id}/complete`),
  postpone: (id: number, body: Record<string, unknown>) => api.patch(`/api/events/${id}/postpone`, body),
};

export const bookingsApi = {
  create: (body: Record<string, unknown>) => api.post('/api/bookings', body),
  my: () => getList<BookingDto>('/api/bookings/my'),
  list: () => getList<BookingDto>('/api/bookings'),
  get: (id: number) => getOne<BookingDto>(`/api/bookings/${id}`),
  confirm: (id: number) => api.patch(`/api/bookings/${id}/confirm`),
  cancel: (id: number) => api.patch(`/api/bookings/${id}/cancel`),
  complete: (id: number) => api.patch(`/api/bookings/${id}/complete`),
};

export const paymentsApi = {
  createMock: (bookingId: number, method: PaymentMethod) =>
    api.post(`/api/bookings/${bookingId}/payments/mock-create`, { method }),
  list: () => getList<PaymentDto>('/api/payments'),
  my: () => getList<PaymentDto>('/api/payments/my'),
  get: (id: number) => getOne<PaymentDto>(`/api/payments/${id}`),
  paid: (id: number) => api.patch(`/api/payments/${id}/mock-paid`),
  partial: (id: number) => api.patch(`/api/payments/${id}/mock-partial`),
  refund: (id: number) => api.patch(`/api/payments/${id}/mock-refund`),
  cancel: (id: number) => api.patch(`/api/payments/${id}/mock-cancel`),
};

export const employeesApi = {
  create: (body: Record<string, unknown>) => api.post('/api/employees', body),
  list: () => getList<EmployeeDto>('/api/employees'),
  get: (id: number) => getOne<EmployeeDto>(`/api/employees/${id}`),
  update: (id: number, body: Record<string, unknown>) => api.put(`/api/employees/${id}`, body),
  deactivate: (id: number) => api.patch(`/api/employees/${id}/deactivate`),
  instructors: () => getList<UserDto>('/api/instructors'),
  schedule: (id: number) => getList<EventDto>(`/api/instructors/${id}/schedule`),
};

export const instructorApi = {
  events: () => getList<EventDto>('/api/instructor/events'),
  event: (id: number) => getOne<EventDto>(`/api/instructor/events/${id}`),
  participants: (id: number) => getList<Record<string, unknown>>(`/api/instructor/events/${id}/participants`),
  report: (id: number, content = '') => api.post(`/api/instructor/events/${id}/report`, { content }),
};

export const equipmentApi = {
  list: () => getList<EquipmentDto>('/api/equipment'),
  create: (body: Record<string, unknown>) => api.post('/api/equipment', body),
  get: (id: number) => getOne<EquipmentDto>(`/api/equipment/${id}`),
  update: (id: number, body: Record<string, unknown>) => api.put(`/api/equipment/${id}`, body),
  remove: (id: number) => api.delete(`/api/equipment/${id}`),
  assign: (eventId: number, body: Record<string, unknown>) => api.post(`/api/events/${eventId}/equipment`, body),
  returnAssignment: (id: number) => api.patch(`/api/equipment-assignments/${id}/return`),
  eventEquipment: (eventId: number) => getList<Record<string, unknown>>(`/api/events/${eventId}/equipment`),
  categories: () => getList<Record<string, unknown>>('/api/equipment-categories'),
  createCategory: (body: Record<string, unknown>) => api.post('/api/equipment-categories', body),
};

export const reviewsApi = {
  create: (body: Record<string, unknown>) => api.post('/api/reviews', body),
  byActivity: (activityId: number) => getList<ReviewDto>(`/api/activities/${activityId}/reviews`),
  list: () => getList<ReviewDto>('/api/reviews'),
  moderate: (id: number) => api.patch(`/api/reviews/${id}/moderate`),
  hide: (id: number) => api.patch(`/api/reviews/${id}/hide`),
  remove: (id: number) => api.delete(`/api/reviews/${id}`),
};

export const notificationsApi = {
  my: () => getList<NotificationDto>('/api/notifications/my'),
  read: (id: number) => api.patch(`/api/notifications/${id}/read`),
  readAll: () => api.patch('/api/notifications/read-all'),
};

export const documentsApi = {
  contract: (bookingId: number) => api.post(`/api/bookings/${bookingId}/documents/contract`),
  participantList: (eventId: number) => api.post(`/api/events/${eventId}/documents/participant-list`),
  eventReport: (eventId: number) => api.post(`/api/events/${eventId}/documents/event-report`),
  get: (id: number) => getOne<DocumentDto>(`/api/documents/${id}`),
  byBooking: (bookingId: number) => getList<DocumentDto>(`/api/bookings/${bookingId}/documents`),
};

const reportParams = (params: Record<string, unknown>) => new URLSearchParams(Object.entries(params).filter(([, value]) => value).map(([key, value]) => [key, String(value)])).toString();

export const reportsApi = {
  bookings: (params: Record<string, unknown>) => getOne<ReportResponseDto>('/api/reports/bookings?' + reportParams(params)),
  revenue: (params: Record<string, unknown>) => getOne<ReportResponseDto>('/api/reports/revenue?' + reportParams(params)),
  popular: (params: Record<string, unknown>) => getOne<ReportResponseDto>('/api/reports/popular-activities?' + reportParams(params)),
  workload: (params: Record<string, unknown>) => getOne<ReportResponseDto>('/api/reports/instructor-workload?' + reportParams(params)),
  equipment: (params: Record<string, unknown>) => getOne<ReportResponseDto>('/api/reports/equipment-usage?' + reportParams(params)),
  cancellations: (params: Record<string, unknown>) => getOne<ReportResponseDto>('/api/reports/cancellations?' + reportParams(params)),
};

export const dashboardApi = {
  manager: () => getOne<ManagerDashboardDto>('/api/dashboard/manager'),
  admin: () => getOne<AdminDashboardDto>('/api/dashboard/admin'),
};

