# ApiGateway

## Project Structure

Your local folder must look exactly like this:

```
marketplace/
├── ApiGateway/
│   ├── docker-compose.yml
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
├── AuthService/
├── UserService/
└── OrderService/
```

**IMPORTANT:** All 4 services must be in the same parent folder, on the same level

## Links to other microservices

- **AuthService** → https://github.com/oldvabik/marketplace-auth-service
- **UserService** → https://github.com/oldvabik/marketplace-user-service
- **OrderService** → https://github.com/oldvabik/marketplace-order-service

## How to run everything

1. **Clone/download all 4 services** into one folder so the structure above is matched
2. **Go to the `ApiGateway` folder and create a `.env` file:**
```
SPRING_PROFILES_ACTIVE=docker

POSTGRES_USER=user
POSTGRES_PASSWORD=password
POSTGRES_DB_AUTH=auth_db
POSTGRES_DB_USER=user_db
POSTGRES_DB_ORDER=order_db
POSTGRES_URL_AUTH=jdbc:postgresql://postgres-auth:5432/auth_db
POSTGRES_URL_USER=jdbc:postgresql://postgres-user:5432/user_db
POSTGRES_URL_ORDER=jdbc:postgresql://postgres-order:5432/order_db

REDIS_HOST=redis
REDIS_PORT=6379

JWT_SECRET=caa92feefa6438c8c96d1b75f1bc388c7df2cc5e5425a2b91077d3aa5c5c2e4416acf9bfe7b9a122b07d3b071cd626c6f81a68b406078a787a3a524db986dea1
```
3. Run from the ApiGateway folder
```
cd ApiGateway

# First time (builds all images)
docker compose up --build

# Next times
docker compose up

# Stop everything
docker compose down

# Stop + completely delete all databases and Redis
docker compose down -v
```

## Basic Flow (User & Admin)

1. Register regular user
```
POST http://localhost:8079/api/v1/auth/register
{
  "name": "Maksim",
  "surname": "Vabishchevich",
  "birthDate": "2006-03-26",
  "email": "oldvabik@gmail.com",
  "password": "123456789",
  "role": "USER"
}
```
2. Login as user
```
POST http://localhost:8079/api/v1/auth/login
{
    "email": "oldvabik@gmail.com",
    "password": "123456789"
}
```
Copy accessToken → set as Bearer Token
3. Add payment card
```
POST http://localhost:8079/api/v1/cards
{
  "number": "1234 5678 4321 8765",
  "expirationDate": "2027-03-25",
  "userId": 1
}
```
4. View your profile (including cards)
```
GET http://localhost:8079/api/v1/users/1
```
Response:
```
{
  "id": 1,
  "name": "Maksim",
  "surname": "Vabishchevich",
  "birthDate": "2006-03-26",
  "email": "oldvabik@gmail.com",
  "cards": [
    {
      "id": 1,
      "number": "1234 5678 4321 8765",
      "holder": "Maksim Vabishchevich",
      "expirationDate": "2027-03-25",
      "userId": 1
    }
  ]
}
```
5. Register admin
```
POST http://localhost:8079/api/v1/auth/register
{
  "name": "Maksim",
  "surname": "Vabishchevich",
  "birthDate": "2006-03-26",
  "email": "maksim.vabishchevich@innowise.com",
  "password": "123456789",
  "role": "ADMIN"
}
```
6. Login as admin
```
POST http://localhost:8079/api/v1/auth/login
{
    "email": "maksim.vabishchevich@innowise.com",
    "password": "123456789"
}
```
Use this token for admin actions

7. Admin: Add items to catalog
```
POST http://localhost:8079/api/v1/items
{
    "name": "Arduino Nano",
    "price": 8.79
}

POST http://localhost:8079/api/v1/items
{
    "name": "Датчик Холла SSF41",
    "price": 1.39
}
```

8. User: Create order
```
POST http://localhost:8079/api/v1/orders
{
  "email": "oldvabik@gmail.com",
  "items": [
    {
        "name": "Arduino Nano",
        "quantity": 2
    },
    {
        "name": "Датчик Холла SSF41",
        "quantity": 1
    }
  ]
}
```

9. Admin: Change order status
```
PUT http://localhost:8079/api/v1/orders/1
{
    "status": "SHIPPED"
}
```
Response:
```
{
    "id": 1,
    "userId": 1,
    "status": "SHIPPED",
    "creationDate": "2025-12-01T13:32:18.126333",
    "items": [
        {
            "id": 1,
            "itemId": 1,
            "name": "Arduino Nano",
            "price": 8.79,
            "quantity": 2
        },
        {
            "id": 2,
            "itemId": 2,
            "name": "Датчик Холла SSF41",
            "price": 1.39,
            "quantity": 1
        }
    ],
    "user": {
        "id": 1,
        "name": "Maksim",
        "surname": "Vabishchevich",
        "birthDate": "2006-03-26",
        "email": "oldvabik@gmail.com"
    }
}
```

