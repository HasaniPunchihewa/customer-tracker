# Customer Tracker API

A RESTful API for managing customers, products, and orders for a retail business. Built with Java and Spring Boot, deployed on Railway with a live PostgreSQL database.

**Live API:** `https://customer-tracker-production.up.railway.app`

---

## Tech Stack

- **Java 21**
- **Spring Boot 4.0.6**
- **Spring Security** — JWT-based authentication
- **Spring Data JPA + Hibernate** — ORM and database access
- **PostgreSQL** — relational database
- **Lombok** — boilerplate reduction
- **Maven** — build tool
- **Railway** — cloud deployment

---

## Features

- Customer management — create, view, update, and delete customers with contact details and notes
- Product catalogue — manage products with pricing
- Order tracking — track orders per customer with status and sales channel
- Order items — line items per order with price snapshot at time of purchase
- JWT authentication — register, login, and protect all endpoints with Bearer tokens

---

## Data Model

```
customers ──< orders ──< order_items >── products
```

- A customer has many orders
- An order has many order items
- An order item references one product
- Order status: `PENDING`, `DELIVERED`, `CANCELLED`
- Order source: `FACEBOOK`, `INSTAGRAM`, `WHATSAPP`, `WEBSITE`, `OTHER`

---

## Running Locally

### Prerequisites
- Java 21
- Maven
- PostgreSQL

### Steps

1. Clone the repository
   ```bash
   git clone https://github.com/HasaniPunchihewa/customer-tracker.git
   cd customer-tracker
   ```

2. Create a PostgreSQL database called `customer_tracker`

3. Update `src/main/resources/application.properties` with your local database credentials:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/customer_tracker
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   app.jwt.secret=your-secret-key
   app.jwt.expiration=86400000
   ```

4. Run the app
   ```bash
   mvn spring-boot:run
   ```

The API will be available at `http://localhost:8080`.

---

## API Reference

All endpoints except `/api/auth/**` require a Bearer token in the `Authorization` header.

### Auth

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Login and receive a JWT |

**Register / Login request body:**
```json
{
  "email": "you@example.com",
  "password": "yourpassword"
}
```

**Response:**
```json
{
  "token": "eyJhbGci...",
  "email": "you@example.com"
}
```

---

### Customers

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/customers` | Get all customers |
| GET | `/api/customers/{id}` | Get a customer by ID |
| POST | `/api/customers` | Create a customer |
| PUT | `/api/customers/{id}` | Update a customer |
| DELETE | `/api/customers/{id}` | Delete a customer |

**Request body (create/update):**
```json
{
  "name": "Anika Silva",
  "email": "anika@example.com",
  "phone": "0771234567",
  "address": "42 Galle Road, Colombo",
  "instagramHandle": "@anika.s",
  "notes": "Prefers size S"
}
```

---

### Products

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/products` | Get all products |
| GET | `/api/products/{id}` | Get a product by ID |
| POST | `/api/products` | Create a product |
| PUT | `/api/products/{id}` | Update a product |
| DELETE | `/api/products/{id}` | Delete a product |

**Request body (create/update):**
```json
{
  "name": "Siren Dress",
  "description": "A dark, elegant evening dress",
  "price": 89.99
}
```

---

### Orders

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/orders` | Get all orders |
| GET | `/api/orders/{id}` | Get an order by ID |
| GET | `/api/orders/customer/{customerId}` | Get all orders for a customer |
| POST | `/api/orders` | Create an order |
| PATCH | `/api/orders/{id}/status?status=` | Update order status |
| DELETE | `/api/orders/{id}` | Delete an order |

**Request body (create):**
```json
{
  "customerId": "uuid-here",
  "status": "PENDING",
  "source": "INSTAGRAM",
  "notes": "Gift wrap requested",
  "items": [
    {
      "productId": "uuid-here",
      "quantity": 1
    }
  ]
}
```

---

## Architecture

The project follows a standard Spring Boot layered architecture:

```
Controller  →  receives HTTP requests, returns responses
Service     →  holds business logic
Repository  →  handles database access via JPA
```

Each layer only communicates with the one directly below it. This separation makes the codebase easy to test and maintain.

---

## Authentication Flow

1. Register or login via `/api/auth` to receive a JWT
2. Include the token in the `Authorization` header for all subsequent requests:
   ```
   Authorization: Bearer your-token-here
   ```
3. Tokens expire after 24 hours

Passwords are hashed with BCrypt. The server is stateless — no sessions are stored.
