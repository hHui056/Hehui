package com.beidouapp.et.core.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.beidouapp.et.client.api.IBaseWeb;
import com.beidouapp.et.client.api.IContext;
import com.beidouapp.et.client.domain.UserInfo;
import com.beidouapp.et.common.constant.EtConstants;
import com.beidouapp.et.common.constant.EtKeyConstant;
import com.beidouapp.et.core.pojo.LBResponseInfo;
import com.beidouapp.et.exception.EtExceptionCode;
import com.beidouapp.et.exception.EtRuntimeException;
import com.beidouapp.et.handler.EtExecutable;
import com.beidouapp.et.handler.impl.ObtainIPHandler;
import com.beidouapp.et.http.HttpRequest;
import com.beidouapp.et.http.HttpRequest.HttpRequestException;
import com.beidouapp.et.util.codec.EncryptUtil;
import com.beidouapp.et.util.param.CheckingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 默认基础的Http接口实现.
 */
public class BaseWebImpl implements IBaseWeb {
    public static final Logger logger = LoggerFactory.getLogger(BaseWebImpl.class);
    public static final String ERROR_MSG = "param is null!";
    public static final String USER_ID = "userid";
    public static final String USER_NAME = "username";
    public static final String USER = "user";
    public static final String TOPIC = "topic";
    public static final String USER_LIST = "userlist";
    private IContext etContext;

    /**
     * 通过负载均衡服务初始化Web Client 服务.
     *
     * @param etContext
     */
    public BaseWebImpl(IContext etContext) {
        this.etContext = etContext;
    }


    /**
     * 获取随机数.<br/>
     * 默认获取当前时间的毫秒数.从右往左接触len个长度.
     *
     * @param len 随机数长度.
     * @return 随机数.
     */
    protected String getRandomString(int len) {
        String randomString = Long.toString(System.currentTimeMillis());
        int length = randomString.length();
        CheckingUtil.checkArgument(length > len, "指定长度大于预设长度!");
        return randomString.substring(length - len);
    }

    /**
     * 发送POST请求.
     *
     * @param url    请求URL.
     * @param params 用户的参数.
     * @return 请求结果字符串表示.
     */
    protected String doPost(String url, Map<String, String> params) {
        HttpRequest request = HttpRequest.post(url);
        request = request.form(params, EtConstants.CHARSETS_UTF8);
        String body = request.body();
        StringBuilder sb = new StringBuilder(url.length() + body.length() + 500);
        sb.append(EtConstants.LINE_SEPARATOR);
        sb.append("----------------------------------------------------------------------------");
        sb.append(EtConstants.LINE_SEPARATOR);
        sb.append("Request     URL: ").append(url).append(EtConstants.LINE_SEPARATOR);
        sb.append("Response result: ").append(body).append(EtConstants.LINE_SEPARATOR);
        sb.append("----------------------------------------------------------------------------");
        sb.append(EtConstants.LINE_SEPARATOR);
        logger.debug(sb.toString());
        return body;
    }

