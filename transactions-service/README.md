# Transactions Service

## Overview
The Transactions Service is a Spring Boot microservice that handles all transaction-related operations in the virtual banking system. It manages transaction records, transaction history, and provides transaction analytics and reporting capabilities.

## Features
- Transaction recording and management
- Transaction history tracking
- Transaction status management
- Transaction analytics and reporting
- PostgreSQL database integration
- RESTful API endpoints
- Integration with Account Service

## Technology Stack
- **Java**: 21
- **Spring Boot**: 3.5.3
- **Spring Data JPA**: For data persistence
- **PostgreSQL**: Primary database
- **Spring Web**: For REST API functionality

## Database Configuration
- **Database**: PostgreSQL
- **Database Name**: Transactions-service
- **Host**: localhost:5432
- **Username**: YOUR_USERNAME
- **Password**: YOUR_PASSWORD

## API Endpoints
- **POST** `/transactions/transfer/initiation` - Initiate new transaction record
- **POST** `/transactions/transfer/initiation` - Execute transaction record
- **GET** `/transactions/latest` - Get Last Transaction for Account
- **GET** `/account/{accountId}/transactions` - Get transactions by account


## Running the Service

### Prerequisites
- Java 21
- PostgreSQL database running on localhost:5432

### Steps
1. Navigate to the transactions service directory:
   ```bash
   cd transactions-service
   ```

2. Install dependencies:
   ```bash
   ./mvnw clean install
   ```

3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

The service will start on port **8082**.

## Environment Variables
Make sure to set up the following in your environment:
- PostgreSQL database named `Transactions-service`

## Dependencies
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- PostgreSQL Driver
