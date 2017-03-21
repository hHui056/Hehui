package com.beidouapp.et.common.constant;

import java.io.Serializable;

/**
 * ET-SDK key常量.
 *
 * @author mhuang.
 */
public class EtKeyConstant implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 负载均衡服务器IP.
     */
    public static final String LB_IP = "lbIp";

    /**
     * 负载均衡服务器Port.
     */
    public static final String LB_PORT = "lbPort";

    /** 负载均衡服务器IP(备用). */
    //public static final String LB_BACKUP_IP = "lbBackupIp";

    /** 负载均衡服务器Port(备用). */
    //public static final String LB_BACKUP_PORT = "lbBackupPort";


    /**
     * IM服务超时缓存标识.
     */
    public static final String CACHE_IM = "1";

    /**
     * WEB服务超时缓存标识.
     */
    public static final String CACHE_WEB = "2";

    /**
     * FILE服务超时缓存标识.
     */
    public static final String CACHE_FILE = "3";


    /**
     * IM服务器IP.
     */
    public static final String IM_IP = "imIp";

    /**
     * IM服务器Port.
     */
    public static final String IM_PORT = "imPort";

    /**
     * Web服务器URL.
     */
    public static final String WEB_URL = "webUrl";

    /**
     * File服务器IP.
     */
    public static final String FILE_IP = "fileIp";

    /**
     * File服务器 端口.
     */
    public static final String FILE_PORT = "filePort";

    /**
     * AppKey.
     */
    public static final String APP_KEY = "appKey";

    /**
     * SecretKey.
     */
    public static final String SECRET_KEY = "secretKey";

    /**
     * 用户ID(客户端ID).
     */
    public static final String CLIENT_ID = "clientId";

    /**
     * 用户名.
     */
    public static final String USER_NAME = "userName";

    /**
     * 用户密码.
     */
    public static final String USER_PWD = "userPwd";

    /**
     * 心跳.
     */
    public static final String KEEP_ALIVE = "keepAlive";

    /**
     * 默认QoS.
     */
    public static final String DEFAULT_QOS = "defaultQos";

    /**
     * 默认消息保留.
     */
    public static final String DEFAULT_RETAIN = "defaultRetain";

    /**
     * 上下文.
     */
    public static final String CONTEXT = "context";

    /** 管理器. */
    //public static final String I_MANAGER = "imanager";
    //public static final String I_MODULETYPE = "imoduleType";

    /**
     * 首次重连次数.
     */
    public static final String FIRST_MAX_CONNECT_COUNT = "firstMaxConnectCount";

    /**
     * 重连次数.
     */
    public static final String RECONNECT_COUNT = "reConnectCount";

    /**
     * 首次重连间隔时间.
     */
    public static final String FIRST_RECONNECT_DELAY = "firstReconnectDelay";

    /**
     * 重连间隔时间上限.
     */
    public static final String RECONNECT_DELAY_MAX = "reconnectDelayMax";

    /**
     * 实例化服务默认等待时间(毫秒数).
     */
    public static final String DEFAULT_INSTANCE_TIMEOUT = "defaultInstanceTimeout";

    /**
     * 连接前清空会话信息.
     */
    public static final String CLEAN_SESSION_STATUS = "cleanSessionStatus";

    /**
     * SSL启用状态.
     */
    public static final String SSL_ENABLE_STATUS = "sslEnableStatus";

    /**
     * 是否调试模式.
     */
    public static final String IS_DEBUG = "isDebug";

    /**
     * 是否实例化HTTP服务.
     */
    public static final String IS_EXISTS_HTTP = "isExistsHttp";

    /**
     * 是否实例化File服务.
     */
    public static final String IS_EXISTS_FILE = "isExistsFile";

    /**
     * 是否实例化音视频服务.
     */
    public static final String IS_EXISTS_AV = "isExistsAV";

    /**
     * 消息接收监听器.
     */
    public static final String RECEIVE_LISTENER = "receiveListener";

    /**
     * 是否开启SDK内部跟踪信息.
     */
    public static final String TRACER_ENABLE = "tracerEnable";

    /**
     * 默认S-SDK平台标识.
     */
    public static final String PLATFORM_FOR_S_SDK = "dpf";

    // ------------------------------------------------------------------
    /**
     * 主消息监听器.
     */
    public static final String LISTENER_MASTER = "listenerMaster";
    /**
     * 文件息监听器.
     */
    public static final String LISTENER_FILE = "listenerFile";
    /**
     * 音视频息监听器.
     */
    public static final String LISTENER_AV = "listenerAv";
    /**
     * 关注用户状态监听器.
     */
    public static final String LISTENER_USER_STATUS = "listenerUserStatus";
    // ------------------------------------------------------------------
    /**
     * SIP服务器URL.
     */
    public static final String SIP_URL = "sipUrl";

    /** ICE服务器URL. */
    // public static final String ICE_URL = "iceUrl";

    /**
     * STUN服务器URL.
     */
    public static final String STUN_URL = "stunUrl";

    /**
     * TURN服务器URL.
     */
    public static final String TURN_URL = "turnUrl";

    /**
     * TURN 登录用户名.
     */
    public static final String TURN_USERNAME = "turnUsername";

    /**
     * TURN 登录校验.
     */
    public static final String TURN_CREDENTIAL = "turnCredential";

}