/**
 *
 */
package com.beidouapp.et;

/**
 * 服务器信息。
 */
public class Server {
    public static final String PROXY_SERVER_ID = "proxy_server";
    /**
     * 设备处于内网模式
     */
    public static final int TYPE_LAN = 0;
    /**
     * 设备处于外网模式
     */
    public static final int TYPE_WAN = 1;

    /**
     * 设备当前所处的网络类型
     */
    private int type = TYPE_LAN;

    /**
     * 设备的ID
     */
    private String id = null;
    /**
     * 设备TCP/mqtt服务器 的地址
     */
    private String ip = null;
    /**
     * 设备TCP/mqtt服务器 的port
     */
    private int port = -1;

    public Server() {

    }

    /**
     * @param type type
     * @param id
     * @param ip
     * @param port
     */
    public Server(int type, String id, String ip, int port) {
        this.type = type;
        this.id = id;
        this.ip = ip;
        this.port = port;
    }

    /**
     * 获取服务器的类型。
     *
     * @return 服务器类型， {@link Server#TYPE_LAN} or {@link Server#TYPE_WAN}
     */
    public int getType() {
        return type;
    }

    /**
     * 获取服务器的id。
     *
     * @return <b>服务器</b>的id
     */
    public String getId() {
        return id;
    }

    /**
     * 获取服务器的ip。
     *
     * @return <b>服务器</b>的IP
     */
    public String getIp() {
        return ip;
    }

    /**
     * 获取服务器的端口。
     *
     * @return <b>服务器</b>的端口
     */
    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "Server [type=" + type + ", id=" + id + ", ip=" + ip + ", port=" + port + "]";
    }


}
