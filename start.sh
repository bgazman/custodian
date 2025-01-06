#!/bin/bash

set -e # Exit on error

# === Constants ===
BASE_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
BACKEND_DIR="$BASE_DIR/backend"
FRONTEND_DIR="$BASE_DIR/frontend"
DOCKER_COMPOSE_FILE="$BASE_DIR/docker-compose.yml"

POSTGRES_HOST="postgres"
POSTGRES_PORT=5432
REDIS_HOST="redis"
REDIS_PORT=6379

SPRING_DATASOURCE_URL="jdbc:postgresql://$POSTGRES_HOST:$POSTGRES_PORT/custodian-db"
SPRING_DATASOURCE_USERNAME="custodian"
SPRING_DATASOURCE_PASSWORD="custodian123"

REACT_ENV_FILE="$FRONTEND_DIR/.env"

# Define OAuth2 and JWT parameters
JWKS_URI="http://backend:8080/.well-known/jwks.json"
ISSUER_URI="http://backend:8080"

# Define Root User Parameters
ROOT_USER_NAME="Root Admin"
ROOT_USER_EMAIL="root@system.local"
ROOT_USER_PASSWORD="rootpass123!"

# Define React CORS and redirect URIs
CORS_ALLOWED_ORIGINS="http://frontend"
REDIRECT_URI="http://frontend/callback"

# === Functions ===

generate_client_credentials() {
    echo "Generating CLIENT_ID..."
    CLIENT_ID=$(uuidgen || echo "default-client-id")
    echo "Generated CLIENT_ID=$CLIENT_ID"
}

update_docker_compose_env() {
    echo "Updating Docker Compose environment variables..."
    cat <<EOF > .env
SPRING_DATASOURCE_URL=$SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME=$SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD=$SPRING_DATASOURCE_PASSWORD
ROOT_USER_NAME=$ROOT_USER_NAME
ROOT_USER_EMAIL=$ROOT_USER_EMAIL
ROOT_USER_PASSWORD=$ROOT_USER_PASSWORD
REDIRECT_URI=$REDIRECT_URI
JWKS_URI=$JWKS_URI
ISSUER_URI=$ISSUER_URI
CORS_ALLOWED_ORIGINS=$CORS_ALLOWED_ORIGINS
CLIENT_ID=$CLIENT_ID
EOF
    echo "Docker Compose .env file updated successfully"
}

update_react_env() {
    echo "Updating React environment variables..."
    cat <<EOF > "$REACT_ENV_FILE"
VITE_BACKEND_URL=$ISSUER_URI
VITE_CLIENT_ID=$CLIENT_ID
VITE_REDIRECT_URI=$REDIRECT_URI
EOF
    echo "React .env file updated successfully at $REACT_ENV_FILE"
}

start_services() {
    echo "Starting all services using Docker Compose..."
    docker-compose -f "$DOCKER_COMPOSE_FILE" up --build -d
    echo "All services have been started. You can access the application at http://localhost"
}

handle_error() {
    echo "An error occurred. Exiting..."
    exit 1
}

trap handle_error ERR

# === Main Execution Flow ===

generate_client_credentials
update_docker_compose_env
update_react_env
start_services
