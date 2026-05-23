# Информационная система активного отдыха

Spring Boot + React система для управления активностями, мероприятиями, бронированиями, тестовыми оплатами, снаряжением, отзывами, уведомлениями, документами и отчётами.

Интерфейс рассчитан на русскоязычных пользователей. Enum-значения в API остаются техническими на английском, а во frontend отображаются русские подписи.

## Стек

- Backend: Java 17, Spring Boot 3, Spring Web, Spring Security, JWT, Spring Data JPA, Hibernate, PostgreSQL, Flyway, Lombok, MapStruct, Bean Validation, Springdoc OpenAPI, JUnit 5, Mockito.
- Frontend: React, Vite, TypeScript, axios, React Router, lucide-react.

## Структура

- `src/main/java` - backend-код.
- `src/main/resources/db/migration` - Flyway-миграции.
- `src/test/java` - backend-тесты.
- `frontend` - React/Vite frontend.
- `docker-compose.yml` - PostgreSQL и backend в Docker.

## Запуск

Backend через Docker:

```powershell
docker compose up --build
```

Backend локально:

```powershell
mvn spring-boot:run
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
- `JWT_EXPIRATION_MS` - срок действия access token

## Тестовые пользователи

Данные создаются при старте приложения:

- `admin@example.com / admin123` - администратор
- `manager@example.com / manager123` - менеджер
- `instructor@example.com / instructor123` - инструктор
- `client@example.com / client123` - клиент

## Роли и доступы

- `CLIENT`: свои бронирования, оплаты, уведомления и отзывы.
- `INSTRUCTOR`: свои мероприятия, участники и отчёт по мероприятию.
- `MANAGER`: активности, мероприятия, бронирования, тестовые оплаты, снаряжение, отзывы, документы и отчёты.
- `ADMIN`: полный доступ, включая пользователей и сотрудников.
- Гость: публичный каталог, категории, мероприятия, отзывы и Swagger.

## Тестовая оплата

Реальная платёжная система не подключена. Stripe, PayPal, YooKassa, CloudPayments, Robokassa и другие внешние сервисы не используются.

Оплата хранится как внутренняя сущность `Payment` с `mockTransactionId` вида `MOCK-UUID`. Клиент или менеджер может создать тестовую оплату по бронированию. Статус оплаты вручную меняет менеджер или администратор:

- `PENDING` - ожидает оплаты
- `PARTIALLY_PAID` - частично оплачена
- `PAID` - оплачена
- `REFUNDED` - возврат
- `CANCELLED` - отменена

При переводе оплаты в `PAID` бронирование становится `PAID`. Клиент видит только свои оплаты, менеджер и администратор видят все оплаты.

## Сценарий проверки

1. Войти как клиент `client@example.com / client123`.
2. Открыть каталог, применить фильтры и открыть карточку активности.
3. Создать бронирование на доступную дату.
4. Открыть кабинет клиента и создать тестовую оплату.
5. Войти как менеджер `manager@example.com / manager123`.
6. Подтвердить бронирование, открыть раздел оплат и отметить оплату как оплаченную.
7. Убедиться, что бронирование стало `Оплачена`.
8. Войти как инструктор и проверить назначенные мероприятия.
9. После завершения мероприятия создать отчёт инструктора.
10. Войти как администратор и проверить пользователей, сотрудников и отчёты.

## Проверка сборки

Backend:

```powershell
mvn clean install
```

Frontend:

```powershell
cd frontend
npm install
npm run build
```
