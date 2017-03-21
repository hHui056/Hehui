package com.beidouapp.et.client.domain;

import java.util.Arrays;

/**
 * IM消息对象.</br>
 * 
 * @author mhuang.
 *
 */
public class EtMsg {
	/** 消息唯一标识(用户自己设置). */
	private String msgId;

	/** 主题. */
	private String topic;

	/** 消息内容. */
	private byte[] payload;

	/** 消息质量等级. */
	private int qos;

	/** 消息发送者. */
	private String sendUserId;

	/**
	 * 获得 消息唯一标识(用户自己设置).
	 * 
	 * @return
	 */
	public String getMsgId() {
		return msgId;
	}

	/**
	 * 设置 消息唯一标识(用户自己设置).
	 * 
	 * @param msgId
	 * @return
	 */
	public EtMsg setMsgId(String msgId) {
		this.msgId = msgId;
		return this;
	}

	/**
	 * 主题.
	 * 
	 * @return
	 */
	public String getTopic() {
		return topic;
	}

	/**
	 * 主题
	 * 
	 * @param topic
	 * @return
	 */
	public EtMsg setTopic(String topic) {
		this.topic = topic;
		return this;
	}

	/**
	 * 内容.
	 * 
	 * @return
	 */
	public byte[] getPayload() {
		return payload;
	}

	/**
	 * 内容.
	 * 
	 * @param payload
	 * @return
	 */
	public EtMsg setPayload(byte[] payload) {
		this.payload = payload;
		return this;
	}

	/**
	 * 消息质量等级.
	 * 
	 * @return
	 */
	public int getQos() {
		return qos;
	}

	/**
	 * 消息质量等级.
	 * 
	 * @param qos
	 * @return
	 */
	public EtMsg setQos(int qos) {
		this.qos = qos;
		return this;
	}

	/**
	 * 消息发送者.
	 * 
	 * @return
	 */
	public String getSendUserId() {
		return sendUserId;
	}

	/**
	 * 消息发送者.
	 * 
	 * @param sendUserId
	 */
	public EtMsg setSendUserId(String sendUserId) {
		this.sendUserId = sendUserId;
		return this;
	}

	@Override
	public String toString() {
		return "EtMsg [msgId=" + msgId + ", topic=" + topic + ", payload=" + Arrays.toString(payload) + ", qos=" + qos + ", sendUserId=" + sendUserId + "]";
	}
}