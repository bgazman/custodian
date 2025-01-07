Custodian IDP
Custodian is an identity provider (IDP) application inspired by solutions like Keycloak and Authentik. It provides robust identity and access management (IAM) capabilities, including OAuth 2.0, user authentication, role-based access control, and policy-based permissions. Custodian is designed to be easy to set up and run, whether for local development or production deployment.

Features
OAuth 2.0 Authorization Server
Role-Based Access Control (RBAC)
Multi-Factor Authentication (MFA) Support
User and Group Management
JSON Web Token (JWT) support
Policy-based permission management
Redis for session and token caching
PostgreSQL for data persistence
Getting Started
Prerequisites
Ensure the following are installed on your machine:

Java 17+
Node.js 16+ and npm
Docker and Docker Compose
How to Run
The entire application can be started using the provided start.sh script. Follow these steps:

Clone the Repository

bash
Copy code
git clone [repository-url]
cd custodian
Run the Application Execute the start.sh script to build and start the application:

bash
Copy code
./start.sh
What start.sh Does:

Generates a unique CLIENT_ID and sets it as the audience for OAuth.
Updates environment variables for both the backend and React frontend.
Builds the React frontend and copies the static files to the backend.
Configures Docker Compose to manage PostgreSQL, Redis, and the application container.
Starts all services using Docker Compose.
Access the Application Once the script completes, the application will be running at:

Frontend URL: http://localhost:8080
Environment Variables
The following environment variables are configured automatically by start.sh:

Variable	Description
SPRING_DATASOURCE_URL	PostgreSQL connection string
SPRING_DATASOURCE_USERNAME	Database username
SPRING_DATASOURCE_PASSWORD	Database password
SPRING_DATA_REDIS_HOST	Redis hostname
SPRING_DATA_REDIS_PORT	Redis port
CLIENT_ID	OAuth 2.0 client ID
AUDIENCE	OAuth 2.0 audience
VITE_BACKEND_URL	API backend URL for the React frontend
VITE_REDIRECT_URI	Redirect URI for OAuth flows
ROOT_USER_NAME	Default admin username
ROOT_USER_EMAIL	Default admin email
ROOT_USER_PASSWORD	Default admin password
Architecture
Custodian uses a microservice-inspired architecture:

Backend:
A Spring Boot application handling OAuth 2.0, user authentication, and API requests.
Frontend:
A React application for user-facing interfaces (e.g., login pages, dashboards).
Redis:
Used for caching sessions and tokens.
PostgreSQL:
A robust relational database for persisting user, role, and policy data.
Database Schema
The PostgreSQL database schema includes:

Users: Basic user details and MFA configurations.
Roles and Permissions: Support for RBAC.
Groups: User groupings with role assignments.
Policies: JSON-based policies for fine-grained access control.
OAuth Clients: Configuration for registered OAuth 2.0 clients.
Testing the Application
You can test the application locally using the following steps:

API Endpoints: Use tools like curl or Postman to test key endpoints:

Login: POST /oauth/login
Token Exchange: POST /oauth/token
User Info: GET /api/users/me
React Frontend: Open the app in your browser: http://localhost:8080

Database Verification: Verify that users and roles are being created in PostgreSQL:

bash
Copy code
docker exec -it custodian-db psql -U custodian -d custodian-db
Troubleshooting
Redis Connection Issues:

Ensure Redis is running in the container (custodian-redis).
Check the connection string in the .env file.
Database Initialization Fails:

Verify the SQL scripts in backend/src/main/resources.
Check PostgreSQL logs for errors:
bash
Copy code
docker logs custodian-db
Frontend Issues:

Ensure VITE_BACKEND_URL and VITE_CLIENT_ID are correctly set in frontend/.env.
License
MIT License

