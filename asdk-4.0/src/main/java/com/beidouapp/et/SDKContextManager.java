/**
 *
 */
package com.beidouapp.et;

import android.content.Context;
import com.beidouapp.et.client.domain.UserInfo;
import com.beidouapp.et.core.SDKContext;

/**
 * 创建/销毁{@link ISDKContext}。
 */
public class SDKContextManager {
    /**
     * 创建ISDKContext实例。
     *
     * @param parameters SdkContext配置参数。
     * @throws IllegalArgumentException 如果参数为null。
     */
    public static ISDKContext createContext(SDKContextParameters parameters, Context context) {
        if (parameters == null) {
            throw new IllegalArgumentException("sdk context parameters can not be null");
        }
        return new SDKContext(parameters, context);
    }

    /**
     * 销毁ISDKContext实例。
     *
     * @throws IllegalArgumentException 如果参数为null。
     */
    public static void destroyContext(ISDKContext sdkContext) {
        if (sdkContext == null) {
            throw new IllegalArgumentException("context 不能为null");
        }
        sdkContext.destroyContext();
    }

    /**
     * 注册新用户.
     *
     * @param username   用户名
     * @param nickname   昵称
     * @param serverHost 注册服务器的主机地址
     * @param serverPort 注册服务器的端口号
     * @param appKey     平台分配的APPKey
     * @param secretKey  平台分配的secretKey
     * @param listener   注册用户成功，回调 {@link IFriendsActionListener#onResultData(Object)}
     *                   ，参数类型是{@link UserInfo}，表示注册成功的用户信息； 否则，回调
     *                   {@link IFriendsActionListener#onFailure(ErrorInfo)}
     * @throws IllegalArgumentException 如果参数为null。
     */
    public static void addUser(String username, String nickname, String serverHost, int serverPort, String appKey, String secretKey, IFriendsActionListener listener) {
        SDKContext.registerUser(username, nickname, serverHost, serverPort, appKey, secretKey, listener);
    }

}
