# Customer Management System (CMS)

This is a microservices-based Customer Management System that handles customer onboarding and card management. The system is built using **Java 17**, **Spring Boot**, and **Docker**, and follows a reactive, scalable architecture.

---

## 📦 Services

The system is composed of the following services:

1. **Customer Service** – Manages customer data.
2. **Account Service** – Handles customer bank accounts.
3. **Card Service** – Manages cards linked to accounts.
4. **Gateway Service** – API Gateway that routes requests to underlying services.
5. **Eureka Discovery Service** – Service registry for microservices.

---

## 🚀 Requirements

- Java 17+
- Docker
- Docker Compose

---

## 🛠️ Setup and Running

### 1. Clone the Repository

```bash
git clone https://github.com/Jmumo/CustomerManagementSystem.git
cd CustomerManagementSystem
```

### 2. Build Docker Images

```bash
docker compose build
```

### 3. Start the System

```bash
docker compose up
```

### 4. Stop the System

```bash
docker compose down
```

---

## 🧪 Test Data Workflow

Follow the steps below to manually test the application using `curl`.

### Step 1: Create a Customer

```bash
curl -X POST http://localhost:8080/customers \
  -H "Content-Type: application/json" \
  -d '{
        "firstName": "John",
        "lastName": "Doe",
        "email": "john.doe@example.com",
        "phoneNumber": "0712345678"
      }'
```

✅ *Response will contain the `publicId` of the created customer.*

---

### Step 2: Create an Account using Customer Public ID

```bash
curl -X POST http://localhost:8080/accounts \
  -H "Content-Type: application/json" \
  -d '{
        "customerPublicId": "PASTE_CUSTOMER_PUBLIC_ID_HERE",
        "accountType": "SAVINGS",
        "currency": "KES"
      }'
```

✅ *Response will contain the `publicId` of the created account.*

---

### Step 3: Create a Card using Account Public ID

```bash
curl -X POST http://localhost:8080/cards \
  -H "Content-Type: application/json" \
  -d '{
        "accountPublicId": "PASTE_ACCOUNT_PUBLIC_ID_HERE",
        "cardType": "VISA"
      }'
```

✅ *Response will confirm that the card has been created and linked.*

---



## Individual Service Endpoints are At:

CardsApi

```bash
  http://localhost:7071/swagger-ui/webjars/swagger-ui/index.html#/
```


---

## 🧩 Architecture Overview

- Spring Boot Reactive Services
- Eureka Discovery Service
- Spring Cloud Gateway
- PostgreSQL for persistent storage
- Redis (optional, for caching or rate limiting)
- Docker & Docker Compose for orchestration

---

## 📫 Contact

For questions or contributions, feel free to reach out or open an issue.
