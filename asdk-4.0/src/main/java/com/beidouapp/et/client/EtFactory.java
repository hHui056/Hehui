package com.beidouapp.et.client;

import com.beidouapp.et.client.api.IBaseWeb;
import com.beidouapp.et.client.api.IContext;
import com.beidouapp.et.common.constant.EtKeyConstant;
import com.beidouapp.et.core.EtContext;
import com.beidouapp.et.core.EtManagerImpl;
import com.beidouapp.et.core.impl.BaseWebImpl;
import com.beidouapp.et.core.pojo.LBResponseInfo;
import com.beidouapp.et.handler.EtExecutable;
import com.beidouapp.et.handler.impl.ObtainIPHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * SDK 工厂.<br/>
 * 通过工厂可以创建EtManager管理器，和http管理实例.
 *
 * @author mhuang.
 */
public class EtFactory {

	/**
	 * 用户配置上下文对象.
	 */
	private IContext ctx;

	/**
	 * 创建EtManager管理器实例.
	 *
	 * @param etContext
	 *            参数配置上下文.
	 * @return EtManager 管理器.
	 */
	public EtManager create(IContext etContext) {
		logger.debug("starting create EtManager.");

		LBResponseInfo info = etContext.get(EtKeyConstant.CACHE_WEB);
		if (info == null
				|| info.getTimeExpiration() <= System.currentTimeMillis()) {
			logger.debug("first usage or time expiration.");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("domain", etContext.get(EtKeyConstant.LB_IP)); // 负载均衡服务器IP
																		// or
																		// domain
			params.put("port", etContext.get(EtKeyConstant.LB_PORT)); // 负载均衡服务器port
			params.put("serverType", 1); // 服务器类型.
			params.put("userId", etContext.getUserName()); // 用户ID.
			EtExecutable<LBResponseInfo> e = new ObtainIPHandler();
			info = e.execute(params);
			etContext.set(EtKeyConstant.CACHE_IM, info);
		}
		if (info.getData() != null) {
			etContext.set(EtKeyConstant.IM_IP, info.getDomain()).set(
					EtKeyConstant.IM_PORT, info.getPort());
			EtManager m = new EtManagerImpl(etContext);
			logger.debug("create EtManager success.");
			return m;
		} else {
			return null;
		}

	}

	public IBaseWeb createWebWithLB(IContext etContext) {
		return new BaseWebImpl(etContext);
	}

	public synchronized IContext createContext() {
		if (ctx == null) {
			ctx = new EtContext();
		}
		logger.debug("IContext hashcode is {}", ctx.toString());
		return ctx;
	}

	public String getSdkVersion() {
		return "1.7.0";
	}

	public static final Logger logger = LoggerFactory
			.getLogger(EtFactory.class);
}