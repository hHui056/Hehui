package com.beidouapp.et.exception;

import java.io.Serializable;

/**
 * ET-SDK 异常编码.
 *
 * @author mhuang.
 */
public class EtExceptionCode implements Serializable {
	private static final long serialVersionUID = 1L;

	// 预留信息编码(范围0~199).
	/** 成功. */
	public static final int SUCCESS = 0;

	/** 失败. */
	public static final int FAIL = -1;

	/** 数据过长，内存溢出. */
	public static final int BUFFER_OVERFLOW = -2;

	/** 参数非法. */
	public static final int PARAM_ILLEGAL = -3;

	/** 资源不足. */
	public static final int ALLOC_FAILED = -4;

	/** 不支持的设置. */
	public static final int CONFIG_NONSUPPORT = -5;

	// public static final int ET_ERR_MUTEX_FAILED = -6; ///< 创建mutex失败

	public static final int ET_ERR_ACCOUNT_INVALID = -7; // /< 账号信息无效
	public static final int ET_ERR_LB_SOCKET_FAILED = -8; // /< 创建LB Socket失败
	public static final int ET_ERR_LB_DNS_FAILED = -9; // /< LB DNS解析失败
	public static final int ET_ERR_LB_CONN_FAILED = -10; // /< LB 连接失败
	public static final int ET_ERR_LB_GET_SERVER_FAILED = -11; // /<
																// 从LB获取服务器地址失败
	public static final int ET_ERR_SERVER_DNS_FAILED = -12; // /< 解析各功能服务器地址失败
	public static final int ET_ERR_LB_RESP_ERROR = -13; // /< LB返回数据存在错误或长度不正确
	public static final int ET_ERR_LB_PROTOCOL_INVALID = -14; // /< LB协议不支持
	public static final int ET_ERR_LB_UID_INVALID = -15; // /< LB UID非法
	public static final int ET_ERR_LB_SERVICE_INVALID = -16; // /< LB 不存在该服务
	public static final int LB_SERVICE_UNKNOWN_EXCEPTION = -17;      ///< LB 未知异常

	/** 连接iLink服务器失败. */
	public static final int CONNECTION_FAILED = -4099;

	/** 网络路由异常. */
	public static final int NO_ROUTE_TO_HOST = 206;

	/** IM未连接. */
	public static final int IM_OFFLINE = 10000;

	/** 系统未知异常. */
	public static final int SYSTEM_UNKNOWN_EXCEPTION = 200;

	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// 系统信息(范围200~4099).

	/** 系统初始化异常. */
	public static final int SYSTEM_INIT_EXCEPTION = 201;

	/** 字符集不匹配. */
	public static final int CHARACTER_SET_INCORRECT = 202;

	/** 对象未实例化. */
	public static final int OBJECT_NOT_INSTANTIATED = 203;

	/** 获取应用服务器地址信息异常. */
	public static final int INIT_APPLICATION_SERVER_INFO = 204;

	/** 网络或IO异常. */
	public static final int IO_NET_EXCEPTION = 205;

	/** 服务器主动断开. */
	public static final int SERVER_PEER_DISCONNECTED = 207;

	/** 参数为Null. */
	public static final int PARAM_NULL = 210;

	/** 参数为Null 或 空. */
	public static final int PARAM_NULL_OR_EMPTY = 211;

	/** 非法参数. */
	// public static final int PARAM_ILLEGAL = 212;

	/** 参数状态异常. */
	public static final int PARAM_STATE_ILLEGAL = 213;

	/** 连接负载均衡服务器异常. */
	public static final int CONNECT_LOAD_BALANCE = 214;

	/** 解析状态码异常. */
	public static final int ANALY_STATUS_CODES = 215;

	/** 内容过长. */
	public static final int CONTENT_IS_TOO_LONG = 216;

	/** 用户在线失败. */
	public static final int ONLINE = 217;

	/** 用户下线失败. */
	public static final int OFFLINE = 218;

	/** 端口范围异常(1025~65535). */
	public static final int PORT_WHITOUT_RANGE = 219;

	/** 非法状态异常. */
	public static final int STATE_ILLEGAL = 220;

	/** key对应的值未找到. */
	public static final int KEY_VALUE_NOT_EXIST = 300;

	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// 用户错误信息.(范围5001~9999).
	/** 客户端异常. */
	public static final int CLIENT_EXCEPTION = 5001;

	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================

	// CONNACK 连接响应配置 (10000-10099).

	/** 离线状态. */
	public static final int CONNACK_OFFLINE = 10000;

	/** 协议版本不正确. */
	public static final int CONNACK_CONNECTION_REFUSED_UNACCEPTED_PROTOCOL_VERSION = 10001;

