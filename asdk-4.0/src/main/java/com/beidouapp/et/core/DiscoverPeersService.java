/**
 *
 */
package com.beidouapp.et.core;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import com.beidouapp.et.DiscoverOptions;
import com.beidouapp.et.ErrorCode;
import com.beidouapp.et.ErrorInfo;
import com.beidouapp.et.Server;
import com.beidouapp.et.util.CheckCodeUtil;
import com.beidouapp.et.util.HexUtil;
import com.beidouapp.et.util.Log;
import com.beidouapp.et.util.LogFactory;

import java.io.IOException;
import java.net.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 发现设备终端后台服务，持续保持连接。只适用于内网udp广播
 *
 * @author allen
 */
public class DiscoverPeersService {

    private static final int REMOETE_UDP_BROADCAST_PORT = 2073;
    // private static final int LOCAL_UDP_BROADCAST_PORT = 2074;

    private static final int DEVICE_TCP_PORT = 18883;
    private OnDiscoverListener mOnDiscoverListener = null;
    private static final String TAG = DiscoverPeersService.class.getSimpleName();
    private Log LOG = LogFactory.getLog("java");

    private volatile DatagramSocket mDiscoverSocket = null;

    private SDKContext mContext = null;

    // discover的可选参数
    private DiscoverOptions mDiscoverOptions = null;

    private volatile boolean mRunning = false;

    private Timer mDiscoverTimer = new Timer();
    private int mDiscoverTimeout = 10;// 默认10s
    private Context context;

    public DiscoverPeersService(SDKContext ctx, Context context) {
        mContext = ctx;
        this.context = context;
    }


    /**
     * 获取广播包地址
     *
     * @param context android 上下文对象
     * @return
     * @throws UnknownHostException
     */
    public static InetAddress getBroadcastAddress(Context context) throws UnknownHostException {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        if (dhcp == null) {
            return InetAddress.getByName("255.255.255.255");
        }
        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }

    /**
     * @return true 初始化socket成功；false，失败。
     */
    private boolean init() {
        try {
            mDiscoverSocket = new DatagramSocket();
        } catch (SocketException e1) {
            LOG.d(TAG, "分配端口失败！");
            return false;
        }
        return true;
    }

    public void setOnDiscoverListener(OnDiscoverListener onDiscoverListener) {
        mOnDiscoverListener = onDiscoverListener;
    }

