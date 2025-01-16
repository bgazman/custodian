#!/bin/bash

set -e # Exit on error

# === Constants ===
BASE_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
DOCKER_COMPOSE_FILE="$BASE_DIR/docker-compose.yml"
FRONTEND_DIR="$BASE_DIR/frontend"
 
POSTGRES_HOST="postgres"
POSTGRES_PORT=5432
REDIS_HOST="redis"
REDIS_PORT=6379

SPRING_DATASOURCE_URL="jdbc:postgresql://$POSTGRES_HOST:$POSTGRES_PORT/custodian-db"
SPRING_DATASOURCE_USERNAME="custodian"
SPRING_DATASOURCE_PASSWORD="custodian123"

APP_BASE_URL="http://localhost:8080"

# Define Root User Parameters
ROOT_USER_NAME="Root Admin"
ROOT_USER_EMAIL="root@system.local"
ROOT_USER_PASSWORD="rootpass123!"

# === Functions ===

check_dependencies() {
    echo "Checking for required dependencies..."

    if ! command -v docker-compose &>/dev/null; then
        echo "ERROR: docker-compose is not installed. Please install it and try again."
        exit 1
    fi

    if ! command -v uuidgen &>/dev/null; then
        echo "ERROR: uuidgen is not installed. Please install it and try again."
        exit 1
    fi

    echo "All required dependencies are installed."
}

generate_client_credentials() {
    echo "Generating CLIENT_ID..."
    CLIENT_ID=$(uuidgen || echo "default-client-id")
    echo "Generated CLIENT_ID=$CLIENT_ID"

    AUDIENCE="$CLIENT_ID"
    echo "Set AUDIENCE=$AUDIENCE"
}

setup_oauth_parameters() {
    JWKS_URI="$APP_BASE_URL/.well-known/jwks.json"
    ISSUER_URI="$APP_BASE_URL"
    REDIRECT_URI="$APP_BASE_URL/oauth-callback"
    CORS_ALLOWED_ORIGINS="http://localhost:8080"
}

update_docker_compose_env() {
    echo "Updating Docker Compose environment variables..."
    cat <<EOF > .env
VITE_BACKEND_URL=$APP_BASE_URL
VITE_CLIENT_ID=$CLIENT_ID
VITE_REDIRECT_URI=$REDIRECT_URI
SPRING_DATA_REDIS_HOST=$REDIS_HOST
SPRING_DATA_REDIS_PORT=$REDIS_PORT
SPRING_DATASOURCE_URL=$SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME=$SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD=$SPRING_DATASOURCE_PASSWORD
APP_ROOT_USER_NAME=$ROOT_USER_NAME
APP_ROOT_USER_EMAIL=$ROOT_USER_EMAIL
APP_ROOT_USER_PASSWORD=$ROOT_USER_PASSWORD
APP_REDIRECT_URIS=$REDIRECT_URI
APP_CLIENT_ID=$CLIENT_ID
APP_BASE_URL=$APP_BASE_URL
SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI=$JWKS_URI
SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI=$ISSUER_URI
SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_AUDIENCE=$AUDIENCE
CORS_ALLOWED_ORIGINS=$CORS_ALLOWED_ORIGINS
EOF
    echo "Docker Compose .env file updated successfully."
    cat .env
}



cleanup() {
    echo "Stopping and removing all Docker containers, networks, and volumes..."
    docker-compose -f "$DOCKER_COMPOSE_FILE" down -v
    echo "Cleanup complete."
}

start_services() {
    if [[ "$1" == "cleanup" ]]; then
        cleanup
    fi

    echo "Starting all services using Docker Compose..."
    docker-compose -f "$DOCKER_COMPOSE_FILE" up --build -d
    echo "All services have been started. Access the app at $APP_BASE_URL"
}

handle_error() {
    echo "An error occurred in the script."
    echo "Failed at command: $BASH_COMMAND"
    echo "Error occurred at line: $LINENO"
    exit 1
}

# === Main Execution Flow ===

trap handle_error ERR
check_dependencies
generate_client_credentials
setup_oauth_parameters
update_docker_compose_env
start_services "$1"
