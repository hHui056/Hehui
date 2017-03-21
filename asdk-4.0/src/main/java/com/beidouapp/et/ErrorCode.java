package com.beidouapp.et;

import java.util.Hashtable;

/**
 * A-SDK的错误码。
 * <p>
 * 在{@link IActionListener#onFailure(ErrorInfo)}中使用。
 * </p>
 */
public class ErrorCode {
    // SDK错误1XXX, IM错误2xxx，FILE错误3xxx，WEB错误4xxx，AV错误5xxx，其他错误6xxx
    // ******************************** SDK错误 ********************************
    /**
     * 参数为null
     */
    public final static int NULL_PARAMETER = 1000;
    /**
     * 操作超时
     */
    public final static int TIMEOUT = 1001;
    /**
     * sdk内部状态错误
     */
    public final static int INTERNAL_ERROR = 1002;
    /**
     * Uid 不合法
     */
    public final static int UIDERROR = 1003;
    /**
     * groupId 不合法
     */
    public final static int GROUPID_ERROR = 1004;
    /**
     * appkey 不合法
     */
    public final static int APPKEY_ERROR = 1005;
    /**
     * secretkey 不合法
     */
    public final static int SECRETKEY_ERROR = 1006;

    /**
     * 还没有连接到指定的服务器，不能发送消息、文件等
     */
    public final static int NONE_SERVER_CONNECTED = 1100;
    /**
     * 已经与服务器建立了连接，不能重复连接
     */
    public final static int SERVER_HAS_CONNECTED = 1101;
    /**
     * 已经与服务器断开连接，不能重复断开连接
     */
    public final static int SERVER_HAS_DISCONNECTED = 1102;
    /**
     * 用户名错误
     */
    public final static int USERNAME_WRONG = 1103;
    /**
     * 密码错误
     */
    public final static int PASSWORD_WRONG = 1104;
    /**
     * 正在连接到服务器
     */
    public final static int CONNECTING = 1105;

    /**
     * 正在断开服务器
     */
    public final static int DISCONNECTING = 1106;
    /**
     * 与服务器丢失连接
     */
    public final static int CONNECTION_LOST = 1107;
    /**
     * 连接服务器失败
     */
    public final static int CONNECT_FAIL = 1108;
    /**
     * 连接服务器超时
     */
    public final static int CONNECT_SERVER_TIMEOUT = 1109;
    /**
     * 发现服务器失败
     */
    public final static int DISCOVER_SERVER_FAIL = 1200;
    /**
     * 初始化发现socket失败
     */
    public final static int DISCOVER_SERVER_INIT_FAIL = 1201;
    /**
     * UID在其他地方登陆，您被迫下线
     */
    public final static int LOGINED_IN_OTHER_PLACE = 1301;
    /**
     * 时间大小错误  <0
     */
    public final static int TIME_NUMBER_ERROR = 1302;
    /**
     * 服务器主动断连
     */
    public final static int PEER_DISCONNECTED = 1303;


    // ********* IM错误 ************

    // *****************************
    public final static int CHAT_TO_FAIL = 2010;
    public final static int PUBLISH_FAIL = 2020;
    public final static int SUBSCRIBE_FAIL = 2030;
    public final static int UNSUBSCRIBE_FAIL = 2040;
    public final static int PEER_STATE_FAIL = 2050;
    public final static int STATE_SUBSCRIBLE_FAIL = 2060;
    public final static int STATE_UNSUBSCRIBE_FAIL = 2070;
    public final static int REQUEST_OFFLINE_MESSAGE_FAIL = 2080;
    public final static int CANNOT_CHAT_TO_YOURSELF = 2090;
    /**
     * topic含特殊字符
     */
    public final static int TOPIC_CONTAINS_SPSTR = 2100;
    // ********* FILE错误 *********
    public final static int FILE_FROM_FAIL = 3000;
    public final static int FILE_TO_FAIL = 3010;
    public final static int DOWNLOAD_FILE_FAIL = 3020;

    // ********** WEB错误 **********
    public final static int REGISTER_USER_FAIL = 4000;
    public final static int ADD_BUDDY_FAIL = 4010;
    public final static int REMOVE_BUDDY_FAIL = 4020;
    public final static int QUERY_ALL_BUDDIES_FAIL = 4030;
    public final static int CREATE_GROUP_FAIL = 4040;
    public final static int QUERY_ALL_GROUPS_FAIL = 4050;
    public final static int ADD_MEMBERS_TO_GROUP_FAIL = 4060;
    public final static int REMOVE_MEMBER_FROM_GROUP_FAIL = 4070;
    public final static int QUERY_ALL_MEMBERS_OF_GROUP_FAIL = 4080;
    public final static int REMOVE_GROUP_FAIL = 4090;
    public final static int WEB_SERVER_CONNECT_FAIL = 4001;
    public final static int EXIT_GROUP_FAIL = 4002;//主动退出群失败
    // ********** AV错误 **********

    // ********* 其它错误 ***********
    /**
     * 网络连接异常
     */
    public final static int NETWORK_EXCEPTION = 6000;
    public final static int IO_EXCEPTION = 6010;

