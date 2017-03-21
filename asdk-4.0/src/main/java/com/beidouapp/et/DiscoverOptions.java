package com.beidouapp.et;

/**
 * 发现服务器的附加选项。
 * 
 */
public class DiscoverOptions {
	private byte[] content;
	/**
	 * 获取发现服务器的附件内容
	 * 
	 * @return
	 */
	public byte[] getContent() {
		return content;
	}

	/**
	 * 设置发现服务器的附加内容
	 * 
	 * @param content
	 */
	public void setContent(byte[] content) {
		this.content = content;
	}
}