    /**
     * 搜索一次设备
     *
     * @param timeoutSecond 最大搜索时间
     */
    public void doDiscovery(int timeoutSecond, DiscoverOptions option) {
        mDiscoverTimeout = timeoutSecond;
        mDiscoverOptions = option;
        if (mRunning == false) {
            if (init()) {
                start();
            } else {
                if (mOnDiscoverListener != null) {
                    mOnDiscoverListener.onDiscoverFail(new ErrorInfo(ErrorCode.DISCOVER_SERVER_INIT_FAIL));
                }
            }
        } else {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    sendDiscoverDatagramPacket();
                }
            }).start();
        }
    }

    /**
     * 开始服务
     */
    public void start() {
        mRunning = true;
        final Thread sender = new Thread(new Runnable() {

            @Override
            public void run() {
                sendDiscoverDatagramPacket();
            }
        }, "DiscoverPeersService-send-thread");

        final Thread receiver = new Thread(new Runnable() {

            @Override
            public void run() {
                while (mRunning) {
                    try {
                        byte[] data = new byte[128];
                        DatagramPacket packet = new DatagramPacket(data, data.length);
                        if (mDiscoverSocket == null || mDiscoverSocket.isClosed()) {
                            break;
                        }
                        mDiscoverSocket.receive(packet);
                        Server result = parseDiscoverResult(packet);
                        if (result != null) {
                            if (mOnDiscoverListener != null) {
                                mOnDiscoverListener.onResult(result);
                            }
                            mDiscoverTimer.cancel();
                        }
                    } catch (IOException e) {
                        LOG.d(TAG,
                                "receive broadcast data exception in [DiscoverPeersService.start().new Runnable() {...}.run()]");
                        if (mRunning && mOnDiscoverListener != null) {
                            mOnDiscoverListener.onDiscoverFail(new ErrorInfo(ErrorCode.DISCOVER_SERVER_FAIL));
                        }
                    }
                }
            }
        }, "DiscoverPeersService-receive-thread");
        sender.start();
        receiver.start();
    }

    /**
     * 发送udp广播包
     */
    private void sendDiscoverDatagramPacket() {
        if (mDiscoverTimer != null) {
            mDiscoverTimer.cancel();
            mDiscoverTimer = new Timer();
        }
        mDiscoverTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                if (mRunning && mOnDiscoverListener != null) {
                    mOnDiscoverListener.onDiscoverFail(new ErrorInfo(ErrorCode.TIMEOUT));
                    mDiscoverTimer.cancel();
                }
            }
        }, mDiscoverTimeout * 1000);
        try {
            DatagramPacket packet = createDiscoverDatagramPacket();// 构建发送udp包
            if (packet != null && mDiscoverSocket != null) {
                mDiscoverSocket.send(packet);
                LOG.d(TAG, "send discover data success.");
                if (mOnDiscoverListener != null) {
                    mOnDiscoverListener.onSuccess();
                }
            } else {
                if (mOnDiscoverListener != null) {
                    mOnDiscoverListener.onDiscoverFail(new ErrorInfo(ErrorCode.NULL_PARAMETER));
                }
            }
        } catch (IOException e) {
            LOG.d(TAG, "send discover data exception");
            if (mRunning && mOnDiscoverListener != null) {
                mOnDiscoverListener.onDiscoverFail(new ErrorInfo(ErrorCode.DISCOVER_SERVER_FAIL, e.getMessage()));
            }
        }
    }

    /**
     * 创建udp广播包
     *
     * @return
     */
    private DatagramPacket createDiscoverDatagramPacket() {
        try {
            // 广播命令
            // 字段 --------- 长度 --------- 解释
            // start_flag -- 2 --------- 开始标志位固定为 0xFFFF
            // length ------ 2 --------- cmd 开始到整个数据包结束所占用的字节数
            // cmd --------- 1 --------- 发送为0x60,接收为0x6F
            // seq --------- 1 --------- 发送者给出的序号，回复者必须把相应序号返回给发送者
            // data ------- 变长 --------- 全部暴露给用户传入opt的内容
            // bcc --------- 1 --------- 数据校验，length到data数据校验和（异或）
            // String uid = mContext.getContextParameters().getUid();
            // if (uid == null) {
            // return null;
            // }
            byte cmd = 0x60;
            byte seq = 0x01;
            byte[] data = "".getBytes();
            int len = 1 + 1 + data.length + 1;
            String _cmd = HexUtil.int2HexString(cmd, 2);
            String _seq = HexUtil.int2HexString(seq, 2);
            String _data = HexUtil.printHexString(data);
            if (mDiscoverOptions != null) {
                byte[] optContent = mDiscoverOptions.getContent();
                if (optContent != null) {
                    _data += HexUtil.printHexString(optContent);
                }
                len = 1 + 1 + _data.length() / 2 + 1;
            }
            String _len = HexUtil.int2HexString(len, 4);
            String xor = _len + _cmd + _seq + _data;
            byte bcc = CheckCodeUtil.getBccCode(HexUtil.hexString2Bytes(xor));
            String _bcc = HexUtil.int2HexString(bcc, 2);

            byte[] buff = HexUtil.hexString2Bytes("FFFF" + _len + _cmd + _seq + _data + _bcc);
            // LOG.d(TAG, "要发送的数据：" + HexUtil.printHexString(buff));
            DatagramPacket packet = new DatagramPacket(buff, 0, buff.length);
            InetAddress address = getBroadcastAddress(context);
            packet.setAddress(address);
            packet.setPort(REMOETE_UDP_BROADCAST_PORT);

            return packet;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解析返回的数据
     *
     * @param packet
     * @return
     */
    private Server parseDiscoverResult(DatagramPacket packet) {
        // 广播命令
        // 字段 --------- 长度 --------- 解释
        // start_flag -- 2 --------- 开始标志位固定为 0xFFFF
        // length ------ 2 --------- cmd 开始到整个数据包结束所占用的字节数
        // cmd --------- 1 --------- 发送为0x60,接收为0x6F
        // seq --------- 1 --------- 发送者给出的序号，回复者必须把相应序号返回给发送者
        // data ------- 变长 --------- 发送为app的uid，接收为device的uid
        // bcc --------- 1 --------- 数据校验，length到data数据校验和（异或）
        byte[] buff1 = packet.getData();
        int length = packet.getLength();
        byte[] buff = new byte[length];
        for (int i = 0; i < length; i++) {
            buff[i] = buff1[i];
        }

        LOG.d(TAG, "广播from:" + packet.getAddress().getHostAddress());
        LOG.d(TAG, "接收到广播数据：" + HexUtil.printHexString(buff));
        LOG.d(TAG, "len:" + packet.getLength());
        if (length < 7) {
            LOG.d(TAG, "接收到不完整的广播包");
            return null;
        }
        String packetData = HexUtil.printHexString(buff);
        String _startFlag = packetData.substring(0, 4);
        String _length = packetData.substring(4, 8);
        String _cmd = packetData.substring(8, 10);
        String _seq = packetData.substring(10, 12);
        int len = Integer.parseInt(_length, 16);
        if (len + 4 != length) {
            LOG.d(TAG, "长度不正确，不完整的广播包");
            return null;
        }
        int dataLen = len - 3;

        String _data = packetData.substring(12, 12 + dataLen * 2);
        String _bcc = packetData.substring(12 + dataLen * 2, packetData.length());
        byte __bcc = HexUtil.hexString2Bytes(_bcc)[0];
        if (CheckCodeUtil
                .getBccCode(HexUtil.hexString2Bytes(packetData.substring(4, packetData.length() - 2))) != __bcc) {
            LOG.d(TAG, "接收到广播非法的广播数据");
            return null;
        }

        if ("6F".equalsIgnoreCase(_cmd)) {
            Server svr = new Server(Server.TYPE_LAN, new String(HexUtil.hexString2Bytes(_data)),
                    packet.getAddress().getHostAddress(), DEVICE_TCP_PORT);
            LOG.d(TAG, "received svr：" + svr.toString());
            return svr;
        } else {
            LOG.d(TAG, "接收到客户端的广播, id:" + new String(HexUtil.hexString2Bytes(_data)));
            return null;
        }
    }

    /**
     * 停止扫描设备，释放资源
     */
    public void stop() {
        mRunning = false;
        if (mDiscoverSocket != null) {
            mDiscoverSocket.disconnect();
            mDiscoverSocket.close();
        }
    }

}