10. Admin: View all users/orders
```
GET http://localhost:8079/api/v1/users
```
Response:
```
{
    "content": [
        {
            "id": 1,
            "name": "Maksim",
            "surname": "Vabishchevich",
            "birthDate": "2006-03-26",
            "email": "oldvabik@gmail.com",
            "cards": [
                {
                    "id": 1,
                    "number": "1234 5678 4321 8765",
                    "holder": "Maksim Vabishchevich",
                    "expirationDate": "2027-03-25",
                    "userId": 1
                }
            ]
        },
        {
            "id": 2,
            "name": "Maksim",
            "surname": "Vabishchevich",
            "birthDate": "2006-03-26",
            "email": "maksim.vabishchevich@innowise.com",
            "cards": []
        }
    ],
    "pageable": {
        "pageNumber": 0,
        "pageSize": 5,
        "sort": {
            "unsorted": true,
            "sorted": false,
            "empty": true
        },
        "offset": 0,
        "unpaged": false,
        "paged": true
    },
    "totalPages": 1,
    "totalElements": 2,
    "last": true,
    "numberOfElements": 2,
    "size": 5,
    "number": 0,
    "sort": {
        "unsorted": true,
        "sorted": false,
        "empty": true
    },
    "first": true,
    "empty": false
}
```
```
GET http://localhost:8079/api/v1/orders?statuses=SHIPPED
```
```
{
    "content": [
        {
            "id": 1,
            "userId": 1,
            "status": "SHIPPED",
            "creationDate": "2025-12-01T13:32:18.126333",
            "items": [
                {
                    "id": 1,
                    "itemId": 1,
                    "name": "Arduino Nano",
                    "price": 8.79,
                    "quantity": 2
                },
                {
                    "id": 2,
                    "itemId": 2,
                    "name": "Датчик Холла SSF41",
                    "price": 1.39,
                    "quantity": 1
                }
            ],
            "user": {
                "id": 1,
                "name": "Maksim",
                "surname": "Vabishchevich",
                "birthDate": "2006-03-26",
                "email": "oldvabik@gmail.com"
            }
        }
    ],
    "pageable": {
        "pageNumber": 0,
        "pageSize": 5,
        "sort": {
            "unsorted": true,
            "sorted": false,
            "empty": true
        },
        "offset": 0,
        "paged": true,
        "unpaged": false
    },
    "totalPages": 1,
    "totalElements": 1,
    "last": true,
    "numberOfElements": 1,
    "size": 5,
    "number": 0,
    "sort": {
        "unsorted": true,
        "sorted": false,
        "empty": true
    },
    "first": true,
    "empty": false
}
```

## All Available Endpoints
### Items

| Method | Endpoint                     | Access         | Description                            |
|--------|------------------------------|----------------|----------------------------------------|
| GET    | `/api/v1/items`              | USER / ADMIN   | List items (supports `?name=...` search) |
| GET    | `/api/v1/items/{id}`         | USER / ADMIN   | Get item by ID                         |
| POST   | `/api/v1/items`              | ADMIN          | Create new item                        |
| PUT    | `/api/v1/items/{id}`         | ADMIN          | Update item                            |
| DELETE | `/api/v1/items/{id}`         | ADMIN          | Delete item                            |

### Orders

| Method | Endpoint                     | Access         | Description                            |
|--------|------------------------------|----------------|----------------------------------------|
| POST   | `/api/v1/orders`             | USER / ADMIN   | Create order (items by **name**)       |
| GET    | `/api/v1/orders`             | ADMIN          | All orders + filter `?statuses=...` & `?ids=...` |
| GET    | `/api/v1/orders/{id}`        | USER / ADMIN   | Get order by ID (own or any)           |
| PUT    | `/api/v1/orders/{id}`        | ADMIN          | Update order (e.g. change status)      |
| DELETE | `/api/v1/orders/{id}`        | ADMIN          | Delete order                           |

### Users & Cards

| Method | Endpoint                     | Access       | Description                    |
|--------|------------------------------|--------------|--------------------------------|
| GET    | `/api/v1/users/{id}`         | USER / ADMIN | View user + cards (own or any) |
| GET    | `/api/v1/users`              | ADMIN        | All users (paginated)          |
| GET    | `/api/v1/users/search`       | USER / ADMIN | Find user by email             |
| PUT    | `/api/v1/users/{id}`         | USER / ADMIN | Update user info               |
| DELETE | `/api/v1/users/{id}`         | ADMIN        | Delete user                    |
| POST   | `/api/v1/cards`              | USER / ADMIN | Add payment card               |
| GET    | `/api/v1/cards`              | ADMIN        | All cards                      |
| PUT    | `/api/v1/cards/{id}`         | USER / ADMIN | Update card                    |
| DELETE | `/api/v1/cards/{id}`         | USER / ADMIN | Delete card                    |

## Postman Collection

[Marketplace – Basic Flow (User + Admin)](https://web.postman.co/workspace/My-Workspace~a4f63bdd-ddca-40ab-8eb0-ccd86ebf2f4a/collection/42244467-dba663c3-54ad-4989-bed1-e2dd85397eb3?action=share&source=copy-link&creator=42244467)