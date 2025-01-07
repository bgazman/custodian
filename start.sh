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

# Define base URL for the application
APP_BASE_URL="http://localhost:8080"

# Generate CLIENT_ID first as it's needed for other variables
generate_client_credentials() {
    echo "Generating CLIENT_ID..."
    CLIENT_ID=$(uuidgen || echo "default-client-id")
    echo "Generated CLIENT_ID=$CLIENT_ID"

    # Set AUDIENCE based on CLIENT_ID
    AUDIENCE="$CLIENT_ID"
    echo "Set AUDIENCE=$AUDIENCE"
}

setup_oauth_parameters() {
    JWKS_URI="$APP_BASE_URL/.well-known/jwks.json"
    ISSUER_URI="$APP_BASE_URL"
    REDIRECT_URI="$APP_BASE_URL/oauth-callback"
    CORS_ALLOWED_ORIGINS="$APP_BASE_URL"
}




# Define Root User Parameters
ROOT_USER_NAME="Root Admin"
ROOT_USER_EMAIL="root@system.local"
ROOT_USER_PASSWORD="rootpass123!"

update_docker_compose_env() {
    echo "Updating Docker Compose environment variables..."
    cat <<EOF > .env
SPRING_DATA_REDIS_HOST=$REDIS_HOST
SPRING_DATA_REDIS_PORT=$REDIS_PORT    
SPRING_DATASOURCE_URL=$SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME=$SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD=$SPRING_DATASOURCE_PASSWORD
ROOT_USER_NAME=$ROOT_USER_NAME
ROOT_USER_EMAIL=$ROOT_USER_EMAIL
ROOT_USER_PASSWORD=$ROOT_USER_PASSWORD
REDIRECT_URI=$REDIRECT_URI
JWKS_URI=$JWKS_URI
ISSUER_URI=$ISSUER_URI
AUDIENCE=$AUDIENCE
CORS_ALLOWED_ORIGINS=$CORS_ALLOWED_ORIGINS
CLIENT_ID=$CLIENT_ID
EOF
    echo "Docker Compose .env file updated successfully"
}

update_react_env() {
    echo "Updating React environment variables..."

    # Generate .env file for development
    cat <<EOF > "$FRONTEND_DIR/.env"
VITE_BACKEND_URL=$APP_BASE_URL
VITE_CLIENT_ID=$CLIENT_ID
VITE_REDIRECT_URI=$APP_BASE_URL/oauth-callback
EOF
    echo "React development env file updated successfully at $FRONTEND_DIR/.env"

    # Generate .env.production file for production
    cat <<EOF > "$FRONTEND_DIR/.env.production"
VITE_BACKEND_URL=$APP_BASE_URL
VITE_CLIENT_ID=$CLIENT_ID
VITE_REDIRECT_URI=$APP_BASE_URL/oauth-callback
EOF
    echo "React production env file updated successfully at $FRONTEND_DIR/.env.production"
}


start_services() {
    echo "Starting all services using Docker Compose..."
    docker-compose -f "$DOCKER_COMPOSE_FILE" up --build -d
    echo "All services have been started. You can access the application at $APP_BASE_URL"
}

handle_error() {
    echo "An error occurred. Exiting..."
    exit 1
}

trap handle_error ERR

# === Main Execution Flow ===

generate_client_credentials
setup_oauth_parameters
update_docker_compose_env
update_react_env
start_services