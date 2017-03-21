package com.beidouapp.et.common.constant;

/**
 * 常量集合类型.
 *
 * @author mhuang.
 */
public class EtConstants {
    /** -------------------------分隔符--------------------------------. */
    /**
     * 协议版本号.
     */
    public static final String PROTOCOL_VERSION = "3.1.1";

    /**
     * 分隔符 冒号.
     */
    public static final String SEPARATOR_COLON = ":";

    /**
     * 分隔符 逗号.
     */
    public static final String SEPARATOR_COMMA = ",";

    /**
     * 分隔符 英文句号.
     */
    public static final String SEPARATOR_DOT = ".";

    /**
     * 分隔符 @.
     */
    public static final String SEPARATOR_AIT = "@";

    /**
     * 竖线分隔符|.
     */
    public static final String SEPARATOR_VERTICAL = "|";

    /**
     * 分隔符 &.
     */
    public static final String SEPARATOR_VERSUS = "&";

    /**
     * 服务器IP信息key.
     */
    public static final String SERVER_ADDRESS = "_server_address_";

    /**
     * 服务器类型key.
     */
    public static final String SERVER_TYPE = "_server_type_";

    /**
     * APP授权key.
     */
    public static final String APP_KEY = "appkey";
    /**
     * 加密串key.
     */
    public static final String ENCRYPT_KEY = "encrypt";

    /**
     * 安全秘钥key.
     */
    public static final String SECRET_KEY = "secretkey";

    /**
     * App Token key.
     */
    public static final String TOKEN_KEY = "apptoken";
    /**
     * UID.
     */
    public static final String UID = "uid";
    /**
     * 随机信息key.
     */
    public static final String RANDOM_KEY = "random";

    /**
     * 业务数据key.
     */
    public static final String DATA_KEY = "data";

    /**
     * 随机数截取长度(10位).
     */
    public static final int RANDOM_LENGTH = 10;

    /**
     * HTTP协议(http://).
     */
    public static final String PROTOCOL_HTTP = "http://";

    /**
     * TCP协议(tcp://).
     */
    public static final String PROTOCOL_TCP = "tcp://";

    /**
     * 消息响应编码.
     */
    public static final String MSG_RESPONSE_CODE = "ret";

    /**
     * 消息响应内容.
     */
    public static final String MSG_RESPONSE_CONTENT = "message";

    /**
     * 字符编码(UTF-8).
     */
    public static final String CHARSETS_UTF8 = "UTF-8";

    /**
     * MD5.
     */
    public static final String MD5 = "MD5";

    /**
     * 默认消息处理最大长度.
     */
    public static final int DEFAULT_MSG_MAX_INFLIGHT_LENGTH = 65535;

    /**
     * 接收消息监听器常量.
     */
    public static final String LISTENER_RECEIVE = "listenerReceive";

    /**
     * 主题.
     */
    public static final String TOPIC = "topic";

    /**
     * 主题内容.
     */
    public static final String CONTENT = "content";

    /**
     * MsgClient.
     */
    public static final String MSG_CLIENT = "MsgClient";

    /**
     * countDownLatch标识符.
     */
    public static final String COUNT_DOWN_LATCH = "countDownLatch";

    /**
     * 系统默认回车换行符.
     */
    public static String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * Java-SDK 平台标识符.
     */
    public static Integer PLATFORM_FOR_S_SDK = 6;

    /**
     * 发送给自己的异常信息.
     */
    public static String SENT_TO_YOURSELF_EXCEPTION = "you can not be send to yourself.";

    /**
     * IM Publish消息前缀 .
     */
    public static String PUBLISH_PREFIX = "p@";
}