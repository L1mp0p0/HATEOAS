# user-service (Spring Boot)

REST API управления пользователями. Spring Boot 3.5, Spring Web, Spring Data JPA, PostgreSQL,
**Spring HATEOAS** (формат HAL) и **Swagger / OpenAPI** (springdoc).

## Документация API (Swagger)

Запусти приложение (`mvn spring-boot:run`) и открой:

- Swagger UI: http://localhost:8080/swagger-ui.html — можно читать описание и пробовать запросы кнопкой *Try it out*
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## API

| Метод | URL | Описание | Коды |
|---|---|---|---|
| GET | /api | точка входа (ссылки на ресурсы и документацию) | 200 |
| GET | /api/users | список | 200 |
| GET | /api/users/{id} | один | 200, 400, 404 |
| POST | /api/users | создать (+ заголовок `Location`) | 201, 400, 409 |
| PUT | /api/users/{id} | обновить | 200, 400, 404, 409 |
| DELETE | /api/users/{id} | удалить | 204, 404 |

Тело запроса: `{"name":"Alex","email":"alex@mail.ru","age":20}`

## HATEOAS

Ответы содержат `_links` — ссылки для навигации. Пример `GET /api/users/1`:

```json
{
  "id": 1, "name": "Alex", "email": "alex@mail.ru", "age": 20,
  "createdAt": "2026-01-01T12:00:00",
  "_links": {
    "self":   { "href": "http://localhost:8080/api/users/1" },
    "users":  { "href": "http://localhost:8080/api/users" },
    "update": { "href": "http://localhost:8080/api/users/1" },
    "delete": { "href": "http://localhost:8080/api/users/1" }
  }
}
```

Список `GET /api/users` приходит в виде `{"_embedded": {"users": [ ... ]}, "_links": {"self": ...}}`;
у каждого элемента свои ссылки. `update` и `delete` указывают на тот же URL, что и `self`,
метод HTTP (PUT / DELETE) определяется по названию ссылки.

## Тесты
`mvn test` (интеграционные тесты используют Testcontainers — нужен запущенный Docker).
