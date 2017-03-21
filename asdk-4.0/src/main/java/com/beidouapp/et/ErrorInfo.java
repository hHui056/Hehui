package com.beidouapp.et;

/**
 * SDK方法调用的错误信息。<\br> 通常在{@link IActionListener#onFailure(ErrorInfo)}中返回。
 *
 */
public class ErrorInfo {

	private int code;
	private String reason;

	public ErrorInfo(int code) {
		this(code, ErrorCode.getErrorReason(code));
	}

	public ErrorInfo(int code, String reason) {
		this.code = code;
		this.reason = reason;
	}

	/**
	 * 错误码。
	 * 
	 * @return 调用sdk的某个方法，发生异常，或者请求失败时的错误码。
	 * @see ErrorCode
	 */
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * 错误原因。
	 * 
	 * @return 调用sdk的某个方法，发生异常，或者请求失败时的原因；如果原因不详，为null。
	 */
	public String getReason() {
		if (reason == null) {
			reason = ErrorCode.getErrorReason(code);
		}
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

}
