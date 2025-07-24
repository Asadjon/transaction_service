# Balance Service

Handles creation and retrieval of transaction records such as deposits, withdrawals, and transfers between users. This service is part of the Transaction Management System and communicates with the Auth and Balance services.

---

## 📜 Overview

The Transaction Service allows creating and fetching financial transactions between users. It includes support for:
* Deposits
* Withdrawals
* Transfers
* Fetching transaction history by user or date range

All endpoints are protected using JWT and accessed through the API Gateway.

---

## 🚀 Tech Stack

- Java 21
- Spring Boot 3.5.3
- PostgreSQL
- JWT (JSON Web Token)
- Docker

---

## 📦 Clone All Required Repositories
To run the full Transaction Management System, you'll need to clone each microservice repository into a common workspace folder. You can do it manually or with the following commands:

### 1. Create a project directory
   ```
   mkdir transaction-system && cd transaction-system
   ```

### > 2. Clone all required services
> * [auth_service](https://github.com/Asadjon/balance_service.git) 
> * [balance_service](https://github.com/Asadjon/balance_service.git)
> * [transaction_service](https://github.com/Asadjon/transaction_service.git) ← this repository
> * [api_gateway](https://github.com/Asadjon/api_gateway.git)

---

## 🚀 Running with Docker
### 1. Create app-network (only once)
If you haven't created the custom network yet, run:
```
docker network create app-network
```

### 2. Build and start the container
Inside the directory where your Dockerfile and docker-compose.yml are located, run:
```
docker-compose up --build
```

### 3. Useful Docker commands
Inspect all containers connected to app-network:
```docker
docker network inspect app-network
```

Stop and remove the container(s):
```
docker-compose down
```

---

## 🔐 Authentication

All endpoints are protected using JWT. The token must be passed in the header:
```http
Authorization: Bearer <access_token>
```

---

## 🔁 API Endpoints

| Method | Endpoint                            | Description                          | Request Body              |
|--------|-------------------------------------|--------------------------------------|---------------------------|
| GET    | `/api/v1/transaction/all`           | Get all transactions                 |                           |
| GET    | `/api/v1/transaction/user/{userId}` | Get transactions by user ID          | Path variable: `userId`   |
| GET    | `/api/v1/transaction/range`         | Get transactions by date range       | Query params: `from`,`to` |
| POST   | `/api/v1/transaction/transfer`      | Transfer money between two users     | `TransferRequest`         |
| POST   | `/api/v1/transaction/withdraw`      | Withdraw money from a user's balance | `WithdrawRequest`         |
| POST   | `/api/v1/transaction/deposit`       | Deposit money to a user's balance    | `DepositRequest`          |


---

## 📦 Request & Response Body Structures

### 🔍 Get Transactions by Date Range `GET /api/v1/transaction/range?from=2025-07-01&to=2025-07-21`
**Response:**
```json
[
  {
    "fromUserId": 1,
    "toUserId": 2,
    "amount": 100000.00,
    "createdAt": "2025-07-20T16:34:48.86775",
    "type": "TRANSFER",
    "status": "FAILED",
    "description": "From user id must be different to user id"
  },
  {
    "fromUserId": 10,
    "toUserId": 1,
    "amount": 1000.00,
    "createdAt": "2025-07-19T16:35:34.367306",
    "type": "TRANSFER",
    "status": "SUCCESS",
    "description": "Transfer successful"
  }
]
```

### 🔁 Transfer `POST /api/v1/transaction/transfer`
**Request Body:**
```json
{
  "fromUserId": 10,
  "toUserId": 1,
  "amount": 1000
}
```
**Response:** `Transfer successful`

### 💸 Withdraw `POST /api/v1/transaction/withdraw`
**Request Body:**
```json
{
  "userId": 1,
  "amount": 300.00
}
```
**Response:** `Withdrawal successful`

### 💰 Deposit `POST /api/v1/transaction/deposit`
**Request Body:**
```json
{
  "userId": 1,
  "amount": 400.00
}
```
**Response:** `Transfer successful`