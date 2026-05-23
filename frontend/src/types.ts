export type Role = 'CLIENT' | 'MANAGER' | 'INSTRUCTOR' | 'ADMIN';
export type ActivityStatus = 'ACTIVE' | 'HIDDEN' | 'ARCHIVED';
export type EventStatus = 'PLANNED' | 'COMPLETED' | 'CANCELLED' | 'POSTPONED';
export type BookingStatus = 'NEW' | 'CONFIRMED' | 'PAID' | 'CANCELLED' | 'COMPLETED';
export type PaymentStatus = 'PENDING' | 'PARTIALLY_PAID' | 'PAID' | 'REFUNDED' | 'CANCELLED';
export type PaymentMethod = 'CASH' | 'CARD_MOCK' | 'BANK_TRANSFER_MOCK';
export type EquipmentConditionStatus = 'AVAILABLE' | 'NEEDS_REPAIR' | 'WRITTEN_OFF';
export type EquipmentAssignmentStatus = 'ASSIGNED' | 'RETURNED';
export type DocumentType = 'CONTRACT' | 'BOOKING_FORM' | 'PARTICIPANT_LIST' | 'EVENT_REPORT';
export type NotificationType =
  | 'BOOKING_CREATED'
  | 'BOOKING_CONFIRMED'
  | 'BOOKING_CANCELLED'
  | 'PAYMENT_STATUS_CHANGED'
  | 'EVENT_CHANGED'
  | 'INSTRUCTOR_ASSIGNED';

export interface ApiErrorBody {
  timestamp?: string;
  status?: number;
  error?: string;
  message?: string;
  path?: string;
}

export interface UserDto {
  id: number;
  email: string;
  fullName?: string;
  name?: string;
  phone?: string;
  role: Role;
  enabled?: boolean;
  blocked?: boolean;
  active?: boolean;
  [key: string]: unknown;
}

export interface AuthResponse {
  accessToken?: string;
  token?: string;
  jwt?: string;
  user?: UserDto;
}

export interface ActivityCategoryDto {
  id: number;
  name: string;
  description?: string;
  [key: string]: unknown;
}

export interface ActivityDto {
  id: number;
  title?: string;
  name?: string;
  description?: string;
  shortDescription?: string;
  category?: ActivityCategoryDto | string;
  difficultyLevel?: string;
  location?: string;
  price?: number;
  status?: ActivityStatus;
  durationHours?: number;
  [key: string]: unknown;
  minAge?: number;
  maxParticipants?: number;
  minParticipants?: number;
  healthRestrictions?: string;
  requiredEquipmentDescription?: string;
  imageUrl?: string;
  galleryImages?: string;
  includedServices?: string;
  notIncludedServices?: string;
  routeDescription?: string;
  averageRating?: number;
  reviewCount?: number;
  nearestEvent?: EventSummaryDto;
}

export interface EventSummaryDto {
  id: number;
  startDateTime?: string;
  endDateTime?: string;
  totalPlaces?: number;
  availablePlaces?: number;
  status?: EventStatus;
}

export interface ActivityDetailsDto {
  activity: ActivityDto;
  nearestEvents: EventDto[];
  reviews: ReviewDto[];
  averageRating?: number;
  reviewCount?: number;
}

export interface EventDto {
  id: number;
  activity?: ActivityDto;
  activityId?: number;
  title?: string;
  startDateTime?: string;
  date?: string;
  location?: string;
  capacity?: number;
  totalPlaces?: number;
  availablePlaces?: number;
  instructor?: UserDto;
  instructorId?: number;
  status?: EventStatus;
  [key: string]: unknown;
}

export interface BookingDto {
  id: number;
  event?: EventDto;
  eventId?: number;
  client?: UserDto;
  clientId?: number;
  participantsCount?: number;
  status?: BookingStatus;
  totalPrice?: number;
  createdAt?: string;
  [key: string]: unknown;
}

export interface PaymentDto {
  id: number;
  bookingId?: number;
  amount?: number;
  method?: PaymentMethod;
  status?: PaymentStatus;
  mockTransactionId?: string;
  paidAt?: string;
  createdAt?: string;
  [key: string]: unknown;
}

export interface EquipmentDto {
  id: number;
  name: string;
  category?: string | { id: number; name: string };
  quantityTotal?: number;
  quantityAvailable?: number;
  conditionStatus?: EquipmentConditionStatus;
  status?: EquipmentConditionStatus;
  description?: string;
  [key: string]: unknown;
}

export interface ReviewDto {
  id: number;
  activity?: ActivityDto;
  activityId?: number;
  author?: UserDto;
  client?: UserDto;
  rating?: number;
  text?: string;
  comment?: string;
  hidden?: boolean;
  moderated?: boolean;
  [key: string]: unknown;
}

export interface NotificationDto {
  id: number;
  type?: NotificationType;
  title?: string;
  message?: string;
  read?: boolean;
  createdAt?: string;
  [key: string]: unknown;
}

export interface DocumentDto {
  id: number;
  bookingId?: number;
  type?: DocumentType;
  fileName?: string;
  content?: string;
  createdAt?: string;
  [key: string]: unknown;
}

export interface EmployeeDto {
  id: number;
  user: UserDto;
  specialization?: string;
  experienceYears?: number;
  bio?: string;
  active?: boolean;
  [key: string]: unknown;
}

export interface ReportRow {
  [key: string]: unknown;
}

export interface ReportResponseDto {
  name: string;
  data: unknown;
}

export interface ManagerDashboardDto {
  newBookings: number;
  confirmedBookings: number;
  paidBookings: number;
  paidRevenue: number;
  nearestEvents: EventDto[];
  popularActivities: Record<string, number>;
  instructorWorkload: Record<string, number>;
}

export interface AdminDashboardDto {
  users: number;
  clients: number;
  employees: number;
  activities: number;
  bookings: number;
  paidRevenue: number;
  recentActions: NotificationDto[];
}

export type ListResponse<T> = T[] | { content?: T[]; items?: T[]; data?: T[]; [key: string]: unknown };
