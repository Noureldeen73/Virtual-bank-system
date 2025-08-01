# User Service

## Overview
The User Service is a Spring Boot microservice responsible for managing user information and authentication in the virtual banking system. It handles user registration, profile management, and user-related operations.

## Features
- User registration and management
- User profile operations
- User authentication and authorization
- PostgreSQL database integration
- RESTful API endpoints
- Integration with other microservices

## Technology Stack
- **Java**: 21
- **Spring Boot**: 3.5.3
- **Spring Data JPA**: For data persistence
- **PostgreSQL**: Primary database
- **Spring Security**: For authentication and authorization

## Database Configuration
- **Database**: PostgreSQL
- **Database Name**: User-service
- **Host**: localhost:5432
- **Username**: YOUR_USERNAME
- **Password**: YOUR_PASSWORD

## API Endpoints
- **POST** `/users/register` - Create new user
- **POST** `/users/login` - login user
- **GET** `/users/{id}/profile` - Get user details

## Running the Service

### Prerequisites
- Java 21
- PostgreSQL database running on localhost:5432

### Steps
1. Navigate to the user service directory:
   ```bash
   cd user-service
   ```

2. Install dependencies:
   ```bash
   ./mvnw clean install
   ```

3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

The service will start on the default port **8080**.

## Environment Variables
Make sure to set up the following in your environment:
- PostgreSQL database named `User-service`

## Dependencies
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- PostgreSQL Driver
- Spring Boot Starter Security
