import administrator.Administrator;
import data.DataManager;
import models.Course;
import models.User;
import professor.Professor;
import student.Student;

import java.util.Scanner;


public class Main {
	public static void main(String[] args) {
		DataManager db = DataManager.loadData();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        
        while (running) {
            System.out.println("\n====================================================");
            System.out.println("  Welcome to the University Course Registration System");
            System.out.println("====================================================");
            System.out.println("1. Enter the Application");
            System.out.println("2. Exit the Application");
            System.out.print("Choose an option: ");

            int choice = readInt(scanner);

            switch (choice) {
                case 1:
                    roleSelectionMenu(db, scanner);
                    break;
                case 2:
                    db.saveData();
                    System.out.println("All session changes saved. Exiting the application. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please enter 1 or 2.");
            }
        }

        scanner.close();
    }
	
	
	private static void roleSelectionMenu(DataManager db, Scanner scanner) {
        boolean selecting = true;

        while (selecting) {
            System.out.println("\n--- Login / Sign Up As ---");
            System.out.println("1. Student");
            System.out.println("2. Professor");
            System.out.println("3. Administrator");
            System.out.println("4. Back to Main Screen");
            System.out.print("Choose your role: ");

            int roleChoice = readInt(scanner);
            User loggedInUser = null;

            switch (roleChoice) {
                case 1:
                    loggedInUser = authenticateStudent(db, scanner);
                    break;
                case 2:
                    loggedInUser = authenticateProfessor(db, scanner);
                    break;
                case 3:
                    loggedInUser = authenticateAdmin(db, scanner);
                    break;
                case 4:
                    selecting = false;
                    continue;
                default:
                    System.out.println("Invalid choice. Please select 1-4.");
                    continue;
            }

            if (loggedInUser != null) {
                runUserSession(loggedInUser, db, scanner);
            }
        }
    }
	
	
	private static Student authenticateStudent(DataManager db, Scanner scanner) {
        System.out.println("\n--- Student Access ---");
        System.out.println("1. Login");
        System.out.println("2. Sign Up");
        System.out.print("Choose an option: ");
        int option = readInt(scanner);

        if (option == 1) {
            System.out.print("Enter Student Email: ");
            String email = scanner.nextLine().trim();
            System.out.print("Enter Password: ");
            String password = scanner.nextLine();

            Student student = db.findStudentByEmail(email);
            if (student != null && student.login(email, password)) {
                System.out.println("Login successful! Welcome, " + student.getEmail());
                return student;
            }
            System.out.println("Invalid student email or password.");
            return null;

        } else if (option == 2) {
            System.out.print("Create Student Email: ");
            String email = scanner.nextLine().trim();
            System.out.print("Create Password: ");
            String password = scanner.nextLine();

            if (email.isEmpty() || password.isEmpty()) {
                System.out.println("Email and password cannot be empty.");
                return null;
            }
            return db.signUpStudent(email, password);
        }

        System.out.println("Invalid option.");
        return null;
    }
	
	

	private static void runUserSession(User currentUser, DataManager db, Scanner scanner) {
        boolean loggedIn = true;

        while (loggedIn) {
            // Runtime Polymorphism: dynamically invokes the subclass's displayMenu()
            currentUser.displayMenu();
            System.out.print("Choose an option: ");
            int choice = readInt(scanner);

            if (currentUser instanceof Student) {
                loggedIn = handleStudentChoice((Student) currentUser, choice, db, scanner);
            } else if (currentUser instanceof Professor) {
                loggedIn = handleProfessorChoice((Professor) currentUser, choice, db, scanner);
            } else if (currentUser instanceof Administrator) {
                loggedIn = handleAdminChoice((Administrator) currentUser, choice, db, scanner);
            }
        }

        // Runtime Polymorphism: dynamically invokes the subclass's logout()
        currentUser.logout();
        db.saveData();
    }



	private static boolean handleAdminChoice(Administrator admin, int choice, DataManager db, Scanner scanner) {
        switch (choice) {
            case 1:
                admin.manageCourseCatalog(db, scanner);
                break;
            case 2:
                admin.manageStudentRecords(db, scanner);
                break;
            case 3:
                assignProfessorMenu(admin, db, scanner);
                break;
            case 4:
                admin.handleComplaints(db, scanner);
                break;
            case 5:
                return false; // Logout
            default:
                System.out.println("Invalid choice. Please select 1-5.");
        }
        return true;
    }


