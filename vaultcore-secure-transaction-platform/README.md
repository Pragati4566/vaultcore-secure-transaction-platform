# 🔐 VaultCore — Secure Transaction & Wallet Platform

> A production-oriented fintech backend built with **Java 21**, **Spring Boot 4**, **PostgreSQL**, **Redis**, **Spring Security**, **JWT**, **Flyway**, **Docker**, **Testcontainers**, and **GitHub Actions**.

---

## ✨ Overview

**VaultCore** is a secure digital wallet and transaction platform designed to demonstrate modern backend engineering practices beyond basic CRUD applications.

The platform focuses on secure authentication, wallet management, money transfers, QR-based payments, transaction consistency, API protection, automated testing, and containerized deployment.

The backend is designed with maintainability and reliability in mind, incorporating:

* Secure authentication and authorization
* Transactional money transfers
* Database versioning with Flyway
* Redis-based session management and rate limiting
* PostgreSQL persistence
* Automated unit and integration testing
* Docker-based local development
* CI/CD with GitHub Actions
* API documentation using Swagger / OpenAPI
* Global validation and exception handling

---

## 🎯 Project Goals

VaultCore is designed to provide hands-on experience with production-oriented backend engineering concepts, including:

* Building secure **REST APIs**
* Designing transactional backend services
* Implementing authentication and authorization
* Handling concurrent wallet updates safely
* Using Redis for session management and rate limiting
* Managing database schema changes with Flyway
* Writing unit and integration tests
* Containerizing backend services with Docker
* Automating software delivery with GitHub Actions
* Designing modular and maintainable application architecture

---

# ✨ Core Features

## 🔐 Authentication

* User Registration
* Email OTP Verification
* Login
* Logout
* JWT Authentication
* BCrypt Password Hashing
* Redis Session Management
* Current User Endpoint
* Protected API Routes

---

## 💰 Wallet Management

* Create Wallet
* Deposit Money
* Withdraw Money
* View Wallet Balance
* Balance Validation
* Concurrent Balance Protection
* Pessimistic Database Locking

Wallet operations are implemented with transactional consistency to prevent invalid balance updates during concurrent operations.

---

## 💸 Money Transfers

* Transfer Money Between Users
* Transaction History
* Transaction Search
* Transaction Summary
* Recent Contacts
* Transaction Validation
* Idempotency-Key Support
* Atomic Money Transfers

The transfer workflow is designed so that debit and credit operations are handled atomically, reducing the risk of inconsistent wallet balances.

---

## 📱 QR Payments

VaultCore supports QR-based payment workflows:

* Generate Personal QR
* Resolve QR
* Pay Using QR

QR payments reuse the same secure transaction workflow as standard money transfers.

---

# 🛡️ Security

Security is implemented using **Spring Security**, **JWT**, and **Redis**.

### Security Components

* Spring Security
* JWT Authentication
* JWT Authorization Filter
* BCrypt Password Hashing
* Protected API Routes
* Public Route Configuration
* Request Validation
* Global Exception Handling
* Redis Session Management
* Redis Rate Limiting

Authentication and authorization are handled centrally so that protected endpoints consistently enforce access control.

---

# 🚦 Rate Limiting

VaultCore uses **Redis** to protect APIs from excessive requests.

### Public APIs

Rate limiting can be applied to:

* Signup
* Login
* OTP Verification

### Protected APIs

Rate limiting can also be applied to sensitive operations such as:

* Money Transfer
* QR Payments

### Rate-Limiting Strategy

* IP-based Rate Limiting
* User-based Rate Limiting
* Redis TTL
* Automatic Counter Expiry

This provides a lightweight protection mechanism for authentication and transaction-sensitive endpoints.

---

# 🗄️ Data & Persistence

### PostgreSQL

PostgreSQL is used as the primary relational database for:

* Users
* Wallets
* Transactions
* QR payment records
* Authentication-related data

### Flyway

Database schema changes are managed through **Flyway migrations**.

Benefits include:

* Version-controlled schema changes
* Repeatable development environments
* Consistent database initialization
* Safer schema evolution

### JPA / Hibernate

The persistence layer uses:

* Spring Data JPA
* Hibernate
* Entity relationships
* Transaction management
* Repository abstractions

---

# ⚡ Redis

Redis is used for fast, short-lived application data and API protection.

### Redis Use Cases

* Session management
* Rate limiting
* TTL-based counters
* Temporary authentication-related state

Using Redis reduces the need to repeatedly access PostgreSQL for short-lived data.

---

# 🐳 Docker & Infrastructure

VaultCore supports containerized development using **Docker** and **Docker Compose**.

