# IT342 Phase 2 - Mobile Development Submission

## 1. GitHub Link
Repository Name: IT342-Canadilla-WildcatsLounge  
GitHub Link: https://github.com/jakkiii/IT342-Canadilla-WildcatsLounge

## 2. Final Commit
Required Commit Message:  
IT342 Phase 2 - Mobile Development Completed

Current Hash (before final submission commit):  
`1df1bea`

Final Commit Hash:  
`<PASTE_FINAL_COMMIT_HASH_HERE>`

Final Commit Link:  
https://github.com/jakkiii/IT342-Canadilla-WildcatsLounge/commit/<PASTE_FINAL_COMMIT_HASH_HERE>

## 4. Short Summary (1 Page)
### How Registration Works
The mobile application provides a registration screen with input fields for first name, last name, email, optional student ID, password, and confirm password. The app performs client-side validation before sending data to the backend. Validation includes checking required fields, validating email format, checking password minimum length, confirming that password and confirm password match, and validating student ID format when provided.

After passing validation, the app sends a POST request to the backend registration endpoint. The request body uses the backend contract fields: `firstname`, `lastname`, `email`, `password`, and optional `studentId`. The backend checks for duplicate email or student ID, encrypts the password using BCrypt, assigns a role (`student` if student ID is present, otherwise `staff`), stores the user in PostgreSQL (Supabase), and returns an authentication payload with user data and JWT tokens.

On successful response, the mobile app displays a success message and redirects the user to the login screen. If validation fails or the backend returns an error (for example duplicate email), the app displays a clear error message to guide the user.

### How Login Works
The mobile login screen accepts two credentials: identifier and password. The identifier supports either email address or student ID, matching backend behavior. The app validates that fields are not empty, verifies email format when the identifier contains `@`, and checks password length.

When the user submits login, the app calls the backend login endpoint. The backend resolves the identifier (email or student ID), verifies the BCrypt password hash, and returns authenticated user details plus access and refresh JWT tokens. On success, the mobile app stores session data locally using SharedPreferences and navigates to the dashboard screen.

The dashboard displays account information (name, email, student ID, role) and includes logout. On logout, the app clears the local session and navigates to a logout-success screen with a button to return to the login screen.

### API Integration Used
The mobile app integrates with the Spring Boot backend via Retrofit and Gson over HTTP. The base API path is configured as:

- Emulator: `http://10.0.2.2:8080/api/`
- Endpoints used:
  - `POST /api/auth/register`
  - `POST /api/auth/login`

The backend is connected to Supabase PostgreSQL using environment-based datasource configuration (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`) and JWT settings (`JWT_SECRET`). API responses follow a standard envelope containing `success`, `data`, `error`, and `timestamp`, which the mobile app parses to handle success and failure consistently.

Error handling is implemented on both sides. The mobile app catches network and API errors and shows user-friendly messages. The backend validates payloads, returns proper HTTP status codes, and prevents unsafe operations such as duplicate account creation or invalid login attempts.
