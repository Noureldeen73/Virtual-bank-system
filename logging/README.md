# Logging Service

## Overview
The Logging Service is a Spring Boot microservice that centralizes logging functionality for the virtual banking system. It consumes log events from other microservices via Apache Kafka and stores them in a PostgreSQL database for audit trails, monitoring, and compliance purposes.

## Features
- Centralized logging for all microservices
- Kafka-based event consumption
- Persistent log storage in PostgreSQL
- Log analytics and querying capabilities
- Audit trail management
- Real-time log processing
- Log retention policies

## Technology Stack
- **Java**: 21
- **Spring Boot**: 3.5.4
- **Spring Data JPA**: For data persistence
- **PostgreSQL**: Primary database for log storage
- **Apache Kafka**: For consuming log events
- **Spring Kafka**: For Kafka integration

## Database Configuration
- **Database**: PostgreSQL
- **Database Name**: logging_service
- **Host**: localhost:5432
- **Username**: YOUR_USERNAME
- **Password**: YOUR_PASSWORD

## Kafka Configuration
- **Bootstrap Servers**: localhost:9092
- **Consumer Group**: logging-group
- **Auto Offset Reset**: earliest
- **Key Deserializer**: StringDeserializer
- **Value Deserializer**: StringDeserializer


## Running the Service

### Prerequisites
- Java 21
- PostgreSQL database running on localhost:5432
- Apache Kafka running on localhost:9092

### Steps
1. Navigate to the logging service directory:
   ```bash
   cd logging
   ```

2. Install dependencies:
   ```bash
   ./mvnw clean install
   ```

3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

The service will start on port **8085**.

## Environment Variables
Make sure to set up the following in your environment:
- PostgreSQL database named `logging_service`
- Kafka broker running on `localhost:9092`

## Kafka Topics
The service listens to the following Kafka topics:
- `account-logs` - Logs from Account Service
- `user-logs` - Logs from User Service  
- `transaction-logs` - Logs from Transactions Service
- `bff-logs` - Logs from BFF Service

## Dependencies
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- PostgreSQL Driver
- Spring Kafka
