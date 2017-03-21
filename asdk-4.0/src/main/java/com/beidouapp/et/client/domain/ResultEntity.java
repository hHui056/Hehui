package com.beidouapp.et.client.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * 结果实体信息.
 * 
 * @author mhuang.
 */
public class ResultEntity {

	/** 结果编码. */
	private String code;

	/** 消息描述. 配合code编码使用. */
	private String messageInfo;

	/** 简单数据结果 存储简单的信息. */
	private String simpleData;

	/** 原始json字符串 可为空. */
	private String originJsonString;

	/** 存储复杂结果的map. */
	private Map<String, Object> dataMap = new HashMap<String, Object>();

	public ResultEntity() {
	}

	public ResultEntity(String code, String info) {
		this.code = code;
		this.messageInfo = info;
	}

	/**
	 * 结果编码.
	 * 
	 * @return
	 */
	public String getCode() {
		return this.code;
	}

	/**
	 * 结果编码.
	 * 
	 * @param code
	 * @return
	 */
	public ResultEntity setCode(String code) {
		this.code = code;
		return this;
	}

	/**
	 * 消息描述. 配合code编码使用. <br/>
	 * 非零时,此消息内容代表异常信息.
	 * 
	 * @return
	 */
	public String getMessageInfo() {
		return this.messageInfo;
	}

	/**
	 * 消息描述. 配合code编码使用. <br/>
	 * 非零时,此消息内容代表异常信息.
	 * 
	 * @param info
	 * @return
	 */
	public ResultEntity setMessageInfo(String info) {
		this.messageInfo = info;
		return this;
	}

	/**
	 * 存储复杂结果的Map.
	 * 
	 * @return
	 */
	public Map<String, Object> getData() {
		return this.dataMap;
	}

	/**
	 * 存储复杂结果的Map.
	 * 
	 * @param dataMap
	 * @return
	 */
	public ResultEntity setData(Map<String, Object> dataMap) {
		this.dataMap = dataMap;
		return this;
	}

	/**
	 * 向复杂数据对象中 链式添加键值对.
	 * 
	 * @param key
	 *            键.
	 * @param value
	 *            key对应的值.
	 * @return
	 */
	public ResultEntity addData(String key, Object value) {
		this.dataMap.put(key, value);
		return this;
	}

	/**
	 * 简单数据结果 存储简单的信息.
	 * 
	 * @return
	 */
	public String getSimpleData() {
		return this.simpleData;
	}

	/**
	 * 简单数据结果 存储简单的信息.
	 * 
	 * @param simpleData
	 * @return
	 */
	public ResultEntity setSimpleData(String simpleData) {
		this.simpleData = simpleData;
		return this;
	}

	/**
	 * 原始json字符串 可为空.
	 * 
	 * @return
	 */
	public String getOriginJsonString() {
		return this.originJsonString;
	}

	/**
	 * 原始json字符串 可为空.
	 * 
	 * @param originJsonString
	 * @return
	 */
	public ResultEntity setOriginJsonString(String originJsonString) {
		this.originJsonString = originJsonString;
		return this;
	}

	@Override
	public String toString() {
		return "ResultEntity [code=" + code + ", messageInfo=" + messageInfo + ", simpleData=" + simpleData
				+ ", originJsonString=" + originJsonString + ", dataMap=" + dataMap + "]";
	}
}