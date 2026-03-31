# Cebu Artist Repository (CAR) 🎸🎨✍️

A centralized digital directory for Cebu-based artists across multiple disciplines.

The platform enables artists, bands, and collectives to create public profiles while allowing event organizers, collaborators, and the public to discover talent and send inquiries.

---

## 📋 Project Overview

**Cebu Artist Repository (CAR)** is a discovery and inquiry platform designed to showcase Cebuano artists and simplify communication between artists and organizers.

The system supports:
- Artist profile creation and management
- Public browsing and search
- Genre and discipline categorization
- Locality-based discovery
- Booking inquiries (contact requests)
- Admin moderation and verification

> The platform does not manage booking contracts, payments, or negotiation workflows. It functions as a **directory and inquiry gateway**.

---

## 🚀 Core Features

### Artist Profiles

Artists can create and manage profiles representing:
- Individuals
- Bands or groups
- Organizations or collectives

Profiles include biography, portfolio links, social media links, genre classifications, Cebu locality, and visibility controls.

### Multi-Manager Artist Profiles

Multiple user accounts may manage a single artist profile.

Examples:
- Band manager + vocalist
- Label rep managing multiple artists
- Organization administrator managing multiple groups

### Discovery & Search

Visitors can discover artists through:
- Keyword search
- Art branch filtering
- Genre filtering
- Cebu locality filtering

### Inquiry System

Visitors can contact artists through a booking or inquiry form. Inquiries are currently stored in the system and can be viewed by profile managers. Email delivery is planned.

### Admin Moderation

Admin moderation and verification workflows are planned.

---

## 🏗 System Architecture

```
Planned Frontend (React + Vite)
             │
             │  REST API
             ▼
Spring Boot Backend (implemented)
        │
        ▼
PostgreSQL Database
```

Optional future components: Background Workers · Redis Cache · Email Queue · Scraping Service

---

## 🛠 Tech Stack

### Backend
| | |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot |
| Security | Spring Security dependency (authentication flows planned) |
| Persistence | Spring Data JPA |
| Migrations | Flyway |
| Database | PostgreSQL |
| Build | Maven |

### Frontend
| | |
|---|---|
| Language | TypeScript *(planned)* |
| Framework | React + Vite *(planned)* |
| Routing | React Router *(planned)* |
| Data Fetching | TanStack Query *(planned)* |
| Forms | React Hook Form *(planned)* |
| Styling | Tailwind CSS *(planned)* |

> Redux may be added later if global client-state complexity grows.

---

## 📂 Project Structure

### Backend

```
src/main/java/com/project/car/
 ├── artist/         # Profile management, portfolio, social links, public directory
 ├── inquiry/        # Public inquiry submission + manager inquiry access
 ├── taxonomy/       # Localities, art branches, genres (public taxonomy endpoints)
 ├── user/           # User entity/repository
 ├── common/         # Shared enums, base entity, exception handlers
 ├── audit/          # Audit entity (initial schema)
 └── CarApplication.java
```

### Frontend

```
Planned as a separate React + Vite project.
```

---

## 🗄 Database Design

Main entities:

```
users
  └──< artist_profile_managers >── artist_profiles
                                        │
                                        ├── portfolio_items
                                        ├── social_links
                                        ├── inquiries
                                        ├── artist_profile_genres ── genres ── art_branches
                                        └── locality_id ── localities
```

Full entity list: `users` · `artist_profiles` · `artist_profile_managers` · `art_branches` · `genres` · `artist_profile_genres` · `localities` · `portfolio_items` · `social_links` · `inquiries` · `audit_logs`

---

## 🔐 Roles & Permissions

### System Roles

| Role | Description |
|---|---|
| `USER` | Standard user |
| `ADMIN` | System administrator |

### Artist Profile Manager Roles

| Role | Description |
|---|---|
| `OWNER` | Primary profile manager |
| `MANAGER` | Secondary profile manager |

### Profile Status

