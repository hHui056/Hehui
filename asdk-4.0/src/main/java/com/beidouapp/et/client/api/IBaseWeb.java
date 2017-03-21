package com.beidouapp.et.client.api;

import com.beidouapp.et.client.domain.UserInfo;

import java.util.List;

/**
 * 可用Web访问的接口.
 *
 * @author mhuang.
 */
public interface IBaseWeb {
    /**
     * 注册用户.<br/>
     * 同一个AppKey下，此接口将以username为校验属性.<br/>
     * 除了userid属性外，其他属性用户可填写，填写后，将会被保存。<br/>
     * 如果用户填写userid属性后，服务器将忽略。 成功返回时，userid将会分配相关取值，而非null.
     *
     * @param userInfoList 要添加的用户列表. 必填.
     * @return json格式的字符串标识.
     * 否则抛WEB_REGIST_USER（10721）异常.
     */
    public List<UserInfo> addUser(List<UserInfo> userInfoList);
}