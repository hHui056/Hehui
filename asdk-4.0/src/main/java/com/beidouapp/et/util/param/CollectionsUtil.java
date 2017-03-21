package com.beidouapp.et.util.param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 集合工具类.
 * 
 * @author mhuang.
 */
public class CollectionsUtil
{
    /**
     * 创建一个新集合.
     * 
     * @param t 指定的参数.
     * @return
     */
    public static <T> List <T> newArrayList (T... t)
    {
        List <T> list = new ArrayList <T> (t.length);
        for (T tt : t)
        {
            list.add (tt);
        }
        return list;
    }

    /**
     * 创建以K为键，V为值的Map对象. <br />
     * 由此类推.
     * 
     * @param k1 第一位的键.
     * @param v1 第一位的值.
     * @param k2 第二位的键.
     * @param v2 第二位的键.
     * @param k3
     * @param v3
     * @param k4
     * @param v4
     * @param k5
     * @param v5
     * @param k6
     * @param v6
     * @return
     */
    public static <K, V> Map <K, V> ImmutableMap (K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6,
                                                  V v6)
    {
        Map <K, V> map = new HashMap <K, V> (18);
        map.put (k1, v1);
        map.put (k2, v2);
        map.put (k3, v3);
        map.put (k4, v4);
        map.put (k5, v5);
        map.put (k6, v6);
        return map;
    }

    /**
     * 创建以K为键，V为值的Map对象. <br />
     * 由此类推.
     * 
     * @param k1
     * @param v1
     * @param k2
     * @param v2
     * @param k3
     * @param v3
     * @param k4
     * @param v4
     * @param k5
     * @param v5
     * @return
     */
    public static <K, V> Map <K, V> ImmutableMap (K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5)
    {
        Map <K, V> map = new HashMap <K, V> (15);
        map.put (k1, v1);
        map.put (k2, v2);
        map.put (k3, v3);
        map.put (k4, v4);
        map.put (k5, v5);
        return map;
    }

    public static <K, V> Map <K, V> ImmutableMap (K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4)
    {
        Map <K, V> map = new HashMap <K, V> (12);
        map.put (k1, v1);
        map.put (k2, v2);
        map.put (k3, v3);
        map.put (k4, v4);
        return map;
    }

    public static <K, V> Map <K, V> ImmutableMap (K k1, V v1, K k2, V v2, K k3, V v3)
    {
        Map <K, V> map = new HashMap <K, V> (10);
        map.put (k1, v1);
        map.put (k2, v2);
        map.put (k3, v3);
        return map;
    }

    public static <K, V> Map <K, V> ImmutableMap (K k1, V v1, K k2, V v2)
    {
        Map <K, V> map = new HashMap <K, V> (8);
        map.put (k1, v1);
        map.put (k2, v2);
        return map;
    }

    public static <K, V> Map <K, V> ImmutableMap (K k1, V v1)
    {
        Map <K, V> map = new HashMap <K, V> (5);
        map.put (k1, v1);
        return map;
    }

    public static void checkEntryNotNull (Object key, Object value)
    {
        if (key == null)
        {
            throw new NullPointerException ("null key in entry: null=" + value);
        }
        else if (value == null)
        {
            throw new NullPointerException ("null value in entry: " + key + "=null");
        }
    }
}