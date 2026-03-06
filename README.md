# 🌐 Sphere

Sphere is a full-stack community and news platform where people can join communities, discuss any topic, and stay updated with real-time news  all enhanced by AI-powered personalization and moderation.

## 🚀 Features

### 👤 User
- Register & Login with JWT authentication
- Profile management (bio, avatar)
- Avatar upload via AWS S3
- Default DiceBear avatars
- Follow / Unfollow users
- Block / Unblock users

### 🏘️ Community
- Create public/private communities
- Join / Leave communities
- Browse all communities

### 📝 Posts
- Create text, image & link posts
- Upvote / Downvote posts
- Delete posts
- Browse posts by community

### 💬 Comments
- Comment on posts
- Nested replies
- Delete comments

### 📰 News
- Top headlines
- Browse by category (Sports, Tech, Gaming, etc.)
- Search news articles

### 🤖 AI (Powered by Groq)
- Summarize news articles
- Content moderation
- Personalized feed insights

### 🔔 Notifications
- Real-time notifications via WebSocket
- Get notified on comments, upvotes, follows & DMs

### 💌 Direct Messages
- Send & receive DMs
- Real-time messaging via WebSocket
- Conversation history
- Unread message count
- Block protection

### 🛡️ Admin
- Ban / Unban users
- Promote users to moderator
- Delete posts, comments & communities
- Dashboard stats

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 21 |
| Framework | Spring Boot 3.5.11 |
| Database | MySQL |
| Authentication | JWT |
| Real-time | WebSocket (STOMP) |
| Storage | AWS S3 |
| AI | Groq (llama-3.3-70b) |
| News | NewsAPI |
| Build Tool | Maven |

## 📁 Project Structure
```
com.sphere
├── admin/          # Admin dashboard
├── ai/             # Groq AI integration
├── auth/           # JWT authentication
├── comment/        # Comments & nested replies
├── common/         # Shared utilities (JWT, config, exception, storage)
├── community/      # Communities
├── dm/             # Direct messages
├── news/           # News feed
├── notifications/  # Real-time WebSocket notifications
├── post/           # Posts & voting
└── user/           # Users, follow, block
```

## ⚙️ Setup

### Prerequisites
- Java 21
- MySQL
- Maven

### Configuration
Create `application-local.yml` with:
```yaml
spring:
  datasource:
    username: your_db_username
    password: your_db_password

jwt:
  secret: your_jwt_secret

groq:
  api:
    key: your_groq_api_key
    model: llama-3.3-70b-versatile

news:
  api:
    key: your_newsapi_key

aws:
  s3:
    access-key: your_aws_access_key
    secret-key: your_aws_secret_key
    bucket-name: your_bucket_name
    region: your_region
```

### Run
```bash
./mvnw spring-boot:run
```

## 📡 API Endpoints

### Auth
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/auth/register | Register new user |
| POST | /api/auth/login | Login |

### Users
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/users/me | Get my profile |
| PUT | /api/users/me | Update profile |
| POST | /api/users/me/avatar | Upload avatar |
| DELETE | /api/users/me/avatar | Remove avatar |
| GET | /api/users/{username} | Get user profile |
| POST | /api/users/{username}/follow | Follow user |
| DELETE | /api/users/{username}/unfollow | Unfollow user |
| GET | /api/users/{username}/followers | Get followers |
| GET | /api/users/{username}/following | Get following |
| POST | /api/users/{username}/block | Block user |
| DELETE | /api/users/{username}/unblock | Unblock user |
| GET | /api/users/blocked | Get blocked users |

### Communities
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/communities | Create community |
| GET | /api/communities | Get all communities |
| GET | /api/communities/{name} | Get community |
| GET | /api/communities/me | Get my communities |
| POST | /api/communities/{id}/join | Join community |
| DELETE | /api/communities/{id}/leave | Leave community |

### Posts
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/posts | Create post |
| GET | /api/posts | Get all posts |
| GET | /api/posts/{id} | Get post |
| GET | /api/posts/community/{id} | Get posts by community |
| GET | /api/posts/me | Get my posts |
| DELETE | /api/posts/{id} | Delete post |
| POST | /api/posts/{id}/vote | Vote on post |

### Comments
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/comments | Create comment |
| GET | /api/comments/post/{id} | Get comments by post |
| DELETE | /api/comments/{id} | Delete comment |

### News
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/news | Top headlines |
| GET | /api/news/category/{category} | News by category |
| GET | /api/news/search?query= | Search news |

### AI
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/ai/summarize | Summarize content |
| POST | /api/ai/moderate | Moderate content |
| POST | /api/ai/insight | Get feed insight |

### DMs
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/dm/send | Send DM |
| GET | /api/dm/conversations | Get conversations |
| GET | /api/dm/conversations/{id}/messages | Get messages |
| DELETE | /api/dm/messages/{id} | Delete message |

### Admin
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/admin/stats | Dashboard stats |
| GET | /api/admin/users | Get all users |
| PUT | /api/admin/users/{id}/ban | Ban user |
| PUT | /api/admin/users/{id}/unban | Unban user |
| PUT | /api/admin/users/{id}/promote | Promote to moderator |
| DELETE | /api/admin/posts/{id} | Delete post |
| DELETE | /api/admin/comments/{id} | Delete comment |
| DELETE | /api/admin/communities/{id} | Delete community |

## 🔌 WebSocket

Connect to `ws://localhost:8080/ws` with STOMP protocol.

Subscribe to:
- `/user/queue/notifications` — Real-time notifications
- `/user/queue/messages` — Real-time DMs

## 👨‍💻 Author

Built with ❤️ by Sumeet