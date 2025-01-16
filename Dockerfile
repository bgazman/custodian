FROM node:18-alpine AS frontend-build
WORKDIR /app
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ ./
ARG VITE_BACKEND_URL
ARG VITE_CLIENT_ID
ARG VITE_REDIRECT_URI
RUN npm run build

# Build Spring Boot app
FROM maven:3.8.4-openjdk-17 AS backend-build
WORKDIR /app
COPY backend/pom.xml ./
COPY backend/src ./src
# Create static folder and copy React build into it
RUN mkdir -p src/main/resources/static
COPY --from=frontend-build /app/dist src/main/resources/static
RUN mvn clean package -DskipTests

# Final image 
FROM openjdk:17-slim
COPY --from=backend-build /app/target/*.jar app.jar


# Expose port and set entrypoint
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
