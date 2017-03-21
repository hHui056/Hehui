package com.beidouapp.et.util;

/**
 * @author allen
 */
public class JLog implements Log {
    private final static int ERROR = 3;
    private final static int DEBUG = 1;
    private final static int INFO = 0;
    private final static int level = ERROR;

    @Override
    public void i(String tag, String msg) {
        if (level <= INFO) {
            System.out.println("[" + tag + "][INFO]" + ":" + msg);
        }
    }

    @Override
    public void d(String tag, String msg) {
        if (level <= DEBUG) {
            System.out.println("[" + tag + "][DEBUG]" + ":" + msg);
        }
    }

    @Override
    public void e(String tag, String msg) {
        if (level <= ERROR) {
            System.err.println("[" + tag + "][ERROR]" + ":" + msg);
        }
    }

}
