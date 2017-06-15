package com.xxx.sample.spring.cloud.model.cas.user;

import java.io.Serializable;

/**
 * Created by Orrin on 2017/1/20.
 */
public class UserModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer Id;

	private String userName;
	private String userPwd;
	private String userMobile;
	private String loginName;

	public Integer getId() {
		return Id;
	}

	public void setId(Integer id) {
		Id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPwd() {
		return userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public String getUserMobile() {
		return userMobile;
	}

	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	@Override
	public String toString() {
		return "UserModel{" +
				"Id=" + Id +
				", userName='" + userName + '\'' +
				", userPwd='" + userPwd + '\'' +
				", userMobile='" + userMobile + '\'' +
				", loginName='" + loginName + '\'' +
				'}';
	}
}