### Infrastructure Components

* Spring Boot application
* PostgreSQL
* Redis

### Docker Features

* Multi-stage Docker build
* Docker Compose
* Service configuration through environment variables
* Health checks
* Reproducible local development environment

Start the complete local environment with:

```bash
docker compose up --build
```

---

# 🧪 Testing

Testing is an important part of the project.

### Testing Stack

* **JUnit 5**
* **Mockito**
* **Testcontainers**
* PostgreSQL Test Container
* Spring Boot Test
* Flyway test migrations

### Testing Coverage

The test suite is designed to cover important business and security components such as:

* Authentication services
* JWT services
* Wallet services
* Transaction services
* QR payment services
* Rate-limiting services
* Security behavior
* End-to-end money transfer workflows

Testcontainers allows integration tests to execute against real containerized infrastructure instead of relying only on mocks.

---

# 📚 API Documentation

VaultCore exposes API documentation using **Swagger / OpenAPI**.

After starting the application:

```text
http://localhost:8080/swagger-ui/index.html
```

The documentation provides an interactive view of available endpoints, request parameters, responses, and authentication requirements.

---

# ⚙️ CI/CD

VaultCore uses **GitHub Actions** for automated software validation and container image generation.

## Pipeline

```text
Developer
    │
    ├── Pull Request
    │
    ▼
GitHub Actions
    │
    ├── Compile
    ├── Unit Tests
    ├── Integration Tests
    └── Build Verification
          │
          ▼
      Main Branch
          │
          ├── Build BootJar
          ├── Build Docker Image
          └── Publish Image
                  │
                  ▼
        GitHub Container Registry
```

### CI Workflow

Pull requests and code changes are automatically validated through:

* Compilation
* Unit tests
* Integration tests
* Application build

### Container Workflow

After successful validation on the main branch:

* Spring Boot application is packaged
* Docker image is built
* Container image is published to **GitHub Container Registry (GHCR)**

This keeps the build and delivery workflow automated and repeatable.

---

# 🏗️ Architecture

```text
                         Client
                           │
                           ▼
                  Spring Boot Application
                           │
             ┌─────────────┼─────────────┐
             │             │             │
             ▼             ▼             ▼
          Auth/API      Wallet/API    QR/API
             │             │             │
             └─────────────┼─────────────┘
                           │
            ┌──────────────┴──────────────┐
            │                             │
            ▼                             ▼
       PostgreSQL                       Redis
            │                             │
            ▼                             ▼
     Flyway Migrations             Sessions / Limits
            │
            ▼
      Transaction Layer

                           │
                           ▼
                    GitHub Actions
                           │
                           ▼
                GitHub Container Registry
```

The application is organized into domain-oriented modules so authentication, wallet, transaction, QR, security, and common infrastructure concerns remain separated.

---

# 📁 Project Structure

```text
src
├── main
│   ├── java
│   │   └── com.vaultcore.vaultcore
│   │       ├── auth
│   │       ├── config
│   │       ├── qr
│   │       ├── security
│   │       ├── transaction
│   │       ├── wallet
│   │       ├── exception
│   │       └── common
│   │
│   └── resources
│       ├── db
│       │   └── migration
│       ├── application.yaml
│       └── application-test.yaml
│
└── test
    ├── integration
    ├── service
    └── security
```

---

# 🚀 Getting Started

## Prerequisites

Make sure the following are installed:

* Java 21
* Docker
* Docker Compose
* Git

---

## Clone the Repository

```bash
git clone https://github.com/Pragati4566/vaultcore-secure-transaction-platform.git

cd vaultcore-secure-transaction-platform
```

---

## Environment Configuration

Create a `.env` file in the project root.

Example:

```env
# ----------------------------
# PostgreSQL
# ----------------------------

POSTGRES_DB=vaultcore
POSTGRES_USER=postgres
POSTGRES_PASSWORD=password

SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/vaultcore
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=password

# ----------------------------
# Redis
# ----------------------------

SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379

# ----------------------------
# JWT
# ----------------------------

JWT_SECRET=your-super-secret-jwt-key

# ----------------------------
# Mail / OTP
# ----------------------------

MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=example@gmail.com
MAIL_PASSWORD=your-app-password

# ----------------------------
# CORS
# ----------------------------

CORS_ALLOWED_ORIGIN=http://localhost:3000
```

> Never commit real credentials, JWT secrets, database passwords, or mail credentials to GitHub.

---

# 🐳 Running with Docker

Start the complete application stack:

```bash
docker compose up --build
```

Backend:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

---

# 💻 Running Locally

### 1. Start PostgreSQL and Redis

