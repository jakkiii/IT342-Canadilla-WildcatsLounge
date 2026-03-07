# Short Implementation Summary
## IT342 Phase 1 – WEB User Registration and Login
### Project: Wildcats Lounge | Developer: Canadilla

---

## User Registration

### Registration Fields Used
The registration form collects the following fields:
- **First Name** (`firstname`) – required, minimum 2 characters, maximum 100 characters
- **Last Name** (`lastname`) – required, minimum 2 characters, maximum 100 characters
- **Email** (`email`) – required, must be a valid email format, maximum 100 characters
- **Student ID** (`studentId`) – optional (for staff accounts); must follow format `##-####-###`
- **Password** (`password`) – required, minimum 6 characters
- **Confirm Password** – client-side only; must match the password field before submission

### Validation Process
Validation is performed at two levels:

**Frontend (Next.js)**
- All required fields are checked to ensure they are not empty before the request is sent
- Password length is validated (minimum 6 characters)
- Password and Confirm Password fields are compared and must match

**Backend (Spring Boot)**
- Bean Validation annotations are applied to the `RegisterRequest` DTO:
  - `@NotBlank` – ensures `firstname`, `lastname`, `email`, and `password` are provided
  - `@Email` – ensures the email follows a valid format
  - `@Size` – enforces minimum and maximum length constraints
  - `@Pattern(regexp = "^\\d{2}-\\d{4}-\\d{3}$")` – validates the Student ID format
- If any validation fails, a `400 Bad Request` response is returned with a descriptive error message

### How Duplicate Accounts Are Prevented
Before creating a new account, `UserService` performs two checks:
1. `userRepository.existsByEmail(email)` – throws `RuntimeException("Email already registered")` if the email is taken
2. `userRepository.existsByStudentId(studentId)` – throws `RuntimeException("Student ID already registered")` if the student ID is already in use

The email is normalized to lowercase before comparison to prevent case-sensitive duplicates.

### How Passwords Are Stored Securely
Passwords are **never stored in plain text**. The `PasswordUtil` class uses **BCrypt** (via Spring Security's `BCryptPasswordEncoder`) to hash the password before it is persisted. BCrypt applies a cryptographic salt automatically, making it resistant to rainbow table and brute-force attacks. On login, `passwordEncoder.matches()` compares the plain-text input against the stored hash without reversing it.

---

## User Login

### Login Credentials Used
- **Identifier** – either the registered email address **or** the Student ID (e.g. `22-1234-567`)
- **Password** – the account password

### How the System Verifies Users
1. The `AuthController` receives a `POST /api/auth/login` request with `{ identifier, password }`
2. Bean Validation checks that the `identifier` and `password` fields are not blank
3. `UserService.loginUser()` determines the lookup strategy:
   - If `identifier` contains `"@"` → look up by **email** via `findByEmail()`
   - Otherwise → look up by **student_id** via `findByStudentId()`
4. If no user is found, a generic error *"Invalid credentials"* is returned (prevents user enumeration)
5. `PasswordUtil.verifyPassword()` uses BCrypt to compare the submitted password with the stored hash
6. If the password does not match, the same generic error is returned
7. On success, JWT `accessToken` and `refreshToken` are generated and returned

### What Happens After Successful Login
- The backend returns a response containing: `user` (email, firstname, lastname, studentId, role), `accessToken`, and `refreshToken`
- The frontend stores `user` in `localStorage` under the key `"user"`, and stores the tokens under `"accessToken"` / `"refreshToken"`
- The user is redirected to the `/dashboard` page
- The dashboard reads user data from `localStorage` and displays name, email, student ID, and role
- If `localStorage` has no user data, the user is automatically redirected back to `/login`
- A **Logout** button clears all `localStorage` entries and redirects the user to `/login`

---

## Database Table

The system uses a **PostgreSQL** database hosted on **Supabase**.

| Column       | Type           | Constraints                        |
|--------------|----------------|------------------------------------|
| `id`         | BIGINT         | PRIMARY KEY, AUTO INCREMENT        |
| `firstname`  | VARCHAR(100)   | NOT NULL                           |
| `lastname`   | VARCHAR(100)   | NOT NULL                           |
| `email`      | VARCHAR(100)   | NOT NULL, UNIQUE                   |
| `student_id` | VARCHAR(20)    | UNIQUE, nullable (staff have none) |
| `password`   | VARCHAR        | NOT NULL (BCrypt hash)             |
| `role`       | VARCHAR(20)    | NOT NULL (`student` or `staff`)    |
| `created_at` | TIMESTAMP      | NOT NULL, set on insert            |
| `updated_at` | TIMESTAMP      | set on insert and update           |

The table is automatically created/updated by Hibernate via `spring.jpa.hibernate.ddl-auto=update`.

---

## API Endpoints

| Method | Endpoint               | Description                    | Request Body                                              | Success Response                                              |
|--------|------------------------|--------------------------------|-----------------------------------------------------------|---------------------------------------------------------------|
| GET    | `/api/auth/health`     | Health check                   | None                                                      | `{ success: true, data: "...", error: null, timestamp: "..." }` |
| POST   | `/api/auth/register`   | Register a new user            | `{ email, password, firstname, lastname, studentId? }`   | `201` – `{ success: true, data: { user, accessToken, refreshToken }, error: null, timestamp }` |
| POST   | `/api/auth/login`      | Login by email or student_id   | `{ identifier, password }`                               | `200` – `{ success: true, data: { user, accessToken, refreshToken }, error: null, timestamp }` |

---

## Technology Stack Summary

| Layer        | Technology                                   |
|--------------|----------------------------------------------|
| Backend      | Spring Boot 3.5.0, Maven                     |
| Language     | Java 17                                      |
| Database     | PostgreSQL (Supabase)                        |
| ORM          | Spring Data JPA / Hibernate                  |
| Security     | BCrypt (Spring Security Crypto) + JJWT 0.11.5 |
| Frontend     | Next.js 14, TypeScript, Tailwind CSS         |
| HTTP Client  | Axios                                        |

---

*Group ID: `edu.cit.canadilla` | Artifact ID: `wildcatslounge`*
