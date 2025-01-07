# Custodian IDP

Custodian is an identity provider (IDP) application inspired by solutions like Keycloak and Authentik. It provides robust identity and access management (IAM) capabilities, including OAuth 2.0, user authentication, role-based access control, and policy-based permissions. Custodian is designed to be easy to set up and run, whether for local development or production deployment.

## Features

- OAuth 2.0 Authorization Server
- Role-Based Access Control (RBAC)
- Multi-Factor Authentication (MFA) Support
- User and Group Management
- JSON Web Token (JWT) support
- Policy-based permission management
- Redis for session and token caching
- PostgreSQL for data persistence

## Getting Started

### Prerequisites

Ensure the following are installed on your machine:

- **Java 17+**
- **Node.js 16+ and npm**
- **Docker** and **Docker Compose**

### How to Run

The entire application can be started using the provided `start.sh` script. Follow these steps:

1. **Clone the Repository**
   ```bash
   git clone [repository-url]
   cd custodian
   ```

2. **Run the Application**
   Execute the `start.sh` script to build and start the application:
   ```bash
   ./start.sh
   ```

   #### What `start.sh` Does:
   - Generates a unique `CLIENT_ID` and sets it as the audience for OAuth
   - Updates environment variables for both the backend and React frontend
   - Builds the React frontend and copies the static files to the backend
   - Configures Docker Compose to manage PostgreSQL, Redis, and the application container
   - Starts all services using Docker Compose

3. **Access the Application**
   Once the script completes, the application will be running at:
   - **Frontend URL**: http://localhost:8080

## Environment Variables

The following environment variables are configured automatically by `start.sh`:

| Variable | Description |
|----------|-------------|
| `SPRING_DATASOURCE_URL` | PostgreSQL connection string |
| `SPRING_DATASOURCE_USERNAME` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | Database password |
| `SPRING_DATA_REDIS_HOST` | Redis hostname |
| `SPRING_DATA_REDIS_PORT` | Redis port |
| `CLIENT_ID` | OAuth 2.0 client ID |
| `AUDIENCE` | OAuth 2.0 audience |
| `VITE_BACKEND_URL` | API backend URL for the React frontend |
| `VITE_REDIRECT_URI` | Redirect URI for OAuth flows |
| `ROOT_USER_NAME` | Default admin username |
| `ROOT_USER_EMAIL` | Default admin email |
| `ROOT_USER_PASSWORD` | Default admin password |

## Architecture

Custodian uses a microservice-inspired architecture with dual security configurations:

1. **Backend**:
   - A Spring Boot application with two security filter chains:
     - OAuth 2.0 IDP Chain (`/oauth/**`, `/login`, etc.)
     - Resource Server Chain (`/api/**`)
   - Thymeleaf templates for IDP pages (login, forgot-password, reset-password)
   - System initialization with predefined roles, permissions, and policies

2. **Admin Dashboard**:
   - React application for system administration
   - Protected by OAuth 2.0 authentication
   - Automatically registered as the default OAuth client during initialization

3. **Redis**:
   - Used for caching sessions and tokens

4. **PostgreSQL**:
   - A robust relational database for persisting user, role, and policy data

### Security Configuration

The application implements two separate security filter chains:

1. **OAuth/IDP Security Chain** (Order 1):
   ```java
   // Handles authentication and OAuth endpoints
   .securityMatcher("/oauth/**", "/login", "/.well-known/**", "/client/register", "/mfa/**", "/forgot-password/**")
   ```
   - Manages OAuth 2.0 endpoints
   - Handles login pages and MFA
   - Processes password reset flows

2. **Resource Server Chain** (Order 2):
   ```java
   // Protects API endpoints
   .securityMatcher("/api/**")
   .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
   ```
   - Protects API endpoints
   - Validates JWT tokens
   - Implements CORS configuration

### System Initialization

During startup, the system automatically initializes with:

1. **Default Roles**:
   - SUPER_ADMIN: Complete system access
   - USER_MANAGER: Manage users and roles
   - AUDIT_VIEWER: View audit logs

2. **Default Permissions**:
   - USER_READ: Read user information
   - USER_WRITE: Modify user information
   - AUDIT_READ: View audit logs

3. **System Policies**:
   - FullAccessPolicy: Grants all permissions (assigned to SUPER_ADMIN)

4. **Root User**:
   - Created with SUPER_ADMIN role
   - Credentials configurable via environment variables

## Database Schema

The PostgreSQL database schema includes:

- **Users**: Basic user details and MFA configurations
- **Roles and Permissions**: Support for RBAC
- **Groups**: User groupings with role assignments
- **Policies**: JSON-based policies for fine-grained access control
- **OAuth Clients**: Configuration for registered OAuth 2.0 clients

## Testing the Application

You can test the application locally using the following steps:

1. **API Endpoints**: Use tools like `curl` or Postman to test key endpoints:
   - **Login**: `POST /oauth/login`
   - **Token Exchange**: `POST /oauth/token`
   - **User Info**: `GET /api/users/me`

2. **Admin Dashboard**: Access the React admin interface at http://localhost:8080/admin
   **IDP Pages**: Access the Thymeleaf-based IDP pages at:
   - Login: http://localhost:8080/login
   - Forgot Password: http://localhost:8080/forgot-password
   - Reset Password: http://localhost:8080/reset-password

3. **Database Verification**: Verify that users and roles are being created in PostgreSQL:
   ```bash
   docker exec -it custodian-db psql -U custodian -d custodian-db
   ```

## Troubleshooting

1. **Redis Connection Issues**:
   - Ensure Redis is running in the container (`custodian-redis`)
   - Check the connection string in the `.env` file

2. **Database Initialization Fails**:
   - Verify the SQL scripts in `backend/src/main/resources`
   - Check PostgreSQL logs for errors:
     ```bash
     docker logs custodian-db
     ```

3. **Frontend Issues**:
   - Ensure `VITE_BACKEND_URL` and `VITE_CLIENT_ID` are correctly set in `frontend/.env`

## License

MIT License