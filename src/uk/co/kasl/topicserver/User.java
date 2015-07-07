package uk.co.kasl.topicserver;

public class User {
	public String username;
	public String password;
	public Role role;
	public enum Role{
		ADMIN,
		USER;
		
		public String getRole(Role role){
			String roleName = "";
			switch(role){
			case ADMIN: roleName = "Administrator"; break;
			case USER: roleName = "User"; break;
			}
			return roleName;
		}
	}
	public User(String user, String pass, Role userRole){
		username = user;
		password = pass;
		role = userRole;
		System.out.println("User " + username + " has been created.");
	}
	public User(String user, String pass){
		this(user, pass, Role.USER);
	}
}