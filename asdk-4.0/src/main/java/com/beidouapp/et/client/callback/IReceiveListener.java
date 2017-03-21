package com.beidouapp.et.client.callback;

import com.beidouapp.et.client.domain.EtMsg;

/**
 * 接收服务器消息监听器.
 *
 * @author mhuang.
 */
public interface IReceiveListener extends IListener {
	/**
	 * 连接丢失.
	 * 
	 * @param cause
	 */
	public void connectionLost(Throwable cause);

	/**
	 * 消息处理.
	 * 
	 * @param msg
	 *            IM消息对象.
	 * @see EtMsg
	 */
	public void onMessage(EtMsg msg);
}