    public String getWebUrlByLB(IContext etContext) {
        LBResponseInfo info = etContext.get(EtKeyConstant.CACHE_WEB);
        if (info == null || info.getTimeExpiration() <= System.currentTimeMillis()) {
            logger.debug("first usage or time expiration.");
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("domain", etContext.get(EtKeyConstant.LB_IP)); // 负载均衡服务器IP or domain
            params.put("port", etContext.get(EtKeyConstant.LB_PORT)); // 负载均衡服务器port
            params.put("serverType", 2); // 服务器类型.
            params.put("userId", ObtainIPHandler.UID); // 用户ID.
            EtExecutable<LBResponseInfo> e = new ObtainIPHandler();
            info = e.execute(params);
            etContext.set(EtKeyConstant.CACHE_WEB, info);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("http://").append(info.getDomain()).append(EtConstants.SEPARATOR_COLON).append(info.getPort());
        String s = sb.toString();
        etContext.set(EtKeyConstant.WEB_URL, s);
        return s;
    }

    /**
     * 请求Web服务.
     *
     * @param msg
     * @param businessTypeEnum
     * @return
     */
    protected String requestWeb(String msg, WebBusinessEnum businessTypeEnum) {
        String randomStr = getRandomString(EtConstants.RANDOM_LENGTH);
        StringBuilder sb = new StringBuilder(150);
        sb.append(etContext.getAppKey()).append(etContext.getSecretKey()).append(randomStr);
        // Md5(sort(appkey+secretkey+ timestamp)) 生成32位小写的验证字符串
        String apptoken = EncryptUtil.sort(sb.toString());
        apptoken = EncryptUtil.MD5(apptoken);
        String base64Data = "";
        try {
            base64Data = EncryptUtil.encode(msg.getBytes(EtConstants.CHARSETS_UTF8));
        } catch (UnsupportedEncodingException e1) {
            logger.error(msg + " is not encoding UTF-8.", e1);
        }
        logger.debug("After base64 encoding Data = " + base64Data);
        Map<String, String> params = new HashMap<String, String>();
        params.put(EtConstants.APP_KEY, etContext.getAppKey());
        params.put(EtConstants.TOKEN_KEY, apptoken);
        params.put(EtConstants.RANDOM_KEY, randomStr);
        params.put(EtConstants.DATA_KEY, base64Data);
        StringBuilder st = new StringBuilder(50);
        //st.append(etContext.get(EtKeyConstant.WEB_URL)).append(businessTypeEnum.getCode());
        st.append(getWebUrlByLB(this.etContext)).append(businessTypeEnum.getCode());
        try {
            String content = doPost(st.toString(), params);
            return content;
        } catch (HttpRequestException e) {
            throw new EtRuntimeException(EtExceptionCode.WEB_SERVER_CONNECT, e.getLocalizedMessage());
        } catch (Exception e) {
            throw new EtRuntimeException(EtExceptionCode.WEB_UNKNOWN_EXCEPTION, "request 【" + businessTypeEnum.getName() + "】 business is fail!", e);
        }
    }

    @Override
    public List<UserInfo> addUser(List<UserInfo> userInfoList) {
        logger.debug("call addUser(list={})", userInfoList);
        String returnInfo = null;
        CheckingUtil.checkEmpty(userInfoList, ERROR_MSG);
        List<PlatformtUserInfo> tempList = new ArrayList<PlatformtUserInfo>();
        Integer platformType = etContext.get(EtKeyConstant.PLATFORM_FOR_S_SDK);
        if (platformType == null) {
            platformType = EtConstants.PLATFORM_FOR_S_SDK;
        } else if (!(platformType == 1 || platformType == 6)) {
            logger.error("platform type is incorrect");
            throw new EtRuntimeException(EtExceptionCode.PLATFORM_TYPE, "platform type is incorrect");
        }
        try {
            for (int i = 0; i < userInfoList.size(); i++) {
                UserInfo userInfo = userInfoList.get(i);
                if (null == userInfo || null == userInfo.getUsername() || userInfo.getUsername().isEmpty()) {
                    throw new RuntimeException("In the index " + i + ", the username can not be empty or null!");
                }
                PlatformtUserInfo info = new PlatformtUserInfo(userInfo);
                info.setPlatformtype(platformType);
                tempList.add(info);
            }
            SimplePropertyPreFilter filter = new SimplePropertyPreFilter(PlatformtUserInfo.class);
            filter.getExcludes().add("userid");
            String jsonText = JSON.toJSONString(tempList, filter);
            StringBuilder sb = new StringBuilder(jsonText.length() + 20);
            sb.append("{\"userlists\":").append(jsonText).append("}");
            String resutJson = requestWeb(sb.toString(), WebBusinessEnum.ADD_USER);
            JSONObject jo = JSON.parseObject(resutJson);
            int returnCode = jo.getInteger(EtConstants.MSG_RESPONSE_CODE);
            returnInfo = (String) jo.get(EtConstants.MSG_RESPONSE_CONTENT);
            if (0 != returnCode) {
                throw new RuntimeException(returnInfo );
            }
            List<UserInfo> result = JSON.parseObject(jo.get(USER).toString(), new TypeReference<ArrayList<UserInfo>>() {
            });
            logger.debug("return list={}", result);
            return result;
        } catch (Exception e) {
            logger.error("call regist(list={}) failed! occur {}", userInfoList, e);
            throw new EtRuntimeException(EtExceptionCode.WEB_REGIST_USER, e.getMessage());
        }
    }
}