package com.example.activeleisure.common;

public final class Enums {
    private Enums() {
    }

    public enum Role { CLIENT, MANAGER, INSTRUCTOR, ADMIN }
    public enum ActivityStatus { ACTIVE, HIDDEN, ARCHIVED }
    public enum EventStatus { PLANNED, COMPLETED, CANCELLED, POSTPONED }
    public enum BookingStatus { NEW, CONFIRMED, PAID, CANCELLED, COMPLETED }
    public enum PaymentStatus { PENDING, PARTIALLY_PAID, PAID, REFUNDED, CANCELLED }
    public enum PaymentMethod { CASH, CARD_MOCK, BANK_TRANSFER_MOCK }
    public enum EquipmentConditionStatus { AVAILABLE, NEEDS_REPAIR, WRITTEN_OFF }
    public enum EquipmentAssignmentStatus { ASSIGNED, RETURNED }
    public enum NotificationType {
        BOOKING_CREATED, BOOKING_CONFIRMED, BOOKING_CANCELLED, PAYMENT_STATUS_CHANGED,
        EVENT_CHANGED, INSTRUCTOR_ASSIGNED
    }
    public enum DocumentType { CONTRACT, BOOKING_FORM, PARTICIPANT_LIST, EVENT_REPORT }
}
