# Mini Wallet Service

A REST API for a simple digital wallet system built with **Java 21+**, **Spring Boot 3.x**, and **PostgreSQL**.  
Features user-facing endpoints, admin endpoints, JWT authentication, role-based access control, and database transaction integrity.

---

## Tech Stack

- **Language:** Java 21+
- **Framework:** Spring Boot 3.4.x
- **Database:** PostgreSQL
- **Migration:** Flyway
- **Security:** JWT (jjwt 0.12.x), BCrypt
- **Build:** Maven
- **Testing:** JUnit 5, Mockito

---

## Setup Instructions

### Prerequisites

- JDK 21+
- PostgreSQL running on `localhost:5432`
- Maven 3.9+

### 1. Create Database

```sql
CREATE DATABASE miniwallet;
```

### 2. Configure Environment Variables (Optional)

| Variable       | Default    | Description           |
|----------------|------------|-----------------------|
| `DB_USERNAME`  | `postgres` | PostgreSQL username   |
| `DB_PASSWORD`  | `postgres` | PostgreSQL password   |
| `JWT_SECRET`   | *(built-in)* | JWT signing key (Base64) |

### 3. Run the Application

```bash
# Build and run
mvn clean package
java -jar target/mini-wallet-1.0.0.jar

# Or using Maven
mvn spring-boot:run
```

### 4. Run Tests

```bash
mvn test
```

---

## Base URL

```
http://localhost:8080/api/v1
```

Open Swagger UI in your browser:
http://localhost:8080/swagger-ui/index.html


All request and response bodies are JSON.  
All authenticated endpoints require the header:

```
Authorization: Bearer <access_token>
```

### Token Lifetimes

- Access token: **15 minutes**
- Refresh token: **7 days**

---

## Common Error Responses

### 400 Bad Request — Validation Error
```json
{
  "timestamp": "2025-01-15T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "details": {
    "email": "must be a valid email address",
    "amount": "must be greater than 0"
  }
}
```

### 401 Unauthorized
```json
{
  "timestamp": "2025-01-15T10:30:00Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid or expired token",
  "details": null
}
```

### 403 Forbidden
```json
{
  "timestamp": "2025-01-15T10:30:00Z",
  "status": 403,
  "error": "Forbidden",
  "message": "Access denied: ADMIN role required",
  "details": null
}
```

### 404 Not Found
```json
{
  "timestamp": "2025-01-15T10:30:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id: 999",
  "details": null
}
```

### 409 Conflict
```json
{
  "timestamp": "2025-01-15T10:30:00Z",
  "status": 409,
  "error": "Conflict",
  "message": "Email already registered: john@example.com",
  "details": null
}
```

### 500 Internal Server Error
```json
{
  "timestamp": "2025-01-15T10:30:00Z",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred. Please try again later.",
  "details": null
}
```

---

## Endpoints

### Authentication

#### Register
```
POST /api/v1/auth/register
```

**Request:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "SecurePass123!"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "role": "USER",
  "createdAt": "2025-01-15T10:30:00Z"
}
```

---

#### Login
```
POST /api/v1/auth/login
```

**Request:**
```json
{
  "email": "john@example.com",
  "password": "SecurePass123!"
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "accessTokenExpiry": "2025-01-15T10:45:00Z",
  "user": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "role": "USER",
    "createdAt": "2025-01-15T10:30:00Z"
  }
}
```

**Error (401 — invalid credentials):**
```json
{
  "timestamp": "2025-01-15T10:30:00Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid email or password",
  "details": null
}
```

---

#### Refresh Token
```
POST /api/v1/auth/refresh
```

**Request:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "accessTokenExpiry": "2025-01-15T11:00:00Z"
}
```

---

### User Endpoints

#### Get Own Profile
```
GET /api/v1/users/me
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "role": "USER",
  "createdAt": "2025-01-15T10:30:00Z"
}
```

---

### Wallet Endpoints

#### Get Own Wallet
```
GET /api/v1/wallets/me
```

**Response (200 OK):**
```json
{
  "id": 1,
  "balance": 1500.00,
  "currency": "BDT",
  "createdAt": "2025-01-15T10:30:00Z",
  "updatedAt": "2025-01-16T14:20:00Z"
}
```

---

### Deposit Endpoints

#### Deposit Funds
```
POST /api/v1/deposits
```

**Request:**
```json
{
  "amount": 500.00
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "fromWalletId": null,
  "toWalletId": 1,
  "amount": 500.00,
  "type": "DEPOSIT",
  "status": "SUCCESS",
  "createdAt": "2025-01-16T14:20:00Z"
}
```

---

### Transfer Endpoints

#### Transfer Funds
```
POST /api/v1/transfers
```

**Request:**
```json
{
  "recipientEmail": "jane@example.com",
  "amount": 250.00
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "fromWalletId": 1,
  "toWalletId": 2,
  "amount": 250.00,
  "type": "TRANSFER",
  "status": "SUCCESS",
  "createdAt": "2025-01-16T14:20:00Z"
}
```

**Error (400 — insufficient balance):**
```json
{
  "timestamp": "2025-01-15T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Insufficient balance. Available: 100.00, Requested: 250.00",
  "details": null
}
```

**Error (409 — self-transfer):**
```json
{
  "timestamp": "2025-01-15T10:30:00Z",
  "status": 409,
  "error": "Conflict",
  "message": "Cannot transfer funds to yourself",
  "details": null
}
```

---

### Transaction Endpoints

#### List Own Transactions
```
GET /api/v1/transactions/me?page=0&size=20&type=TRANSFER&fromDate=2025-01-01&toDate=2025-01-31
```

