/**
 * 
 */
package com.beidouapp.et.util;

/**
 * @author ztao
 * 
 */
public class LogFactory {

	public static Log getLog(String type) {
		if ("java".equals(type)) {
			return new JLog();
		}
		return null;
	}
}
