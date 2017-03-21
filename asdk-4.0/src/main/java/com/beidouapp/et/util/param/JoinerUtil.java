package com.beidouapp.et.util.param;

import java.util.List;

/**
 * 对象连接工具类.
 * 
 * @author mhuang.
 */
public class JoinerUtil
{
    public static <T> String joinList (List <T> list, String separator)
    {
        StringBuilder sb = new StringBuilder (list.size () * 10);
        for (int i = 0; i < list.size (); i++)
        {
            T t = list.get (i);
            if (t == null)
            {
                continue;
            }
            sb.append (t.toString ());
            if (i != list.size () - 1)
            {
                sb.append (separator);
            }
        }
        return sb.toString ();
    }
}