package com.beidouapp.et.client.callback;

/**
 * 用户状态接口.<br/>
 * 处理关于用户的所有状态定义接口.
 * 
 * @author mhuang.
 */
public interface IUserStatusListener extends IListener{

	/**
	 * 关注的用户上下线状态变更处理.<br/>
	 * 当用户关注指定用户后，
	 * 
	 * @param userId
	 *            关注的用户Id.
	 * @param statusCode
	 *            值域[0:离线, 1:在线].
	 */
	public void concernOnlineStatus(String userId, String statusCode);
}