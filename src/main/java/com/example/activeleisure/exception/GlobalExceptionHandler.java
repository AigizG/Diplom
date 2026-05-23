package com.example.activeleisure.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    ResponseEntity<ApiError> notFound(EntityNotFoundException ex, HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, normalize(ex.getMessage(), "Запрошенный ресурс не найден"), request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ApiError> denied(AccessDeniedException ex, HttpServletRequest request) {
        return error(HttpStatus.FORBIDDEN, normalize(ex.getMessage(), "У вас нет доступа к этому ресурсу"), request);
    }

    @ExceptionHandler({ValidationException.class, IllegalArgumentException.class})
    ResponseEntity<ApiError> validation(Exception ex, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, normalize(ex.getMessage(), "Проверьте корректность данных"), request);
    }

    @ExceptionHandler(IllegalStateException.class)
    ResponseEntity<ApiError> illegalState(IllegalStateException ex, HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, normalize(ex.getMessage(), "Операцию нельзя выполнить в текущем состоянии"), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> invalid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return error(HttpStatus.BAD_REQUEST, message, request);
    }

    private ResponseEntity<ApiError> error(HttpStatus status, String message, HttpServletRequest request) {
        return ResponseEntity.status(status)
                .body(new ApiError(Instant.now(), status.value(), reason(status), message, request.getRequestURI()));
    }

    private String reason(HttpStatus status) {
        return switch (status) {
            case BAD_REQUEST -> "Ошибка валидации";
            case FORBIDDEN -> "Доступ запрещён";
            case NOT_FOUND -> "Не найдено";
            case CONFLICT -> "Конфликт данных";
            default -> "Ошибка";
        };
    }

    private String normalize(String message, String fallback) {
        if (message == null || message.isBlank()) return fallback;
        return switch (message) {
            case "User not found" -> "Пользователь не найден";
            case "Activity not found" -> "Активность не найдена";
            case "Activity category not found" -> "Категория активности не найдена";
            case "Event not found" -> "Мероприятие не найдено";
            case "Booking not found" -> "Бронирование не найдено";
            case "Payment not found" -> "Оплата не найдена";
            case "Equipment not found" -> "Снаряжение не найдено";
            case "Equipment category not found" -> "Категория снаряжения не найдена";
            case "Equipment assignment not found" -> "Назначение снаряжения не найдено";
            case "Review not found" -> "Отзыв не найден";
            case "Employee not found" -> "Сотрудник не найден";
            case "Access denied to booking" -> "У вас нет доступа к этому бронированию";
            case "Access denied to payment" -> "У вас нет доступа к этой оплате";
            case "Booking belongs to another client" -> "Бронирование принадлежит другому клиенту";
            case "Event belongs to another instructor" -> "Мероприятие назначено другому инструктору";
            case "Email already registered" -> "Пользователь с такой электронной почтой уже зарегистрирован";
            case "Employee role must not be CLIENT" -> "Для сотрудника нельзя указать роль клиента";
            case "Cannot book cancelled or completed event" -> "Нельзя забронировать отменённое или завершённое мероприятие";
            case "Not enough available places" -> "Недостаточно свободных мест";
            case "Event end date must be after start date" -> "Дата окончания мероприятия должна быть позже даты начала";
            case "Assigned user must be an instructor" -> "Назначенный пользователь должен быть инструктором";
            case "Instructor is busy at this time" -> "Нельзя назначить инструктора на пересекающиеся мероприятия";
            case "Total places below existing bookings" -> "Количество мест меньше уже оформленных бронирований";
            case "Written-off equipment cannot be assigned" -> "Нельзя назначить списанное снаряжение";
            case "Not enough equipment available" -> "Снаряжение недоступно в нужном количестве";
            case "Review is allowed only for completed booking" -> "Отзыв можно оставить только после завершённого бронирования";
            default -> message;
        };
    }
}
