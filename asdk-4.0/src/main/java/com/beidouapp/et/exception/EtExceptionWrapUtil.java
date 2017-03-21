package com.beidouapp.et.exception;

import com.beidouapp.et.client.api.IContext;
import org.fusesource.mqtt.client.MQTTException;

import java.io.EOFException;
import java.io.IOException;
import java.net.NoRouteToHostException;

/**
 * 异常包装工具.
 *
 * @author mhuang.
 */
public class EtExceptionWrapUtil {

    public static EtRuntimeException getWrapExcetpion(IContext ctx, Throwable ex) {
        EtRuntimeException ere = null;
        try {
            throw ex;
        } catch (NoRouteToHostException e) {
            ere = new EtRuntimeException(EtExceptionCode.NO_ROUTE_TO_HOST, "No route to host exception", e);
        } catch (MQTTException e) {
            ere = new EtRuntimeException(EtExceptionCode.CONNECTION_FAILED, "User " + ctx.getUserName() + " connect im server failed.", e);
        } catch (Throwable e) {
            ere = new EtRuntimeException(EtExceptionCode.SYSTEM_UNKNOWN_EXCEPTION, "Unknown exception", e);
        }
        return ere;
    }

    public static EtRuntimeException getWrapExcetpion(Throwable e) {
        if (e instanceof IOException) {
            if ("Could not connect: CONNECTION_REFUSED_BAD_USERNAME_OR_PASSWORD".equalsIgnoreCase(e.getMessage())) {
                return new EtRuntimeException(EtExceptionCode.CONNACK_CONNECTION_REFUSED_BAD_USERNAME_OR_PASSWORD,
                        "please check appkey、securekey or username is match.", e);
            }
            if ("Could not connect: CONNECTION_REFUSED_SERVER_UNAVAILABLE".equalsIgnoreCase(e.getMessage())) {//服务器不可用
                return new EtRuntimeException(EtExceptionCode.CONNACK_CONNECTION_REFUSED_SERVER_UNAVAILABLE, "connection refused server unavailable", e);
            }
            if ("Network is unreachable: no further information".equalsIgnoreCase(e.getMessage())) {
                return new EtRuntimeException(EtExceptionCode.CONNACK_CONNECTION_NETWORK_IS_UNREACHABLE, "network is unreachable",
                        e);
            } else if (e instanceof EOFException) {
                if (e.getMessage().contains("ssl")) {
                    return new EtRuntimeException(EtExceptionCode.CONNECT_SSL_HANDSHAKE, e.getLocalizedMessage());
                } else if (e.getMessage().contains("Peer")) {
                    return new EtRuntimeException(EtExceptionCode.SERVER_PEER_DISCONNECTED, e.getLocalizedMessage());
                }
            }
        } else if (e instanceof IllegalStateException) {
            if ("Disconnected".equalsIgnoreCase(e.getMessage())) {
                return new EtRuntimeException(EtExceptionCode.CONNACK_CONNECTION_NETWORK_IS_UNREACHABLE, "network is unreachable",
                        e);
            }
        } else if (e instanceof EtRuntimeException) {
            return (EtRuntimeException) e;
        }

        return new EtRuntimeException(EtExceptionCode.SYSTEM_UNKNOWN_EXCEPTION, "system unknown exception.", e);
    }
}