```bash
docker compose up postgres redis
```

### 2. Start the Spring Boot application

```bash
./gradlew bootRun
```

Flyway will apply the configured database migrations when the application starts.

Backend:

```text
http://localhost:8080
```

---

# 🔎 API Examples

Typical API groups include:

```text
/api/auth/*
/api/users/*
/api/wallet/*
/api/transactions/*
/api/qr/*
```

Exact endpoints are documented through Swagger/OpenAPI.

---

# 🔄 Transaction Safety

Money movement is treated as a critical business operation.

The transaction workflow is designed around:

* Database transactions
* Balance validation
* Pessimistic locking
* Atomic updates
* Idempotency protection
* Consistent error handling

These mechanisms help prevent invalid or duplicated money-transfer operations.

---

# 📈 Reliability & Engineering Practices

VaultCore focuses on engineering practices that are useful in production-oriented backend systems:

* Input validation
* Centralized exception handling
* Database migrations
* Automated testing
* API documentation
* Rate limiting
* Transaction boundaries
* Containerized development
* Health checks
* CI/CD automation
* Version control
* Modular application structure

---

# 🛠️ Tech Stack

| Category                | Technology                       |
| ----------------------- | -------------------------------- |
| Language                | Java 21                          |
| Framework               | Spring Boot 4                    |
| Security                | Spring Security, JWT             |
| Database                | PostgreSQL                       |
| Cache / Fast Data Store | Redis                            |
| ORM                     | Spring Data JPA, Hibernate       |
| Database Migration      | Flyway                           |
| Build Tool              | Gradle                           |
| API Documentation       | Swagger / OpenAPI                |
| Testing                 | JUnit 5, Mockito, Testcontainers |
| Containerization        | Docker                           |
| Orchestration           | Docker Compose                   |
| CI/CD                   | GitHub Actions                   |
| Container Registry      | GitHub Container Registry        |

---

# 📌 Development Roadmap

## Phase 1 — Core Backend

* [ ] Authentication
* [ ] OTP verification
* [ ] JWT authorization
* [ ] User management
* [ ] Wallet creation
* [ ] Deposit / withdrawal
* [ ] Money transfers
* [ ] Transaction history

## Phase 2 — Security & Reliability

* [ ] Redis session management
* [ ] API rate limiting
* [ ] Idempotency keys
* [ ] Concurrent transaction protection
* [ ] Global exception handling
* [ ] Request validation

## Phase 3 — Engineering Infrastructure

* [ ] Flyway migrations
* [ ] Docker
* [ ] Docker Compose
* [ ] Unit tests
* [ ] Integration tests
* [ ] Testcontainers
* [ ] Swagger / OpenAPI
* [ ] GitHub Actions

## Phase 4 — Advanced Improvements

* [ ] Refresh-token workflow
* [ ] Notification service
* [ ] Monitoring and observability
* [ ] Payment gateway integration
* [ ] Ledger service
* [ ] Event-driven architecture

---

# 🚧 Current Project Status

VaultCore is being developed as a **production-oriented backend engineering project**.

The implementation roadmap prioritizes:

**Security → Transactions → Testing → CI/CD → Reliability → Observability**

The README should be updated as each feature is actually implemented and tested.

---

# 🎓 Engineering Concepts Demonstrated

VaultCore is intended to demonstrate practical understanding of:

* REST API design
* Backend service development
* Object-oriented programming
* Layered architecture
* Authentication and authorization
* Database transactions
* Concurrency control
* Data consistency
* Caching
* Rate limiting
* Unit testing
* Integration testing
* CI/CD
* Docker containerization
* Database migrations
* API documentation
* Secure application development

---

# 🤝 Contributing

This repository is primarily a personal engineering project.

For local development:

```bash
git checkout -b feature/<feature-name>
```

Make your changes, add tests where applicable, and create a pull request after validating the application locally.

---

# 📄 License

This project is intended to be released under the **MIT License**.

See the [`LICENSE`](LICENSE) file for details.

> If any source code is directly adapted from an MIT-licensed reference repository, retain the required copyright and license notices as applicable.

---

# 👩‍💻 Author

**Pragati Chaudhary**

B.Tech, Electronics and Communication Engineering
Indira Gandhi Delhi Technical University for Women

**GitHub:** https://github.com/Pragati4566

---

## ⭐ Project Focus

VaultCore is built to demonstrate that a backend project can go beyond basic CRUD by combining:

**Secure APIs + Transactional Consistency + Testing + CI/CD + Containerization + Reliability**

The goal is to build a backend that is understandable, testable, maintainable, and suitable for real-world software engineering practices.
