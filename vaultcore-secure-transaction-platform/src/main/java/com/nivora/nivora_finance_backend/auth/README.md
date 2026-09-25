# Nivora Finance - Auth Module

## Purpose

The Auth Module is responsible for:

* User Registration
* OTP Verification
* User Authentication
* JWT Token Generation
* User Login
* User Logout
* Session Management using Redis
* Retrieving Current User Information
* Protecting secured routes

Only verified users can access protected resources.

---

# Authentication Flow

## 1. Signup

Endpoint:

POST /api/v1/auth/signup

What happens:

1. User submits:

   * Name
   * Email
   * Password

2. System checks whether the email already exists.

3. Password is encrypted using BCrypt.

4. User is created with:

   verified = false

5. OTP is generated.

6. OTP is stored in Redis with expiration.

7. User record is saved.

Result:

User account is created but remains unverified until OTP verification.

---

## 2. Verify OTP

Endpoint:

POST /api/v1/auth/verify-otp

What happens:

1. User submits:

   * Email
   * OTP

2. OTP is retrieved from Redis.

3. OTP validity is checked.

4. User account is marked:

   verified = true

5. User record is updated.

6. JWT token is generated.

Result:

User account becomes verified and authenticated.

---

## 3. Login

Endpoint:

POST /api/v1/auth/login

What happens:

1. User submits:

   * Email
   * Password

2. User existence is verified.

3. Password is validated.

4. Account verification status is checked.

5. JWT token is generated.

6. Session is stored in Redis.

Result:

User receives an access token and becomes authenticated.

---

## 4. Logout

Endpoint:

POST /api/v1/auth/logout

What happens:

1. Current JWT token is identified.

2. Session information is removed from Redis.

Result:

User session is terminated.

---

## 5. Get Current User

Endpoint:

GET /api/v1/auth/me

What happens:

1. Current authenticated user is retrieved from Security Context.

2. User profile information is returned.

Result:

User receives profile information.

---

# User Entity

Stores:

* id
* name
* email
* password
* verified
* createdAt

Purpose:

Represents application users.

---

# Redis Usage

Redis is used for:

## OTP Storage

Stores temporary OTP values.

Purpose:

OTP verification.

---

## Session Storage

Stores authenticated sessions.

Purpose:

Fast authentication validation.

---

# JWT Authentication

JWT is used for:

* User Authentication
* Protected Routes
* Stateless Authorization

JWT contains:

* User Email
* Expiration Time

Purpose:

Secure access to protected resources.

---

# Route Types

## Public Routes

POST /api/v1/auth/signup

POST /api/v1/auth/verify-otp

POST /api/v1/auth/login

---

## Protected Routes

POST /api/v1/auth/logout

GET /api/v1/auth/me

Valid JWT is required.

---

# Validation Rules

## Unique Email

Duplicate email registration is not allowed.

---

## Password Validation

Passwords are stored only after BCrypt encryption.

Raw passwords are never stored.

---

## OTP Validation

OTP must:

* Exist
* Match
* Not be expired

Invalid OTP requests are rejected.

---

## Verification Check

Unverified users cannot log in.

---

# Security Features

## Password Encryption

BCrypt Password Encoder is used.

---

## JWT Authentication

All protected endpoints require valid JWT.

---

## Redis Session Validation

Active sessions are validated using Redis.

---

## OTP Expiration

OTP automatically expires after configured TTL.

---

# Unit Testing

The Auth Module includes tests for:

* Successful Signup
* Duplicate Email Registration
* Successful OTP Verification
* Invalid OTP Handling
* Expired OTP Handling
* Successful Login
* Invalid Password Handling
* User Not Found Handling
* Unverified User Login Prevention
* Logout Functionality
* Current User Retrieval

---

# Module Status

Status: COMPLETE

Implemented Features:

* Signup
* OTP Verification
* Login
* Logout
* JWT Authentication
* Redis Session Management
* Current User Retrieval
* Password Encryption
* Global Exception Handling
* Unit Tests
