# 🚀 Nivora Finance Backend

> A production-oriented fintech backend built with **Spring Boot 4**, **Java 21**, **PostgreSQL**, **Redis**, **JWT Authentication**, **Flyway**, **Docker**, **Testcontainers**, and **GitHub Actions**.

---

## ✨ Overview

Nivora Finance Backend is a production-oriented digital wallet backend designed to demonstrate modern backend engineering practices beyond basic CRUD applications.

The project implements secure authentication, wallet management, money transfers, QR-based payments, and Redis-powered rate limiting while focusing on scalability, maintainability, and clean architecture.

To make the project closer to a real-world backend, it incorporates database versioning with Flyway, integration testing using Testcontainers, containerized development with Docker, automated CI/CD using GitHub Actions, and production-ready development practices.

---

## 🎯 Project Goals

This project was built with the objective of learning and implementing production-grade backend development concepts, including:

- Secure authentication and authorization using Spring Security and JWT
- Reliable wallet transactions with transactional consistency
- Database versioning using Flyway
- Redis-backed session management and rate limiting
- Containerized development using Docker and Docker Compose
- Automated testing with JUnit, Mockito, and Testcontainers
- Continuous Integration using GitHub Actions
- Clean, modular, and maintainable backend architecture


## ✨ Features

## 🔐 Authentication

- User Registration
- Email OTP Verification
- Login
- Logout
- JWT Authentication
- BCrypt Password Encryption
- Redis Session Management
- Current User Endpoint

---

## 💰 Wallet

- Create Wallet
- Deposit Money
- Withdraw Money
- Wallet Balance
- Balance Validation
- Pessimistic Database Locking for Concurrent Updates

---

## 💸 Transactions

- Money Transfer
- Transaction History
- Transaction Search
- Transaction Summary
- Recent Contacts
- Idempotency-Key Support
- Atomic Money Transfers
- Transaction Validation

---

## 📱 QR Payments

- Generate Personal QR
- Resolve QR
- Pay Using QR

---

## 🛡️ Security

- Spring Security
- JWT Authorization Filter
- Protected APIs
- Public Route Configuration
- BCrypt Password Encoding
- Redis Session Management
- Redis Rate Limiting
- Global Exception Handling
- Request Validation

---

## 🚦 Rate Limiting

Implemented using Redis.

### Public APIs

- Signup
- Login
- Verify OTP

### Protected APIs

- Money Transfer
- QR Payments

### Features

- IP-based Rate Limiting
- User-based Rate Limiting
- Redis TTL
- Automatic Counter Expiry

---

## 🐳 Infrastructure

- Docker
- Docker Compose
- PostgreSQL
- Redis
- Flyway Database Migrations
- Multi-stage Docker Build
- Health Checks

---

## ⚙️ CI/CD

Implemented using GitHub Actions.

The pipeline automatically:

- Runs Unit Tests
- Runs Integration Tests
- Builds BootJar
- Builds Docker Image
- Publishes Docker Image to GitHub Container Registry (GHCR)

---

## 🧪 Testing

Testing is implemented using:

- JUnit 5
- Mockito
- Testcontainers
- PostgreSQL Test Container
- Flyway Test Migrations

Current coverage includes:

- AuthService
- JwtService
- WalletService
- TransactionService
- QrService
- RateLimitService
- End-to-End Transfer Integration Test



## 🛠️ Tech Stack

| Category | Technology |
|-----------|------------|
| Language | Java 21 |
| Framework | Spring Boot 4 |
| Security | Spring Security, JWT |
| Database | PostgreSQL |
| Cache | Redis |
| ORM | Spring Data JPA / Hibernate |
| Database Migration | Flyway |
| Build Tool | Gradle |
| API Documentation | Swagger / OpenAPI |
| Testing | JUnit 5, Mockito, Testcontainers |
| Containerization | Docker |
| Orchestration | Docker Compose |
| CI/CD | GitHub Actions |
| Container Registry | GitHub Container Registry (GHCR) |

---

## 🏗️ Architecture

```
                           Client
                              │
                              ▼
                     Spring Boot Backend
          ┌───────────────────┼───────────────────┐
          │                   │                   │
          ▼                   ▼                   ▼
     PostgreSQL             Redis           Mail Service
          ▲
          │
     Flyway Migrations

                              │
                              ▼
                  GitHub Actions CI/CD

                              │
                              ▼
               GitHub Container Registry
```

---

## 📁 Project Structure

```
src
├── main
│   ├── java
│   │   └── com.nivora.nivora_finance_backend
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

## Clone the Repository

```bash
git clone https://github.com/Harshjha002/nivora-finance-backend.git

cd nivora-finance-backend
```

---

## Configure Environment Variables

Create a `.env` file in the project root.

Example:

```env
# ----------------------------
# PostgreSQL
# ----------------------------

POSTGRES_DB=nivora
POSTGRES_USER=postgres
POSTGRES_PASSWORD=password

SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/nivora
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
# Mail
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

---

## 🐳 Running with Docker

Start all required services using Docker Compose.

```bash
docker compose up --build
```

Once the containers are running:

Backend

```
http://localhost:8080
```

Swagger UI

```
http://localhost:8080/swagger-ui/index.html
```

---

## 💻 Running Locally

### 1. Start PostgreSQL and Redis

If you're using Docker:

```bash
docker compose up postgres redis
```

### 2. Run the application

```bash
./gradlew bootRun
```

The application will automatically:

- Apply Flyway database migrations
- Connect to PostgreSQL
- Connect to Redis
- Start the embedded web server

Backend

```
http://localhost:8080
```

---

## 📖 API Documentation

Swagger UI is available after the application starts.

```
http://localhost:8080/swagger-ui/index.html
```

---

# ⚙️ CI/CD Pipeline

The project uses **GitHub Actions** to automate build verification and container image generation.

### Pipeline Workflow

```
Developer

     │

 Git Push / Pull Request

     │

     ▼

GitHub Actions

     │

     ▼

Run Unit Tests

     │

     ▼

Run Integration Tests

     │

     ▼

Build BootJar

     │

     ▼

Build Docker Image

     │

     ▼

Publish Docker Image to GitHub Container Registry (GHCR)
```

Every push or pull request to the `main` branch automatically:

- Runs Unit Tests
- Runs Integration Tests using Testcontainers
- Builds the Spring Boot application
- Builds a Docker image
- Publishes the Docker image to GitHub Container Registry (GHCR)

---

# 📌 Current Project Status

### ✅ Completed

- Authentication Module
- Wallet Module
- Transaction Module
- QR Payment Module
- Spring Security
- JWT Authentication
- Redis Session Management
- Redis Rate Limiting
- Flyway Database Migrations
- Docker
- Docker Compose
- GitHub Actions CI/CD
- GitHub Container Registry (GHCR)
- Unit Testing
- Integration Testing (Testcontainers)

### 🚧 Planned

- AWS Deployment
- Refresh Token Support
- Kafka Notification Service
- Email Notification Service
- Monitoring & Observability
- Payment Gateway Integration
- Ledger Service
- Microservices Migration

---

# 🤝 Contributing

Contributions, suggestions, and improvements are welcome.

If you'd like to contribute:

1. Fork the repository.
2. Create a new feature branch.
3. Commit your changes.
4. Open a Pull Request.

---

# 📄 License

This project is licensed under the **MIT License**.

See the [LICENSE](LICENSE) file for more details.

---

# 👨‍💻 Author

**Harsh Jha**

Backend Developer | Java | Spring Boot | PostgreSQL | Redis | Docker

If you found this project helpful or interesting, consider giving it a ⭐ on GitHub.