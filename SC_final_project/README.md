# University Course Registration System (CRTS)

This is a **Java Swing-based University Course Registration System (CRTS)** developed as a semester project for the **Software Construction** course. The application allows administrators to manage courses and students, and enables students to register for, drop, and view their courses with conflict-free timetables.

---

## Table of Contents

- [Features](#features)  
- [Screens & Descriptions](#screens--descriptions)  
- [Technologies Used](#technologies-used)  
- [Database Schema](#database-schema)  
- [Installation & Setup](#installation--setup)  
- [Usage](#usage)  
- [Future Enhancements](#future-enhancements)  
- [Author](#author)  

---

## Features

### Admin Features
- Add, update, delete, and search courses.  
- Add new students with program and semester details.  
- Generate optimized, conflict-free timetables for all students.  
- View course enrollment reports.  

### Student Features
- Register for courses across all time slots automatically.  
- Drop registered courses with seat updates.  
- View personal weekly timetable.  
- Login using username or email with secure password hashing.  

### System Features
- Automatic prerequisite checking before registration.  
- Seat availability validation during registration.  
- Conflict detection and resolution for timetable slots.  
- Transaction-based database operations to ensure data consistency.  

---

## Screens & Descriptions

1. **Login Screen:** Input fields for username/email and password with login button; redirects based on role.  
2. **Admin Dashboard:** Buttons to manage courses, add students, generate timetables, view reports, and logout.  
3. **Add Student Screen:** Form to add new student details including username, email, password, program, and semester.  
4. **Course Manager:** Table to manage courses with fields to add, update, delete, or search.  
5. **Course Registration:** Students can register for selected courses while checking prerequisites, time conflicts, and seat availability.  
6. **Drop Course:** Allows students to drop courses by entering course code; restores seat availability.  
7. **Generate Timetable:** Generates optimized, conflict-free timetables for all students.  
8. **Course Enrollment Report:** Displays each course with the number of enrolled students per day and time slot.  
9. **Student Dashboard:** Shows logged-in student details and buttons to register/drop courses, view timetable, or logout.  
10. **Timetable:** Displays student’s weekly schedule with course, day, time, and room.  

---

## Technologies Used

- **Java 8+** with **Swing GUI**  
- **MySQL** / **MariaDB** database  
- JDBC for database connectivity  
- Password hashing for security  
- **MVC-like modular design** with separate frames for each feature  

---

## Database Schema

The system uses the following tables:

- `users` – Stores login credentials and role (ADMIN/STUDENT).  
- `students` – Stores student program and semester information.  
- `courses` – Stores course details including code, title, day, time slot, room, max seats, and available seats.  
- `registrations` – Tracks student course registrations.  
- `course_prerequisites` – Tracks prerequisite courses for validation.  

> All database operations use transactions to ensure consistency and integrity.  

---

## Installation & Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/CRTS.git