    private final static Hashtable<Integer, String> sErrorCodeMap = new Hashtable<Integer, String>();

    /** 注册错误码 */
    static {
        // sdk错误
        sErrorCodeMap.put(NULL_PARAMETER, "参数为null");
        sErrorCodeMap.put(TIMEOUT, "操作超时");
        sErrorCodeMap.put(TIME_NUMBER_ERROR, "超时时间小于0");
        sErrorCodeMap.put(TOPIC_CONTAINS_SPSTR, "topic不能包含 # $ +");
        sErrorCodeMap.put(INTERNAL_ERROR, "sdk内部状态错误");
        sErrorCodeMap.put(NONE_SERVER_CONNECTED, "还没有连接到指定的服务器，不能发送消息、文件等");
        sErrorCodeMap.put(SERVER_HAS_CONNECTED, "已经与服务器建立了连接，不能重复连接");
        sErrorCodeMap.put(SERVER_HAS_DISCONNECTED, "已经与服务器断开连接，不能重复断开连接 ");
        sErrorCodeMap.put(USERNAME_WRONG, "用户名错误");
        sErrorCodeMap.put(PASSWORD_WRONG, "密码错误");
        sErrorCodeMap.put(CONNECTING, "正在连接到服务器");
        sErrorCodeMap.put(DISCONNECTING, "正在断开服务器");
        sErrorCodeMap.put(CONNECTION_LOST, "与服务器丢失连接");
        sErrorCodeMap.put(CONNECT_FAIL, "连接服务器失败");
        sErrorCodeMap.put(DISCOVER_SERVER_FAIL, "发现服务器失败");
        sErrorCodeMap.put(DISCOVER_SERVER_INIT_FAIL, "发现服务器失败，初始化异常");
        sErrorCodeMap.put(LOGINED_IN_OTHER_PLACE, "您的UID已经在其它地方登陆，您被迫下线");
        sErrorCodeMap.put(PEER_DISCONNECTED, "服务器主动断开连接");

        // IM错误
        sErrorCodeMap.put(CHAT_TO_FAIL, "发送点对点消息失败");
        sErrorCodeMap.put(PUBLISH_FAIL, "发送群消息失败");
        sErrorCodeMap.put(SUBSCRIBE_FAIL, "订阅主题失败");
        sErrorCodeMap.put(UNSUBSCRIBE_FAIL, "取消订阅主题失败");
        sErrorCodeMap.put(PEER_STATE_FAIL, "查询用户在线状态失败");
        sErrorCodeMap.put(STATE_SUBSCRIBLE_FAIL, "订阅用户在线状态失败");
        sErrorCodeMap.put(STATE_UNSUBSCRIBE_FAIL, "取消订阅用户在线状态失败");
        sErrorCodeMap.put(REQUEST_OFFLINE_MESSAGE_FAIL, "请求离线消息失败");
        sErrorCodeMap.put(CANNOT_CHAT_TO_YOURSELF, "不能发送消息给自己");
        // FILE错误
        sErrorCodeMap.put(FILE_FROM_FAIL, "请求文件失败");
        sErrorCodeMap.put(FILE_TO_FAIL, "发送文件失败");
        sErrorCodeMap.put(DOWNLOAD_FILE_FAIL, "下载文件失败");
        // WEB错误
        sErrorCodeMap.put(REGISTER_USER_FAIL, "注册新用户失败");
        sErrorCodeMap.put(ADD_BUDDY_FAIL, "添加好友失败");
        sErrorCodeMap.put(REMOVE_BUDDY_FAIL, "删除好友失败");
        sErrorCodeMap.put(QUERY_ALL_BUDDIES_FAIL, "查询好友列表失败");
        sErrorCodeMap.put(CREATE_GROUP_FAIL, "创建群失败");
        sErrorCodeMap.put(QUERY_ALL_GROUPS_FAIL, "查询所有群失败");
        sErrorCodeMap.put(ADD_MEMBERS_TO_GROUP_FAIL, "添加群成员失败");
        sErrorCodeMap.put(REMOVE_MEMBER_FROM_GROUP_FAIL, "删除群成员失败");
        sErrorCodeMap.put(QUERY_ALL_MEMBERS_OF_GROUP_FAIL, "查询群成员列表失败");
        sErrorCodeMap.put(REMOVE_GROUP_FAIL, "删除群失败");
        sErrorCodeMap.put(WEB_SERVER_CONNECT_FAIL, "无法连接到web服务器");
        // AV错误
        // 其他错误
        sErrorCodeMap.put(NETWORK_EXCEPTION, "网络连接异常");
        sErrorCodeMap.put(IO_EXCEPTION, "IO异常");
    }

    /**
     * 获取错误码对应的文本解释。
     *
     * @param errorCode <code>A-SDK</code>已定义的错误代码。
     * @return 错误原因解释，如果{@link #}不存在，那么返回<code>null</code>。
     */
    public static String getErrorReason(int errorCode) {
        return sErrorCodeMap.get(errorCode);
    }

}