**Query Parameters:**
| Parameter  | Type   | Default | Description                         |
|------------|--------|---------|-------------------------------------|
| page       | int    | 0       | Page number (zero-indexed)          |
| size       | int    | 20      | Page size (max 100)                 |
| type       | string | null    | Filter: TRANSFER or DEPOSIT         |
| fromDate   | date   | null    | Start date (yyyy-MM-dd)             |
| toDate     | date   | null    | End date (yyyy-MM-dd)               |

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "fromWalletId": 1,
      "toWalletId": 2,
      "amount": 250.00,
      "type": "TRANSFER",
      "status": "SUCCESS",
      "createdAt": "2025-01-16T14:20:00Z"
    }
  ],
  "page": {
    "number": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

---

### Admin Endpoints (ADMIN role required)

#### List All Users
```
GET /api/v1/admin/users?page=0&size=20&role=USER
```

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "name": "John Doe",
      "email": "john@example.com",
      "role": "USER",
      "createdAt": "2025-01-15T10:30:00Z"
    }
  ],
  "page": {
    "number": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

---

#### View User Profile & Wallet
```
GET /api/v1/admin/users/{id}
```

**Response (200 OK):**
```json
{
  "user": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "role": "USER",
    "createdAt": "2025-01-15T10:30:00Z"
  },
  "wallet": {
    "id": 1,
    "balance": 1250.00,
    "currency": "BDT",
    "createdAt": "2025-01-15T10:30:00Z",
    "updatedAt": "2025-01-16T14:20:00Z"
  }
}
```

---

#### System-Wide Transaction Log
```
GET /api/v1/admin/transactions?page=0&size=20&type=TRANSFER&status=SUCCESS&fromDate=2025-01-01&toDate=2025-01-31
```

**Query Parameters:**
| Parameter  | Type   | Default | Description                                  |
|------------|--------|---------|----------------------------------------------|
| page       | int    | 0       | Page number (zero-indexed)                   |
| size       | int    | 20      | Page size (max 100)                          |
| userId     | long   | null    | Filter by user ID                            |
| type       | string | null    | Filter: TRANSFER or DEPOSIT                  |
| status     | string | null    | Filter: SUCCESS or FAILED                    |
| fromDate   | date   | null    | Start date (yyyy-MM-dd)                      |
| toDate     | date   | null    | End date (yyyy-MM-dd)                        |

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "fromWalletId": 1,
      "toWalletId": 2,
      "amount": 250.00,
      "type": "TRANSFER",
      "status": "SUCCESS",
      "createdAt": "2025-01-16T14:20:00Z"
    }
  ],
  "page": {
    "number": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

---

#### Dashboard Stats
```
GET /api/v1/admin/stats
```

**Response (200 OK):**
```json
{
  "totalUsers": 150,
  "totalTransactions": 1234,
  "totalVolume": 987650.00,
  "totalSuccessfulTransfers": 1100,
  "totalFailedTransfers": 134
}
```

---

## Endpoint Summary

| Method | Path                         | Auth     | Description                        |
|--------|------------------------------|----------|------------------------------------|
| POST   | /api/v1/auth/register        | None     | Register a new user                |
| POST   | /api/v1/auth/login           | None     | Login and get JWT tokens           |
| POST   | /api/v1/auth/refresh         | None     | Refresh access token               |
| GET    | /api/v1/users/me             | USER/ADMIN | Get own profile                  |
| GET    | /api/v1/wallets/me           | USER/ADMIN | Get own wallet balance           |
| POST   | /api/v1/deposits             | USER/ADMIN | Deposit funds (201)              |
| POST   | /api/v1/transfers            | USER/ADMIN | Transfer funds (201)             |
| GET    | /api/v1/transactions/me      | USER/ADMIN | List own transaction history     |
| GET    | /api/v1/admin/users          | ADMIN    | List all users                     |
| GET    | /api/v1/admin/users/{id}     | ADMIN    | View user profile and wallet       |
| GET    | /api/v1/admin/transactions   | ADMIN    | System-wide transaction log        |
| GET    | /api/v1/admin/stats          | ADMIN    | Dashboard statistics               |

---

## Architecture

```
Controller → Service → Repository
├── No business logic in controllers
├── No raw SQL in services (JPA only)
└── All endpoints validate input
```

### Key Design Decisions

- **Transactions:** Fund transfers use `@Transactional` with pessimistic write locks to prevent race conditions.
- **Failed transfers:** When a transfer fails due to insufficient balance, a FAILED transaction record is created for audit.
- **Self-transfers:** Prevented with a 409 Conflict response — no transaction record is created.
- **Refresh token rotation:** Each refresh invalidates the old token and issues a new pair.
- **Pagination:** All list endpoints are paginated with customizable page size (max 100).

---

## Database Schema

```sql
-- Users
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(10) DEFAULT 'USER' CHECK (role IN ('USER', 'ADMIN')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Wallets
CREATE TABLE wallets (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id),
    balance DECIMAL(19,2) DEFAULT 0.00,
    currency VARCHAR(3) DEFAULT 'BDT',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Transactions
CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    from_wallet_id BIGINT REFERENCES wallets(id),
    to_wallet_id BIGINT REFERENCES wallets(id),
    amount DECIMAL(19,2) NOT NULL,
    type VARCHAR(10) CHECK (type IN ('TRANSFER', 'DEPOSIT')),
    status VARCHAR(10) DEFAULT 'SUCCESS' CHECK (status IN ('SUCCESS', 'FAILED')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Refresh Tokens
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);