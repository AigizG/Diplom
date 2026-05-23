# Информационная система активного отдыха

Spring Boot + React приложение для компании активного отдыха: каталог активностей, расписание мероприятий, бронирования, внутренняя тестовая оплата, кабинеты по ролям, снаряжение, отзывы, документы и отчёты.

## Стек

Backend: Java 17, Spring Boot 3, Spring Security, JWT, Spring Data JPA, PostgreSQL, Flyway, Lombok, MapStruct, Bean Validation, Springdoc OpenAPI, JUnit 5, Mockito.

Frontend: React, Vite, TypeScript, axios.

## Запуск

Backend:

```powershell
mvn spring-boot:run
```

Или вместе с PostgreSQL:

```powershell
docker compose up --build
```

Frontend:

```powershell
cd frontend
npm install
npm run dev
```

Адреса:

- API: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui/index.html`
- Frontend: `http://localhost:5173`
- PostgreSQL: `localhost:5432`, база `active_leisure`

## Переменные окружения

- `DB_URL` - JDBC URL, по умолчанию `jdbc:postgresql://localhost:5432/active_leisure`
- `DB_USERNAME` - пользователь БД
- `DB_PASSWORD` - пароль БД
- `JWT_SECRET` - секрет для подписи JWT
- `JWT_EXPIRATION_MS` - время жизни access token

## Тестовые пользователи

Данные создаются при старте приложения:

- `admin@example.com / admin123` - администратор
- `manager@example.com / manager123` - менеджер
- `instructor@example.com / instructor123` - инструктор
- `client@example.com / client123` - клиент

## Роли

- `CLIENT`: свои бронирования, оплаты, уведомления и отзывы.
- `INSTRUCTOR`: свои мероприятия, участники и отчёт по мероприятию.
- `MANAGER`: бронирования, мероприятия, тестовые оплаты, снаряжение, отзывы, документы и отчёты.
- `ADMIN`: полный доступ, включая пользователей и сотрудников.

## Тестовая оплата

Реальная платёжная система не подключена. Stripe, PayPal, YooKassa, CloudPayments, Robokassa и банковские SDK не используются.

Оплата хранится как внутренняя сущность `Payment` с локальным `mockTransactionId` формата `MOCK-UUID`. Менеджер или администратор вручную меняет статус оплаты:

- `PENDING` - ожидает оплаты
- `PARTIALLY_PAID` - частично оплачена
- `PAID` - оплачена
- `REFUNDED` - возврат
- `CANCELLED` - отменена

При переводе оплаты в `PAID` бронирование становится `PAID`. Клиент видит только свои оплаты, менеджер и администратор видят все оплаты.

## Основные API

- Auth: `/api/auth/register`, `/api/auth/login`
- Users: `/api/users/me`, `/api/users`
- Activities: `/api/activities`, `/api/activities/{id}/details`, `/api/activity-categories`
- Events: `/api/events`
- Bookings: `/api/bookings`, `/api/bookings/my`
- Mock payments: `/api/bookings/{bookingId}/payments/mock-create`, `/api/payments`, `/api/payments/my`, `/api/payments/{id}/mock-paid`, `/api/payments/{id}/mock-partial`, `/api/payments/{id}/mock-refund`, `/api/payments/{id}/mock-cancel`
- Equipment: `/api/equipment`, `/api/events/{eventId}/equipment`
- Dashboards: `/api/dashboard/manager`, `/api/dashboard/admin`
- Instructor cabinet: `/api/instructor/events`, `/api/instructor/events/{id}/participants`, `/api/instructor/events/{id}/report`
- Reports: `/api/reports/*`

## Страницы frontend

- `/` - главная страница с поиском, категориями, популярными активностями и ближайшими датами.
- `/activities` - каталог активностей с фильтрами, сортировкой и карточками.
- `/activities/{id}` - страница активности с расписанием, описанием, отзывами и формой бронирования.
- `/client` - кабинет клиента: бронирования, тестовые оплаты, уведомления и отзывы.
- `/manager` - панель менеджера: заявки, мероприятия, оплаты, снаряжение, документы, отчёты.
- `/admin` - административная панель: пользователи, сотрудники, активности и справочники.
- `/instructor` - кабинет инструктора: свои мероприятия, участники и отчёт.

## Сценарий проверки

1. Войти как клиент `client@example.com / client123`.
2. Найти активность в каталоге.
3. Открыть активность и создать бронирование.
4. Войти как менеджер `manager@example.com / manager123`.
5. Подтвердить бронирование.
6. Создать тестовую оплату.
7. Отметить оплату как оплаченную.
8. Проверить, что бронирование стало `Оплачена`.
9. Войти как инструктор `instructor@example.com / instructor123`.
10. Посмотреть свои мероприятия и участников.
11. Создать отчёт по мероприятию.
12. Войти как администратор `admin@example.com / admin123`.
13. Проверить пользователей, сотрудников и отчёты.

## Проверка сборки

Backend:

```powershell
mvn clean test
```

Frontend:

```powershell
cd frontend
npm install
npm run build
```
