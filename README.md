# Hyperativa Test API

This project is a RESTful API built with Spring Boot, designed to manage cards and users. It includes JWT authentication, batch file processing for cards, and is fully containerized with Docker.

## Features

*   **User Management**: Create and update users.
*   **Authentication**: JWT (JSON Web Token) based authentication for secure access.
*   **Card Management**:
    *   Create individual cards.
    *   **Batch Upload**: Upload `.txt` files to create multiple cards at once (optimized with batch processing).
    *   **Encryption**: Card numbers are encrypted in the database using AES and decrypted upon retrieval.
*   **Dockerized**: Easy setup and deployment using Docker Compose.

## Prerequisites

*   Docker & Docker Compose
*   Java 17 (if running locally without Docker)
*   Maven (if running locally without Docker)

## Getting Started

### 1. Run with Docker (Recommended)

The easiest way to run the application is using Docker Compose. This will set up the MySQL database and the Spring Boot application.

1.  Open a terminal in the project root.
2.  Run the following command:

    ```bash
    docker-compose up -d --build
    ```

3.  The application will be available at `http://localhost:8080`.

### 2. Authentication

The API uses JWT for authentication. You need to obtain a token to access protected endpoints (like creating cards).

**Default Admin User:**
A default admin user is created automatically on startup:
*   **Email:** `admin@hyperativa.com`
*   **Password:** `admin123`

**How to get a Token:**

1.  Make a `POST` request to: `http://localhost:8080/users/check-password`
2.  **Body (JSON):**
    ```json
    {
      "email": "admin@hyperativa.com",
      "password": "admin123"
    }
    ```
3.  **Response:** You will receive a JSON containing the `jwtToken`. This token is valid for **2 hours**.

**Using the Token:**
Include the token in the `Authorization` header of your subsequent requests:
*   **Key:** `Authorization`
*   **Value:** `Bearer <YOUR_TOKEN_HERE>`

### 3. Card Management Endpoints

Once authenticated, you can manage cards.

#### Create a Single Card
*   **Endpoint:** `POST /api/cards`
*   **Body (JSON):**
    ```json
    {
      "identifier": "C1",
      "numberInBatch": "000001",
      "cardNumber": "4456897999999999"
    }
    ```

#### Upload Batch File (.txt)
For high-volume data, upload a text file.
*   **Endpoint:** `POST /api/cards/upload`
*   **Body (form-data):**
    *   Key: `file` (Type: File)
    *   Value: Select your `.txt` file.

**File Format Example:**
```text
DESAFIO-HYPERATIVA           20180524LOTE0001000010
C2     4456897999999999
C1     4456897922969999
...
```

### 4. Testing

Unit tests are included, specifically focusing on the `CardService` logic, including batch processing and encryption.

To run tests (if you have Maven installed):
```bash
mvn test
```
(Note: Tests are also run automatically during the Docker build process).

## Postman Collection

A Postman collection is available (if provided in the repo) to help you test the endpoints quickly.

## Notes

*   **Encryption:** Card numbers are stored encrypted in the database for security.
*   **Other Endpoints:** There are other endpoints for user management, but the core flow described above is sufficient for the challenge.
