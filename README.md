# Virtual Banking System

## Overview
A comprehensive microservices-based virtual banking system built with Spring Boot. This system provides core banking functionalities including user management, account operations, transaction processing, and centralized logging through a distributed architecture.

## Architecture
The system follows a microservices architecture pattern with the following components:

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │────│   BFF Service   │────│  User Service   │
│  Application    │    │   (Port 8083)   │    │   (Port 8080)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                       ┌─────────────────┐    ┌─────────────────┐
                       │ Account Service │    │Transaction Svc  │
                       │   (Port 8081)   │    │   (Port 8082)   │
                       └─────────────────┘    └─────────────────┘
                                │
                       ┌─────────────────┐
                       │ Logging Service │
                       │   (Port 8085)   │
                       └─────────────────┘
                                │
                       ┌─────────────────┐
                       │     Kafka       │
                       │ Message Broker  │
                       └─────────────────┘
```

## Microservices

### 1. User Service (Port 8080)
- **Purpose**: User management and authentication
- **Database**: User-service (PostgreSQL)
- **Features**: User registration, profile management, authentication

### 2. Account Service (Port 8081)
- **Purpose**: Account management and money transfers
- **Database**: Account-service (PostgreSQL)
- **Features**: Account creation, balance management, fund transfers
- **Integrations**: Kafka for logging, OpenFeign for service communication

### 3. Transactions Service (Port 8082)
- **Purpose**: Transaction history and analytics
- **Database**: Transactions-service (PostgreSQL)
- **Features**: Transaction recording, history tracking, reporting

### 4. BFF Service (Port 8083)
- **Purpose**: Backend for Frontend - API Gateway
- **Features**: Request routing, response aggregation, unified API for frontend
- **Integrations**: Communicates with all other services

### 5. Logging Service (Port 8085)
- **Purpose**: Centralized logging and audit trails
- **Database**: logging_service (PostgreSQL)
- **Features**: Event consumption from Kafka, log storage, audit management

## Technology Stack

### Core Technologies
- **Java**: 21
- **Spring Boot**: 3.3.1 - 3.5.4
- **Spring Data JPA**: Database operations
- **PostgreSQL**: Primary database for all services
- **Apache Kafka**: Event streaming and messaging

### Additional Dependencies
- **OpenFeign**: Inter-service communication
- **Spring Security**: Authentication and authorization
- **Spring Web**: REST API functionality

## Prerequisites

### System Requirements
- Java 21 or higher
- Maven 3.6+
- PostgreSQL 12+
- Apache Kafka 2.8+

### Database Setup
Create the following PostgreSQL databases:
```sql
CREATE DATABASE "User-service";
CREATE DATABASE "Account-service";
CREATE DATABASE "Transactions-service";
CREATE DATABASE "logging_service";
```

### Kafka Setup
Ensure Kafka is running on `localhost:9092` with the following topics:
- `account-logs`
- `user-logs`
- `transaction-logs`
- `bff-logs`

## Quick Start

### 1. Start Infrastructure Services
```bash
# Start PostgreSQL
sudo systemctl start postgresql

# Start Kafka
cd kafka_2.13-2.8.0
bin/kafka-server-start.sh config/server.properties
```

### 2. Start Microservices (in order)
```bash
# Terminal 1 - User Service
cd user-service
./mvnw spring-boot:run

# Terminal 2 - Account Service
cd account-service
./mvnw spring-boot:run

# Terminal 3 - Transactions Service
cd transactions-service
./mvnw spring-boot:run

# Terminal 4 - Logging Service
cd logging
./mvnw spring-boot:run

# Terminal 5 - BFF Service
cd bff
./mvnw spring-boot:run
```

### 3. Verify Services
- User Service: http://localhost:8080
- Account Service: http://localhost:8081
- Transactions Service: http://localhost:8082
- BFF Service: http://localhost:8083
- Logging Service: http://localhost:8085

## API Usage Examples

### Create a User
```bash
curl -X POST http://localhost:8083/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com"}'
```

### Create an Account
```bash
curl -X POST http://localhost:8083/api/accounts \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"initialBalance":1000.00}'
```

### Transfer Money
```bash
curl -X PUT http://localhost:8083/api/accounts/transfer \
  -H "Content-Type: application/json" \
  -d '{"fromAccountId":1,"toAccountId":2,"amount":500.00}'
```

## Monitoring and Logging
- All services send logs to Kafka topics
- Centralized logging service stores all events

## Development Guidelines

### Service Communication
- Use OpenFeign clients for synchronous communication
- Use Kafka for asynchronous event publishing
- BFF service aggregates responses from multiple services

### Database Design
- Each service has its own database (Database per Service pattern)
- No direct database access between services
- Use API calls for cross-service data needs

### Error Handling
- Use structured logging for better debugging
- Return meaningful HTTP status codes

## Contributing
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly across all services
5. Submit a pull request

## License
This project is part of the Ejada Internship Final Project.
