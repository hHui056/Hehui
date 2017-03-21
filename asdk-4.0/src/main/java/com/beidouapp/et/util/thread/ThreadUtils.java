package com.beidouapp.et.util.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程工具类.
 * 
 * @author mhuang.
 */
public class ThreadUtils
{
    private static ExecutorService threadPools = Executors.newCachedThreadPool ();

    /**
     * 执行线程.
     * 
     * @param runnable 可执行接口对象.
     */
    public static void doExecute (Runnable runnable)
    {
        threadPools.execute (runnable);
    }

    public static void stop ()
    {
        threadPools.shutdown ();
    }
}
