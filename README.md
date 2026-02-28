# 🌐 Sphere

> One platform. Every conversation. Every story.

Sphere is a full-stack community and news platform where people can join communities, discuss any topic, and stay updated with real-time news — all enhanced by AI-powered personalization and moderation.

---

## 🚀 Features

- **Communities** — Create or join communities around any topic. Post, comment, upvote, and debate.
- **News Feed** — Categorized news sections: Sports, Geopolitics, Gaming, Tech, Entertainment and more — powered by NewsAPI.
- **AI Integration** — Groq-powered AI for content moderation, news summarization, and personalized feed recommendations.
- **Authentication** — Secure JWT-based auth with role management.
- **Real-time Notifications** — Stay updated on replies, mentions, and trending posts.
- **Multi-user & Scalable** — Built for communities of all sizes and all age groups.

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java, Spring Boot |
| Database | MySQL |
| AI | Groq API |
| News | NewsAPI |
| Auth | JWT |
| Deployment | GCP / AWS / Render |

---

## 📁 Project Structure

```
sphere/
├── src/
│   ├── main/
│   │   ├── java/com/sphere/
│   │   │   ├── auth/
│   │   │   ├── community/
│   │   │   ├── news/
│   │   │   ├── ai/
│   │   │   ├── user/
│   │   │   └── SphereApplication.java
│   │   └── resources/
│   │       └── application.yml
├── .gitignore
├── pom.xml
└── README.md
```

---

## ⚙️ Getting Started

### Prerequisites
- Java 17+
- MySQL
- Maven
- Groq API Key
- NewsAPI Key

### Setup

```bash
# Clone the repo
git clone https://github.com/yourusername/sphere.git
cd sphere

# Configure environment variables
cp src/main/resources/application.yml.example src/main/resources/application.yml
# Add your DB, Groq, and NewsAPI credentials

# Run the app
mvn spring-boot:run
```

---

## 🌍 Roadmap

- [x] Project setup & architecture
- [ ] User auth (JWT)
- [ ] Community CRUD
- [ ] Post & comment system
- [ ] News feed integration
- [ ] Groq AI moderation & summarization
- [ ] Personalized feed recommendations
- [ ] Real-time notifications
- [ ] Deployment

---

## 🤝 Contributing

This is a personal project currently in active development. Contributions, ideas, and feedback are welcome!

---

<p align="center">Built with 💙 by Sumeet</p>
