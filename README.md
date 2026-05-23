# Информационная система активного отдыха

Система управления бизнес-процессами компании по организации активного отдыха. Проект состоит из Spring Boot backend и React frontend: каталог активностей, расписание мероприятий, бронирования, внутренняя тестовая оплата, кабинеты по ролям, снаряжение, отзывы, уведомления, документы и JSON-отчёты.

Интерфейс и пользовательские сообщения ориентированы на русскоязычную аудиторию. Enum-значения в API остаются техническими на английском, а во frontend отображаются русские подписи.

## Стек

Java 17, Spring Boot 3, Spring Web, Spring Security, JWT, Spring Data JPA, Hibernate, PostgreSQL, Flyway, Lombok, MapStruct, Bean Validation, Springdoc OpenAPI, JUnit 5, Mockito.

Frontend: React, Vite, TypeScript, axios.

## Запуск

Backend:

```powershell
docker compose up --build
```

Или локально:

```powershell
mvn spring-boot:run
```

Frontend:

```powershell
cd C:\Users\yuutu\DiplomFront
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

## Роли и доступы

- `CLIENT`: свои бронирования, оплаты, уведомления, отзывы.
- `INSTRUCTOR`: свои мероприятия, участники, отчёт по мероприятию.
- `MANAGER`: бронирования, мероприятия, тестовые оплаты, снаряжение, отзывы, документы, отчёты.
- `ADMIN`: полный доступ, включая пользователей и сотрудников.
- Гость: публичный каталог, категории, мероприятия, отзывы и Swagger.

## Тестовая оплата

Реальная платёжная система не подключена. Не используются Stripe, PayPal, YooKassa, CloudPayments, Robokassa или другие внешние сервисы.

Оплата хранится как внутренняя сущность `Payment` с локальным `mockTransactionId` вида `MOCK-UUID`. Менеджер или администратор вручную меняет статус оплаты:

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

## Проверка работоспособности

1. Войти как клиент `client@example.com / client123`.
2. Открыть каталог активностей и применить фильтры.
3. Открыть активность и создать бронирование.
4. Войти как менеджер `manager@example.com / manager123`.
5. Подтвердить бронирование.
6. Создать тестовую оплату.
7. Отметить оплату как оплаченную.
8. Убедиться, что бронирование стало “Оплачена”.
9. Войти как инструктор и посмотреть назначенные мероприятия.
10. Отправить отчёт по мероприятию.
11. Войти как администратор и проверить пользователей, сотрудников и отчёты.

## Проверка сборки

Backend:

```powershell
$env:JAVA_HOME='C:\Users\yuutu\.jdks\ms-21.0.11'
& 'C:\Program Files\JetBrains\IntelliJ IDEA 2026.1.1\plugins\maven\lib\maven3\bin\mvn.cmd' test
```

Frontend:

```powershell
cd C:\Users\yuutu\DiplomFront
npm run build
```
