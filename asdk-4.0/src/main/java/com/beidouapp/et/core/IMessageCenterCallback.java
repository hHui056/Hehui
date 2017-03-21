/**
 * 
 */
package com.beidouapp.et.core;

import com.beidouapp.et.Server;

/**
 * 消息中心回调函数
 */
public interface IMessageCenterCallback {
	/**
	 * 接收到设备/mqtt服务器返回的消息
	 * 
	 * @param msg
	 */
	public void onMessageArrived(Server peer, EtMessage msg);

	/**
	 * 消息发送成功
	 * 
	 * @param msg
	 */
	public void onMsgSendSuccess(EtMessage msg);

	/**
	 * 与设备或者服务器断开连接
	 * 
	 * @param peer
	 * @param errorCode
	 */
	public void onConnectLost(Server svr, int errorCode);
}
