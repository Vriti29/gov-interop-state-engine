# Unified Governance & Interoperability State Engine

[![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.x-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring_Security-Stateless_JWT_%26_RBAC-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white)](https://spring.io/projects/spring-security)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Supabase-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)](https://supabase.com/)
[![Docker](https://img.shields.io/badge/Docker-Containerized-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)

A secure, enterprise-grade core backend orchestration service engineered to eliminate public-sector data fragmentation, enforce zero-trust inter-departmental verification, and execute fine-grained consent policy controls aligned with the **Digital Personal Data Protection (DPDP) Act 2023**.

---

## 🏛️ System Architecture

The platform operates as an interoperable **Middleware & Finite State Machine (FSM)** engine connecting Citizen ingress flows, external departmental verification connectors (Identity, Revenue, Education), and Administrative Officer Review queues without requiring legacy database migrations.

```text
                  [ Citizen Ingress Flow ]
                             │
                             ▼ (Stateless JWT)
┌───────────────────────────────────────────────────────────┐
│                SECURITY GATEWAY & RBAC                   │
│  • JWT Bearer Token Validation (HMAC-SHA256)             │
│  • Role Authority Guard (CITIZEN / OFFICER / ADMIN)       │
│  • Privilege Escalation Locks (Public Signup = CITIZEN)   │
└────────────────────────────┬──────────────────────────────┘
                             │
            ┌────────────────┴────────────────┐
            ▼                                 ▼
┌─────────────────────────┐       ┌─────────────────────────┐
│  APPLICATION WORKFLOW   │       │  CONSENT POLICY ENGINE  │
│         ENGINE          │       │       (DPDP 2023)       │
│ • State Machine (FSM)   │◄─────►│ • Purpose-Limited Grants│
│ • Step Verification     │       │ • Selective Disclosure  │
│ • Review Queue Locking  │       │ • Real-time Pre-checks  │
└───────────┬─────────────┘       └───────────┬─────────────┘
            │                                 │
            └────────────────┬────────────────┘
                             ▼
┌───────────────────────────────────────────────────────────┐
│        IMMUTABLE AUDIT LOGGER & SUPABASE POSTGRESQL       │
│  • Non-repudiation timestamps (ISO 8601 UTC)             │
│  • Relational integrity constraints on status transitions │
│  • Transaction pooler (Port 6543) connection architecture │
└───────────────────────────────────────────────────────────┘
🔑 Key Engineering CapabilitiesDeterministic Finite State Machine (FSM): Enforces a strict, non-tamperable verification lifecycle:SUBMITTED $\rightarrow$ VERIFICATION_IN_PROGRESS $\rightarrow$ OFFICER_REVIEW $\rightarrow$ APPROVED / REJECTED.The system prevents officers from taking action on an application until all required departmental steps (IDENTITY, REVENUE, EDUCATION) transition to COMPLETED.DPDP Act (2023) Consent Governance: Implements field-level selective disclosure and purpose limitation (SCHOLARSHIP_ELIGIBILITY). Applications cannot pull external department data without an active, citizen-granted consent record.Zero-Trust Role-Based Access Control (RBAC): Configured with custom Spring Security filter chains evaluating JWT claims across ROLE_CITIZEN, ROLE_OFFICER, and ROLE_ADMIN authorities.Data Freshness Enforcement: Verifies timestamp validity on academic and income records, rejecting stale cached records before advancing workflow states.Asynchronous Immutable Audit Ledger: Captures audit records for policy evaluations, step completions, and administrative decisions to guarantee non-repudiation and forensic accountability.🛠️ Tech StackLanguage & Runtime: Java 17+Framework: Spring Boot 3.3.x (Web, Security, Data JPA, Validation)Security: Stateless Spring Security, JWT (HMAC-SHA256), BCrypt Password HashingDatabase: Managed PostgreSQL (Hosted on Supabase via Transaction Pooler)API Documentation: OpenAPI 3.0 / Springdoc Swagger UIBuild & Deployment: Maven Wrapper (mvnw), Docker, Render Cloud📡 REST API SpecificationsAuthentication & Consent ManagementMethodEndpointAccessDescriptionPOST/api/v1/auth/loginPublicAuthenticates credentials and returns signed JWT with rolesPOST/api/v1/policy/consent/grantCITIZENRecords explicit, purpose-limited data sharing consentPOST/api/v1/policy/checkPublic / GatewayPre-checks field-level consent eligibility prior to data fetchGET/api/v1/policy/consents/citizen/{id}AuthenticatedRetrieves active consent grants for a given citizen IDApplication Workflow & Verification PipelineMethodEndpointAccessDescriptionPOST/api/v1/applicationsCITIZENSubmits a new scheme application and initializes workflow stepsGET/api/v1/applications/{id}/statusAuthenticatedFetches granular step verification timeline and overall statusPUT/api/v1/applications/{id}/steps/{department}Internal GatewayWebhook trigger updating step completion (IDENTITY, REVENUE, EDUCATION)GET/api/v1/applications/review-queueOFFICER, ADMINFetches applications that have passed all automated verificationsPOST/api/v1/applications/department/reviewOFFICER, ADMINEnacts final decision (APPROVE / REJECT) and commits audit log🗄️ Database Schema Designusers: Manages identity records, BCrypt password hashes, assigned roles (CITIZEN, OFFICER, ADMIN), and administrative jurisdictions.applications: Tracks scheme references, citizen identifiers, departmental step statuses (IDENTITY, REVENUE, EDUCATION), and the macro overall_status.consents: Manages DPDP policy agreements, provider departments, approved field sets, purposes, and valid lifecycle states (ACTIVE, REVOKED).audit_logs: Captures immutable event logs containing actor identities, target departments, action types, and UTC timestamps.🚀 Running LocallyPrerequisitesJDK 17 or higherGitPostgreSQL instance or Supabase project credentialsStepsClone the repository:Bashgit clone https://github.com/Vriti29/gov-interop-state-engine.git
cd gov-interop-state-engine
Configure Environment Variables:Update src/main/resources/application.properties with database and security configurations:Propertiesspring.datasource.url=jdbc:postgresql://<host>:5432/<database>
spring.datasource.username=<username>
spring.datasource.password=<password>
spring.jpa.hibernate.ddl-auto=update

jwt.secret=<your-256-bit-secret-key>
jwt.expiration=86400000
Build and Run:Bash./mvnw clean spring-boot:run
