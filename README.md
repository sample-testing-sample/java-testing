# Exam Proctoring System

A fully functional Exam Proctoring System for Computer-Based Exams.

## Features

- User roles: Admin and Student
- Authentication system with session handling
- Admin: Create exams, view results, monitor activities
- Student: Take exams with timer, view results
- Proctoring: Tab switching detection, disable right-click/copy-paste, fullscreen enforcement, activity logging

## Tech Stack

- Frontend: HTML, CSS, JavaScript (vanilla)
- Backend: Core Java (Servlets)
- Database: SQLite

## Setup Instructions

1. Install dependencies:
   ```
   sudo apt update
   sudo apt install maven openjdk-11-jdk sqlite3 python3
   ```

2. Initialize the database:
   ```
   cd backend
   sqlite3 exam.db < ../database/schema.sql
   ```

3. Start the backend:
   ```
   cd backend
   mvn clean compile jetty:run
   ```
   The backend will run on http://localhost:8080

4. Start the frontend server (in a new terminal):
   ```
   cd frontend
   python3 -m http.server 8000
   ```

5. Open the application:
   Open http://localhost:8000/index.html in your browser.

## Usage

- Login as admin: username `admin`, password `admin123`
- Login as student: username `student1`, password `pass123`
- Register new students from the login page.

## Project Structure

- `frontend/`: HTML, CSS, JS files
- `backend/`: Java Servlets, models, DAOs
- `database/`: SQL schema

## Running in GitHub Codespaces

This project is designed to run in GitHub Codespaces. Follow the setup instructions above.
