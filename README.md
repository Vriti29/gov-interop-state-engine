# Core Backend Service & DPDP Policy Engine (Member 3)

Centralized backend service powering authentication, role-based authorization, DPDP Act 2023 consent enforcement, deterministic multi-department workflow tracking, statutory SLA monitoring, and anti-insider security guards.

---

## 1. Tech Stack
- **Language / Runtime:** Java 17+
- **Framework:** Spring Boot 3.3.x (Web, Security, Data JPA, Validation)
- **Security:** Stateless Spring Security, JWT (HMAC-SHA256), RBAC
- **Database:** PostgreSQL (Hosted on Supabase)
- **API Documentation:** Springdoc OpenAPI (Swagger UI)
- **Build Tool:** Maven Wrapper (`./mvnw`)

---

## 2. Overview of Work Done (Member 3 Scope)
- **Stateless Authentication & RBAC:** Implemented secure JWT-based registration and login flows supporting segregated roles (`CITIZEN`, `OFFICER`, `ADMIN`).
- **DPDP Act 2023 Compliance:** Built a deterministic consent lifecycle engine (`ACTIVE`, `REVOKED`, `EXPIRED`) with citizen-driven revocation and dynamic field-level data minimization.
- **Workflow State Machine:** Designed a two-tier orchestration system tracking Identity, Revenue, and Education verifications with safety protections against false citizen rejections during external connector failures.
- **Statutory 7-Day SLA Monitoring:** Enforced automated temporal deadline checks on applications (`slaBreached: true/false`) for escalations.
- **Anti-Insider Threat Guards:** Implemented an in-memory 3-strike out-of-jurisdiction lockout, active case association checks, and sliding-window rate limiting (10 req/min).

---

## 3. Project Directory Structure

```text
backend/
├── src/
│   ├── main/
│   │   ├── java/com/interop/backend/
│   │   │   ├── config/             # Spring Security, OpenAPI Swagger, Web configs
│   │   │   ├── controller/         # REST Controllers (Auth, Application, Policy)
│   │   │   ├── dto/                # Request and Response payload objects
│   │   │   ├── entity/             # JPA Entities (User, Application, Consent, Step)
│   │   │   ├── repository/         # Spring Data JPA Repositories
│   │   │   ├── security/           # JWT Token Provider, Filters, UserDetails
│   │   │   └── service/            # Core State Machine & Policy Engine Logic
│   │   └── resources/
│   │       └── application.properties # Supabase DB, JWT, and Server configs
│   └── test/                       # Unit and Integration test suites
├── .mvn/wrapper/                   # Maven wrapper binaries
├── Dockerfile                      # Containerization configuration
├── mvnw                            # Unix build script
├── mvnw.cmd                        # Windows build script
├── pom.xml                         # Project dependencies and plugins
└── README.md                       # Service documentation
```
---

## 2. Quick Start & Setup

### Prerequisites
- Java 17 or higher (`java -version`)
- Internet connection to connect to the shared Supabase cloud database

### Database Configuration
The service is pre-configured to connect to the team's shared Supabase PostgreSQL instance via `src/main/resources/application.properties`:
- **Host:** `db.sclzhbarirzpyxcdhyze.supabase.co`
- **Port:** `5432`
- **Database:** `postgres`
- **Username:** `postgres`
- **SSL Mode:** `require`
- **DDL Auto:** `update` (auto-creates and syncs tables on startup)

### Run the Application
Run the following command from the `backend/` directory:

```bash
./mvnw clean spring-boot:run
```

## SWAGGER
All endpoints, request bodies, and response schemas are interactively documented in Swagger:
http://localhost:8080/swagger-ui.html

## AUTH
POST /api/v1/auth/login - Use on frontend / route. Returns JWT token and user role (CITIZEN, OFFICER, ADMIN).

Pass Authorization: Bearer <token> header for all private routes.

## Workflow & Tracking
POST /api/v1/applications - Submit new citizen application (Starts 7-day SLA).

GET /api/v1/applications/{id}/status - Tracking screen data. Returns step statuses (identityStep, revenueStep, educationStep) and slaBreached: true/false.

PUT /api/v1/applications/{id}/steps/{department}?status=... - Update step status. External failures (FAILED_EXTERNAL, RETRY_PENDING) safely map to WAITING_FOR_DEPARTMENT.

PUT /api/v1/applications/{id}/review?status=APPROVED|REJECTED - Officer review. Final state is permanently frozen.

## DPDP Policy Engine
POST /api/v1/policy/consent/{id}/revoke - Citizen consent revocation.

POST /api/v1/policy/check - Verifies consent, enforces dynamic data minimization, checks jurisdiction, and applies 3-strike insider threat guards.
