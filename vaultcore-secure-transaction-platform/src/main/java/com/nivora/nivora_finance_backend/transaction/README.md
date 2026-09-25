# Nivora Finance - Transaction Module

## Purpose

The Transaction Module is responsible for:

* Transferring money between users
* Maintaining transaction history
* Retrieving transaction details
* Preventing duplicate transfer requests
* Validating transfer rules
* Tracking CREDIT and DEBIT transactions
* Protecting transaction data from unauthorized access

Only users involved in a transaction can access its details.

---

# Transaction Flow

## 1. Transfer Money

Endpoint:

POST /api/v1/transactions/transfer

What happens:

1. Authenticated user initiates a transfer.

2. Transfer amount is validated.

3. Receiver existence is verified.

4. Self-transfer attempts are rejected.

5. Sender and receiver wallets are fetched.

6. Sender balance is checked.

7. Transaction is created with:

   status = PENDING

8. Sender wallet balance is reduced.

9. Receiver wallet balance is increased.

10. Wallet updates are saved.

11. Transaction status is updated:

    status = SUCCESS

12. Transaction is stored in PostgreSQL.

Result:

Money is transferred successfully between users.

---

## 2. Get My Transactions

Endpoint:

GET /api/v1/transactions

What happens:

1. Current authenticated user is identified.

2. All transactions are fetched where the user is either:

   sender

   or

   receiver

3. Transaction direction is determined:

   CREDIT

   or

   DEBIT

4. Transaction history is returned.

Result:

User receives their complete transaction history.

---

## 3. Get Transaction By ID

Endpoint:

GET /api/v1/transactions/{transactionId}

What happens:

1. Transaction is fetched using its ID.

2. System verifies that the current user is either:

   sender

   or

   receiver

3. Transaction direction is determined.

4. Transaction details are returned.

Result:

Authorized users can view transaction details.

Unauthorized users cannot access them.

---

# Transaction Types

## TRANSFER

Represents money transferred between users.

Example:

User A sends money to User B.

---

## ADD_MONEY

Represents money added to the wallet.

Reserved for wallet operations.

---

## WITHDRAW

Represents money withdrawn from the wallet.

Reserved for wallet operations.

---

# Transaction Status

## PENDING

Transaction has been initiated but not completed.

---

## SUCCESS

Transaction completed successfully.

---

## FAILED

Transaction processing failed.

Reserved for future enhancements.

---

# Transaction Direction

## DEBIT

Money moved out of the user's account.

Example:

User sends money.

---

## CREDIT

Money moved into the user's account.

Example:

User receives money.

---

# Idempotency Support

Transfers require an idempotency key.

Header:

Idempotency-Key: <unique-key>

Example:

Idempotency-Key: transfer-001

Purpose:

Prevents accidental duplicate transfer processing caused by retries or repeated requests.

---

# Transfer Validation Rules

## Minimum Transfer Amount

Minimum allowed amount:

$1

Transfers below this amount are rejected.

---

## Maximum Transfer Amount

Maximum allowed amount:

$100

Transfers above this amount are rejected.

---

## Self Transfer Prevention

Users cannot transfer money to themselves.

Such requests are rejected.

---

## Receiver Validation

Receiver account must exist.

Transfers to invalid users are rejected.

---

## Balance Validation

Sender must have sufficient wallet balance.

Insufficient balance results in transfer rejection.

---

# Route Types

## Protected Routes

These routes require a valid JWT.

POST /api/v1/transactions/transfer

GET /api/v1/transactions

GET /api/v1/transactions/{transactionId}

Requests without valid authentication are rejected.

---

# Transaction Storage

Transactions are stored in PostgreSQL.

Each transaction stores:

* Sender ID
* Receiver ID
* Amount
* Transaction Type
* Transaction Status
* Idempotency Key
* Created Timestamp

Purpose:

Provides a permanent audit trail of user activity.

---

# Security Features

## Ownership Validation

Users can only access transactions that belong to them.

Unauthorized access attempts are rejected.

---

## JWT Authentication

All transaction endpoints require valid authentication.

---

## Duplicate Transfer Prevention

Idempotency keys prevent duplicate transaction execution.

---

## Transaction Integrity

Money deduction and credit operations occur within a single transaction.

If any operation fails, changes are rolled back.

---

# Unit Testing

The Transaction Module includes tests for:

* Successful money transfer
* Insufficient balance handling
* Self-transfer prevention
* Minimum amount validation
* Maximum amount validation
* Receiver not found scenarios
* Transaction history retrieval
* CREDIT direction mapping
* DEBIT direction mapping
* Transaction detail retrieval
* Unauthorized transaction access

---

# Module Status

Status: COMPLETE

Implemented Features:

* Money Transfer
* Transaction History
* Transaction Details
* CREDIT/DEBIT Direction Support
* Idempotency Key Support
* Ownership Validation
* Transfer Validations
* Global Exception Handling
* Unit Tests




