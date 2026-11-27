package models;

import student.Student;
import java.io.Serializable;

public class Complaint implements Serializable {
    private static final long serialVersionUID = 1L;
	private String description;
	private String status;
	private Student student;
	private String resolutionDetails;
	private String date;
	
	public Complaint(String description, Student student,String date) {
		super();
		this.description = description;
		this.student = student;
		this.status = "Pending";
		this.resolutionDetails = null;
        this.setDate((date != null && !date.trim().isEmpty()) ? date.trim() : "N/A");
	}
	
	// Overloaded constructor 
    public Complaint(String description, Student student) {
        this(description, student, "N/A");
    }
	
    
    @Override
    public String toString() {
        String res = (resolutionDetails != null && !resolutionDetails.isEmpty())
                ? " | Resolution: " + resolutionDetails
                : "";
        return "[" + status + "] (" + date + ") " + description
                + " | By: " + student.getEmail() + res;
    }
    

	public String getStatus() {
		return this.status;
	}
	

	public String getDescription() {
		return this.description;
	}

	
	public Student getStudent() {
		return this.student;
	}

	
	public void updateStatus(String newStatus) {
        if (newStatus != null &&
                (newStatus.equalsIgnoreCase("Pending") || newStatus.equalsIgnoreCase("Resolved"))) {
            this.status = newStatus.equalsIgnoreCase("Resolved") ? "Resolved" : "Pending";
        } else {
            System.out.println("Invalid status. Status must be 'Pending' or 'Resolved'.");
        }
    }
	

    public void setResolutionDetails(String resolutionDetails) {
        this.resolutionDetails = resolutionDetails;
    }
    

	public String getResolutionDetails() {
        return this.resolutionDetails;
    }
	

	public String getDate() {
		return date;
	}
	

	public void setDate(String date) {
        if (date != null && !date.trim().isEmpty()) {
            this.date = date.trim();
        }
    }
}
