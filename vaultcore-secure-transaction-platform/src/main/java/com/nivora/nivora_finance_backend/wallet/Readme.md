# Wallet Module

## Overview

The Wallet Module manages user wallet balances inside Nivora Finance.

A wallet is automatically created when a user successfully verifies their account using OTP verification.

The module currently supports:

* Get Wallet Balance
* Add Money
* Withdraw Money
* Balance Validation
* Exception Handling
* Unit Testing

---

## Features

### Get Balance

Authenticated users can view their current wallet balance.

**Endpoint**

```http
GET /api/v1/wallet/balance
```

**Response**

```json
{
  "balance": 100.00
}
```

---

### Add Money

Users can add money to their wallet.

**Rules**

* Minimum amount: $1
* Maximum amount: $100

**Endpoint**

```http
POST /api/v1/wallet/add-money
```

**Request**

```json
{
  "amount": 50
}
```

**Response**

```json
{
  "message": "Money added successfully",
  "balance": 50.00
}
```

---

### Withdraw Money

Users can withdraw money from their wallet.

**Rules**

* Minimum withdrawal amount: $1
* Wallet balance must be sufficient

**Endpoint**

```http
POST /api/v1/wallet/withdraw
```

**Request**

```json
{
  "amount": 20
}
```

**Response**

```json
{
  "message": "Money withdrawn successfully",
  "balance": 30.00
}
```

---

## Database Schema

### wallets

| Column     | Type      |
| ---------- | --------- |
| id         | BIGINT    |
| user_id    | BIGINT    |
| balance    | DECIMAL   |
| created_at | TIMESTAMP |
| updated_at | TIMESTAMP |

---

## Exception Handling

The module uses centralized exception handling through `GlobalExceptionHandler`.

### Supported Exceptions

* ResourceNotFoundException
* InsufficientFundsException
* IllegalArgumentException

Example:

```json
{
  "success": false,
  "message": "Insufficient wallet balance",
  "data": null
}
```

---

## Security

All wallet endpoints require authentication.

The authenticated user is retrieved from Spring Security's `SecurityContextHolder`.

---

## Testing

Unit tests implemented using:

* JUnit 5
* Mockito

### Covered Test Cases

* getBalance_ShouldReturnBalance
* addMoney_ShouldIncreaseBalance
* addMoney_ShouldThrow_WhenAmountLessThan1
* addMoney_ShouldThrow_WhenAmountGreaterThan100
* withdrawMoney_ShouldDecreaseBalance
* withdrawMoney_ShouldThrow_WhenInsufficientFunds

---

## Current Status

Wallet Module: Completed ✅

Future Enhancements:

* Bank Integration
* Transaction Service Integration
* Ledger Integration
* Monthly Statement Generation
* PDF Export to Amazon S3
* Wallet Analytics
