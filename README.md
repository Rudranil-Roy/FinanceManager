# Personal Finance Manager API

This backend api service allows users to securely track their income, expenses, and savings goals, manage custom categories, and generate detailed financial reports.

## 🚀 Tech Stack

* **Language:** Java 21
* **Framework:** Spring Boot 3.5.14
* **Security:** Spring Security 6 (Session-based with environment-adaptive secure cookies)
* **Database:** H2 In-Memory Database (Ephemeral)
* **Testing:** JUnit 5, Mockito
* **Build Tool:** Maven

---

## 🛠️ Setup Instructions 

### Prerequisites
* Java 21 installed
* Maven installed
* Git

### 1. Clone the Repository
Open your terminal and clone the repository:
```bash
git clone https://github.com/Rudranil-Roy/FinanceManager.git
cd FinanceManager
```

### 2. Configure the Environment
The application uses a unified `application.yml` file. By default, it runs perfectly on local environments. You can override properties using environment variables:
* `SECURE_COOKIES`: Set to `true` in production, defaults to `false` locally.
* `FRONTEND_URL`: Set to your deployed frontend domain, defaults to `http://localhost:5173`.

### 3. Build and Run the Application
Compile the project and start the Spring Boot server:
```bash
mvn clean install
mvn spring-boot:run
```
The server will start on `http://localhost:8080`.

### 🐳 Docker Support
You can also package and run the application in a Docker container using the provided `Dockerfile`:

1. **Build the JAR file**:
   ```bash
   ./mvnw clean package -DskipTests
   ```
2. **Build the Docker Image**:
   ```bash
   docker build -t finance-manager .
   ```
3. **Run the Docker Container**:
   ```bash
   docker run -p 8080:8080 -e SECURE_COOKIES=false finance-manager
   ```
   The application will be accessible at `http://localhost:8080`.

---

## 🧪 Testing

The application includes a comprehensive test suite aiming for >80% code coverage, including edge-case entity coverage and mocked repository interactions.

### Run Unit and Service Tests
```bash
mvn test
```



---

## 📖 API Documentation & Endpoints

### Authentication Flow
This API uses **Session-Based Authentication**.
1. Call `POST /api/auth/register` or `POST /api/auth/login`.
2. The server sets a `JSESSIONID` cookie in the response.
3. Your client (Browser, Postman, curl) must include this cookie in the headers for all subsequent requests to access protected endpoints.

### Endpoints Overview

#### 1. Authentication
* `POST /api/auth/register` - Create a new user account.
* `POST /api/auth/login` - Authenticate and receive session cookie.
* `POST /api/auth/logout` - Invalidate the current session.

#### 2. Transactions
* `POST /api/transactions` - Create a new income/expense record.
* `GET /api/transactions?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD&categoryId=ID` - Get filtered transactions.
* `PUT /api/transactions/{id}` - Update amount and description (date is immutable).
* `DELETE /api/transactions/{id}` - Delete a transaction.

#### 3. Categories
* `GET /api/categories` - Fetch all immutable default categories + user's custom categories.
* `POST /api/categories` - Create a new custom category.
* `DELETE /api/categories/{name}` - Delete a custom category (fails if referenced by transactions).

#### 4. Savings Goals
* `POST /api/goals` - Create a savings target. Defaults `startDate` to today if omitted.
* `GET /api/goals` - Get all goals with dynamically calculated progress.
* `GET /api/goals/{id}` - Get a specific goal's details.
* `PUT /api/goals/{id}` - Update target amount or target date.
* `DELETE /api/goals/{id}` - Delete a goal.

#### 5. Reports and Analytics
* `GET /api/reports/monthly/{year}/{month}` - Total income/expenses by category and net savings for the month.
* `GET /api/reports/yearly/{year}` - Aggregated financial overview for the entire year.
