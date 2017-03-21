package com.beidouapp.et.client.domain;

/**
 * 文件信息.
 * 
 * @author mhuang.
 */
public class DocumentInfo {
	/** 文件名称. */
	private String fileName;

	/** 文件大小(单位字节). */
	private String size;

	/** 文件服务器上的ID. */
	private String fileId;

	/** Topic类型(pull、push). */
	private String type;

	/** 文件跟踪服务器IP. */
	private String ip;

	/** 文件跟踪服务器端口. */
	private int port;

	/** 文件校验码. */
	private String crc;

	/** 描述信息. */
	private String descn;

	/** 文件在服务器上的HTTP(s)地址. */
	private String url;
	/** 文件上传时间 */
	private long time;

	public DocumentInfo() {

	}

	public DocumentInfo(String fileName, String size, String fileId, String type) {
		this.fileName = fileName;
		this.size = size;
		this.fileId = fileId;
		this.type = type;
	}

	public DocumentInfo(String fileName, String size, String fileId,
			String type, String url) {
		this(fileName, size, fileId, type);
		this.url = url;
	}

	/**
	 * 设置文件上传时间
	 * 
	 * @param time
	 */
	public void setTime(long time) {
		this.time = time;
	}

	/**
	 * 获取文件时间
	 * 
	 * @return
	 */
	public long getTime() {
		return time;
	}

	/**
	 * 文件名称.
	 * 
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * 文件名称.
	 * 
	 * @param fileName
	 */
	public DocumentInfo setFileName(String fileName) {
		this.fileName = fileName;
		return this;
	}

	/**
	 * 文件大小(单位KB).
	 * 
	 * @return
	 */
	public String getSize() {
		return size;
	}

	/**
	 * 文件大小(单位KB).
	 * 
	 * @param size
	 */
	public DocumentInfo setSize(String size) {
		this.size = size;
		return this;
	}

	/**
	 * 文件服务器上的URL路径.
	 * 
	 * @return
	 */
	public String getFileId() {
		return fileId;
	}

	/**
	 * 文件服务器上的URL路径.
	 * 
	 * @param url
	 */
	public DocumentInfo setFileId(String fileId) {
		this.fileId = fileId;
		return this;
	}

	/**
	 * Topic类型(pull、push).
	 * 
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * Topic类型(pull、push).
	 * 
	 * @param type
	 */
	public DocumentInfo setType(String type) {
		this.type = type;
		return this;
	}

	public String getIp() {
		return ip;
	}

	public DocumentInfo setIp(String ip) {
		this.ip = ip;
		return this;
	}

	public int getPort() {
		return port;
	}

	public DocumentInfo setPort(int port) {
		this.port = port;
		return this;
	}

	public String getDescn() {
		return descn;
	}

	public DocumentInfo setDescn(String descn) {
		this.descn = descn;
		return this;
	}

	/**
	 * 文件校验码.
	 * 
	 * @return
	 */
	public String getCrc() {
		return crc;
	}

	/**
	 * 文件校验码.
	 * 
	 * @param crc
	 */
	public DocumentInfo setCrc(String crc) {
		this.crc = crc;
		return this;
	}

	/**
	 * 文件在服务器上的HTTP(s)地址.
	 * 
	 * @return
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * 文件在服务器上的HTTP(s)地址.
	 * 
	 * @param url
	 */
	public DocumentInfo setUrl(String url) {
		this.url = url;
		return this;
	}

	@Override
	public String toString() {
		return "DocumentInfo [fileName=" + fileName + ", size=" + size
				+ ", fileId=" + fileId + ", type=" + type + ", ip=" + ip
				+ ", port=" + port + ", crc=" + crc + ", descn=" + descn
				+ ", url=" + url + "]";
	}
}