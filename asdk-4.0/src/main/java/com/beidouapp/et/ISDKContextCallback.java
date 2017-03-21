/**
 * 
 */
package com.beidouapp.et;

import com.beidouapp.et.client.domain.DocumentInfo;

/**
 * 通知A-SDK的状态变化，接收到新消息，新文件，搜索到新服务器，与服务器丢失连接等。
 * 
 */
public interface ISDKContextCallback {

	/**
	 * 接收到新消息，根据<code>type</code>不同，<code>topic</code>的内容不同。
	 * <p>
	 * 当<code>type</code>是{@link MessageType#CHAT_TO}, <code>topic</code>
	 * 表示发送者的uid。<br>
	 * 当<code>type</code>是{@link MessageType#PUBLISH}, <code>topic</code>
	 * 表示消息的主题。
	 * </p>
	 * 
	 * @param type
	 *            消息类型。
	 * @param topic
	 *            主题。
	 * @param msg
	 *            消息。
	 */
	public void onMessage(MessageType type, String topic, Message msg);

	/**
	 * 关注的用户上下线状态变化通知。<br/>
	 * 
	 * @param uid
	 *            关注的用户Id。
	 * @param statusCode
	 *            值域[0:离线, 1:在线]。
	 */
	public void onPeerState(String uid, String statusCode);

	/**
	 * 有新的文件需要接收。
	 * <p>
	 * 使用
	 * {@link ISDKContext#downloadFile(DocumentInfo, String, com.beidouapp.et.client.callback.FileCallBack)}
	 * 下载文件。
	 * </p>
	 * 
	 * @param senderId
	 *            发送者的uid。
	 * @param documentInfo
	 *            文件信息。
	 */
	public void onFileReceived(String senderId, DocumentInfo documentInfo);

	/**
	 * 与<b>服务器</b>断开连接。
	 * 
	 * @param svr
	 *            已经断开的<b>服务器</b>
	 * @param errorCode
	 *            断开连接的原因代码
	 * @param reason
	 *            断开连接的原因解释
	 */
	public void onBroken(Server svr, int errorCode, String reason);

	/**
	 * 消息成功发送到<b>服务器</b>
	 * 
	 * @param svr
	 *            接收消息的<b>服务器</b>
	 * @param topic
	 *            消息的主题
	 * @param msg
	 *            消息
	 */
	// public void onDelivery(Server svr, String topic, Message msg);// XXX:不需要?

	/**
	 * 发现到新的<b>服务器</b>。
	 * 
	 * @param svr
	 *            新加入到网络中的<b>服务器</b>。
	 */
	public void onServer(Server svr);

}