	/** 标识符异常. */
	public static final int CONNACK_CONNECTION_REFUSED_IDENTIFIER_REJECTED = 10002;

	/** 服务器不可用. */
	public static final int CONNACK_CONNECTION_REFUSED_SERVER_UNAVAILABLE = 10003;

	/** 非法的用户名密码. */
	public static final int CONNACK_CONNECTION_REFUSED_BAD_USERNAME_OR_PASSWORD = 10004;

	/** 授权被拒绝. */
	public static final int CONNACK_CONNECTION_REFUSED_NOT_AUTHORIZED = 10005;

	/** 网络不可用. */
	public static final int CONNACK_CONNECTION_NETWORK_IS_UNREACHABLE = 10006;

	/** SSL握手异常. */
	public static final int CONNECT_SSL_HANDSHAKE = 10007;

	/** 平台标识异常. */
	public static final int PLATFORM_TYPE = 10008;

	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================

	// PUBLISH 发布配置 (10100-10199).
	/** 发布失败. */
	public static final int PUBLISH_FAIL = 10100;

	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// SUBSCRIBE 订阅配置 (10200-10299).
	/** 订阅失败. */
	public static final int SUBSCRIBE_FAIL = 10200;

	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// UNSUBSCRIBE 取消订阅配置 (10300-10399).
	/** 取消订阅失败. */
	public static final int UNSUBSCRIBE_FAIL = 10300;

	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// PEERSTATE 获取好友状态配置 (10400-10499).
	/** 获取好友状态失败. */
	public static final int PEERSTATE_FAIL = 10400;

	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// CHATTO 发送点对点消息配置 (10500-10599).
	/** 获取点对点发送消息失败. */
	public static final int CHAT_TO_FAIL = 10500;

	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// disconnect 断开链接 消息配置 (10600-10699).
	/** 主动断开连接. */
	public static final int DISCONNECT = 10600;

	/** 被踢下线. */
	public static final int SYS_KICK = 10601;

	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// Web 请求 消息配置 (10700-10799).
	/** web 未知异常. */
	public static final int WEB_UNKNOWN_EXCEPTION = 10700;

	/** web 初始化配置异常. */
	public static final int WEB_INIT_CONFIG = 10701;

	/** web 功能未配置. */
	public static final int WEB_NOT_CONFIGURED = 10702;

	/** web 服务器连接异常. */
	public static final int WEB_SERVER_CONNECT = 10703;

	/** web 服务器地址获取. */
	public static final int WEB_URL_INFO = 10704;

	/** 用户在线状态. */
	public static final int WEB_PEER_STATE = 10711;

	/** 添加好友. */
	public static final int WEB_ADD_BUDDY = 10712;

	/** 删除好友. */
	public static final int WEB_REMOVE_BUDDY = 10713;

	/** 获取好友列表. */
	public static final int WEB_BUDDIES = 10714;

	/** 创建群. */
	public static final int WEB_CREATE_GRP = 10715;

	/** 获取群列表. */
	public static final int WEB_GRPS = 10716;

	/** 注销群. */
	public static final int WEB_DISMISS_GRP = 10717;

	/** 添加群成员. */
	public static final int WEB_ADD_GRP_MEMBER = 10718;

	/** 删除群成员. */
	public static final int WEB_REMOVE_GRP_MEMBER = 10719;

	/** 获取群成员列表. */
	public static final int WEB_GRP_MEMBERS = 10720;

	/** 添加用户. */
	public static final int WEB_REGIST_USER = 10721;

	/** Web发布消息. */
	public static final int WEB_PUBLISH = 10722;

	/** 用户主动退群. */
	public static final int WEB_GRP_QUIT = 10723;

	/** 添加好友扩展(是否通知对方). */
	public static final int WEB_ADD_BUDDY_EX = 10724;

	/** 删除好友扩展(是否通知对方). */
	public static final int WEB_REMOVE_BUDDY_EX = 10725;

	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// ====================================================
	// File 请求 消息配置 (10800-10899).
	/** File 未知异常. */
	public static final int FILE_UNKNOWN_EXCEPTION = 10800;

	/** File 初始化配置异常. */
	public static final int FILE_INIT_CONFIG = 10801;

	/** File 功能未配置. */
	public static final int FILE_NOT_CONFIGURED = 10802;

	/** File 下载异常. */
	public static final int FILE_DOWNLOAD = 10803;

	/** File 删除异常. */
	public static final int FILE_REMOVE = 10804;

	/** File 错误码开始位，与服务器返回值累加得出异常. */
	public static final int FILE_BASE = 10810;

}