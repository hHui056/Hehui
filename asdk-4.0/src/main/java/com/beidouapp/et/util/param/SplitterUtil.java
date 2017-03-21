package com.beidouapp.et.util.param;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 字符串分割工具类.
 * 
 * @author mhuang.
 */
public class SplitterUtil
{
    public static List <String> splitterList (String content, String separator)
    {
        if (separator == null || separator.length () == 0)
        {
            throw new IllegalArgumentException ("Separator can not be empty!");
        }
        if (content == null || content.length () == 0)
        {
            return Collections.emptyList ();
        }
        String[] arr = content.split (separator);
        return Arrays.asList (arr);
    }
}