package models;

import professor.Professor;
import java.io.Serializable;
import student.Student;
import java.util.ArrayList;
import java.util.List;


public class Course implements Serializable {
    private static final long serialVersionUID = 1L;
	private String courseCode;
    private String title;
    private int credits;
    private String timings;
    private String location;
    private int semester;
    private int enrollmentLimit;
    private String syllabus;
    private String officeHours;
    private Professor assignedProfessor;
    private List<Course> prerequisites;
    private List<Student> enrolledStudents;
	
    public Course(String courseCode, String title, int credits, String timings,int enrollmentLimit, int semester, String location) {
	  this.courseCode = courseCode;
	  this.title = title;
	  this.credits = (credits == 2 || credits == 4) ? credits : 4;
	  this.timings = timings;
	  this.enrollmentLimit = Math.max(enrollmentLimit, 1);
	  this.setSemester(Math.max(semester, 1));
	  this.setLocation((location != null && !location.isEmpty()) ? location : "TBA");
	  this.syllabus = "Not yet updated";
	  this.officeHours = "TBA";
	  this.prerequisites = new ArrayList<>();
	  this.enrolledStudents = new ArrayList<>();
	}
	
    
    //overloading 
    public Course(String courseCode, String title, int credits, String timings, int enrollmentLimit) {
        this(courseCode, title, credits, timings, enrollmentLimit, 1, "TBA");
    }
    
    
    @Override
    public String toString() {
        return courseCode + " - " + title + " (" + credits + " credits, Sem " + semester + ")";
    }
    
    
	public void setAssignedProfessor(Professor assignedProfessor) {
		this.assignedProfessor = assignedProfessor;
	}


	public String getCourseCode() {
		return this.courseCode;
	}

	public String getTitle() {
		return this.title;
	}

	public int getCredits() {
		return credits;
	}

	public void setCredits(int credits) {
		if (credits == 2 || credits == 4) {
            this.credits = credits;
        } else {
            System.out.println("Invalid credits. Course credits must be 2 or 4.");
        }
	}

	public String getTimings() {
		return timings;
	}

	public void setTimings(String timings) {
		this.timings = timings;
	}

	public int getEnrollmentLimit() {
		return enrollmentLimit;
	}

	public void setEnrollmentLimit(int enrollmentLimit) {
		if (enrollmentLimit >= this.enrolledStudents.size()) {
            this.enrollmentLimit = enrollmentLimit;
        } else {
            System.out.println("Enrollment limit cannot be less than current enrolled students ("
                    + this.enrolledStudents.size() + ").");
        }
	}

	public List <Course> getPrerequisites() {
		return prerequisites;
	}

	public void setPrerequisites(List <Course> prerequisites) {
		this.prerequisites = prerequisites;
	}

	public void setSyllabus(String syllabus) {
        this.syllabus = syllabus;
    }

	public List<Student> getEnrolledStudents() {
	    return this.enrolledStudents;
	}

	public String getSyllabus() {
	    return this.syllabus;
	}

	public String getOfficeHours() {
	    return this.officeHours;
	}

	public void setOfficeHours(String officeHours) {
        this.officeHours = officeHours;
    }

	public void addPrerequisite(Course prereq) {
        if (prereq != null && !prereq.equals(this) && !this.prerequisites.contains(prereq)) {
            this.prerequisites.add(prereq);
        }
    }

	public void removePrerequisite(Course prereq) {
        this.prerequisites.remove(prereq);
    }


	public int getSemester() {
		return semester;
	}


	public boolean isFull() {
        return this.enrolledStudents.size() >= this.enrollmentLimit;
    }
	
	public boolean enrollStudent(Student student) {
        if (isFull() || this.enrolledStudents.contains(student)) {
            return false;
        }
        this.enrolledStudents.add(student);
        return true;
    }
	
	public void removeStudent(Student student) {
        this.enrolledStudents.remove(student);
    }
	
	
	public void setSemester(int semester) {
		if (semester >= 1) {
            this.semester = semester;
        }
	}


	public String getLocation() {
		return location;
	}


	public void setLocation(String location) {
		this.location = location;
	}
	
	public void updateDetails(String syllabus, String timings, int credits,int enrollmentLimit, String officeHours, String location) {
		setSyllabus(syllabus);
		setTimings(timings);
		setCredits(credits);
		setEnrollmentLimit(enrollmentLimit);
		setOfficeHours(officeHours);
		setLocation(location);
	}
	
	public String getDetails() {
        String profName = (assignedProfessor != null) ? assignedProfessor.getEmail() : "Unassigned";

        StringBuilder prereqList = new StringBuilder();
        if (prerequisites.isEmpty()) {
            prereqList.append("None");
        } else {
            for (int i = 0; i < prerequisites.size(); i++) {
                prereqList.append(prerequisites.get(i).getCourseCode());
                if (i < prerequisites.size() - 1) {
                    prereqList.append(", ");
                }
            }
        }

        return String.format(
                "Course Code: %s | Title: %s | Semester: %d | Credits: %d%n" +
                "Professor: %s | Timings: %s | Location: %s%n" +
                "Enrollment: %d/%d | Office Hours: %s%n" +
                "Prerequisites: %s%n" +
                "Syllabus: %s",
                courseCode, title, semester, credits,
                profName, timings, location,
                enrolledStudents.size(), enrollmentLimit, officeHours,
                prereqList.toString(), syllabus
        );
    }


	public Professor getAssignedProfessor() {
	    return this.assignedProfessor;
	}
	
}
