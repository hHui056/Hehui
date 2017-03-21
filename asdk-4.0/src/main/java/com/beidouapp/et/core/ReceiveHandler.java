package com.beidouapp.et.core;

import com.alibaba.fastjson.JSON;
import com.beidouapp.et.client.api.IContext;
import com.beidouapp.et.client.callback.IFileReceiveListener;
import com.beidouapp.et.client.callback.IListener;
import com.beidouapp.et.client.callback.IReceiveListener;
import com.beidouapp.et.client.callback.IUserStatusListener;
import com.beidouapp.et.client.domain.DocumentInfo;
import com.beidouapp.et.client.domain.EtMsg;
import com.beidouapp.et.common.constant.EtConstants;
import com.beidouapp.et.common.constant.EtKeyConstant;
import com.beidouapp.et.common.enums.FileTransferTypeEnum;
import com.beidouapp.et.core.impl.TopicTypeEnum;
import com.beidouapp.et.exception.EtExceptionCode;
import com.beidouapp.et.exception.EtRuntimeException;
import com.beidouapp.et.util.param.SplitterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;


/**
 * 接收消息处理器.
 *
 * @author mhuang.
 */
public class ReceiveHandler {
    public static final Logger logger = LoggerFactory.getLogger(ReceiveHandler.class);

    /**
     * 处理SDK内部消息.非系统消息将处理后通知用户.
     *
     * @param topic       主题.
     * @param payload     内容.
     * @param listenerMap 监听器对象缓存.
     */
    public static void processInnerTopic(IContext etContext, final String topic, final byte[] payload,
                                         Map<String, IListener> listenerMap) {
        logger.debug("receive msg topic={}", topic);
        IReceiveListener receiveListener = (IReceiveListener) listenerMap.get(EtKeyConstant.LISTENER_MASTER);
        // TODO:此处可采用策略模式或拦截器.如需必要，采用队列消息机制并发处理.
        if (topic.contains(TopicTypeEnum.SFILE.getCode())) {//收到文件
            IFileReceiveListener fileListener = (IFileReceiveListener) listenerMap.get(EtKeyConstant.LISTENER_FILE);
            if (fileListener == null) {
                logger.warn("You do not have to configure the fileListener");
                return;
            }
            fileHandle(topic, payload, fileListener);
            return;
        }

        if (topic.contains(TopicTypeEnum.ONLINE.getCode())) {//用户在线通知
            IUserStatusListener userStatusListener = (IUserStatusListener) listenerMap.get(EtKeyConstant.LISTENER_USER_STATUS);

            if (userStatusListener == null) {
                logger.warn("You do not have to configure the UserStatusListener");
                return;
            }
            try {
                List<String> list = SplitterUtil.splitterList(topic, EtConstants.SEPARATOR_AIT);
                userStatusListener.concernOnlineStatus(list.get(1), "1");
            } catch (Exception e) {
                logger.error("online@userId is failed! topic=" + topic, e);
                throw new EtRuntimeException(EtExceptionCode.ONLINE, "concern online status topic resolve is failed", e);
            }
            return;
        }
        if (topic.contains(TopicTypeEnum.OFFLINE.getCode())) {//用户离线通知
            IUserStatusListener userStatusListener = (IUserStatusListener) listenerMap.get(EtKeyConstant.LISTENER_USER_STATUS);
            if (userStatusListener == null) {
                logger.warn("You do not have to configure the UserStatusListener");
                return;
            }
            try {
                List<String> list = SplitterUtil.splitterList(topic, EtConstants.SEPARATOR_AIT);
                userStatusListener.concernOnlineStatus(list.get(1), "0");
            } catch (Exception e) {
                logger.error("offline@userId is failed! topic=" + topic, e);
                throw new EtRuntimeException(EtExceptionCode.OFFLINE, "concern offline status topic resolve is failed", e);
            }
            return;
        }

//		if (topic.contains(TopicTypeEnum.STATUS.getCode())) {//查询用户状态
//			IUserStatusListener userStatusListener = (IUserStatusListener) listenerMap.get(EtKeyConstant.LISTENER_USER_STATUS);
//			if (userStatusListener == null) {
//				logger.warn("You do not have to configure the UserStatusListener");
//				return;
//			}
//			try {
//				// 0&414
//				List<String> list = SplitterUtil.splitterList(new String(payload, EtConstants.CHARSETS_UTF8), EtConstants.SEPARATOR_VERSUS);
//				userStatusListener.concernOnlineStatus(list.get(1), list.get(0));
//			} catch (Exception e) {
//				logger.error("status@userId is failed! topic=" + topic, e);
//				throw new EtRuntimeException(EtExceptionCode.PEERSTATE_FAIL, "get user status is failed!", e);
//			}
//			return;
//		}
        if (receiveListener == null) {
            logger.warn("You not set IReceiveListener.");
            return;
        }
        EtMsg msg = new EtMsg().setTopic(topic).setPayload(payload);
        analyzeMsg(topic, msg);
        receiveListener.onMessage(msg);
    }

