# TryBooking Ticketing System

This is a simple ticketing system application with a Java backend and a React frontend.

## Project Structure

- `oop_20221398_Sheshanth_ticketingSystem/`: Contains the Java backend code.
- `Frontend/`: Contains the React frontend code.

## Getting Started

Follow these instructions to set up and run the project locally.

### Prerequisites

- Java Development Kit (JDK)
- Maven
- Node.js
- npm or yarn

### Backend Setup (Java)

1. Navigate to the backend directory:
   ```bash
   cd oop_20221398_Sheshanth_ticketingSystem
   ```
2. Build the project using Maven:
   ```bash
   mvn clean install
   ```
3. Run the backend application:
   ```bash
   mvn spring-boot:run
   ```
   The backend should start and be accessible, typically on `http://localhost:8080`.

### Frontend Setup (React)

1. Navigate to the frontend directory:
   ```bash
   cd Frontend
   ```
2. Install the dependencies:
   ```bash
   npm install
   # or yarn install
   ```
3. Run the frontend development server:
   ```bash
   npm run dev
   # or yarn dev
   ```
   The frontend application should now be running, typically on `http://localhost:5173`.

## Technologies Used

**Backend:**
- Java
- Spring Boot
- Maven

**Frontend:**
- React
- TypeScript
- Material UI
- Framer Motion
- Vite

## Features

- View available events/shows.
- Purchase tickets for events.
- (Assumed) Admin functionalities (e.g., viewing dashboards).

## Future Improvements

- Implement user authentication.
- Add payment gateway integration.
- Enhance error handling and validation.
- Improve responsiveness and mobile compatibility. 