**Approval Status**

| Status | Meaning |
|---|---|
| `PENDING` | Awaiting admin review |
| `APPROVED` | Visible in directory |
| `REJECTED` | Denied by admin |
| `SUSPENDED` | Disabled profile |

**Verification Status**

| Status | Meaning |
|---|---|
| `UNVERIFIED` | Not verified |
| `VERIFIED` | Confirmed authentic by admin |

---

## 🚦 Development Setup

### Prerequisites

- Java 21
- PostgreSQL
- Maven
- Git
- Node.js 18+ (only needed once the separate frontend project is added)

### Backend

```bash
cd car
./mvnw spring-boot:run
```

On Windows PowerShell, you can run:

```powershell
cd car
.\mvnw.cmd spring-boot:run
```

Backend API runs on `http://localhost:8080`. Flyway automatically applies database migrations on startup.

### Frontend

Planned as a separate repository/app.

Example commands (when available):

```bash
npm install
npm run dev
```

Expected frontend dev server (when available): `http://localhost:5173`.

---

## 🗺 Development Workflow

| Phase | Focus |
|---|---|
| **Phase 1** | Spring Boot setup · PostgreSQL schema · Flyway migrations *(completed)* |
| **Phase 2** | Artist profile creation · Manager relationships · Taxonomy endpoints *(in progress)* |
| **Phase 3** | Public directory · Search & filtering · Public profile page *(in progress)* |
| **Phase 4** | Inquiry submission *(implemented)* · Email notifications *(planned)* · Admin approval/verification *(planned)* |
| **Phase 5** | Portfolio/visibility enhancements · Manager assignment UI · Audit logging workflows *(planned)* |

---

## 📡 API Overview

### Artist Profiles
```
POST /api/v1/artist-profiles
GET /api/v1/artist-profiles/me/managed
GET /api/v1/artist-profiles/{id}
PATCH /api/v1/artist-profiles/{id}

GET /api/v1/artist-profiles/{id}/social-links
POST /api/v1/artist-profiles/{id}/social-links
PATCH /api/v1/artist-profiles/{id}/social-links/{linkId}
DELETE /api/v1/artist-profiles/{id}/social-links/{linkId}

GET /api/v1/artist-profiles/{id}/portfolio-items
POST /api/v1/artist-profiles/{id}/portfolio-items
PATCH /api/v1/artist-profiles/{id}/portfolio-items/{itemId}
DELETE /api/v1/artist-profiles/{id}/portfolio-items/{itemId}

GET /api/v1/artist-profiles/{id}/genres
POST /api/v1/artist-profiles/{id}/genres/{genreId}
DELETE /api/v1/artist-profiles/{id}/genres/{genreId}
```

### Public Directory
```
GET /api/v1/public/artists
GET /api/v1/public/artists/{slug}
```

Notes for `GET /api/v1/public/artists`:
- Optional query params: `keyword`, `localityId`, `artBranchId`, `genreId`
- Pagination params: `page` (default `0`), `size` (default `20`)

### Inquiries
```
POST /api/v1/public/artist-profiles/{artistProfileId}/inquiries
GET /api/v1/artist-profiles/{id}/inquiries
```

### Public Taxonomy
```
GET /api/v1/public/taxonomy/localities
GET /api/v1/public/taxonomy/art-branches
GET /api/v1/public/taxonomy/genres
```

### Admin
Planned.

---

## 🔭 Future Enhancements

- Automated social media activity syncing
- Analytics dashboard
- Recommendations and similar artists
- Internal messaging
- Event listings
- Redis caching layer
- Dedicated scraping service

---

## 🤝 Contributing

1. Create a feature branch
2. Implement changes
3. Add Flyway migrations if the schema changes
4. Open a pull request

---

## 📄 License

TBD

---

## 📞 Contact

- **Email:** danieldalaota40799@gmail.com
- **Location:** Consolacion Cebu, Philippines 🇵🇭
