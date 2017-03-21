/**
 * 
 */
package com.beidouapp.et;

/**
 * A-SDK的消息体，消息内容使用UTF-8编码。
 * 
 */
public class Message {
	/** 消息内容 */
	private byte[] payload;

	public Message() {

	}

	public Message(byte[] payload) {
		if (payload == null) {
			throw new NullPointerException("payload can not be null!");
		}
		this.payload = payload;
	}

	/**
	 * 设置消息内容。
	 * 
	 * @param payload
	 *            消息内容。
	 * @throws NullPointerException
	 *             如果{@link #payload}为null。
	 */
	public void setPayload(byte[] payload) throws NullPointerException {
		if (payload == null) {
			throw new NullPointerException("payload can not be null!");
		}
		this.payload = payload;
	}

	/**
	 * 获取消息内容。
	 * 
	 * @return 消息内容。
	 */
	public byte[] getPayload() {
		return payload;
	}
}
