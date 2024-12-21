Spring Boot API Boilerplate
This repository provides a reusable and modular framework for building RESTful APIs in Spring Boot. It includes standardized patterns for handling requests, responses, and errors while ensuring consistency across the application.

Features
Centralized Exception Handling: All exceptions are handled globally, ensuring uniform error responses.
Reusable Base Controller: The ApiController simplifies logging, response handling, and header management.
Standardized Response Structure: All API responses follow a consistent structure with ApiResponse and ApiError.
Status Mapping: Application-level statuses are mapped to appropriate HTTP status codes via StatusMapper.
Project Structure
Folder Organization
bash
Copy code
src/main/java/com/example
│
├── common
│   ├── advice
│   │   └── GlobalExceptionHandler.java       # Handles global exceptions
│   ├── controller
│   │   └── ApiController.java               # Base controller for shared logic
│   ├── dto
│   │   ├── ApiRequest.java                  # Wrapper for incoming requests
│   │   ├── ApiResponse.java                 # Standardized API response object
│   │   ├── ApiError.java                    # Detailed error representation
│   │   └── StatusMapper.java                # Maps statuses to HTTP codes
│
├── user
│   ├── controller
│   │   └── UserController.java              # Handles user-related endpoints
│   ├── service
│   │   ├── UserService.java                 # Service interface
│   │   └── UserServiceImpl.java             # Service implementation
│   ├── repository
│   │   └── UserRepository.java              # JPA repository for User entity
│   └── model
│       └── User.java                        # User entity class
Core Components
common.controller.ApiController
An abstract controller that centralizes:

Logging: Logs requests with trace IDs.
Response Handling: Converts ApiResponse to ResponseEntity.
Traceability Headers: Adds X-Trace-Id and X-Response-Id to responses.
common.dto
ApiRequest<T>: Encapsulates incoming request payloads.

ApiResponse<T>: Standard response structure with:

status: "success" or "error".
message: Descriptive response message.
data: Payload for successful responses.
error: Error details for failed responses.
ApiError: Provides detailed error information with:

code: A unique error code.
message: Human-readable error description.
details: Additional context (optional).
StatusMapper: Maps application statuses ("success", "error", etc.) to HTTP status codes.

common.advice.GlobalExceptionHandler
A centralized exception handler that converts exceptions into consistent ApiResponse.error objects.

How to Use
1. API Response Standard
   All endpoints return an ApiResponse:

Success Example:

json
Copy code
{
"status": "success",
"message": "User retrieved successfully",
"data": {
"id": 1,
"name": "John Doe",
"email": "john.doe@example.com"
}
}
Error Example:

json
Copy code
{
"status": "error",
"message": "User not found",
"error": {
"code": "USER_NOT_FOUND",
"message": "User with ID 999 not found"
}
}
2. User Module Example
   Service Interface
   Defines operations for user management:

java
Copy code
public interface UserService {
ApiResponse<List<User>> getAllUsers();
ApiResponse<User> findById(Long id);
ApiResponse<User> save(User user);
ApiResponse<User> update(Long id, User user);
ApiResponse<Void> delete(Long id);
}
Service Implementation
Implements user-related business logic:

java
Copy code
@Service
@Transactional
public class UserServiceImpl implements UserService {
@Autowired
private UserRepository userRepository;

    @Override
    public ApiResponse<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return ApiResponse.error("NO_USERS_FOUND", "No users are currently registered.");
        }
        return ApiResponse.success(users, "Users retrieved successfully.");
    }

    // Other methods follow a similar pattern
}
Controller
Handles HTTP requests and uses ApiController for response management:

java
Copy code
@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController extends ApiController {
@Autowired
private UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        logRequest("GET", "/api/users");
        return handleApiResponse(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        logRequest("GET", "/api/users/" + id);
        return handleApiResponse(userService.findById(id));
    }
}
3. Global Exception Handling
   The GlobalExceptionHandler ensures all exceptions return structured responses:

Example: Unauthorized Exception
java
Copy code
@ExceptionHandler(UnauthorizedException.class)
public ResponseEntity<ApiResponse<Object>> handleUnauthorized(UnauthorizedException ex) {
return ResponseEntity
.status(StatusMapper.toHttpStatus("unauthorized"))
.body(ApiResponse.error(
"UNAUTHORIZED",
"Authentication failed",
ApiError.of("UNAUTHORIZED", ex.getMessage())
));
}
Response:

json
Copy code
{
"status": "error",
"message": "Authentication failed",
"error": {
"code": "UNAUTHORIZED",
"message": "Invalid credentials provided"
}
}
Getting Started
Prerequisites
Java 17+
Maven 3.8+
PostgreSQL (or other relational DB)
Setup
Clone the repository:

bash
Copy code
git clone https://github.com/your-repo/spring-api-boilerplate.git
cd spring-api-boilerplate
Configure your database in application.properties:

properties
Copy code
spring.datasource.url=jdbc:postgresql://localhost:5432/your_db
spring.datasource.username=your_username
spring.datasource.password=your_password
Run the application:

bash
Copy code
mvn spring-boot:run
Access the API at http://localhost:8080/api.

Contributing
Feel free to fork the project and submit pull requests. Contributions are welcome!

License
This project is licensed under the MIT License. See the LICENSE file for details.

Let me know if you'd like to add more sections or further refine this README! 🚀