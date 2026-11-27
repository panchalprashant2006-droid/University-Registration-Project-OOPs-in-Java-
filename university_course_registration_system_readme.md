# University Course Registration System (CSE201 – Assignment 1)

A terminal-based, menu-driven University Course Registration System built in Java using core Object-Oriented Programming (OOP) principles and Java Object Serialization for cross-session data persistence.

---

## 1. Project Structure

    university_system/
    ├── README.md
    ├── UML_Diagram.pdf
    └── src/
        ├── Main.java                 # Entry point, authentication, and polymorphic menu loop
        ├── administrator/
        │   └── Administrator.java    # Catalog management, student records/grades, assignments, complaints
        ├── data/
        │   └── DataManager.java      # Centralized data collections and file serialization
        ├── interfaces/
        │   ├── AcademicTracker.java  # Contract for SGPA and CGPA computation
        │   └── UserActions.java      # Contract for login and logout behavior
        ├── models/
        │   ├── Complaint.java        # Complaint entity (description, status, date, resolution)
        │   ├── Course.java           # Course entity (credits, schedule, capacity, prerequisites, roster)
        │   └── User.java             # Abstract base class for all system users
        ├── professor/
        │   └── Professor.java        # Assigned course updates and enrolled student roster view
        └── student/
            └── Student.java          # Registration, schedule, GPA tracking, dropping, and complaints

---

## 2. How to Compile and Run

1. Open a terminal and navigate to the `src/` directory:

       cd university_system/src

2. Compile all Java source files:

       javac Main.java data/*.java interfaces/*.java models/*.java student/*.java professor/*.java administrator/*.java

3. Run the application:

       java Main

4. **Resetting Saved Data:** Session changes are saved automatically to `university_data.ser` in the working directory upon logout and application exit. To restore the initial hardcoded dataset, delete `university_data.ser` and run `java Main` again.

---

## 3. Pre-Loaded Demonstration Credentials

The application initializes with 1 Administrator, 2 Professors, 3 Students, and 5 Courses when launched for the first time:

### Administrator (Fixed Password)
* **Email:** `admin@gsv.ac.in`
* **Password:** `AdminPassword123`

### Professors
* `prof.KK@gsv.ac.in` | **Password:** `pass@123` | **Assigned Courses:** `CS101`, `CS102`
* `prof.Dune@gsv.ac.in` | **Password:** `pass@910` | **Assigned Courses:** `MA101`

### Students (All start at Semester 1)
* `student1@gsv.ac.in` | **Password:** `stu123`
* `student2@gsv.ac.in` | **Password:** `stu456`
* `student3@gsv.ac.in` | **Password:** `stu789`

### Course Catalog
| Code | Title | Credits | Semester | Timings | Location | Professor | Prerequisites |
| :--- | :--- | :---: | :---: | :--- | :--- | :--- | :--- |
| `CS101` | Intro to Programming | 4 | 1 | Mon/Wed 10:00 AM | LT-101 | `prof.KK@gsv.ac.in` | None |
| `MA101` | Calculus I | 4 | 1 | Mon/Wed/Fri 09:00 AM | LT-102 | `prof.Dune@gsv.ac.in` | None |
| `HU101` | Communication Skills | 2 | 1 | Fri 02:00 PM | Room-204 | Unassigned | None |
| `PH101` | Engineering Physics | 4 | 1 | Tue/Thu 09:00 AM | Lab-105 | Unassigned | None |
| `CS102` | Data Structures | 4 | 2 | Tue/Thu 11:00 AM | LT-201 | `prof.KK@gsv.ac.in` | `CS101` |

---

## 4. Functionalities Implemented

* **Student:**
  * View available courses for the current semester with complete metadata.
  * Register for courses in the active semester with enforcement of prerequisites, enrollment capacity, duplicate prevention, and the 20-credit limit.
  * View weekly schedule (timings, locations, and assigned professors).
  * Track academic progress with completed course grades, SGPA, and CGPA.
  * Drop courses during an ongoing semester.
  * Submit complaints and check their resolution status and details.
* **Professor:**
  * View assigned courses and update course details (syllabus, class timings, credits restricted to 2 or 4, prerequisites, enrollment limits, and office hours) without adding or deleting courses.
  * View enrolled students for assigned courses along with their contact email, current semester, and CGPA.
* **Administrator:**
  * View, add, and delete courses in the course catalog.
  * View and update student personal information and assign grades to registered courses.
  * Assign professors to courses (updating both the course and the professor's assigned list).
  * View all or filtered complaints (by status or date) and mark pending complaints as resolved with resolution notes.

---

## 5. OOP Concepts Applied

* **Abstraction:** `models.User` is defined as an `abstract` class containing common user credentials and declaring `public abstract void displayMenu()`, enforcing that every concrete user role provides its own terminal menu.
* **Inheritance:** `Student`, `Professor`, and `Administrator` extend `models.User`, inheriting email/password getters and setters as well as serialization support while adding role-specific fields and behaviors.
* **Interfaces:**
  * `interfaces.UserActions` establishes the authentication contract (`login`, `logout`) implemented by `User` and its subclasses.
  * `interfaces.AcademicTracker` enforces GPA tracking (`calculateSGPA`, `calculateCGPA`) on `Student`.
  * `java.io.Serializable` is implemented by `User`, `Course`, `Complaint`, and `DataManager` to support binary object persistence.
* **Polymorphism:**
  * *Runtime Polymorphism (Method Overriding):* `Main.runUserSession()` accepts a base `User` reference and calls `currentUser.displayMenu()` and `currentUser.logout()`, dynamically executing the overridden implementation of the logged-in role.
  * *Compile-Time Polymorphism (Constructor Overloading):* `Course` and `Complaint` define overloaded constructors to support both minimal and detailed object creation.
* **Encapsulation:** All entity fields are declared `private` and accessed through public getters and validated setters. For example, `Course.setCredits()` accepts only `2` or `4`, `Course.setEnrollmentLimit()` rejects capacities smaller than current enrollment, and `Complaint.updateStatus()` allows only `"Pending"` or `"Resolved"`.

---

## 6. Data Persistence (File Handling)

* Data persistence is handled via Java Object Serialization (`ObjectOutputStream` and `ObjectInputStream`) inside `DataManager.saveData()` and `DataManager.loadData()`.
* On initial startup (when `university_data.ser` does not exist), the system populates the hardcoded demonstration records.
* Whenever a user logs out or exits the application, the entire `DataManager` object graph—including all students, professors, courses, enrollments, grades, and complaints—is written to `university_data.ser` and restored automatically on the next run.

---

## 7. Assumptions Made

* **Grading Scale:** SGPA and CGPA use a 10-point scale (`A = 10`, `B = 8`, `C = 6`, `D = 4`, `F = 0`).
* **Semester Completion:** A student's semester is considered complete once the Administrator assigns grades to every course currently in the student's `registeredCourses` list, automatically advancing the student to the next semester.
* **Prerequisites:** Prerequisites must be completed in an earlier semester with a passing grade (any grade other than `F`).
* **Course Dropping:** Students can drop any currently registered course at any time during an ongoing semester.
* **Complaint Dates:** Complaint submission dates are recorded as plain text strings (`DD-MM-YYYY`) so date filtering works cleanly without external date/time libraries.
* **Default Package & Compilation:** `Main.java` resides directly in `src/` without `module-info.java` to ensure straightforward command-line compilation across environments.