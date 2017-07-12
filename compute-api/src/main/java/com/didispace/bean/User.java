package com.didispace.bean;

import java.io.Serializable;

public class User implements Serializable {

	private static final long serialVersionUID = -1L;
	
	private Long userId;
	
	private String userKind;
	
	private String userStatus;

	public Long getUserId() {
		return userId;
	}
	
	public User(){
	}
	public User(Long userId){
		this.userId = userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserKind() {
		return userKind;
	}

	public void setUserKind(String userKind) {
		this.userKind = userKind;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}
}
