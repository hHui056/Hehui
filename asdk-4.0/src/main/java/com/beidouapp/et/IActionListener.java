/**
 * 
 */
package com.beidouapp.et;

/**
 * Context的动作回调
 * 
 */
public interface IActionListener {

	/**
	 * 操作成功
	 */
	public void onSuccess();

	/**
	 * 操作失败
	 */
	public void onFailure(ErrorInfo errorInfo);
}
