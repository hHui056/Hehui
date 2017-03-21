package com.beidouapp.et.core.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.beidouapp.et.client.api.IContext;
import com.beidouapp.et.client.api.IWeb;
import com.beidouapp.et.client.domain.GroupInfo;
import com.beidouapp.et.client.domain.PublishData;
import com.beidouapp.et.client.domain.ResultEntity;
import com.beidouapp.et.client.domain.UserInfo;
import com.beidouapp.et.common.constant.EtConstants;
import com.beidouapp.et.exception.EtExceptionCode;
import com.beidouapp.et.exception.EtRuntimeException;
import com.beidouapp.et.util.LogFileUtil;
import com.beidouapp.et.util.param.CheckingUtil;
import com.beidouapp.et.util.param.CollectionsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WebImpl extends BaseWebImpl implements IWeb {
    public static final Logger logger = LoggerFactory.getLogger(WebImpl.class);
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
    public WebImpl(IContext etContext) {
        super(etContext);
        this.etContext = etContext;
    }

    @Override
    public UserInfo addBuddy(String userid, String friendid) {
        CheckingUtil.checkNull(userid, ERROR_MSG);
        CheckingUtil.checkNull(friendid, ERROR_MSG);
        String returnInfo = null;
        try {
            String jsonText = JSON.toJSONString(CollectionsUtil.ImmutableMap(USER_ID, userid, "friendid", friendid));
            String resutJson = requestWeb(jsonText, WebBusinessEnum.ADD_FRIEND);
            JSONObject jo = JSON.parseObject(resutJson);
            Integer returnCode = (Integer) jo.get(EtConstants.MSG_RESPONSE_CODE);
            returnInfo = (String) jo.get(EtConstants.MSG_RESPONSE_CONTENT);
            if (0 != returnCode) {
                throw new RuntimeException(returnInfo);
            }
            UserInfo result = JSON.parseObject(jo.get("friendinfo").toString(), new TypeReference<UserInfo>() {
            });
            return result;
        } catch (Exception e) {
            LogFileUtil.writeErrorLog("addBuddy faild " + e.getMessage());
            logger.error("call addBuddy(userid={}, friendId={}) failed! occur {}", new Object[]{userid, friendid, e});
            throw new EtRuntimeException(EtExceptionCode.WEB_ADD_BUDDY, e.getMessage());
        }
    }

    @Override
    public ResultEntity addBuddy(String userid, String friendid, int notify) {
        CheckingUtil.checkNull(userid, ERROR_MSG);
        CheckingUtil.checkNull(friendid, ERROR_MSG);
        ResultEntity resultEntity = new ResultEntity();
        try {
            String jsonText = JSON.toJSONString(CollectionsUtil.ImmutableMap(USER_ID, userid, "friendid", friendid, "notify", notify));
            String resutJson = requestWeb(jsonText, WebBusinessEnum.ADD_FRIEND_EX);
            resultEntity.setOriginJsonString(resutJson);
            JSONObject jo = JSON.parseObject(resutJson);
            Integer returnCode = (Integer) jo.get(EtConstants.MSG_RESPONSE_CODE);
            String returnInfo = (String) jo.get(EtConstants.MSG_RESPONSE_CONTENT);
            resultEntity.setCode(returnCode.toString()).setMessageInfo(returnInfo);
            JSONObject fi = jo.getJSONObject("friendinfo");
            if (fi != null) {
                resultEntity.addData("userid", fi.get("userid")).addData("nickname", fi.get("nickname")).addData("username", fi.get("username"));
            }
        } catch (Exception e) {
            LogFileUtil.writeErrorLog("addBuddy faild " + e.getMessage());
            logger.error("call addBuddyEx(userid={}, friendId={}, notify={}) failed! occur {}", new Object[]{userid, friendid, notify, e});
            throw new EtRuntimeException(EtExceptionCode.WEB_ADD_BUDDY_EX, e.getMessage());
        }
        return resultEntity;
    }

    @Override
    public UserInfo removeBuddy(String userid, String friendid) {
        CheckingUtil.checkNull(userid, ERROR_MSG);
        CheckingUtil.checkNull(friendid, ERROR_MSG);
        try {
            String jsonText = JSON.toJSONString(CollectionsUtil.ImmutableMap(USER_ID, userid, "friendid", friendid));
            String resutJson = requestWeb(jsonText, WebBusinessEnum.DELETE_FRIEND);
            JSONObject jo = JSON.parseObject(resutJson);
            Integer returnCode = (Integer) jo.get(EtConstants.MSG_RESPONSE_CODE);
            String returnInfo = (String) jo.get(EtConstants.MSG_RESPONSE_CONTENT);
            if (0 != returnCode) {
                throw new RuntimeException(returnInfo);
            }
            UserInfo result = JSON.parseObject(jo.get("friendinfo").toString(), new TypeReference<UserInfo>() {
            });
            return result;
        } catch (Exception e) {
            LogFileUtil.writeErrorLog("removebuddy faild " + e.getMessage());
            logger.error("call removeBuddy(userid={}, friendId={}) failed! occur {}", new Object[]{userid, friendid, e});
            throw new EtRuntimeException(EtExceptionCode.WEB_REMOVE_BUDDY, e.getMessage());
        }
    }

    @Override
    public ResultEntity removeBuddy(String userId, String friendId, int notify) {
        CheckingUtil.checkNull(userId, ERROR_MSG);
        CheckingUtil.checkNull(friendId, ERROR_MSG);
        ResultEntity resultEntity = new ResultEntity();
        try {
            String jsonText = JSON.toJSONString(CollectionsUtil.ImmutableMap(USER_ID, userId, "friendid", friendId, "notify", notify));
            String resutJson = requestWeb(jsonText, WebBusinessEnum.DELETE_FRIEND_EX);
            resultEntity.setOriginJsonString(resutJson);
            JSONObject jo = JSON.parseObject(resutJson);
            Integer returnCode = (Integer) jo.get(EtConstants.MSG_RESPONSE_CODE);
            String returnInfo = (String) jo.get(EtConstants.MSG_RESPONSE_CONTENT);
            resultEntity.setCode(returnCode.toString()).setMessageInfo(returnInfo);
            JSONObject fi = jo.getJSONObject("friendinfo");
            if (fi != null) {
                resultEntity.addData("userid", fi.get("userid")).addData("nickname", fi.get("nickname")).addData("username", fi.get("username"));
            }
        } catch (Exception e) {
            LogFileUtil.writeErrorLog("removebuddy faild " + e.getMessage());
            logger.error("call removeBuddy(userid={}, friendId={}, notify={}) failed! occur {}", new Object[]{userId, friendId, notify, e});
            throw new EtRuntimeException(EtExceptionCode.WEB_REMOVE_BUDDY_EX, e.getMessage());
        }
        return resultEntity;
    }

    @Override
    public List<UserInfo> getBuddies(String userid) {
        CheckingUtil.checkNull(userid, ERROR_MSG);
        try {
            String jsonText = JSON.toJSONString(CollectionsUtil.ImmutableMap(USER_ID, userid));
            String resutJson = requestWeb(jsonText, WebBusinessEnum.GET_FRIEND_LISTS);
            JSONObject jo = JSON.parseObject(resutJson);
            Integer returnCode = (Integer) jo.get(EtConstants.MSG_RESPONSE_CODE);
            String returnInfo = (String) jo.get(EtConstants.MSG_RESPONSE_CONTENT);
            if (0 != returnCode) {
                throw new RuntimeException(returnInfo);
            }
            List<UserInfo> result = JSON.parseObject(jo.get("friendlist").toString(), new TypeReference<ArrayList<UserInfo>>() {
            });
            return result;
        } catch (Exception e) {
            LogFileUtil.writeErrorLog("getBuddies faild " + e.getMessage());
            logger.error("call buddies(userid={}) failed! occur {}", new Object[]{userid, e});
            throw new EtRuntimeException(EtExceptionCode.WEB_BUDDIES, e.getMessage());
        }
    }

    @Override
    public GroupInfo createGroup(String userid, String groupname, List<String> userIdList) {
        CheckingUtil.checkNull(userid, ERROR_MSG);
        CheckingUtil.checkNull(groupname, ERROR_MSG);
        try {
            Map<String, Object> params = CollectionsUtil.ImmutableMap(USER_ID, userid, "groupname", groupname, USER_LIST, userIdList);
            String jsonText = JSON.toJSONString(params);
            String resutJson = requestWeb(jsonText, WebBusinessEnum.CREATE_GROUP);
            JSONObject jo = JSON.parseObject(resutJson);
            Integer returnCode = (Integer) jo.get(EtConstants.MSG_RESPONSE_CODE);
            String returnInfo = (String) jo.get(EtConstants.MSG_RESPONSE_CONTENT);
            if (0 != returnCode) {
                throw new RuntimeException(returnInfo);
            }
            GroupInfo result = JSON.parseObject(jo.get("groupinfo").toString(), new TypeReference<GroupInfo>() {
            });
            return result;
        } catch (Exception e) {
            LogFileUtil.writeErrorLog("createGroup faild " + e.getMessage());
            logger.error("call createGrp(userid={}, groupname={}, List<String>={}) failed! occur {}", new Object[]{userid, groupname, userIdList, e});
            throw new EtRuntimeException(EtExceptionCode.WEB_CREATE_GRP, e.getMessage());
        }
    }

    @Override
    public List<GroupInfo> getGroups(String userid) {
        CheckingUtil.checkNull(userid, ERROR_MSG);
        try {
            String jsonText = JSON.toJSONString(CollectionsUtil.ImmutableMap(USER_ID, userid));
            String resutJson = requestWeb(jsonText, WebBusinessEnum.GET_GROUP_LIST);
            JSONObject jo = JSON.parseObject(resutJson);
            Integer returnCode = (Integer) jo.get(EtConstants.MSG_RESPONSE_CODE);
            String returnInfo = (String) jo.get(EtConstants.MSG_RESPONSE_CONTENT);
            if (0 != returnCode) {
                throw new RuntimeException(returnInfo);
            }
            List<GroupInfo> result = JSON.parseObject(jo.get("grouplist").toString(), new TypeReference<ArrayList<GroupInfo>>() {
            });
            return result;
        } catch (Exception e) {
            LogFileUtil.writeErrorLog("getGroups faild " + e.getMessage());
            logger.error("call grps(userid={}) failed! occur {}", new Object[]{userid, e});
            throw new EtRuntimeException(EtExceptionCode.WEB_GRPS, e.getMessage());
        }
    }

    @Override
    public GroupInfo destroyGroup(String topic, String userid) {
        CheckingUtil.checkNull(topic, ERROR_MSG);
        CheckingUtil.checkNull(userid, ERROR_MSG);
        try {
            String jsonText = JSON.toJSONString(CollectionsUtil.ImmutableMap(TOPIC, topic, USER_ID, userid));
            String resutJson = requestWeb(jsonText, WebBusinessEnum.RELEASE_GROUP);
            JSONObject jo = JSON.parseObject(resutJson);
            Integer returnCode = (Integer) jo.get(EtConstants.MSG_RESPONSE_CODE);
            String returnInfo = (String) jo.get(EtConstants.MSG_RESPONSE_CONTENT);
            if (0 != returnCode) {
                throw new RuntimeException(returnInfo);
            }
            GroupInfo result = JSON.parseObject(jo.get("groupinfo").toString(), new TypeReference<GroupInfo>() {
            });
            return result;
        } catch (Exception e) {
            LogFileUtil.writeErrorLog("destroyGroup faild " + e.getMessage());
            logger.error("call dismissGrp(topic={}, userid={}) failed! occur {}", new Object[]{topic, userid, e});
            throw new EtRuntimeException(EtExceptionCode.WEB_DISMISS_GRP, e.getMessage());
        }
    }

    @Override
    public List<UserInfo> addGroupMembers(String topic, List<String> userlists) {
        CheckingUtil.checkNull(topic, ERROR_MSG);
        CheckingUtil.checkEmpty(userlists, ERROR_MSG);
        try {
            String jsonText = JSON.toJSONString(CollectionsUtil.ImmutableMap(TOPIC, topic, USER_LIST, userlists));
            String resutJson = requestWeb(jsonText, WebBusinessEnum.ADD_GROUP_MEMBER);
            JSONObject jo = JSON.parseObject(resutJson);
            Integer returnCode = (Integer) jo.get(EtConstants.MSG_RESPONSE_CODE);
            String returnInfo = (String) jo.get(EtConstants.MSG_RESPONSE_CONTENT);
            if (0 != returnCode) {
                throw new RuntimeException(returnInfo + ", " + jo.get("user"));
            }
            List<UserInfo> result = JSON.parseObject(jo.get("userlist").toString(), new TypeReference<ArrayList<UserInfo>>() {
            });
            return result;
        } catch (Exception e) {
            LogFileUtil.writeErrorLog("addGroupMembers faild " + e.getMessage());
            logger.error("call addGrpMember(topic={}, List<String>={}) failed! occur {}", new Object[]{topic, userlists, e});
            throw new EtRuntimeException(EtExceptionCode.WEB_ADD_GRP_MEMBER, e.getMessage());
        }
    }

    @Override
    public List<UserInfo> removeGroupMembers(String userId, String topic, List<String> userlists) {
        CheckingUtil.checkNull(userId, ERROR_MSG);
        CheckingUtil.checkNull(topic, ERROR_MSG);
        CheckingUtil.checkNull(userlists, "please delete set user list.");
        try {
            String jsonText = JSON.toJSONString(CollectionsUtil.ImmutableMap(USER_ID, userId, TOPIC, topic, USER_LIST, userlists));
            String resutJson = requestWeb(jsonText, WebBusinessEnum.DELETE_GROUP_MEMBER);
            JSONObject jo = JSON.parseObject(resutJson);
            Integer returnCode = (Integer) jo.get(EtConstants.MSG_RESPONSE_CODE);
            String returnInfo = (String) jo.get(EtConstants.MSG_RESPONSE_CONTENT);
            if (0 != returnCode) {
                throw new RuntimeException(returnInfo);
            }
            List<UserInfo> result = JSON.parseObject(jo.get("userlist").toString(), new TypeReference<ArrayList<UserInfo>>() {
            });
            return result;
        } catch (Exception e) {
            LogFileUtil.writeErrorLog("removeGroupMembers faild " + e.getMessage());
            logger.error("call removeGrpMember(userId={}, topic={}, List<String>={}) failed! occur {}", new Object[]{userId, topic, userlists, e});
            throw new EtRuntimeException(EtExceptionCode.WEB_REMOVE_GRP_MEMBER, e.getMessage());
        }
    }

    @Override
    public void exitGroup(String groupId, String userId) {
        CheckingUtil.checkNull(groupId, ERROR_MSG);
        CheckingUtil.checkNull(userId, ERROR_MSG);
        try {
            String jsonText = JSON.toJSONString(CollectionsUtil.ImmutableMap(TOPIC, groupId, USER_ID, userId));
            String resutJson = requestWeb(jsonText, WebBusinessEnum.LOGOUT_GROUP);
            JSONObject jo = JSON.parseObject(resutJson);
            Integer returnCode = (Integer) jo.get(EtConstants.MSG_RESPONSE_CODE);
            String returnInfo = (String) jo.get(EtConstants.MSG_RESPONSE_CONTENT);
            if (0 != returnCode) {
                throw new RuntimeException(returnInfo);
            }
        } catch (Exception e) {
            LogFileUtil.writeErrorLog("exitGroup faild " + e.getMessage());
            logger.error("call quitGrp(groupId={}, userid={}) failed! occur {}", new Object[]{groupId, userId, e});
            throw new EtRuntimeException(EtExceptionCode.WEB_GRP_QUIT, e.getMessage());
        }
    }

    @Override
    public List<UserInfo> getGroupMembers(String groupId, String userid) {
        CheckingUtil.checkNull(groupId, ERROR_MSG);
        CheckingUtil.checkNull(userid, ERROR_MSG);
        try {
            String jsonText = JSON.toJSONString(CollectionsUtil.ImmutableMap(TOPIC, groupId, USER_ID, userid));
            String resutJson = requestWeb(jsonText, WebBusinessEnum.GET_GROUP_USER_LISTS);
            JSONObject jo = JSON.parseObject(resutJson);
            Integer returnCode = (Integer) jo.get(EtConstants.MSG_RESPONSE_CODE);
            String returnInfo = (String) jo.get(EtConstants.MSG_RESPONSE_CONTENT);
            if (0 != returnCode) {
                throw new RuntimeException(returnInfo);
            }
            List<UserInfo> result = JSON.parseObject(jo.get("userlist").toString(), new TypeReference<ArrayList<UserInfo>>() {
            });
            return result;
        } catch (Exception e) {
            LogFileUtil.writeErrorLog("getGroupMembers faild " + e.getMessage());
            logger.error("call grpMembers(groupId={}, userid={}) failed! occur {}", new Object[]{groupId, userid, e});
            throw new EtRuntimeException(EtExceptionCode.WEB_GRP_MEMBERS, e.getMessage());
        }
    }

    @Override
    public String publishToUsers(List<PublishData> publishs) {
        CheckingUtil.checkNull(publishs, "publishs List " + ERROR_MSG);
        try {
            String jsonText = JSON.toJSONString(CollectionsUtil.ImmutableMap("publishs", publishs));
            String resutJson = requestWeb(jsonText, WebBusinessEnum.PUBLISH);

            JSONObject jo = JSON.parseObject(resutJson);
            Integer returnCode = (Integer) jo.get(EtConstants.MSG_RESPONSE_CODE);
            String returnInfo = (String) jo.get(EtConstants.MSG_RESPONSE_CONTENT);
            if (0 != returnCode) {
                throw new RuntimeException(returnInfo);
            }
            return String.valueOf(returnCode);
        } catch (Exception e) {
            LogFileUtil.writeErrorLog("publishToUsers faild " + e.getMessage());
            logger.error("call publishToUserList(List<PublishData>={}) failed! occur {}", new Object[]{publishs, e});
            throw new EtRuntimeException(EtExceptionCode.WEB_PUBLISH, e.getMessage());
        }
    }
}