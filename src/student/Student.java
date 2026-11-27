package student;

import models.Complaint;
import models.User;
import models.Course;
import data.DataManager;
import interfaces.AcademicTracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Student extends User implements AcademicTracker{
	public static final int MAX_CREDITS = 20;
	private int currentSemester;
	private List <Course> registeredCourses;
	private List <Course> completedCourses;
	private Map <Course,String> grades;
	
	public Student(String email, String password, int currentSemester) {
		super(email, password);
		this.currentSemester = Math.max(currentSemester,1);
		this.registeredCourses = new ArrayList<>();
		this.completedCourses = new ArrayList<>();
		this.grades = new HashMap<>();
	}
	
	@Override
	public void displayMenu() {
		System.out.println("\n--- Student Menu (Semester " + currentSemester + ") ---");
        System.out.println("1. View Available Courses");
        System.out.println("2. Register for Courses");
        System.out.println("3. View Schedule");
        System.out.println("4. Track Academic Progress");
        System.out.println("5. Drop a Course");
        System.out.println("6. Submit / View Complaints");
        System.out.println("7. Logout");
	}
	
	@Override
	public boolean login(String email,String password) {
		return this.getEmail().equals(email) && this.getPassword().equals(password);
	}
	
	@Override
	public void logout() {
		System.out.println("Student logged out.");
	}

	@Override
    public double calculateSGPA() {
        if (currentSemester <= 1 || completedCourses.isEmpty()) {
            return 0.0;
        }

        int lastCompletedSem = currentSemester - 1;
        double totalWeightedPoints = 0.0;
        int totalCredits = 0;

        for (Course c : completedCourses) {
            if (c.getSemester() == lastCompletedSem && grades.containsKey(c)) {
                totalWeightedPoints += gradeToPoints(grades.get(c)) * c.getCredits();
                totalCredits += c.getCredits();
            }
        }
        return (totalCredits == 0) ? 0.0 : totalWeightedPoints / totalCredits;
    }

	private double gradeToPoints(String grade) {
        switch (grade.toUpperCase()) {
            case "A":  return 10.0;
            case "B":  return 8.0;
            case "C":  return 6.0;
            case "D":  return 4.0;
            case "F":  return 0.0;
            default:   return 0.0;
        }
    }

	@Override
	public double calculateCGPA() {
	    if (currentSemester <= 1 || completedCourses.isEmpty()) {
	        return 0.0;
	    }

	    double totalWeightedPoints = 0.0;
	    int totalCredits = 0;

	    for (Course c : completedCourses) {
	        if (c.getSemester() < this.currentSemester && grades.containsKey(c)) {
	            totalWeightedPoints += gradeToPoints(grades.get(c)) * c.getCredits();
	            totalCredits += c.getCredits();
	        }
	    }
	    return (totalCredits == 0) ? 0.0 : totalWeightedPoints / totalCredits;
	}
	
	
	public void registerCourseMenu(DataManager db, Scanner scanner) {
        System.out.print("Enter your current semester to view courses: ");
        int selectedSem = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (selectedSem != this.currentSemester) {
            System.out.println("You can only register for courses in your current semester (Semester "
                    + this.currentSemester + ").");
            return;
        }

        List<Course> semCourses = new ArrayList<>();
        for (Course c : db.courseCatalog) {
            if (c.getSemester() == selectedSem) {
                semCourses.add(c);
            }
        }

        if (semCourses.isEmpty()) {
            System.out.println("No courses available for Semester " + selectedSem + ".");
            return;
        }

        System.out.println("\nCourses available in Semester " + selectedSem + ":");
        for (int i = 0; i < semCourses.size(); i++) {
            Course c = semCourses.get(i);
            System.out.println((i + 1) + ". " + c.getCourseCode() + " - " + c.getTitle()
                    + " (" + c.getCredits() + " credits)");
        }

        System.out.print("Select a course number to register: ");
        int index = scanner.nextInt() - 1;
        scanner.nextLine();

        if (index < 0 || index >= semCourses.size()) {
            System.out.println("Invalid course selection.");
            return;
        }

        registerCourse(semCourses.get(index));
    }
	
	
	public void registerCourse(Course course) {
        // 1. Semester check
        if (course.getSemester() != this.currentSemester) {
            System.out.println("Registration failed: " + course.getCourseCode()
                    + " is offered in Semester " + course.getSemester() + ", not Semester " + this.currentSemester + ".");
            return;
        }

        // 2. Duplicate check
        if (registeredCourses.contains(course) || completedCourses.contains(course)) {
            System.out.println("Registration failed: You have already registered for or completed "
                    + course.getCourseCode() + ".");
            return;
        }

        // 3. Prerequisite check
        for (Course prereq : course.getPrerequisites()) {
            String prereqGrade = grades.get(prereq);
            if (!completedCourses.contains(prereq) || prereqGrade == null || prereqGrade.equalsIgnoreCase("F")) {
                System.out.println("Registration failed: Missing or failed prerequisite "
                        + prereq.getCourseCode() + " (" + prereq.getTitle() + ").");
                return;
            }
        }

        // 4. Credit limit check (max 20 credits)
        int currentCredits = 0;
        for (Course c : registeredCourses) {
            currentCredits += c.getCredits();
        }
        if (currentCredits + course.getCredits() > MAX_CREDITS) {
            System.out.println("Registration failed: Adding " + course.getCourseCode()
                    + " exceeds the " + MAX_CREDITS + "-credit limit (Current: " + currentCredits + " credits).");
            return;
        }

        // 5. Enrollment limit check
        if (course.isFull()) {
            System.out.println("Registration failed: Course " + course.getCourseCode() + " is full.");
            return;
        }

        // All checks passed
        if (course.enrollStudent(this)) {
            registeredCourses.add(course);
            System.out.println("Successfully registered for " + course.getCourseCode() + " - " + course.getTitle());
        }
    }
	
	
	public void dropCourseMenu(Scanner scanner) {
        if (registeredCourses.isEmpty()) {
            System.out.println("You have no registered courses to drop.");
            return;
        }

        System.out.println("\nRegistered Courses:");
        for (int i = 0; i < registeredCourses.size(); i++) {
            Course c = registeredCourses.get(i);
            System.out.println((i + 1) + ". " + c.getCourseCode() + " - " + c.getTitle());
        }

        System.out.print("Select a course number to drop: ");
        int index = scanner.nextInt() - 1;
        scanner.nextLine();

        if (index < 0 || index >= registeredCourses.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        dropCourse(registeredCourses.get(index));
    }
	
	
	public void dropCourse(Course course) {
        if (registeredCourses.remove(course)) {
            course.removeStudent(this);
            System.out.println("Dropped course: " + course.getCourseCode() + " - " + course.getTitle());
        } else {
            System.out.println("You are not currently registered in " + course.getCourseCode() + ".");
        }
    }
	
	public void viewSchedule() {
        System.out.println("\n--- Weekly Schedule (Semester " + currentSemester + ") ---");
        if (registeredCourses.isEmpty()) {
            System.out.println("You are not registered for any courses this semester.");
            return;
        }

        for (Course c : registeredCourses) {
        	String profEmail = (c.getAssignedProfessor() != null)
        	        ? c.getAssignedProfessor().getEmail()
        	        : "Unassigned";
            System.out.printf("%s - %s | Timings: %s | Location: %s | Professor: %s%n",
                    c.getCourseCode(), c.getTitle(), c.getTimings(), c.getLocation(), profEmail);
        }
    }
	
	
	public void complaintMenu(DataManager db, Scanner scanner) {
        System.out.println("\n--- Complaints ---");
        System.out.println("1. Submit a New Complaint");
        System.out.println("2. View My Complaint Status");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
            	System.out.print("Describe your complaint: ");
            	String description = scanner.nextLine().trim();
            	System.out.print("Enter date (DD-MM-YYYY): "); 
            	String date = scanner.nextLine();
            	if (description.isEmpty()) {
            	    System.out.println("Complaint description cannot be empty.");
            	} else {
            	    submitComplaint(db, description,date);
            	}
            	
            	break;
            case 2:
                viewMyComplaints(db);
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }
	
	public void viewMyComplaints(DataManager db) {
        System.out.println("\n--- My Submitted Complaints ---");
        boolean found = false;
        for (Complaint c : db.complaints) {
            if (c.getStudent().equals(this)) {
                System.out.println("- [" + c.getStatus() + "] " + c.getDescription());
                if (c.getStatus().equalsIgnoreCase("Resolved") && c.getResolutionDetails() != null) {
                    System.out.println("  Resolution: " + c.getResolutionDetails());
                }
                found = true;
            }
        }
        if (!found) {
            System.out.println("You have not submitted any complaints.");
        }
    }

	public void submitComplaint(DataManager db, String complaintDescription, String date) {
        Complaint complaint = new Complaint(complaintDescription, this,date);
        db.complaints.add(complaint);
        System.out.println("Complaint submitted successfully. Current status: " + complaint.getStatus());
    }

	public int getCurrentSemester() {
		return this.currentSemester;
	}

	public List<Course> getRegisteredCourses() {
		return this.registeredCourses;
	}

	public List<Course> getCompletedCourses() {
		return this.completedCourses;
	}

	public Map<Course,String> getGrades (){
		return this.grades;
	}


	public void assignGrade(Course targetCourse, String grade) {
		this.grades.put(targetCourse, grade.toUpperCase());
        this.registeredCourses.remove(targetCourse);
        targetCourse.removeStudent(this);
        if (!this.completedCourses.contains(targetCourse)) {
            this.completedCourses.add(targetCourse);
        }
	}

	public void advanceSemester() {
		this.currentSemester++;
	}
	
	public void viewAvailableCourses(DataManager db) {
        System.out.println("\n--- Available Courses for Semester " + currentSemester + " ---");
        boolean found = false;
        for (Course c : db.courseCatalog) {
            if (c.getSemester() == this.currentSemester) {
                System.out.println(c.getDetails());
                System.out.println("----------------------------------------");
                found = true;
            }
        }
        if (!found) {
            System.out.println("No courses are currently available for Semester " + currentSemester + ".");
        }
    }
	
	public void trackAcademicProgress() {
        System.out.println("\n--- Academic Progress ---");
        if (currentSemester == 1 || completedCourses.isEmpty()) {
            System.out.println("No completed semesters yet. Grades and GPA will appear once all courses in your semester are graded.");
            return;
        }

        System.out.println("Completed Courses & Grades:");
        for (Course c : completedCourses) {
            String grade = grades.getOrDefault(c, "N/A");
            System.out.printf("Sem %d | %s - %s (%d credits) | Grade: %s%n",
                    c.getSemester(), c.getCourseCode(), c.getTitle(), c.getCredits(), grade);
        }

        System.out.printf("Last Completed Semester (Sem %d) SGPA: %.2f%n", (currentSemester - 1), calculateSGPA());
        System.out.printf("Cumulative GPA (CGPA): %.2f%n", calculateCGPA());
    }
}
