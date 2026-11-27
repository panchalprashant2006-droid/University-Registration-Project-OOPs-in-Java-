package data;

import java.io.*;
import models.Course;
import models.Complaint;
import student.Student;
import professor.Professor;
import administrator.Administrator;

import java.util.ArrayList;
import java.util.List;

public class DataManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String DATA_FILE = "university_data.ser";
	public List<Course> courseCatalog;
	public List<Student> students;
	public List<Professor> professors;
	public List<Complaint> complaints;
	public List<Administrator> administrators;
	
	public DataManager() {
		super();
		this.courseCatalog = new ArrayList<>();
		this.students = new ArrayList<>();
		this.professors = new ArrayList<>();
		this.complaints = new ArrayList<>();
		this.administrators = new ArrayList<>();
		
		initializeData();
	}
	
	private void initializeData() {
        // 1. Hardcode 1 Administrator (fixed password defined inside Administrator)
        Administrator admin = new Administrator("admin@gsv.ac.in");
        administrators.add(admin);

        // 2. Hardcode 2 Professors
        Professor prof1 = new Professor("prof.KK@gsv.ac.in", "pass@123");
        Professor prof2 = new Professor("prof.Dune@gsv.ac.in", "pass@910");
        professors.add(prof1);
        professors.add(prof2);

        // 3. Hardcode 3 Students (starting at Semester 1)
        students.add(new Student("student1@gsv.ac.in", "stu123", 1));
        students.add(new Student("student2@gsv.ac.in", "stu456", 1)); // Fixed typo: gsc -> gsv
        students.add(new Student("student3@gsv.ac.in", "stu789", 1));

        // 4. Hardcode 5 Courses using the 7-arg constructor (with semester & location)
        Course c1 = new Course("CS101", "Intro to Programming", 4, "Mon/Wed 10:00 AM", 50, 1, "LT-101");
        Course c2 = new Course("MA101", "Calculus I", 4, "Mon/Wed/Fri 09:00 AM", 60, 1, "LT-102");
        Course c3 = new Course("HU101", "Communication Skills", 2, "Fri 02:00 PM", 30, 1, "Room-204");
        Course c4 = new Course("PH101", "Engineering Physics", 4, "Tue/Thu 09:00 AM", 50, 1, "Lab-105");
        Course c5 = new Course("CS102", "Data Structures", 4, "Tue/Thu 11:00 AM", 40, 2, "LT-201");

        // Set prerequisite: CS102 (Sem 2) requires CS101 (Sem 1)
        c5.addPrerequisite(c1);

        // Assign professors BOTH ways (course -> prof AND prof -> course)
        assignProfessorBothWays(c1, prof1);
        assignProfessorBothWays(c5, prof1);
        assignProfessorBothWays(c2, prof2);
        // c3 and c4 remain unassigned so Admin can demonstrate assigning professors

        courseCatalog.add(c1);
        courseCatalog.add(c2);
        courseCatalog.add(c3);
        courseCatalog.add(c4);
        courseCatalog.add(c5);
    }

	private void assignProfessorBothWays(Course course, Professor prof) {
		course.setAssignedProfessor(prof);
        prof.addAssignedCourse(course);
		
	}
	
	
	public Student findStudentByEmail(String email) {
        for (Student s : students) {
            if (s.getEmail().equalsIgnoreCase(email)) {
                return s;
            }
        }
        return null;
    }
	
	
	public Professor findProfessorByEmail(String email) {
        for (Professor p : professors) {
            if (p.getEmail().equalsIgnoreCase(email)) {
                return p;
            }
        }
        return null;
    }

	
    public Administrator findAdminByEmail(String email) {
        for (Administrator a : administrators) {
            if (a.getEmail().equalsIgnoreCase(email)) {
                return a;
            }
        }
        return null;
    }
    

    public Course findCourseByCode(String courseCode) {
        for (Course c : courseCatalog) {
            if (c.getCourseCode().equalsIgnoreCase(courseCode)) {
                return c;
            }
        }
        return null;
    }
    
    
    public Student signUpStudent(String email, String password) {
        if (findStudentByEmail(email) != null) {
            System.out.println("An account with this student email already exists.");
            return null;
        }
        Student newStudent = new Student(email, password, 1);
        students.add(newStudent);
        System.out.println("Student sign-up successful! You can now log in.");
        return newStudent;
    }

    public Professor signUpProfessor(String email, String password) {
        if (findProfessorByEmail(email) != null) {
            System.out.println("An account with this professor email already exists.");
            return null;
        }
        Professor newProf = new Professor(email, password);
        professors.add(newProf);
        System.out.println("Professor sign-up successful! You can now log in.");
        return newProf;
    }
    
    
    public void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(this);
        } catch (IOException e) {
            System.out.println("Warning: Could not save data to file: " + e.getMessage());
        }
    }

    public static DataManager loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            System.out.println("No saved data found. Initializing default university data...");
            return new DataManager();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            DataManager loaded = (DataManager) ois.readObject();
            System.out.println("Saved university data loaded successfully.");
            return loaded;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Could not read saved data (" + e.getMessage() + "). Starting with default data.");
            return new DataManager();
        }
    }
	
}
