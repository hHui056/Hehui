package com.beidouapp.et.client.domain;

import java.io.Serializable;

/**
 * 用户信息.
 * 
 * @author mhuang.
 */
public class UserInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 用户id(clientId). */
	private String userid;

	/** 用户名称. */
	private String username;

	/** 用户昵称. */
	private String nickname;

	public UserInfo() {

	}

	public UserInfo(String userName) {
		this.username = userName;
	}

	public UserInfo(String userid, String userName) {
		this.userid = userid;
		this.username = userName;
	}

	public String getUsername() {
		return username;
	}

	public UserInfo setUsername(String userName) {
		this.username = userName;
		return this;
	}

	public String getUserid() {
		return userid;
	}

	public UserInfo setUserid(String userid) {
		this.userid = userid;
		return this;
	}

	public String getNickname() {
		return this.nickname;
	}

	public UserInfo setNickname(String nickname) {
		this.nickname = nickname;
		return this;
	}

	@Override
	public String toString() {
		return "UserInfo [userid=" + userid + ", username=" + username + ", nickname=" + nickname + "]";
	}
}