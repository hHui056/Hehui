package com.beidouapp.et.handler.impl;

import com.beidouapp.et.handler.EtExecutable;
import com.beidouapp.et.util.param.CheckingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;

/**
 * 校验IP是否可访问.
 *
 * @author mhuang.
 */
public class CheckIPHandler implements EtExecutable<Boolean> {

    @Override
    public Boolean execute(Map<String, ?> params) {
        boolean flag = true;
        String domain = params.get("domain").toString(); // IP or domain
        Integer port = (Integer) params.get("port"); // port
        Integer soTimeout = (Integer) params.get("soTimeout"); // 超时时间.

        CheckingUtil.checkNullOrEmpty(domain, "domain is not null");
        CheckingUtil.checkArgument(!(port < 0), "port is incorrect");
        if (soTimeout == null || soTimeout < 0) {
            soTimeout = 5000;
        }
        Socket socket = new Socket();
        try {
            logger.debug("connecting domain \"{}\", port \"{}\". soTimeout \"{}\"", domain, port, soTimeout);
            socket.connect(new InetSocketAddress(domain, port), soTimeout);
            logger.debug("connected domain \"{}\", port \"{}\". soTimeout \"{}\"", domain, port, soTimeout);
            flag = true;
        } catch (Exception e) {
            flag = false;
            logger.error("connect domain \"{}\", port \"{}\". soTimeout \"{}\" is failed. occure \"{}\"", domain,
                    port, soTimeout, e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("closeing socket domain \"{}\", port \"{}\". soTimeout \"{}\" is fail. occure \"{}\".",
                        domain, port, soTimeout, e);
            }
        }
        return flag;
    }

    /**
     * 日志记录器.
     */
    private static final Logger logger = LoggerFactory.getLogger(CheckIPHandler.class);
}