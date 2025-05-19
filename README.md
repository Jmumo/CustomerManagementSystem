# Microservices Project: Card Service, Customer Service, and Account Service

This project comprises a set of microservices built with Java 17 (or above) and designed to be run using Docker and Docker Compose. It includes the following services:

* **Card Service:** Manages card-related operations.
* **Customer Service:** Manages customer-related information.
* **Account Service:** Manages account-related operations.
* **Gateway:** API Gateway for routing requests to the services.
* **Eureka Discovery:** Service discovery for the microservices.

## Technologies Used

* Java 17+
* Docker
* Docker Compose

## Prerequisites

* Docker installed on your system.
* Docker Compose installed on your system.

## Getting Started

### 1.  Clone the Repository

    ```bash
    git clone <your_repository_url>
    cd <your_repository_directory>
    ```

### 2.  Build the Docker Images

    ```bash
    docker compose build
    ```

### 3.  Run the Services

    ```bash
    docker compose up
    ```

### 4.  Stop the Services

    ```bash
    docker compose down
    ```

## Services

### Card Service

* **Description:** Manages card information.
* **Key Functionalities:**
    * Card creation
    * Card retrieval
    * Card updates
    * Card deletion

### Customer Service

* **Description:** Manages customer information.
* **Key Functionalities:**
    * Customer creation
    * Customer retrieval
    * Customer updates
    * Customer deletion

### Account Service

* **Description:** Manages bank account information.
* **Key Functionalities:**
    * Account creation
    * Account retrieval
    * Account updates
    * Account deletion
    * Linking accounts to customers

### Gateway

* **Description:** API Gateway.  Routes requests to the appropriate backend services (Card, Customer, and Account).  Provides a single entry point for clients.
* **Key Functionalities**
    * Request routing
    * Load balancing
    * Rate limiting
    * Authentication and authorization

### Eureka Discovery

* **Description:** Provides service registration and discovery.  Allows services to discover each other dynamically.
* **Key Functionalities**
    * Service registration
    * Service discovery

## Testing

### Steps to Generate Test Data

The following steps describe how to create test data to verify the relationships between customers, accounts, and cards.

1.  **Create a Customer:**
    * Send a request to the Customer Service to create a new customer.
    * Retrieve the `publicId` of the newly created customer from the response.

2.  **Create an Account for the Customer:**
    * Send a request to the Account Service to create a new account.
    * Use the `publicId` of the customer obtained in the previous step to link the account to the customer.
    * Retrieve the `publicId` of the newly created account.

3.  **Create a Card for the Account:**
    * Send a request to the Card Service to create a new card.
    * Use the `publicId` of the account obtained in the previous step to link the card to the account.

### Sequence Diagram

```mermaid
sequenceDiagram
    participant Client
    participant Gateway
    participant CustomerService
    participant AccountService
    participant CardService

    Client->>Gateway: Create Customer Request
    Gateway->>CustomerService: Create Customer
    CustomerService-->>Gateway: Customer Created Response (with customerPublicId)
    Gateway-->>Client: Customer Created Response

    Client->>Gateway: Create Account Request (with customerPublicId)
    Gateway->>AccountService: Create Account
    AccountService-->>Gateway: Account Created Response (with accountPublicId)
    Gateway-->>Client: Account Created Response

    Client->>Gateway: Create Card Request (with accountPublicId)
    Gateway->>CardService: Create Card
    CardService-->>Gateway: Card Created Response
    Gateway-->>Client: Card Created Response
Important NotesEnsure that Docker is running before executing the docker compose commands.The services will be accessible at their respective ports as defined in the `docker-compose
