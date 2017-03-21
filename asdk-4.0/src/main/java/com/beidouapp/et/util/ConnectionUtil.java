package com.beidouapp.et.util;

import com.beidouapp.et.client.api.IContext;
import com.beidouapp.et.common.constant.EtConstants;
import com.beidouapp.et.common.constant.EtKeyConstant;
import com.beidouapp.et.core.EtCallbackConnection;
import com.beidouapp.et.core.EtMQTT;
import com.beidouapp.et.exception.EtExceptionCode;
import com.beidouapp.et.exception.EtRuntimeException;
import com.beidouapp.et.util.codec.EncryptUtil;
import org.fusesource.mqtt.client.Tracer;
import org.fusesource.mqtt.codec.MQTTFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * IM连接对象工具类.
 *
 * @author mhuang.
 */
public class ConnectionUtil {
    private static Logger logger = LoggerFactory.getLogger(ConnectionUtil.class);

    /**
     * 获得异步连接对象.
     *
     * @param etContext 配置上下文.
     * @return 异步连接对象.
     */
    public static EtCallbackConnection getConnection(IContext etContext) {
        return getMQTT(etContext).callbackConnection();
    }

    private static EtMQTT getMQTT(IContext etContext) {
        EtMQTT mqtt = new EtMQTT();
        if (etContext.getTracerEnable()) {
            mqtt.setTracer(getTracer());
        }
        Integer platformType = etContext.get(EtKeyConstant.PLATFORM_FOR_S_SDK);
        if (platformType == null) {
            platformType = EtConstants.PLATFORM_FOR_S_SDK;
        } else if (!(platformType == 1 || platformType == 6)) {
            logger.error("platform type is incorrect");
            throw new EtRuntimeException(EtExceptionCode.PLATFORM_TYPE, "platform type is incorrect");
        }
        try {
            logger.debug("starting to create a new im connection.");
            String appKey = etContext.getAppKey();
            String userName = etContext.getUserName();
            Boolean cleanSession = etContext.getCleanSession();
            //long firstReconnectionCount = etContext.getFirstReconnectCount(); // 首次重连次数.
            //long reconnectCount = etContext.getReconnectCount(); // 登录后的重连次数.
            //long firstReconnectDelay = etContext.getFirstReconnectDelay(); // 首次重连间隔时间.
            //long reconnectDelayMax = etContext.getReconnectDelayMax(); // 重连最大间隔时间.

            String imIp = etContext.get(EtKeyConstant.IM_IP);
            Integer imPort = Integer.valueOf(etContext.get(EtKeyConstant.IM_PORT).toString());
            mqtt.setVersion(EtConstants.PROTOCOL_VERSION);
            mqtt.setClientId(userName + EtConstants.SEPARATOR_AIT + platformType + "|2.0.0.0");
            mqtt.setUserName(userName);
            mqtt.setPassword(EncryptUtil.generatedPasswords(appKey, userName));
            mqtt.setKeepAlive(etContext.getKeepAlive());
            mqtt.setCleanSession(cleanSession);
            mqtt.setConnectAttemptsMax(0);

            mqtt.setReconnectAttemptsMax(0);// 客户端已经连接到服务器，但因某种原因连接断开时的最大重试次数，超出该次数客户端将返回错误。-1意为无重试上限，默认为-1
            //mqtt.setReconnectDelay(firstReconnectDelay);// 首次重连接间隔毫秒数，默认为1000ms
            //mqtt.setReconnectDelayMax(reconnectDelayMax);// 重连接间隔毫秒数，默认为30000ms
            mqtt.setReconnectBackOffMultiplier(1);// 设置重连接指数回归。设置为1则停用指数回归，默认为2
            if (etContext.getSSLEnable()) {
                setSSLContext(mqtt, imIp, 1884);// 启用SSL,端口强制为1884.
            } else {
                mqtt.setHost(imIp, imPort);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new EtRuntimeException(EtExceptionCode.PARAM_NULL, "create MQTT object is fail!", e);
        }
        logger.debug("created a new im connection.");
        return mqtt;
    }

    public static void setSSLContext(EtMQTT mqtt, String im, Integer port) throws Exception {
        SSLContext sslcontext = SSLContext.getInstance("SSLv3");
        sslcontext.init(null, new TrustManager[]{new DefaultTrustManager()}, null);
        mqtt.setHost("ssl://" + im + ":" + port);
        mqtt.setSslContext(sslcontext);
    }

    static class DefaultTrustManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

    }

    public static Tracer getTracer() {
        return new Tracer() {
            @Override
            public void onReceive(MQTTFrame frame) {
                logger.debug(String.format("%tF %1$tH:%1$tM:%1$tS.%1$tL", System.currentTimeMillis()) + "------RECV: "
                        + frame);
            }

            @Override
            public void onSend(MQTTFrame frame) {
                logger.debug(String.format("%tF %1$tH:%1$tM:%1$tS.%1$tL", System.currentTimeMillis()) + "------SEND: "
                        + frame);
            }

            @Override
            public void debug(String message, Object... args) {
                logger.debug(String.format("debug: " + message, args));
            }
        };
    }
}