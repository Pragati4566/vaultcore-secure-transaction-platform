# 🚀 Load Testing

Nivora Finance is performance tested using **Grafana k6** to validate API reliability, concurrency handling, and response times under realistic workloads.

## Objectives

The load tests validate:

- Authentication performance under concurrent users
- Money transfer reliability under concurrent transactions
- API response latency
- System stability under sustained traffic
- Zero request failures during concurrent execution

---

# Authentication Load Test

### Endpoint

```http
POST /api/v1/auth/login
```

### Configuration

| Property | Value |
|----------|-------|
| Tool | Grafana k6 |
| Virtual Users | 50 |
| Duration | 30 seconds |

### Results

| Metric | Result |
|---------|--------|
| Total Requests | **39,166** |
| Throughput | **1,297 requests/sec** |
| Success Rate | **100%** |
| Failed Requests | **0%** |
| Average Response Time | **38.03 ms** |
| Median Response Time | **29.98 ms** |
| P90 | **77.49 ms** |
| P95 | **93.92 ms** |
| Maximum Response Time | **269.6 ms** |

### Threshold Validation

| Threshold | Status |
|------------|--------|
| P95 < 200 ms | ✅ Passed |
| Request Failure Rate < 1% | ✅ Passed |

---

# Money Transfer Load Test

### Endpoint

```http
POST /api/v1/transactions/transfer
```

### Configuration

| Property | Value |
|----------|-------|
| Tool | Grafana k6 |
| Virtual Users | 50 |
| Duration | 30 seconds |

### Results

| Metric | Result |
|---------|--------|
| Total Requests | **6,427** |
| Throughput | **210 requests/sec** |
| Success Rate | **100%** |
| Failed Requests | **0%** |
| Average Response Time | **234.01 ms** |
| Median Response Time | **266.64 ms** |
| P90 | **350.09 ms** |
| P95 | **387.21 ms** |
| Maximum Response Time | **2.58 s** |

### Threshold Validation

| Threshold | Status |
|------------|--------|
| P95 < 500 ms | ✅ Passed |
| Request Failure Rate < 1% | ✅ Passed |

---

# Stress Testing

Additional stress testing was performed with **100 concurrent virtual users**.

The backend continued processing requests successfully with:

- ✅ 100% successful authentication requests
- ✅ 100% successful money transfers
- ✅ 0% request failures
- ✅ No transaction inconsistencies
- ✅ No deadlocks
- ✅ Stable database state after testing

Although latency increased under higher concurrency, the system remained fully functional without request failures.

---

# Test Environment

| Component | Technology |
|-----------|------------|
| Load Testing | Grafana k6 |
| Backend | Spring Boot |
| Database | PostgreSQL 17 |
| Cache | Redis 7 |
| Authentication | JWT |
| Containerization | Docker Compose |

---

# Running the Tests

Authentication

```bash
k6 run load-tests/auth-profile.js
```

Money Transfer

```bash
k6 run load-tests/transfer.js
```

---

# Key Achievements

- Successfully processed **39,166 authentication requests** with **0% failures**.
- Successfully processed **6,427 concurrent money transfer requests** with **100% success rate**.
- Maintained **zero failed requests** during authentication and transfer benchmarks.
- Validated backend stability under concurrent user load.
- Demonstrated reliable transaction processing using idempotency protection.
- Verified secure JWT authentication under sustained traffic.

---

# Future Improvements

Future versions of Nivora Finance will include:

- Prometheus metrics
- Grafana dashboards
- Distributed tracing
- Notification Service using Kafka
- Asynchronous email processing
- Horizontal scaling benchmarks
- Multi-instance load testing