    private static void fileHandle(String topic, byte[] payload, IFileReceiveListener fileListener) {
        String userId = SplitterUtil.splitterList(topic, EtConstants.SEPARATOR_AIT).get(1);// 获得发送端用户id
        String content = "";
        try {
            content = new String(payload, EtConstants.CHARSETS_UTF8);
        } catch (UnsupportedEncodingException e) {
            // ignore.
        }
        if (fileListener == null) {
            logger.warn("user is not config IFileReceiveListener! but use file module.the msg is lost. topic={}, content={}",
                    new Object[]{topic, content});
            return;
        }
        DocumentInfo documentInfo = null;
        try {
            documentInfo = JSON.parseObject(content, DocumentInfo.class);
        } catch (Exception e) {
            documentInfo = new DocumentInfo();
            documentInfo.setType(FileTransferTypeEnum.EXCEPTION.getCode());
            documentInfo.setDescn("documentInfo json resolve exception \"" + content + "\". " + e.getLocalizedMessage());
        }
        String type = documentInfo.getType();
        // 通知APP接收到文件传输消息.
        if (FileTransferTypeEnum.PUSH.getCode().equalsIgnoreCase(type)) {
            // 通知用户接收文件信息.
            fileListener.onReceived(userId, documentInfo);
        } else if (FileTransferTypeEnum.PULL.getCode().equalsIgnoreCase(type)) {
            fileListener.onCheckingFile(userId, documentInfo);
        } else if (FileTransferTypeEnum.EXCEPTION.getCode().equalsIgnoreCase(type)) {
            // TODO:可以新增一个onException方法，用户处理.
            // 异常信息.让用户自己去处理
            fileListener.onCheckingFile(userId, documentInfo);
        }
    }

    /**
     * 分析消息内容.
     *
     * @param topic 主题.
     * @param msg   消息对象.
     */
    private static void analyzeMsg(String topic, EtMsg msg) {
        if (topic.contains(TopicTypeEnum.CHAT.getCode())) {
            // 类型为 点对点消息.
            List<String> list = SplitterUtil.splitterList(topic, TopicTypeEnum.CHAT.getCode());
            msg.setSendUserId(list.get(1));
        } else if (topic.contains(TopicTypeEnum.CHAT_EX.getCode())) {
            // 类型为 点对点消息(扩展json).
            List<String> list = SplitterUtil.splitterList(topic, TopicTypeEnum.CHAT_EX.getCode());
            msg.setSendUserId(list.get(1));
        } else if (topic.contains(EtConstants.SEPARATOR_AIT)) {
            List<String> list = SplitterUtil.splitterList(topic, EtConstants.SEPARATOR_AIT);
            msg.setSendUserId(list.get(1));
        }
    }

    private static String getWebServerUrl(String topic, String msgInfo) {
        TopicTypeEnum en = TopicTypeEnum.getTopicTypeEnumByContainCode(topic);
        if ("".equalsIgnoreCase(en.getProtocolHeader())) {
            throw new EtRuntimeException(EtExceptionCode.CONNACK_CONNECTION_REFUSED_UNACCEPTED_PROTOCOL_VERSION,
                    "topic = 【" + topic + "】, unaccepted protocol version!");
        }
        return en.getProtocolHeader() + msgInfo;
    }
}