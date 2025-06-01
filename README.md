# ☁️ CloudNova Task Management API

A well-structured, production-ready **Task and User Management REST API** built with **Spring Boot**. This project demonstrates clean architectural principles using **Spring MVC**, **IoC**, **Dependency Injection**, and is fully **Dockerized** for ease of deployment. It includes robust **logging**, **DTO-based responses**, and is fully documented with **Swagger/OpenAPI**.

---

## 📌 Features

### 👥 User Management
- Create, retrieve, update, delete users
- Search users by username, email, or first name
- Check if a user exists
- Retrieve user statistics

### 📃 Task Management
- CRUD operations for tasks
- Cascade deletion of tasks when a user is deleted

### 🏗️ Architecture & Design
- Layered architecture: `controller`, `service`, `repository`, `DTO`, `exception`, `config`, `model`
- Clean separation of concerns and reusable service logic
- Rich **logging** with `INFO` and `DEBUG` levels using **Slf4j**

### 🔷 Component Diagram
```bash
https://www.mermaidchart.com/raw/ecf2eb8e-86e0-45d1-8d61-aa7f499b9bcf?theme=light&version=v0.1&format=svg
```

### 🔍 API Documentation
- Fully integrated with **Swagger UI** via `springdoc-openapi`
- Accessible at [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- Includes:
    - Endpoint metadata
    - Example requests/responses
    - Grouped tags and schema annotations
    - Documented error responses

### 🚨 Error Handling
- Custom error response structure using DTOs
- Status-specific messages for client and server errors

### ✔️ Validation
- Input validation using `@Valid` and `jakarta.validation` annotations

### 📦Containerization
- Fully **Dockerized** for local or production deployment

---

### 🛡️ Tech Stack
- Java 21
- Spring Boot 3
- Spring Web MVC
- In Memory Storage (ConcurrentHashMap)
- Lombok
- Swagger / OpenAPI 3
- Docker

---

### 📁 Project Structure

```bash
com.taskmgt.CloudNova
├── controller       # REST controllers
├── service          # Business logic
├── repository       # JPA repositories
├── model            # Entity models
├── DTO              # Data transfer objects
├── exception        # Custom exceptions & handlers
├── config           # Configuration (e.g. Swagger)

```

## 🚀 Getting Started

### 🧰 Prerequisites

- Java 21+
- Maven 3.8+
- Docker (optional for containerization)
- Postman or Curl (for testing)

---

### 🛠️ Build and Run Locally

```bash
# Clone the repository
git clone https://github.com/Ganza-Kevin-Murinda/CloudNova-Task-Management-System.git
cd CloudNova-Task-Management-System

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```
---
## 📒Summary Notes

```bash
https://www.notion.so/1ff9d11ac5d58078b095e4737a18baeb?v=1ff9d11ac5d58076b127000caea35997&source=copy_link
```

## 🧑‍💻 Author
©️Ganza Kevin Murinda