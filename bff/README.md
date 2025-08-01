# BFF (Backend for Frontend) Service

## Overview
The BFF (Backend for Frontend) Service acts as an API gateway and aggregation layer for the virtual banking system. It provides a unified interface for frontend applications by orchestrating calls to multiple microservices and handling cross-cutting concerns like authentication, routing, and response aggregation.

## Features
- API Gateway functionality
- Request routing to appropriate microservices
- Response aggregation from multiple services
- Authentication and authorization
- Load balancing and service discovery
- Cross-cutting concerns handling
- Frontend-optimized API endpoints

## Technology Stack
- **Java**: 21
- **Spring Boot**: 3.5.4
- **Spring Web**: For REST API functionality
- **Spring Cloud Gateway**: For API gateway features
- **OpenFeign**: For inter-service communication

## Configuration
- **Port**: 8083
- **Service Name**: bff

## API Endpoints
The BFF service provides unified endpoints that aggregate data from multiple microservices:
- **POST** `/api/users` - User registration (proxies to User Service)
- **GET** `/api/users/{id}` - Get user profile with account details
- **POST** `/api/accounts` - Create account (proxies to Account Service)
- **PUT** `/api/accounts/transfer` - Transfer money between accounts
- **GET** `/api/transactions/{userId}` - Get user transaction history
- **GET** `/api/dashboard/{userId}` - Get complete user dashboard data

## Running the Service

### Prerequisites
- Java 21
- User Service running on port 8080
- Account Service running on port 8081
- Transactions Service running on port 8082

### Steps
1. Navigate to the BFF service directory:
   ```bash
   cd bff
   ```

2. Install dependencies:
   ```bash
   ./mvnw clean install
   ```

3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

The service will start on port **8083**.

## Service Dependencies
- User Service (port 8080)
- Account Service (port 8081)
- Transactions Service (port 8082)
- Logging Service (port 8085)

## Dependencies
- Spring Boot Starter Web
- Spring Cloud Starter Gateway
- Spring Cloud Starter OpenFeign
