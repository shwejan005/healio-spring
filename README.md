# Healio

A full-stack mental health and wellness platform built with Spring Boot and Next.js. Healio provides mood tracking, wellness goal management, gratitude journaling, fitness logging, community forums, and AI-powered mental health support through a unified dashboard.

---

## Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Environment Variables](#environment-variables)
- [Database](#database)
- [Deployment](#deployment)
- [API Reference](#api-reference)
- [Project Structure](#project-structure)
- [License](#license)

---

## Features

| Module | Description |
|---|---|
| Mood Check-In | Log daily mood entries with notes and visualize emotional trends over time |
| Wellness Goals | Create, track, and complete personal health and wellness goals |
| Gratitude Journal | Record daily gratitude entries to build a positive mindset |
| Fitness Tracking | Log workouts, sleep patterns, and dietary habits |
| Community Forum | Share experiences and participate in peer-support discussions |
| AI Chat | Receive AI-powered mental health guidance via Google Gemini |
| Real-time Chat | Private chat rooms for one-on-one peer conversations |
| Account Management | Manage user profiles, preferences, and submit application feedback |

---

## Architecture

```
                    +-------------------+
                    |   Clerk Auth      |
                    +--------+----------+
                             |
              +--------------+--------------+
              |                             |
    +---------v----------+      +-----------v---------+
    |  Next.js Frontend  |      |  Spring Boot API    |
    |  (Vercel)          +----->+  (Render / Docker)  |
    |                    | REST |                     |
    |  - React 19        |      |  - Spring Security  |
    |  - TypeScript      |      |  - Spring Data JPA  |
    |  - TailwindCSS     |      |  - H2 Database      |
    +--------------------+      |  - Gemini AI SDK    |
                                +----------+----------+
                                           |
                                  +--------v--------+
                                  |  H2 Database    |
                                  |  (File-based)   |
                                  +-----------------+
```

### Backend

- **Java 17** with **Spring Boot 3.2.5**
- Spring Web, Spring Data JPA, Spring Security
- OAuth2 Resource Server for Clerk JWT validation
- H2 file-based database (development and staging)
- Jackson for JSON serialization
- Maven build system

### Frontend

- **Next.js 15** with **React 19** and **TypeScript**
- TailwindCSS for styling
- Radix UI component primitives
- Clerk for authentication and user management
- Framer Motion for animations
- Recharts for data visualization
- Zustand for state management

---

## Prerequisites

- Java 17+ (JDK)
- Maven 3.8+ (or use the included Maven wrapper)
- Node.js 18+ and npm
- A [Clerk](https://clerk.com) account for authentication
- A [Google Gemini](https://ai.google.dev) API key for AI features

---

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/<your-username>/Healio-spring.git
cd Healio-spring
```

### 2. Start the Backend

```bash
cd backend
```

Configure the required environment variables (see [Environment Variables](#environment-variables)), then start the server:

```bash
./mvnw spring-boot:run
```

If the Maven wrapper is not available, use `mvn spring-boot:run` instead.

The backend starts at **http://localhost:8080**.

### 3. Start the Frontend

In a separate terminal:

```bash
cd frontend
npm install
```

Create a `.env.local` file in the `frontend/` directory (see [Environment Variables](#environment-variables)):

```bash
npm run dev
```

The frontend starts at **http://localhost:3000**.

---

## Environment Variables

### Backend

Set these as system environment variables or configure them in `application.properties`:

| Variable | Description | Default |
|---|---|---|
| `CLERK_ISSUER_URL` | Clerk JWT issuer URI for token validation | -- |
| `GEMINI_API_KEY` | Google Gemini API key for AI features | -- |
| `CORS_ALLOWED_ORIGINS` | Comma-separated list of allowed frontend origins | `http://localhost:3000` |

### Frontend

Create a `frontend/.env.local` file with the following:

| Variable | Description |
|---|---|
| `NEXT_PUBLIC_CLERK_PUBLISHABLE_KEY` | Clerk publishable key |
| `CLERK_SECRET_KEY` | Clerk secret key |
| `NEXT_PUBLIC_API_URL` | Backend API base URL (e.g., `http://localhost:8080`) |
| `NEXT_PUBLIC_GEMINI_API_KEY` | Google Gemini API key (client-side) |
| `NEXT_PUBLIC_PAYPAL_CLIENT_ID` | PayPal client ID for payment integration |

---

## Database

Healio uses an **H2 file-based database** that persists data across server restarts. The database files are stored in the `backend/data/` directory.

### Accessing the H2 Console

The H2 web console is available for direct database inspection:

| Environment | URL |
|---|---|
| Local | `http://localhost:8080/h2-console` |
| Production | `https://<your-backend-domain>/h2-console` |

**Connection settings:**

| Field | Value |
|---|---|
| JDBC URL | `jdbc:h2:file:./data/healio` |
| Username | `sa` |
| Password | *(leave empty)* |

### Inspecting Data

After connecting to the H2 console, you can run standard SQL queries to inspect your data:

```sql
-- View all users
SELECT * FROM APP_USER;

-- View mood entries
SELECT * FROM MOOD_ENTRY;

-- View wellness goals
SELECT * FROM GOAL;

-- View gratitude entries
SELECT * FROM GRATITUDE_ENTRY;

-- View fitness logs
SELECT * FROM FITNESS_LOG;
```

### Production Considerations

H2 file-based storage persists data within the same container, but container re-provisioning (common on Render free tier) will reset the database. For production workloads requiring durable persistence, migrate to a managed PostgreSQL or MySQL instance and update the following properties:

```properties
spring.datasource.url=jdbc:postgresql://<host>:<port>/<database>
spring.datasource.driverClassName=org.postgresql.Driver
spring.datasource.username=<username>
spring.datasource.password=<password>
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

---

## Deployment

### Backend (Render with Docker)

1. Push your code to a GitHub repository.
2. Create a new **Web Service** on [Render](https://render.com) and connect your repository.
3. Set the **Root Directory** to `backend/`.
4. Set the **Environment** to `Docker`. Render will use the included `Dockerfile` to build and deploy.
5. Add the following **environment variables** in the Render dashboard:
   - `GEMINI_API_KEY`
   - `CLERK_ISSUER_URL`
   - `CORS_ALLOWED_ORIGINS` -- set to your Vercel frontend URL (e.g., `https://healio-spring.vercel.app`)
6. Deploy the service and note the generated URL (e.g., `https://healio-backend.onrender.com`).

### Frontend (Vercel)

1. Import your GitHub repository on [Vercel](https://vercel.com).
2. Set the **Framework Preset** to `Next.js` and the **Root Directory** to `frontend/`.
3. Add the following **environment variables**:
   - `NEXT_PUBLIC_CLERK_PUBLISHABLE_KEY`
   - `CLERK_SECRET_KEY`
   - `NEXT_PUBLIC_API_URL` -- set to your Render backend URL (e.g., `https://healio-backend.onrender.com`)
   - `NEXT_PUBLIC_GEMINI_API_KEY`
   - `NEXT_PUBLIC_PAYPAL_CLIENT_ID`
4. Deploy.

### Post-Deployment Checklist

- [ ] Verify `CORS_ALLOWED_ORIGINS` on the backend includes the exact Vercel frontend URL
- [ ] Update the Clerk dashboard to include the Vercel domain as an allowed origin
- [ ] Confirm frontend `NEXT_PUBLIC_API_URL` points to the live backend URL (not `localhost`)
- [ ] Test user sync, mood entries, and AI chat from the deployed frontend
- [ ] For production use, switch PayPal from sandbox to live credentials

---

## API Reference

All endpoints are served under the base path `/api/`.

| Resource | Endpoint | Methods | Description |
|---|---|---|---|
| Users | `/api/users` | GET, POST | User management and sync |
| Mood Entries | `/api/mood-entries` | GET, POST, PUT, DELETE | Daily mood check-ins |
| Goals | `/api/goals` | GET, POST, PUT, DELETE | Wellness goal tracking |
| Gratitude | `/api/gratitude` | GET, POST, PUT, DELETE | Gratitude journal entries |
| Fitness Logs | `/api/fitness-logs` | GET, POST, PUT, DELETE | Workout and health logging |
| Forums | `/api/forums` | GET, POST, PUT, DELETE | Community discussion threads |
| Chat Rooms | `/api/rooms` | GET, POST | Private chat room management |
| Messages | `/api/messages` | GET, POST | Chat messages within rooms |
| AI | `/api/ai` | POST | AI-powered mental health support |
| Feedback | `/api/feedback` | GET, POST | Application feedback submission |
| Account | `/api/account` | GET, PUT | User account and profile management |

---

## Project Structure

```
Healio-spring/
|-- backend/
|   |-- src/main/java/com/healio/
|   |   |-- config/             Security and CORS configuration
|   |   |-- controller/         REST API controllers
|   |   |-- dto/                Data transfer objects
|   |   |-- entity/             JPA entity models
|   |   |-- repository/         Spring Data JPA repositories
|   |   |-- service/            Business logic layer
|   |-- src/main/resources/
|   |   |-- application.properties
|   |-- Dockerfile
|   |-- pom.xml
|-- frontend/
|   |-- app/
|   |   |-- (auth)/             Authentication pages (sign-in, sign-up)
|   |   |-- (dashboard)/        Application pages (mood, goals, AI, etc.)
|   |   |-- (landing-page)/     Public-facing landing page
|   |-- components/             Reusable React components
|   |-- hooks/                  Custom React hooks
|   |-- lib/                    API client and utility functions
|   |-- package.json
|-- README.md
```

---

## Available Commands

### Backend

| Command | Description |
|---|---|
| `./mvnw spring-boot:run` | Start the development server |
| `./mvnw clean install` | Build the project |
| `./mvnw test` | Run the test suite |

### Frontend

| Command | Description |
|---|---|
| `npm run dev` | Start the development server with Turbopack |
| `npm run build` | Create a production build |
| `npm start` | Serve the production build |
| `npm run lint` | Run ESLint checks |

---

## License

This project is developed for educational and personal use.
