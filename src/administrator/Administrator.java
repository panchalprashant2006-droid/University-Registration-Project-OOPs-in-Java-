package administrator;

import models.User;
import models.Complaint;
import data.DataManager;
import models.Course;
import professor.Professor;
import student.Student;

import java.util.List;
import java.util.Iterator;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Administrator extends User {

	private static final String FIXED_PASSWORD = "AdminPassword123";
	
	public Administrator(String email) {
		super(email, FIXED_PASSWORD);
	}

	@Override
	public boolean login(String email, String password) {
		return this.getEmail().equals(email) && password.equals(FIXED_PASSWORD);
	}

	@Override
	public void logout() {
		System.out.println("Administrator logged out. ");
	}

	@Override
	public void displayMenu() {
		System.out.println("\n--- Administrator Menu ---");
        System.out.println("1. Manage Course Catalog");
        System.out.println("2. Manage Student Records");
        System.out.println("3. Assign Professors to Courses");
        System.out.println("4. Handle Complaints");
        System.out.println("5. Logout");
	}
	
	
	public void addCourse(DataManager db,Course newCourse) {
		db.courseCatalog.add(newCourse);
		System.out.println("Course added successfully : "+newCourse.getCourseCode());
	}
	

	public void deleteCourse(DataManager db,String courseCode) {
		Iterator <Course> iterator = db.courseCatalog.iterator();
		while(iterator.hasNext()) {
			Course course = iterator.next();
			if(course.getCourseCode().equalsIgnoreCase(courseCode)) {
				iterator.remove();
				System.out.println("Course "+courseCode+" has been deleted .");
				return;
			}
		}
		System.out.println("Course NOT found in the Catalog !");
	}
	
	
	public void viewCourseCatalog(DataManager db) {
		for(Course c : db.courseCatalog) {
			System.out.println(c.getCourseCode() + " - "+ c.getTitle());	
		}
	}
	
	
	public void manageCourseCatalog(DataManager db, Scanner scanner) {
        System.out.println("\n--- Manage Course Catalog ---");
        System.out.println("1. View Course Catalog");
        System.out.println("2. Add Course");
        System.out.println("3. Delete Course");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (choice) {
            case 1:
                viewCourseCatalog(db);
                break;
            case 2:
                System.out.print("Enter Course Code: ");
                String code = scanner.nextLine();
                System.out.print("Enter Course Title: ");
                String title = scanner.nextLine();
                System.out.print("Enter Credits (2 or 4): ");
                int credits = scanner.nextInt();
                scanner.nextLine();
                System.out.print("Enter Timings: ");
                String timings = scanner.nextLine();
                System.out.print("Enter Enrollment Limit: ");
                int limit = scanner.nextInt();
                scanner.nextLine();

                Course newCourse = new Course(code, title, credits, timings, limit);
                addCourse(db, newCourse);
                break;
            case 3:
                System.out.print("Enter Course Code to delete: ");
                String deleteCode = scanner.nextLine();
                deleteCourse(db, deleteCode);
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }
	
	
	public void manageStudentRecords(DataManager db, Scanner scanner) {
        System.out.println("\n--- Manage Student Records ---");
        System.out.println("1. View All Student Records");
        System.out.println("2. Update Student Personal Info");
        System.out.println("3. Assign Grade to Student");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (choice) {
            case 1:
                viewAllStudents(db);
                break;
            case 2:
                updateStudentInfo(db, scanner);
                break;
            case 3:
                assignGradeToStudent(db, scanner);
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }
	
	
	public void viewAllStudents(DataManager db) {
		if(db.students.isEmpty()) {
			System.out.println("No students found !");
			return;
		}
		
		for(Student s : db.students) {
			System.out.println("Email: "+s.getEmail() + " | Semester: "+s.getCurrentSemester()+" | Registered Courses: " + s.getRegisteredCourses().size() +
                    " | Completed Courses: " + s.getCompletedCourses().size());
			
		}
	}
	
	
	public void updateStudentInfo(DataManager db,Scanner scanner) {
		Student student = findStudentByEmail(db,scanner);
		if(student == null) return;
		
		System.out.println("Enter new email (leave blank to keep current ) : ");
		String email = scanner.nextLine();
		if(!email.isEmpty()) {
			student.setEmail(email);
		}
		
		System.out.print("Enter new password (leave blank to keep current): ");
        String newPassword = scanner.nextLine();
        if (!newPassword.isEmpty()) {
            student.setPassword(newPassword);
        }

        System.out.println("Student information updated successfully.");
	}
	
	
	public void assignGradeToStudent(DataManager db, Scanner scanner) {
        Student student = findStudentByEmail(db, scanner);
        if (student == null) return;

        if (student.getRegisteredCourses().isEmpty()) {
            System.out.println("This student has no registered courses to grade.");
            return;
        }

        System.out.println("Registered Courses:");
        for (Course c : student.getRegisteredCourses()) {
            System.out.println("- " + c.getCourseCode() + ": " + c.getTitle());
        }

        System.out.print("Enter course code to grade: ");
        String courseCode = scanner.nextLine();

        Course targetCourse = null;
        for (Course c : student.getRegisteredCourses()) {
            if (c.getCourseCode().equalsIgnoreCase(courseCode)) {
                targetCourse = c;
                break;
            }
        }

        if (targetCourse == null) {
            System.out.println("Student is not registered in " + courseCode);
            return;
        }

        System.out.print("Enter grade (e.g., A, B, C, D, F): ");
        String grade = scanner.nextLine().toUpperCase();

        student.assignGrade(targetCourse, grade);
        System.out.println("Grade " + grade + " assigned for " + targetCourse.getCourseCode());

        if (student.getRegisteredCourses().isEmpty()) {
            student.advanceSemester();
            System.out.println("All courses graded! Student advanced to Semester " + student.getCurrentSemester());
        }
    }
	
	
	private Student findStudentByEmail(DataManager db, Scanner scanner) {
		System.out.print("Enter student email: ");
        String email = scanner.nextLine();
        for (Student s : db.students) {
            if (s.getEmail().equalsIgnoreCase(email)) {
                return s;
            }
        }
        System.out.println("Student not found.");
        return null;
	}

	public void assignProfessor(Course course,Professor prof) {
		course.setAssignedProfessor(prof);
		prof.addAssignedCourse(course);
		System.out.println("Professor "+prof.getEmail() + " assigned to "+ course.getCourseCode());
	}
	
	
	public void handleComplaints(DataManager db, Scanner scanner) {
	    System.out.println("\n--- Handle Complaints ---");
	    System.out.println("1. View All Complaints");
	    System.out.println("2. View Pending Complaints");
	    System.out.println("3. Resolve a Complaint");
	    System.out.print("Choose an option: ");
	    int choice = scanner.nextInt();
	    scanner.nextLine(); // consume newline

	    switch (choice) {
	        case 1:
	            viewComplaintsByStatus(db, "ALL");
	            break;
	        case 2:
	            viewComplaintsByStatus(db, "Pending");
	            break;
	        case 3:
	            viewComplaintsByStatus(db, "Pending");
	            System.out.print("Enter complaint number to resolve: ");
	            int index = scanner.nextInt() - 1;
	            scanner.nextLine();
	            
	            List<Complaint> pending = db.complaints.stream()
	                    .filter(c -> c.getStatus().equalsIgnoreCase("Pending"))
	                    .collect(Collectors.toList());

	            if (index >= 0 && index < pending.size()) {
	                System.out.print("Enter resolution details: ");
	                String details = scanner.nextLine();
	                resolveComplaints(pending.get(index), details);
	            } else {
	                System.out.println("Invalid complaint number.");
	            }
	            break;
	        default:
	            System.out.println("Invalid choice.");
	    }
	}
	
	
	public void viewComplaintsByStatus(DataManager db, String statusFilter) {
        List<Complaint> filtered = db.complaints.stream()
                .filter(c -> statusFilter.equalsIgnoreCase("ALL") || c.getStatus().equalsIgnoreCase(statusFilter))
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            System.out.println("No complaints found with status: " + statusFilter);
            return;
        }

        for (int i = 0; i < filtered.size(); i++) {
            Complaint c = filtered.get(i);
            System.out.println((i + 1) + ". [" + c.getStatus() + "] " + c.getDescription()
                    + " (By: " + c.getStudent().getEmail() + ")");
        }
    }
	
	
	public void viewComplaintsByDate(DataManager db, String dateFilter) {
	    boolean found = false;
	    for (Complaint c : db.complaints) {
	        if (c.getDate().equalsIgnoreCase(dateFilter.trim())) {
	            System.out.println(c);
	            found = true;
	        }
	    }
	    if (!found) {
	        System.out.println("No complaints found on date: " + dateFilter);
	    }
	}
	
	
	public void resolveComplaints(Complaint complaint, String resolutionDetails) {
        if (complaint.getStatus().equalsIgnoreCase("Pending")) {
            complaint.updateStatus("Resolved");
            complaint.setResolutionDetails(resolutionDetails);
            System.out.println("Complaint status updated to Resolved.");
        } else {
            System.out.println("Complaint is already resolved.");
        }
    }
	
}
