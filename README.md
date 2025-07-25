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

## ⚙️ Setup Instruction
> You can view the installation manual in the [transaction-management-system](https://github.com/Asadjon/transaction-management-system/blob/master/README.md) repository.

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

**🔍 Get Transactions by Date Range** `GET /api/v1/transaction/range?from=2025-07-01&to=2025-07-21`

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

**🔁 Transfer** `POST /api/v1/transaction/transfer`

**Request Body:**
```json
{
  "fromUserId": 10,
  "toUserId": 1,
  "amount": 1000
}
```
**Response:** `Transfer successful`

**💸 Withdraw** `POST /api/v1/transaction/withdraw`

**Request Body:**
```json
{
  "userId": 1,
  "amount": 300.00
}
```
**Response:** `Withdrawal successful`

**💰 Deposit** `POST /api/v1/transaction/deposit`

**Request Body:**
```json
{
  "userId": 1,
  "amount": 400.00
}
```
**Response:** `Transfer successful`