	private static void assignProfessorMenu(Administrator admin, DataManager db, Scanner scanner) {
        if (db.courseCatalog.isEmpty() || db.professors.isEmpty()) {
            System.out.println("No courses or professors available for assignment.");
            return;
        }

        System.out.println("\n--- Select a Course ---");
        for (int i = 0; i < db.courseCatalog.size(); i++) {
            Course c = db.courseCatalog.get(i);
            String currentProf = (c.getAssignedProfessor() != null)
                    ? c.getAssignedProfessor().getEmail()
                    : "Unassigned";
            System.out.println((i + 1) + ". " + c.getCourseCode() + " - " + c.getTitle()
                    + " (Current Prof: " + currentProf + ")");
        }
        System.out.print("Enter course number: ");
        int courseIdx = readInt(scanner) - 1;

        if (courseIdx < 0 || courseIdx >= db.courseCatalog.size()) {
            System.out.println("Invalid course selection.");
            return;
        }

        System.out.println("\n--- Select a Professor ---");
        for (int i = 0; i < db.professors.size(); i++) {
            System.out.println((i + 1) + ". " + db.professors.get(i).getEmail());
        }
        System.out.print("Enter professor number: ");
        int profIdx = readInt(scanner) - 1;

        if (profIdx < 0 || profIdx >= db.professors.size()) {
            System.out.println("Invalid professor selection.");
            return;
        }

        admin.assignProfessor(db.courseCatalog.get(courseIdx), db.professors.get(profIdx));
    }

    // Safe integer reader to prevent crashes when a user enters non-numeric text
    private static int readInt(Scanner scanner) {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }


	private static boolean handleProfessorChoice(Professor professor, int choice, DataManager db, Scanner scanner) {
        switch (choice) {
            case 1:
                professor.manageCoursesMenu(db, scanner);
                break;
            case 2:
                professor.viewEnrolledStudentsMenu(scanner);
                break;
            case 3:
                return false; // Logout
            default:
                System.out.println("Invalid choice. Please select 1-3.");
        }
        return true;
    }
	
	

	private static boolean handleStudentChoice(Student student, int choice, DataManager db, Scanner scanner) {
        switch (choice) {
            case 1:
                student.viewAvailableCourses(db);
                break;
            case 2:
                student.registerCourseMenu(db, scanner);
                break;
            case 3:
                student.viewSchedule();
                break;
            case 4:
                student.trackAcademicProgress();
                break;
            case 5:
                student.dropCourseMenu(scanner);
                break;
            case 6:
                student.complaintMenu(db, scanner);
                break;
            case 7:
                return false; // Logout
            default:
                System.out.println("Invalid choice. Please select 1-7.");
        }
        return true;
    }


	private static Professor authenticateProfessor(DataManager db, Scanner scanner) {
        System.out.println("\n--- Professor Access ---");
        System.out.println("1. Login");
        System.out.println("2. Sign Up");
        System.out.print("Choose an option: ");
        int option = readInt(scanner);

        if (option == 1) {
            System.out.print("Enter Professor Email: ");
            String email = scanner.nextLine().trim();
            System.out.print("Enter Password: ");
            String password = scanner.nextLine();

            Professor prof = db.findProfessorByEmail(email);
            if (prof != null && prof.login(email, password)) {
                System.out.println("Login successful! Welcome, " + prof.getEmail());
                return prof;
            }
            System.out.println("Invalid professor email or password.");
            return null;

        } else if (option == 2) {
            System.out.print("Create Professor Email: ");
            String email = scanner.nextLine().trim();
            System.out.print("Create Password: ");
            String password = scanner.nextLine();

            if (email.isEmpty() || password.isEmpty()) {
                System.out.println("Email and password cannot be empty.");
                return null;
            }
            return db.signUpProfessor(email, password);
        }

        System.out.println("Invalid option.");
        return null;
    }
	
	private static Administrator authenticateAdmin(DataManager db, Scanner scanner) {
        System.out.println("\n--- Administrator Login ---");
        System.out.print("Enter Admin Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter Fixed Admin Password: ");
        String password = scanner.nextLine();

        Administrator admin = db.findAdminByEmail(email);
        if (admin != null && admin.login(email, password)) {
            System.out.println("Login successful! Welcome, Administrator.");
            return admin;
        }
        System.out.println("Invalid administrator credentials.");
        return null;
    }

}
