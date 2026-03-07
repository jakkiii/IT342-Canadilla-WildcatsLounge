# Wildcats Lounge - Backend API

Spring Boot REST API for user authentication and management.

## Technology Stack
- **Framework:** Spring Boot 3.5.0
- **Build Tool:** Maven
- **Database:** MySQL 8.0
- **Java Version:** 17
- **Architecture:** Layered (Controller → Service → Repository)

## Prerequisites
- Java JDK 17+
- Apache Maven 3.6+
- MySQL 8.0+ (running on port 3306)

## Setup & Run

### 1. Configure Database

Edit `src/main/resources/application.properties` if you set a MySQL password:

```properties
spring.datasource.password=YOUR_PASSWORD_HERE
```

### 2. Install Dependencies

```bash
mvn clean install
```

### 3. Run Application

```bash
mvn spring-boot:run
```

The API will start on **http://localhost:8080**

## API Endpoints

### Health Check
```
GET http://localhost:8080/api/auth/health
```

### User Registration
```
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "name": "Juan Dela Cruz",
  "email": "juan@example.com",
  "password": "password123"
}
```

### User Login
```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "juan@example.com",
  "password": "password123"
}
```

## Testing

Use the `test-api.http` file with the REST Client extension in VS Code, or use tools like Postman.

## Project Structure

```
src/main/java/edu/cit/canadilla/wildcatslounge/
├── controller/          # REST API endpoints
│   └── AuthController.java
├── service/             # Business logic
│   └── UserService.java
├── repository/          # Database access
│   └── UserRepository.java
├── entity/              # JPA entities
│   └── User.java
├── dto/                 # Data Transfer Objects
│   ├── RegisterRequest.java
│   ├── LoginRequest.java
│   ├── UserResponse.java
│   └── ApiResponse.java
└── util/                # Utilities
    └── PasswordUtil.java
```

## Database Schema

**Table:** `users`

| Column      | Type         | Constraints           |
|-------------|--------------|------------------------|
| id          | BIGINT       | PRIMARY KEY, AUTO_INCREMENT |
| name        | VARCHAR(100) | NOT NULL              |
| email       | VARCHAR(100) | NOT NULL, UNIQUE      |
| password    | VARCHAR(255) | NOT NULL              |
| created_at  | DATETIME     | NOT NULL              |
| updated_at  | DATETIME     |                       |

## Security Features
- ✅ BCrypt password hashing
- ✅ Email format validation
- ✅ Password strength validation (min 6 characters)
- ✅ Duplicate email prevention
- ✅ CORS enabled for frontend

## Documentation

See the `../docs/` folder for comprehensive guides:
- [TUTORIAL.md](../docs/TUTORIAL.md) - Complete backend tutorial
- [DATABASE_SETUP.md](../docs/DATABASE_SETUP.md) - MySQL setup guide
- [COMPLETE_GUIDE.md](../docs/COMPLETE_GUIDE.md) - Full project guide

## Maven Commands

```bash
# Clean and build
mvn clean install

# Run application
mvn spring-boot:run

# Create JAR file
mvn clean package

# Run tests
mvn test

# Skip tests
mvn clean install -DskipTests
```

## Environment Configuration

Default configuration in `application.properties`:
- Server Port: 8080
- Database: wildcatslounge_db (auto-created)
- MySQL Port: 3306
- Username: root
- Password: (empty)

---

**Backend API is ready to serve requests!** 🚀
