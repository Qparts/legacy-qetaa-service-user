package qetaa.service.user.model;

import java.io.Serializable;
import java.util.List;

public class UserHolder implements Serializable{

	private static final long serialVersionUID = 1L;
	private User user;
	private List<Role> roles;
	private List<Activity> activities;
	private String token;
	
	
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public User getUser() {
		return user;
	}
	public List<Role> getRoles() {
		return roles;
	}
	public List<Activity> getActivities() {
		return activities;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	public void setActivities(List<Activity> activities) {
		this.activities = activities;
	}
	
	
}
