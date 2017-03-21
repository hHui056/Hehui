package com.beidouapp.et.util.param;

import com.beidouapp.et.exception.EtExceptionCode;
import com.beidouapp.et.exception.EtRuntimeException;

import java.util.Collection;

/**
 * 校验工具类.
 * 
 * @author mhuang.
 */
public class CheckingUtil {
	/**
	 * 检查集合是否为空.
	 * 
	 * @param collection
	 * @return true is null or empty, other false;
	 */
	public static <T> boolean isEmpty(Collection<T> collection) {
		return (collection == null || collection.isEmpty());
	}

	public static <T> boolean isNull(T param) {
		return param == null;
	}

	/**
	 * 校验集合为空.<br />
	 * 为null 或 空集合 则抛运行期异常.
	 * 
	 * @param collection
	 * @param errorMessage
	 */
	public static <T> void checkEmpty(Collection<T> collection, String errorMessage) {
		if (isEmpty(collection)) {
			String s = "collection is null or empty!";
			if (errorMessage != null) {
				s = errorMessage + " collection is null or empty!";
			}
			throw new EtRuntimeException(EtExceptionCode.PARAM_NULL_OR_EMPTY, s);
		}
	}

	/**
	 * 验证参数是否为空. <br />
	 * 未通过检查则抛空指针异常.
	 * 
	 * @param param
	 *            受检查的参数.
	 * @param errorMessage
	 *            返回的消息提示.
	 */
	public static <T> T checkNull(T param, String errorMessage) {
		if (param == null) {
			throw new EtRuntimeException(EtExceptionCode.PARAM_NULL, String.valueOf(errorMessage));
		}
		return param;
	}

	public static String checkNullOrEmpty(String param, String errorMessage) {
		if (param == null || param.isEmpty()) {
			throw new EtRuntimeException(EtExceptionCode.PARAM_NULL_OR_EMPTY, String.valueOf(errorMessage));
		}
		return param;
	}

	public static void checkArgument(boolean expression, Object errorMessage) {
		if (!expression) {
			if (errorMessage == null) {
				throw new EtRuntimeException(EtExceptionCode.PARAM_ILLEGAL, "param illegal!");
			}
			throw new EtRuntimeException(EtExceptionCode.PARAM_ILLEGAL, String.valueOf(errorMessage));
		}
	}

	public static <T> T checkNotNull(T reference, Object errorMessage) {
		if (reference == null) {
			if (errorMessage == null) {
				throw new EtRuntimeException(EtExceptionCode.PARAM_NULL, "object is null.");
			}
			throw new EtRuntimeException(EtExceptionCode.PARAM_NULL, String.valueOf(errorMessage));
		}
		return reference;
	}

	public static void checkState(boolean expression, Object errorMessage) {
		if (!expression) {
			throw new EtRuntimeException(EtExceptionCode.PARAM_STATE_ILLEGAL, String.valueOf(errorMessage));
		}
	}

	/**
	 * 判断是否为空.<br/>
	 * null or empty is ture. otherwise false;
	 * 
	 * @param string
	 *            被验证的字符串.
	 * @return
	 */
	public static boolean isNullOrEmpty(String string) {
		return string == null || string.length() == 0;
	}

	/**
	 * 判断Integer参数是否在指定范围内.<br/>
	 * true:in range, otherwise false;
	 * 
	 * @param param
	 *            被验证的Integer.
	 * @param begin
	 *            开始范围.
	 * @param end
	 *            结束范围.
	 * @return
	 */
	public static boolean isRangeWhitInteger(Integer param, int begin, int end) {
		if (param == null) {
			return false;
		}
		int p = param.intValue();
		if (p >= begin && p <= end) {
			return true;
		}
		return false;
	}
}