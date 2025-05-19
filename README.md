# Payment System Microservices

This project is a microservices-based payment system built with Spring Boot and Java, comprising four services: **CardService**, **Customer and Accounts Service**, **Gateway**, and **Eureka Discovery**. These services work together to manage customers, accounts, and cards in a reactive architecture using Spring WebFlux.

## Table of Contents

- [Project Overview](#project-overview)
- [Services](#services)
- [Technologies](#technologies)
- [Prerequisites](#prerequisites)
- [Setup and Running with Docker](#setup-and-running-with-docker)
- [Test Data Setup](#test-data-setup)
- [API Endpoints](#api-endpoints)
- [Contributing](#contributing)
- [License](#license)

## Project Overview

This project implements a payment system using a microservices architecture. Each service is independently deployable and communicates via REST APIs. The services are registered with Eureka for service discovery, and the Gateway routes requests to the appropriate service.

- **CardService**: Manages card-related operations, such as creating and retrieving cards linked to accounts.
- **Customer and Accounts Service**: Handles customer creation, account management, and linking accounts to customers.
- **Gateway**: Acts as an API gateway, routing incoming requests to the appropriate microservice.
- **Eureka Discovery**: Provides service discovery, enabling services to register and locate each other dynamically.

## Services

### 1. CardService
- **Functionality**: Manages card entities (e.g., credit/debit cards) and supports operations like creating cards and searching cards with pagination.
- **Dependencies**: Requires an account's public ID to associate cards with accounts.

### 2. Customer and Accounts Service
- **Functionality**: Manages customer profiles, account creation, and linking accounts to customers. Supports searching customers with pagination.
- **Dependencies**: Links accounts to customers using the customer's public ID.

### 3. Gateway
- **Functionality**: Serves as the entry point for all API requests, routing them to the appropriate microservice based on the URL path.
- **Dependencies**: Integrates with Eureka for service discovery.

### 4. Eureka Discovery
- **Functionality**: Provides a service registry for dynamic discovery of microservices, allowing services to find and communicate with each other.
- **Dependencies**: Used by all services for registration and discovery.

## Technologies

- **Java**: Version 17 or higher
- **Spring Boot**: Version 3.4.5 (with WebFlux for reactive programming)
- **Spring Cloud**: Eureka for service discovery and Gateway for API routing
- **Docker**: For containerization of services
- **Docker Compose**: For orchestration of multi-container setup
- **PostgreSQL**: Database for Customer and Accounts Service and CardService
- **MapStruct**: For mapping between entities and DTOs
- **Lombok**: To reduce boilerplate code
- **Maven**: Dependency management and build tool

## Prerequisites

Before running the project, ensure you have the following installed:

- **Java 17 or higher**: [Download and install](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- **Docker**: [Install Docker](https://docs.docker.com/get-docker/)
- **Docker Compose**: [Install Docker Compose](https://docs.docker.com/compose/install/)
- **Maven**: [Install Maven](https://maven.apache.org/install.html) (optional, if building without Docker)
- A running PostgreSQL instance or use the one provided in the Docker Compose setup

## Setup and Running with Docker

Follow these steps to set up and run the services using Docker and Docker Compose:

1. **Clone the Repository**:
   ```bash
   git clone <repository-url>
   cd <repository-directory>
