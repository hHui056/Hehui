package com.beidouapp.et.handler.impl;

import com.beidouapp.et.ErrorCode;
import com.beidouapp.et.common.constant.EtConstants;
import com.beidouapp.et.core.pojo.LBRequestInfo;
import com.beidouapp.et.core.pojo.LBResponseInfo;
import com.beidouapp.et.exception.EtExceptionCode;
import com.beidouapp.et.exception.EtRuntimeException;
import com.beidouapp.et.handler.EtExecutable;
import com.beidouapp.et.util.codec.EncryptUtil;
import com.beidouapp.et.util.param.CheckingUtil;
import com.beidouapp.et.util.param.SplitterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 从负载均衡获取指定类型服务器的IP及Port.
 *
 * @author mhuang.
 */
public class ObtainIPHandler implements EtExecutable<LBResponseInfo> {

    public static final String UID = "FcUuAGdaymBVtArB12eZ4tPJ4kE8sJPv3w";// 默认填写id

    @Override
    public LBResponseInfo execute(Map<String, ?> params) {
        String domain = (String) params.get("domain"); // 负载均衡服务器IP or domain
        Integer port = (Integer) params.get("port"); // 负载均衡服务器port
        Integer soTimeout = (Integer) params.get("soTimeout"); // 超时时间.
        Integer serverType = (Integer) params.get("serverType"); // 服务器类型.
        String userId = (String) params.get("userId"); // 用户ID.
        CheckingUtil.checkNullOrEmpty(domain,
                "load balancing domain(IP) is null.");
        CheckingUtil.checkArgument(!(port == null || port < 0), String.format(
                "load balancing port is incorrect. port = %s", port));
        CheckingUtil.checkNullOrEmpty(userId, "userId is not null.");
        CheckingUtil.checkArgument(!(serverType == null),
                "request type is null.");
        logger.debug(
                "call params domain={}, port={}, soTimeout={}, serverType={}, userId={}",
                domain, port, soTimeout, serverType, userId);
        String random = String.valueOf(Math.abs(new Random().nextLong()));
        random = random.substring(0, 10);
        String md5 = EncryptUtil.MD5(userId + random).toLowerCase();
        LBRequestInfo info = new LBRequestInfo();
        info.setHeader("LB").setVersion(1).setTotal(78).setEncryType(0)
                .setRequestType(serverType);
        info.setRandrom(random).setMd5(md5).setUid(userId);
        if (2 == serverType) {
            info.setUid(UID);
        }
        if (soTimeout == null || soTimeout < 0) {
            soTimeout = 8000;// 默认8秒.
        }
        logger.debug("connecting load balancing \"{}\", port {}. soTimeout {}",
                domain, port, soTimeout);
        LBResponseInfo response = null;
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(domain, port), soTimeout);
            logger.debug(
                    "connected load balancing \"{}\", port {}. soTimeout {}",
                    domain, port, soTimeout);
            byte[] data = buildRequestData(info);
            long s1 = System.currentTimeMillis();
            BufferedOutputStream bos = new BufferedOutputStream(
                    socket.getOutputStream());
            bos.write(data);
            bos.flush();
            long s2 = System.currentTimeMillis();
            logger.info(
                    "send encrypted data to the server. load balance \"{}\", port {} time consumed {} ms.",
                    socket.getInetAddress().getHostAddress(), socket.getPort(),
                    (s2 - s1));
            long s3 = System.currentTimeMillis();
            List<Byte> list = new ArrayList<Byte>(1024);
            BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
            for (int i = bis.read(); i != -1; ) {
                list.add((byte) i);
                i = bis.read();
            }
            long s4 = System.currentTimeMillis();
            logger.info("receives message \"{}\" consumed {} ms.", list,
                    (s4 - s3));
            long s5 = System.currentTimeMillis();
            response = resolveByte2LBResponseInfo(list.toArray(new Byte[]{}));
            logger.debug(response.toString());
            long s6 = System.currentTimeMillis();
            logger.info("resolve \"{}\" to LBResponseInfo consumed {} ms.",
                    list, (s6 - s5));

        } catch (Exception e) {
            logger.error("connect server Type \"{}\", Load Balancing \"{}\", port \"{}\". soTimeout \"{}\" is failed. occure \"{}\"",
                    serverType, domain, port, soTimeout, e);
            throw new EtRuntimeException(ErrorCode.DISCOVER_SERVER_FAIL, e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                logger.error("closeing socket domain \"{}\", port {}. soTimeout {} is fail. occure \"{}\".",
                        domain, port, soTimeout, e);
            }
        }
        return response;
    }
    /**
     * 构建请求数据.
     *
     * @param info 负载均衡请求对象.
     * @return
     */
    private byte[] buildRequestData(LBRequestInfo info) {
        byte[] b = new byte[83];
        b[0] = (byte) (info.getHeader().charAt(0)); // 设置协议头[0].
        b[1] = (byte) (info.getHeader().charAt(1));// 设置协议头[1].
        b[2] = (byte) info.getVersion(); // 协议版本.
        short[] len = EncryptUtil.intToByteArray(info.getTotal());
        b[3] = (byte) len[len.length - 2]; // 剩余长度1.
        b[4] = (byte) len[len.length - 1];// 剩余长度2.
        b[5] = (byte) info.getEncryType(); // 加密类型.
        b[6] = (byte) info.getRequestType(); // 请求类型.
        String random = String.valueOf(info.getRandrom());
        for (int i = 0; i < random.length(); i++) {
            b[7 + i] = (byte) random.charAt(i); // 随机数.
        }
        String md5 = info.getMd5();
        for (int i = 0; i < md5.length(); i++) {
            b[17 + i] = (byte) md5.charAt(i); // md5(uid+randrom), 标识的Md5,32位小写,
            // 对uid和随机数的md5.
        }
        String uid = info.getUid();
        for (int i = 0; i < uid.length(); i++) {
            b[49 + i] = (byte) uid.charAt(i); // 34字节的uid.
        }
        return b;
    }

    /**
     * 解析负载均衡返回的协议信息.
     *
     * @param bb 字节数组.
     * @return
     */
    private LBResponseInfo resolveByte2LBResponseInfo(Byte[] bb) {
        LBResponseInfo info = new LBResponseInfo();
        info.setHeader((char) bb[0].byteValue() + "" + (char) bb[1].byteValue());
        info.setVersion((short) bb[2]);
        byte[] temp = {bb[3], bb[4]};
        short result = EncryptUtil.byte2Short(temp);
        info.setTotal(result);
        if (result < 0) {
            switch (result) {
                case -1:
                    throw new EtRuntimeException(EtExceptionCode.ET_ERR_LB_PROTOCOL_INVALID, "LB illegal protocols");
                case -2:
                    throw new EtRuntimeException(EtExceptionCode.ET_ERR_LB_UID_INVALID, "UID illegal");
                case -3:
                    throw new EtRuntimeException(EtExceptionCode.ET_ERR_LB_SERVICE_INVALID, "LB service invalid");
                default:
                    throw new EtRuntimeException(EtExceptionCode.LB_SERVICE_UNKNOWN_EXCEPTION, "LB service unknown exception");
            }
        }
        info.setEncryType((short) bb[5]);
        info.setRequestType((short) bb[6]);
        int x = (int) (bb[7]);
        int y = (int) (bb[8]);
        int z = (int) (bb[9]);
        int r = (int) (bb[10]);
        x = x & 0xff;
        y = y & 0xff;
        z = z & 0xff;
        r = r & 0xff;
        x <<= 24;
        y <<= 16;
        z <<= 8;
        info.setTimeout((int) (x | y | z | r));
        StringBuilder sb = new StringBuilder(info.getTotal());
        for (int i = 0; i < info.getTotal() - 6; i++) {
            sb.append((char) (bb[i + 11] & 0xff));
        }
        info.setData(sb.toString());
        if (info.getRequestType() != 3) {
            List<String> list = SplitterUtil.splitterList(info.getData(),
                    EtConstants.SEPARATOR_COLON);
            info.setDomain(list.get(0));// 域名
            info.setPort(Integer.valueOf(list.get(1)));// 端口
        }
        info.setTimeExpiration(System.currentTimeMillis() + info.getTimeout() * 1000);
        return info;
    }

    /**
     * 日志记录器.
     */
    private static final Logger logger = LoggerFactory
            .getLogger(ObtainIPHandler.class);
}