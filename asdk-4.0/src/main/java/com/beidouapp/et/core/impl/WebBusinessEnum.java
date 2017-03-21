package com.beidouapp.et.core.impl;

/**
 * Web业务类型枚举.
 * 
 * @author mhuang.
 */
public enum WebBusinessEnum {
	/** 添加用户. */
	ADD_USER("/webserver_user/adduser", "添加用户"),

	/** 删除用户. */
	DELETE_USER("/webserver_user/deleteuser", "删除用户"),

	/** 查询用户在线状态. */
	GET_USER_STATE("/webserver_user/getUserState", "查询用户在线状态"),

	/** 注册管理员. */
	REGISTER("/webserver_user/register", "注册管理员"),

	/** 添加好友. */
	ADD_FRIEND("/webserver_friend/addfriend", "添加好友"),

	/** 添加好友扩展. */
	ADD_FRIEND_EX("/webserver_friend/addfriendex", "添加好友扩展"),

	/** 删除好友. */
	DELETE_FRIEND("/webserver_friend/deletefriend", "删除好友"),

	/** 删除好友扩展. */
	DELETE_FRIEND_EX("/webserver_friend/deletefriendex", "删除好友扩展"),

	/** 获取好友列表. */
	GET_FRIEND_LISTS("/webserver_friend/getfriendlists", "获取好友列表"),

	/** 创建群. */
	CREATE_GROUP("/webserver_group/creategroup", "创建群"),

	/** 获取群列表. */
	GET_GROUP_LIST("/webserver_group/getgrouplist", "获取群列表"),

	/** 注销群. */
	RELEASE_GROUP("/webserver_group/releasegroup", "注销群"),

	/** 用户主动退出群. */
	LOGOUT_GROUP("/webserver_group/logoutgroup", "用户主动退出群"),

	/** 添加群成员. */
	ADD_GROUP_MEMBER("/webserver_group/addgroupmember", "添加群成员"),

	/** 删除群成员. */
	DELETE_GROUP_MEMBER("/webserver_group/deletegroupmember", "删除群成员"),

	/** 获取群成员列表. */
	GET_GROUP_USER_LISTS("/webserver_group/getgroupuserlists", "获取群成员列表"),

	/** 发布消息. */
	PUBLISH("/webserver_message/publish", "发布消息");

	/** 字典类型代码. */
	private String m_code;

	/** 字典类型名称. */
	private String m_name;

	/**
	 * 构造一个类型.
	 * 
	 * @param code
	 *            代码
	 * @param name
	 *            名称
	 */
	private WebBusinessEnum(String code, String name) {
		this.m_code = code;
		this.m_name = name;
	}

	/**
	 * 获得类型代码.
	 * 
	 * @return 类型代码
	 */
	public String getCode() {
		return m_code;
	}

	/**
	 * 设置类型代码.
	 * 
	 * @param code
	 *            类型代码
	 */
	public void setCode(String code) {
		this.m_code = code;
	}

	/**
	 * 获得类型名称.
	 * 
	 * @return 类型名称
	 */
	public String getName() {
		return m_name;
	}

	/**
	 * 设置类型名称.
	 * 
	 * @param name
	 *            类型名称
	 */
	public void setName(String name) {
		this.m_name = name;
	}
}