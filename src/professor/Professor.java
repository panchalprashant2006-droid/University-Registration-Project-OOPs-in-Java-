package professor;

import data.DataManager;
import student.Student;
import models.User;
import models.Course;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class Professor extends User{
	private List<Course> assignedCourses;
	
	public Professor(String email, String password) {
		super(email, password);
		this.assignedCourses = new ArrayList<>();
	}

	@Override
	public boolean login(String email, String password) {
		return this.getEmail().equals(email) && this.getPassword().equals(password);
	}

	@Override
	public void logout() {
		System.out.println("Professor logged out.");
	}

	@Override
	public void displayMenu() {
		System.out.println("\n--- Professor Menu ---");
        System.out.println("1. View & Manage My Courses");
        System.out.println("2. View Enrolled Students");
        System.out.println("3. Logout");
	}
	
	
	public List<Course> getAssignedCourses(){
		return this.assignedCourses;
	}
	
	
	private Course selectAssignedCourse(Scanner scanner) {
        if (assignedCourses.isEmpty()) {
            System.out.println("You have no courses assigned yet.");
            return null;
        }

        System.out.println("\nYour Assigned Courses:");
        for (int i = 0; i < assignedCourses.size(); i++) {
            Course c = assignedCourses.get(i);
            System.out.println((i + 1) + ". " + c.getCourseCode() + " - " + c.getTitle());
        }

        System.out.print("Select a course number: ");
        int index = scanner.nextInt() - 1;
        scanner.nextLine(); // consume newline

        if (index < 0 || index >= assignedCourses.size()) {
            System.out.println("Invalid course selection.");
            return null;
        }
        return assignedCourses.get(index);
    }
	
	
	public void manageCoursesMenu(DataManager db, Scanner scanner) {
        Course selected = selectAssignedCourse(scanner);
        if (selected != null) {
            manageCourse(selected, db, scanner);
        }
    }
	
	
	public void manageCourse(Course course, DataManager db, Scanner scanner) {
        boolean updating = true;
        while (updating) {
            System.out.println("\n--- Current Course Details ---");
            System.out.println(course.getDetails());

            System.out.println("\nWhat would you like to update?");
            System.out.println("1. Syllabus");
            System.out.println("2. Class Timings");
            System.out.println("3. Credits (2 or 4)");
            System.out.println("4. Prerequisites");
            System.out.println("5. Enrollment Limit");
            System.out.println("6. Office Hours");
            System.out.println("7. Back to Professor Menu");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter new syllabus: ");
                    course.setSyllabus(scanner.nextLine());
                    System.out.println("Syllabus updated.");
                    break;
                case 2:
                    System.out.print("Enter new class timings: ");
                    course.setTimings(scanner.nextLine());
                    System.out.println("Timings updated.");
                    break;
                case 3:
                    System.out.print("Enter new credits (2 or 4): ");
                    int credits = scanner.nextInt();
                    scanner.nextLine();
                    if (credits == 2 || credits == 4) {
                        course.setCredits(credits);
                        System.out.println("Credits updated to " + credits + ".");
                    } else {
                        System.out.println("Invalid credits. Courses must be 2 or 4 credits.");
                    }
                    break;
                case 4:
                    updatePrerequisites(course, db, scanner);
                    break;
                case 5:
                    System.out.print("Enter new enrollment limit: ");
                    int limit = scanner.nextInt();
                    scanner.nextLine();
                    if (limit >= course.getEnrolledStudents().size()) {
                        course.setEnrollmentLimit(limit);
                        System.out.println("Enrollment limit updated to " + limit + ".");
                    } else {
                        System.out.println("Limit cannot be lower than current enrollment ("
                                + course.getEnrolledStudents().size() + ").");
                    }
                    break;
                case 6:
                    System.out.print("Enter new office hours: ");
                    course.setOfficeHours(scanner.nextLine());
                    System.out.println("Office hours updated.");
                    break;
                case 7:
                    updating = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
	
	private void updatePrerequisites(Course course, DataManager db, Scanner scanner) {
        System.out.println("1. Add Prerequisite");
        System.out.println("2. Remove Prerequisite");
        System.out.print("Choose an option: ");
        int subChoice = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter prerequisite course code: ");
        String code = scanner.nextLine();

        if (code.equalsIgnoreCase(course.getCourseCode())) {
            System.out.println("A course cannot be a prerequisite for itself.");
            return;
        }

        Course prereq = null;
        for (Course c : db.courseCatalog) {
            if (c.getCourseCode().equalsIgnoreCase(code)) {
                prereq = c;
                break;
            }
        }

        if (prereq == null) {
            System.out.println("Course " + code + " not found in catalog.");
            return;
        }

        if (subChoice == 1) {
            course.addPrerequisite(prereq);
            System.out.println("Prerequisite " + code + " added.");
        } else if (subChoice == 2) {
            course.removePrerequisite(prereq);
            System.out.println("Prerequisite " + code + " removed.");
        } else {
            System.out.println("Invalid option.");
        }
    }

	public void viewEnrolledStudentsMenu(Scanner scanner) {
        Course selected = selectAssignedCourse(scanner);
        if (selected != null) {
            viewEnrolledStudents(selected);
        }
    }

	public void viewEnrolledStudents(Course course) {
        List<Student> enrolled = course.getEnrolledStudents();
        System.out.println("\n--- Enrolled Students in " + course.getCourseCode() + " ---");

        if (enrolled.isEmpty()) {
            System.out.println("No students are currently enrolled in this course.");
            return;
        }

        for (Student s : enrolled) {
            System.out.printf("Email: %s | Semester: %d | CGPA: %.2f%n",
                    s.getEmail(), s.getCurrentSemester(), s.calculateCGPA());
        }
    }


	public void addAssignedCourse(Course course) {
		if(!this.assignedCourses.contains(course)) {
			this.assignedCourses.add(course);
		}
	}
	
}
