package com.beidouapp.et.util;

/**
 * @author allen
 */
public class ParamUtil {
    static String[] specialStr = {"#", "+", "$"};

    /**
     * 检查参数是否存在null
     *
     * @param objs
     * @return 所有参数都不为null，返回false；否则返回true。
     */
    public static boolean isNull(Object... objs) {
        boolean yes = false;
        if (objs == null) {
            yes = true;
        } else {
            for (Object obj : objs) {
                if (obj == null) {
                    yes = true;
                    break;
                }
            }
        }
        return yes;
    }

    /**
     * 检查是否含有特殊字符
     *
     * @param str
     * @return 有則返回true；沒有返回false
     */
    public static boolean isTopicContansSpecialStr(String str) {
        boolean a = false;
        for (String str1 : specialStr) {
            if (str.contains(str1)) {
                a = true;
                break;
            }
        }
        return a;
    }

    /**
     * 检查Uid是否合法 只允许字母数字  长度为34
     *
     * @param uid
     * @return
     */
    public static boolean isUidContansSpeStr(String uid) {
        boolean isLegal = false;
        String regex = "^[0-9A-Za-z]+$";
        if (!uid.matches(regex)) {
            isLegal = true;
        } else if (uid.length() != 34) {
            isLegal = true;
        }

        return isLegal;
    }

    /**
     * 检查groupId是否合法 只允许小写字母、数字 和 "-"
     *
     * @param groupId
     * @return
     */
    public static boolean isGroupIdHaveSpeStr(String groupId) {
        boolean isHaveSpeStr = false;
        String regex = "^[0-9a-z-]+$";
        if (!groupId.matches(regex)) {
            isHaveSpeStr = true;
        }
        return isHaveSpeStr;
    }

    /**
     * 检查appkey是否合法 只允许小写字母、数字 和 "-"  长度为 20
     *
     * @param appkey
     * @return
     */
    public static boolean isAppKeyHaveSpeStr(String appkey) {
        boolean isHaveSpeStr = false;
        String regex = "^[0-9a-z-]+$";
        if (!appkey.matches(regex)) {
            isHaveSpeStr = true;
        } else if (appkey.length() != 20) {
            isHaveSpeStr = true;
        }
        return isHaveSpeStr;
    }

    /**
     * 检查 secretKey是否合法 只允许输入小写字母和数字  长度为 32
     *
     * @param secretkey
     * @return
     */
    public static boolean isSecretKeyHaveSpeStr(String secretkey) {
        boolean isHaveSpeStr = false;
        String regex = "^[0-9a-z]+$";
        if (!secretkey.matches(regex)) {
            isHaveSpeStr = true;
        } else if (secretkey.length() != 32) {
            isHaveSpeStr = true;
        }
        return isHaveSpeStr;
    }

}
