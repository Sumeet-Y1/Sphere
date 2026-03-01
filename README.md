# 🌐 Sphere

> One platform. Every conversation. Every story.

Sphere is a full-stack community and news platform where people can join communities, discuss any topic, and stay updated with real-time news — all enhanced by AI-powered personalization and moderation.

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 21, Spring Boot 3.5.11 |
| Database | MySQL |
| AI | Groq API (LLaMA 3) |
| News | NewsAPI |
| Auth | JWT (jjwt 0.12.6) |
| Deployment | GCP / AWS / Render |

---

## 📁 Project Structure

```
com.sphere/
├── admin/
│   ├── controller/
│   ├── dto/
│   └── service/
├── ai/
│   └── service/
├── auth/
│   ├── controller/
│   ├── dto/
│   └── service/
├── comment/
│   ├── controller/
│   ├── dto/
│   ├── repository/
│   └── service/
├── common/
│   ├── config/        (SecurityConfig, CORS)
│   ├── exception/     (Global exception handler)
│   ├── jwt/           (JwtUtil, JwtAuthFilter)
│   └── response/      (Standard API response wrapper)
├── community/
│   ├── controller/
│   ├── dto/
│   ├── repository/
│   └── service/
├── news/
│   ├── controller/
│   ├── dto/
│   └── service/
├── notifications/
│   ├── controller/
│   ├── dto/
│   ├── repository/
│   └── service/
├── post/
│   ├── controller/
│   ├── dto/
│   ├── repository/
│   └── service/
└── user/
    ├── controller/
    ├── dto/
    ├── repository/
    └── service/
```

---

## ⚙️ Getting Started

### Prerequisites
- Java 21+
- MySQL
- Maven
- Groq API Key
- NewsAPI Key

### Setup

```bash
# Clone the repo
git clone https://github.com/Sumeet-Y1/Sphere.git
cd Sphere/sphere

# Create the database
mysql -u root -p
CREATE DATABASE sphere_db;

# Configure environment variables
# Create src/main/resources/application-local.yml and add your credentials

# Run the app
.\mvnw.cmd spring-boot:run
```

---

## 🔐 Auth Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Login and get JWT token |

---

## 🌍 Roadmap

- [x] Project setup & architecture
- [x] Folder structure & package organization
- [x] JWT utility & auth filter
- [x] Spring Security configuration
- [x] User entity & repository
- [x] Auth service (register & login)
- [ ] Auth controller
- [ ] Community CRUD
- [ ] Post & comment system
- [ ] News feed integration (NewsAPI)
- [ ] Groq AI moderation & summarization
- [ ] Personalized feed recommendations
- [ ] Real-time notifications (WebSocket)
- [ ] Admin dashboard
- [ ] Deployment

---

## 🚀 Features

- **Communities** — Create or join communities around any topic. Post, comment, upvote, and debate.
- **News Feed** — Categorized news sections: Sports, Geopolitics, Gaming, Tech, Entertainment and more — powered by NewsAPI.
- **AI Integration** — Groq-powered AI for content moderation, news summarization, and personalized feed recommendations.
- **Authentication** — Secure JWT-based auth with role management.
- **Real-time Notifications** — Stay updated on replies, mentions, and trending posts.
- **Multi-user & Scalable** — Built for communities of all sizes and all age groups.

---

<p align="center">Built with 💙 by Sumeet</p>