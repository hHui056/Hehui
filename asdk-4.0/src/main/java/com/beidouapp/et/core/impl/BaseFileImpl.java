package com.beidouapp.et.core.impl;

import com.beidouapp.et.client.api.IContext;
import com.beidouapp.et.common.constant.EtKeyConstant;
import com.beidouapp.et.core.pojo.LBResponseInfo;
import com.beidouapp.et.handler.EtExecutable;
import com.beidouapp.et.handler.impl.ObtainIPHandler;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.TrackerGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseFileImpl {
    public static final Logger logger = LoggerFactory.getLogger(BaseFileImpl.class);

    /**
     * 设置文件服务器IP地址.
     */
    public void obtainFileServerByLB(IContext etContext) {
        LBResponseInfo info = etContext.get(EtKeyConstant.CACHE_FILE);
        if (info == null || info.getTimeExpiration() <= System.currentTimeMillis()) {
            logger.debug("first usage or time expiration.");
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("domain", etContext.get(EtKeyConstant.LB_IP)); // 负载均衡服务器IP or domain
            params.put("port", etContext.get(EtKeyConstant.LB_PORT)); // 负载均衡服务器port
            params.put("serverType", 4); // 文件服务器类型.
            params.put("userId", etContext.getUserName()); // 用户ID.
            EtExecutable<LBResponseInfo> e = new ObtainIPHandler();
            info = e.execute(params);
            etContext.set(EtKeyConstant.CACHE_FILE, info);
        }
        TrackerGroup tg = new TrackerGroup(new InetSocketAddress[]{new InetSocketAddress(info.getDomain(), info.getPort())});
        ClientGlobal.setG_tracker_group(tg);
    }
}