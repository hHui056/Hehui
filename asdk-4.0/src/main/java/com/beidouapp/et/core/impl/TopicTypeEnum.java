package com.beidouapp.et.core.impl;

/**
 * 消息类型枚举.
 * 
 * @author mhuang.
 */
public enum TopicTypeEnum {
	/**
	 * 点对点服务.
	 */
	CHAT("chat@", ""),

	/**
	 * 点对点消息(以json格式传送内容).
	 */
	CHAT_EX("chat_ex@", ""),
	
	/**
	 * publih消息
	 */
	PUBLISH("p@", ""),

	/**
	 * 文件传输服务.
	 */
	SFILE("sfile@", "http://"),

	/**
	 * 获取用户状态.
	 */
	STATUS("status@", ""),

	/**
	 * 踢人下线命名.
	 */
	SYS_KICK("sys@kick", ""),

	/**
	 * 主动获取离线消息.
	 */
	OFFLINEMSG("offline_msg@0", ""),

	/**
	 * 获取服务器时间消息.
	 */
	SERVER_DATETIME("systime@all", ""),

	/**
	 * 状态订阅.
	 */
	STATE_SUB("state_sub@", ""),

	/**
	 * 取消状态订阅.
	 */
	STATE_UNSUB("state_unsub@", ""),

	/**
	 * 用户上线状态.
	 */
	ONLINE("online@", ""),

	/**
	 * 用户下线状态.
	 */
	OFFLINE("offline@", "")

	;

	/** 字典类型代码. */
	private String code;

	// /** 字典类型名称. */
	// private String name;

	/** 协议名称. */
	private String protocolHeader;

	/**
	 * 构造一个类型.
	 * 
	 * @param code
	 *            代码
	 * @param name
	 *            名称
	 */
	private TopicTypeEnum(String code, String protocolHeader) {
		this.code = code;
		this.protocolHeader = protocolHeader;
	}

	/**
	 * 获得类型代码.
	 * 
	 * @return 类型代码
	 */
	public String getCode() {
		return code;
	}

	/**
	 * 设置类型代码.
	 * 
	 * @param code
	 *            类型代码
	 */
	public void setCode(String code) {
		this.code = code;
	}

	public String getProtocolHeader() {
		return protocolHeader;
	}

	public void setProtocolHeader(String protocolHeader) {
		this.protocolHeader = protocolHeader;
	}

	/**
	 * 检查编码是否包含指定的枚举.<br />
	 * 实现原理 str.indexOf().
	 * 
	 * @param code
	 * @return
	 */
	public static TopicTypeEnum getTopicTypeEnumByContainCode(String code) {
		if (code == null || code.isEmpty()) {
			throw new RuntimeException("编码不能为空!");
		}
		for (TopicTypeEnum e : TopicTypeEnum.values()) {
			if (code.indexOf(e.getCode()) != -1) {
				return e;
			}
		}
		throw new RuntimeException("没有找到编码=【" + code + "】的枚举类型.");
	}
}