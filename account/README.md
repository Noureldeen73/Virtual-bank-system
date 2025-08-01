# Account Service

## Overview
The Account Service is a Spring Boot microservice that manages user accounts and financial transactions in a virtual banking system. It handles account creation, balance management, and money transfers between accounts.

## Features
- Account creation and management
- Balance inquiries
- Money transfers between accounts
- Transaction logging via Kafka
- PostgreSQL database integration
- RESTful API endpoints

## Technology Stack
- **Java**: 21
- **Spring Boot**: 3.3.1
- **Spring Data JPA**: For data persistence
- **PostgreSQL**: Primary database
- **Apache Kafka**: For event streaming and logging
- **OpenFeign**: For inter-service communication

## Database Configuration
- **Database**: PostgreSQL
- **Database Name**: Account-service
- **Host**: localhost:5432
- **Username**: postgres
- **Password**: nourysushi

## API Endpoints
- **PUT** `/accounts/transfer` - Transfer money between accounts
- **POST** `/accounts` - Create new account
- **GET** `/accounts/{id}` - Get account details
- **GET** `/users/{userId}/accounts` - Get all accounts for a user

## Running the Service

### Prerequisites
- Java 21
- PostgreSQL database running on localhost:5432
- Apache Kafka running on localhost:9092

### Steps
1. Navigate to the account service directory:
   ```bash
   cd account
   ```

2. Install dependencies:
   ```bash
   ./mvnw clean install
   ```

3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

The service will start on port **8081**.

## Environment Variables
Make sure to set up the following in your environment:
- PostgreSQL database named `Account-service`
- Kafka broker running on `localhost:9092`

## Dependencies
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- PostgreSQL Driver
- Spring Kafka
- Spring Cloud OpenFeign
