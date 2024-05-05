package Beans;

import java.io.Serializable;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import DataModels.User;

@SessionScoped
@Named
@Stateful
public class LoggedUser implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private User LoggedUser = null;
	
	public User getLoggedUser() {
		return LoggedUser;
	}
	
	public void setLoggedUser(User user) {
		this.LoggedUser = user;
	}
	
	public void logout() {
		this.LoggedUser = null;
	}
	
	public boolean isLoggedIn() {
		return this.LoggedUser != null;
	}

	

}
