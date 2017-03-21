package com.beidouapp.et.util.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * reflect util
 */
public class ReflectUtil {
	private static final Logger logger = LoggerFactory.getLogger(ReflectUtil.class.getName());

	/**
	 * set field value
	 * 
	 * @param target
	 * @param field
	 * @param value
	 */
	public static void setFieldValue(Object target, Field field, Object value) {
		try {
			field.setAccessible(true);
			field.set(target, value);
		} catch (Exception e) {
			logger.error("set field value error", e);
		}
	}

	/**
	 * set field value
	 * 
	 * @param target
	 * @param fieldName
	 * @param value
	 */
	public static void setFieldValue(Object target, String fieldName, Object value) {
		try {
			Field field = target.getClass().getDeclaredField(fieldName);
			setFieldValue(target, field, value);
		} catch (Exception e) {
			logger.error("set field value error", e);
		}

	}

	/**
	 * get field value
	 * 
	 * @param target
	 * @param fieldName
	 * @return
	 */
	public static Object getFieldValue(Object target, String fieldName) {
		Object value = null;
		try {
			Field field = target.getClass().getDeclaredField(fieldName);
			value = getFieldValue(target, field);
		} catch (Exception e) {
			logger.error("get field value error", e);
		}
		return value;
	}

	/**
	 * get field value
	 * 
	 * @param target
	 * @param field
	 * @return
	 */
	public static Object getFieldValue(Object target, Field field) {
		Object value = null;
		try {
			field.setAccessible(true);
			value = field.get(target);
		} catch (Exception e) {
			logger.error("get field value error", e);
		}
		return value;
	}

	/**
	 * copy value between two object
	 * 
	 * @param orig
	 * @param target
	 */
	public static void copy(Object orig, Object target) {
		Class<?> targetC = target.getClass();
		try {
			Field[] fields = orig.getClass().getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				Object value = getFieldValue(orig, field);

				Field targetField = targetC.getDeclaredField(field.getName());
				setFieldValue(target, targetField, value);
			}
		} catch (Exception e) {
			logger.error("copy property error", e);
		}
	}
}
