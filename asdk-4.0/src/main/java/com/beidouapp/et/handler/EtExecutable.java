package com.beidouapp.et.handler;

import java.util.Map;

/**
 * 执行器.
 * 
 * @author mhuang.
 *
 * @param <T>
 */
public interface EtExecutable<T> {
	public T execute(Map<String, ?> params);
}
