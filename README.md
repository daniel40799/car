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

Visitors can contact artists through a booking or inquiry form. Inquiries are stored in the system and delivered via email to artists and/or admins. Artists respond outside the platform.

### Admin Moderation

Administrators manage the integrity of the directory. Admins can approve or reject artist profiles, verify artist authenticity, suspend profiles, and manage art branches, genres, and Cebu localities.

---

## 🏗 System Architecture

```
React (TypeScript)
        │
        │  REST API
        ▼
Spring Boot Backend
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
| Language | Java 26 |
| Framework | Spring Boot |
| Security | Spring Security + OAuth2 |
| Persistence | Spring Data JPA |
| Migrations | Flyway |
| Database | PostgreSQL |
| Build | Maven |

### Frontend
| | |
|---|---|
| Language | TypeScript |
| Framework | React + Vite |
| Routing | React Router |
| Data Fetching | TanStack Query |
| Forms | React Hook Form |
| Styling | Tailwind CSS |

> Redux may be added later if global client-state complexity grows.

---

## 📂 Project Structure

### Backend

```
backend/
 ├── auth/           # Login, registration, OAuth
 ├── user/           # User accounts
 ├── artist/         # Artist profiles and managers
 ├── taxonomy/       # Branches, genres, localities
 ├── inquiry/        # Inquiry submission
 ├── admin/          # Moderation and verification
 ├── common/
 ├── config/
 └── security/       # Authentication and authorization
```

### Frontend

```
frontend/src/
 ├── app/
 ├── routes/
 ├── pages/
 ├── features/
 │   ├── auth/
 │   ├── artistProfiles/
 │   ├── publicDirectory/
 │   ├── inquiries/
 │   └── admin/
 ├── components/
 ├── hooks/
 ├── services/
 ├── types/
 └── utils/
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

- Java 26
- Node.js 18+
- PostgreSQL
- Maven
- Git

### Backend

```bash
cd backend
./mvnw spring-boot:run
```

Backend API runs on `http://localhost:8080`. Flyway automatically applies database migrations on startup.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend runs on `http://localhost:5173`.

---

## 🗺 Development Workflow

| Phase | Focus |
|---|---|
| **Phase 1** | Spring Boot setup · PostgreSQL schema · Flyway migrations · React project init |
| **Phase 2** | User authentication · Artist profile creation · Manager relationships · Taxonomy |
| **Phase 3** | Artist directory · Search & filtering · Public profile page |
| **Phase 4** | Inquiry submission · Email notifications · Admin approval & verification |
| **Phase 5** | Portfolio management · Visibility settings · Manager assignment UI · Audit logging |

---

## 📡 API Overview

### Authentication
```
POST  /api/auth/register
POST  /api/auth/login
GET   /api/auth/me
```

### Artist Profiles
```
POST  /api/artist-profiles
GET   /api/artist-profiles/my-managed
GET   /api/artist-profiles/{id}
PUT   /api/artist-profiles/{id}
```

### Public Directory
```
GET   /api/public/artists
GET   /api/public/artists/{slug}
```

### Inquiries
```
POST  /api/public/artists/{artistId}/inquiries
GET   /api/artist-profiles/{artistId}/inquiries
```

### Admin
```
GET   /api/admin/artist-profiles/pending
PATCH /api/admin/artist-profiles/{id}/approve
PATCH /api/admin/artist-profiles/{id}/reject
PATCH /api/admin/artist-profiles/{id}/verify
```

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
