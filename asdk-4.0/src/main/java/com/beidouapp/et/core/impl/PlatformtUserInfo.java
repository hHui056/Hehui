package com.beidouapp.et.core.impl;

import com.beidouapp.et.client.domain.UserInfo;

/**
 * 平台用户信息.
 *
 * @author mhuang.
 */
public class PlatformtUserInfo extends UserInfo
{
	private static final long serialVersionUID = 1L;
	/**
     * 平台标识.(默认为1-android)
     */
    private int platformtype;

    public PlatformtUserInfo(){
    	
    }
    
    public PlatformtUserInfo(UserInfo userInfo)
    {
        super.setUserid(userInfo.getUserid());
        super.setUsername(userInfo.getUsername());
        super.setNickname(userInfo.getNickname());
    }

    /**
     * 平台标识.(默认为1-android).
     *
     * @return 平台标识的整形表示.
     */
    public int getPlatformtype()
    {
        return this.platformtype;
    }

    /**
     * 平台标识.(默认为1-android).
     *
     * @param platformtype 平台类型.
     */
    public void setPlatformtype(int platformtype)
    {
        this.platformtype = platformtype;
    }

    @Override
    public String toString()
    {
        return "PlatformtUserInfo [platformtype=" + platformtype + ", toString()=" + super.toString() + "]";
    }
}