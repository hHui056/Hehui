package com.beidouapp.et;

/**
 * 好友管理回调。
 *
 */
public interface IFriendsActionListener extends IActionListener {

	/**
	 * 操作结果的数据
	 * 
	 * @param data
	 *            具体的数据类型详见好友操作方法描述。
	 */
	public void onResultData(Object data);

}
