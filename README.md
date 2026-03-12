# 🧠 Healio

A full-stack mental health & wellness application built with **Spring Boot** and **Next.js**.

Healio helps users track their mood, set wellness goals, journal gratitude, log fitness activities, chat in community forums, and get AI-powered mental health support — all from a single, beautiful dashboard.

---

## ✨ Features

| Feature | Description |
|---|---|
| **Mood Check-In** | Log daily mood entries and visualize trends over time |
| **Goals** | Set, track, and complete personal wellness goals |
| **Gratitude Journal** | Record daily gratitude entries |
| **Fitness Tracking** | Log workouts, sleep, and diet |
| **Community Forum** | Share stories and engage in peer-support discussions |
| **AI Chat** | Get AI-powered mental health support via Gemini |
| **Real-time Chat** | Private chat rooms for peer conversations |
| **Account & Feedback** | Manage profile and submit app feedback |

---

## 🏗️ Tech Stack

### Backend
- **Java 17** with **Spring Boot 3.2.5**
- Spring Web, Spring Data JPA, Spring Security
- OAuth2 Resource Server (Clerk JWT validation)
- H2 in-memory database (dev) — swap to PostgreSQL/MySQL for production
- Jackson for JSON processing
- Maven build system

### Frontend
- **Next.js 15** with **React 19** and **TypeScript**
- TailwindCSS for styling
- Radix UI component primitives
- Clerk for authentication
- Framer Motion for animations
- Recharts for data visualization
- Zustand for state management

---

## 📋 Prerequisites

- **Java 17+** (JDK)
- **Maven 3.8+** (or use the Maven wrapper if included)
- **Node.js 18+** and **npm**
- A **[Clerk](https://clerk.com)** account (for authentication)
- A **[Google Gemini](https://ai.google.dev)** API key (for AI features)

---

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/<your-username>/Healio-spring.git
cd Healio-spring
```

### 2. Set Up the Backend

```bash
cd backend
```

#### Configure Environment Variables

Set the following environment variables before starting the server:

```bash
export CLERK_ISSUER_URL=https://<your-clerk-domain>
export GEMINI_API_KEY=<your-gemini-api-key>
```

Alternatively, edit `src/main/resources/application.properties` directly.

#### Run the Backend

```bash
./mvnw spring-boot:run
```

> If you don't have the Maven wrapper, use `mvn spring-boot:run` instead.

The backend will start on **http://localhost:8080**.

You can access the H2 database console at **http://localhost:8080/h2-console** (JDBC URL: `jdbc:h2:mem:healio`, username: `sa`, no password).

---

### 3. Set Up the Frontend

Open a **new terminal** and navigate to the frontend:

```bash
cd frontend
```

#### Install Dependencies

```bash
npm install
```

#### Configure Environment Variables

Create a `.env.local` file in the `frontend/` directory with your Clerk keys:

```env
NEXT_PUBLIC_CLERK_PUBLISHABLE_KEY=pk_...
CLERK_SECRET_KEY=sk_...
NEXT_PUBLIC_API_URL=http://localhost:8080
```

#### Run the Frontend

```bash
npm run dev
```

The frontend will start on **http://localhost:3000**.

---

## 🔧 Available Scripts

### Backend

| Command | Description |
|---|---|
| `./mvnw spring-boot:run` | Start the backend server |
| `./mvnw clean install` | Build the project |
| `./mvnw test` | Run tests |

### Frontend

| Command | Description |
|---|---|
| `npm run dev` | Start dev server (with Turbopack) |
| `npm run build` | Build for production |
| `npm start` | Start production server |
| `npm run lint` | Run ESLint |

---

## 📁 Project Structure

```
Healio-spring/
├── backend/
│   ├── src/main/java/com/healio/
│   │   ├── config/         # CORS & Security configuration
│   │   ├── controller/     # REST API controllers
│   │   ├── entity/         # JPA entities
│   │   ├── repository/     # Spring Data repositories
│   │   └── service/        # Business logic services
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml
├── frontend/
│   ├── app/
│   │   ├── (auth)/         # Authentication pages
│   │   ├── (dashboard)/    # Main app pages (mood, goals, AI, etc.)
│   │   └── (landing-page)/ # Public landing page
│   ├── components/         # Reusable UI components
│   ├── hooks/              # Custom React hooks
│   ├── lib/                # API client & utilities
│   └── package.json
└── README.md
```

---

## 🌐 API Endpoints

The backend exposes RESTful APIs under `http://localhost:8080/api/`. Key resource groups:

- `/api/users` — User management
- `/api/mood-entries` — Mood check-ins
- `/api/goals` — Wellness goals
- `/api/gratitude` — Gratitude journal
- `/api/fitness-logs` — Fitness tracking
- `/api/forums` — Community forums
- `/api/rooms` & `/api/messages` — Chat rooms
- `/api/ai` — AI-powered features
- `/api/feedback` — App feedback
- `/api/account` — Account management

---

## 📝 License

This project is for educational / personal use.
