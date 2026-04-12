# Wildcats Lounge - Backend API

Spring Boot REST API for Wildcats Lounge authentication and user management.

## Technology Stack
- **Framework:** Spring Boot 3.5.0
- **Build Tool:** Maven
- **Database:** PostgreSQL (Supabase)
- **Java Version:** 17
- **Architecture:** Layered (Controller -> Service -> Repository)

## Prerequisites
- Java JDK 17+
- Apache Maven 3.6+
- Supabase PostgreSQL database

## Setup & Run

### 1. Configure Environment Variables

Copy `.env.example` to `.env` in the backend folder and provide values:

- `DB_URL`
- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USERNAME`
- `DB_USERNAME_SUFFIX`
- `DB_PASSWORD`
- `JWT_SECRET`

Example DB URL format:

`jdbc:postgresql://db.<your-project-ref>.supabase.co:5432/postgres`

If you use Supabase pooler and get `FATAL: Tenant or user not found`, use:

- `DB_URL=jdbc:postgresql://aws-0-ap-southeast-1.pooler.supabase.com:6543/postgres`
- `DB_USERNAME=postgres`
- `DB_USERNAME_SUFFIX=.<your-project-ref>`

### 2. Install Dependencies

```bash
mvn clean install
```

### 3. Run Application

```bash
mvn spring-boot:run
```

The API will start on **http://localhost:8080**.

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
  "firstname": "Juan",
  "lastname": "Dela Cruz",
  "email": "juan@example.com",
  "password": "password123",
  "studentId": "22-1234-567"
}
```

### User Login
```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "identifier": "juan@example.com",
  "password": "password123"
}
```

`identifier` can be an email address or student ID.

## Testing

Use `test-api.http` in this folder, or test with Postman/cURL.

## Project Structure

```
src/main/java/edu/cit/canadilla/wildcatslounge/
├── controller/          # REST API endpoints
├── service/             # Business logic
├── repository/          # Database access
├── entity/              # JPA entities
├── dto/                 # Data Transfer Objects
└── util/                # Utilities
```

## Database Schema

**Table:** `users`

| Column      | Type         | Constraints                      |
|-------------|--------------|----------------------------------|
| id          | BIGINT       | PRIMARY KEY, AUTO_INCREMENT      |
| firstname   | VARCHAR(100) | NOT NULL                         |
| lastname    | VARCHAR(100) | NOT NULL                         |
| email       | VARCHAR(100) | NOT NULL, UNIQUE                 |
| student_id  | VARCHAR(20)  | UNIQUE, NULLABLE                 |
| password    | VARCHAR(255) | NOT NULL                         |
| role        | VARCHAR(20)  | NOT NULL (`student` or `staff`)  |
| created_at  | TIMESTAMP    | NOT NULL                         |
| updated_at  | TIMESTAMP    | NOT NULL                         |

## Security Features
- BCrypt password hashing
- Input validation (email/password/student ID format)
- Duplicate checks for email and student ID
- JWT access and refresh token generation
- CORS enabled for frontend/mobile access

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
```
