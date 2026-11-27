package models;

import interfaces.UserActions;
import java.io.Serializable;

public abstract class User implements UserActions, Serializable {
    private static final long serialVersionUID = 1L;
	private String email;
	private String password;
	
	public User(String email, String password) {
		super();
		this.email = email;
		this.password = password;
	}
	
	public String getEmail() {
		return email;
	}
	public String getPassword() {
		return password;
	}
	
	public void setEmail(String email) {
	    this.email = email;
	}

	public void setPassword(String password) {
	    this.password = password;
	}
	
	public abstract void displayMenu();
	
}