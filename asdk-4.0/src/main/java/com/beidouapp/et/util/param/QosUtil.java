package com.beidouapp.et.util.param;

import org.fusesource.mqtt.client.QoS;

/**
 * QoS质量工具类.
 * 
 * @author mhuang.
 */
public class QosUtil
{
    /**
     * 通过Level等级获取qos枚举.
     * 
     * @param level
     * @return
     */
    public static QoS getQos (int level)
    {
        CheckingUtil.checkArgument (!(level < 0 || level > 2), "Ranging from 0、1、2.");
        return QoS.values ()[level];
    }

}