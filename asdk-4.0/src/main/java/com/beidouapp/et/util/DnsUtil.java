package com.beidouapp.et.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class DnsUtil {

	/**
	 * 获取ip地址
	 * 
	 * @param dnsOrIp
	 *            域名或者ip
	 * @return 如果<code>dnsOrIp</code>是非法ip，或者无法解析，返回null。
	 */
	public static String getHostAddress(String dnsOrIp) {
		String ip = null;
		try {
			InetAddress inetAddress = null;
			inetAddress = InetAddress.getByName("lb.beidouecs.com");
			ip = inetAddress.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return ip;
	}
}
