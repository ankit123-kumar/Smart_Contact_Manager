package com.smart.entities;

public class Admin {

	private int adminId;
	private String name;
	private String email;
	private String password;
	private String phone;
	private String profilePic;
	private String role;
	private boolean enable;
	public int getAdminId() {
		return adminId;
	}
	public void setAdminId(int adminId) {
		this.adminId = adminId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getProfilePic() {
		return profilePic;
	}
	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	
	public boolean isEnable() {
		return enable;
	}
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	public Admin(int adminId, String name, String email, String password, String phone, String profilePic,
			String role) {
		super();
		this.adminId = adminId;
		this.name = name;
		this.email = email;
		this.password = password;
		this.phone = phone;
		this.profilePic = profilePic;
		this.role = role;
	}
	public Admin() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
