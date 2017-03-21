package com.beidouapp.et.client.api;

import com.beidouapp.et.client.domain.GroupInfo;
import com.beidouapp.et.client.domain.PublishData;
import com.beidouapp.et.client.domain.ResultEntity;
import com.beidouapp.et.client.domain.UserInfo;

import java.util.List;

/**
 * 可用Web访问的接口.
 * 
 * @author mhuang.
 */
public interface IWeb extends IBaseWeb {

	/**
	 * 注册用户.<br/>
	 * 同一个AppKey下，此接口将以username为校验属性.<br/>
	 * 除了userid属性外，其他属性用户可填写，填写后，将会被保存。<br/>
	 * 如果用户填写userid属性后，服务器将忽略。 成功返回时，userid将会分配相关取值，而非null.
	 * 
	 * @param userInfoList
	 *            要添加的用户列表. 必填.
	 * @return 返回结果码，真实数据在map中，userid为key，UserInfo为value。
	 *         否则抛WEB_REGIST_USER（10721）异常.
	 */
	//public ResultEntity addUser(List<UserInfo> userInfoList);

	/**
	 * 添加好友.
	 * 
	 * @param userId
	 *            操作用户id. 必填.
	 * @param friendId
	 *            好友用户id. 必填.
	 * @return 成功返回该好友信息, 否则抛WEB_ADD_BUDDY（10712）异常.
	 */
	@Deprecated
	public UserInfo addBuddy(String userId, String friendId);

	/**
	 * 添加好友.配置是否通知好友.<br/>
	 * 抛运行期异常WEB_ADD_BUDDY_EX(10724)异常.
	 * 
	 * @param userId
	 *            操作用户id. 必填.
	 * @param friendId
	 *            好友用户id. 必填.
	 * @param notify
	 *            通知好友. 必填. 0:加好友时不通知好友; 非0:加好友时通知好友.
	 * 
	 * @return getData() 中key 包含userid、nickname、username.
	 */
	public ResultEntity addBuddy(String userId, String friendId, int notify);

	/**
	 * 获取好友列表.
	 * 
	 * @param userId
	 *            操作用户id. 必填.
	 * @return 返回含有UserInfo的列表.否则抛WEB_BUDDIES（10714）异常.
	 */
	public List<UserInfo> getBuddies(String userId);

	/**
	 * 删除好友.
	 * 
	 * @param userId
	 *            操作用户id. 必填.
	 * @param friendId
	 *            好友用户id. 必填.
	 * @return 成功返回UserInfo对象, 否则抛WEB_REMOVE_BUDDY（10713）异常.
	 */
	@Deprecated
	public UserInfo removeBuddy(String userId, String friendId);

	/**
	 * 删除好友.配置是否通知好友.<br/>
	 * 抛运行期异常WEB_REMOVE_BUDDY_EX(10725)异常.
	 * 
	 * @param userId
	 *            操作用户id. 必填.
	 * @param friendId
	 *            好友用户id. 必填.
	 * @param notify
	 *            通知好友. 必填. 0:加好友时不通知好友; 非0:加好友时通知好友.
	 * @return getData() 中key 包含userid、nickname、username
	 */
	public ResultEntity removeBuddy(String userId, String friendId, int notify);

	/**
	 * 创建群.
	 * 
	 * @param userId
	 *            创建者用户id. 必填.
	 * @param groupName
	 *            组群名称. 必填.
	 * @param userIdList
	 *            拉进群的用户id. 选填.
	 * @return GroupInfo群对象. 否则抛WEB_CREATE_GRP（10715）异常.
	 */
	public GroupInfo createGroup(String userId, String groupName, List<String> userIdList);

	/**
	 * 获取群列表.
	 * 
	 * @param userId
	 *            创建者用户id. 必填.
	 * @return 返回属于该用户的群列表. 否则抛WEB_GRPS（10716）异常.
	 */
	public List<GroupInfo> getGroups(String userId);

	/**
	 * 添加群成员.
	 * 
	 * @param groupId
	 *            群Id，在系统里群Id唯一. 必填.
	 * @param userList
	 *            进群的用户id列表. 必填.
	 * @return 成功返回添加的用户信息列表, 否则抛WEB_ADD_GRP_MEMBER（10718）异常.
	 */
	public List<UserInfo> addGroupMembers(String groupId, List<String> userList);

	/**
	 * 删除群成员.
	 * 
	 * @param userId
	 *            建群用户，创建此群的用户ID. 必填.
	 * @param groupId
	 *            群Id，在系统里群名称唯一. 必填.
	 * @param userList
	 *            该群中要删除的用户id列表. 必填.
	 * @return 成功返回被删除的用户信息列表, 否则WEB_REMOVE_GRP_MEMBER（10719）抛异常.
	 */
	public List<UserInfo> removeGroupMembers(String userId, String groupId, List<String> userList);

	/**
	 * 获取群成员列表.
	 * 
	 * @param groupId
	 *            群Id. 必填.
	 * @param userId
	 *            当前调用者用户id. 必填.
	 * @return 属于该群的所有用户UserInfo对象集合. 否则WEB_GRP_MEMBERS（10720）抛异常.
	 */
	public List<UserInfo> getGroupMembers(String groupId, String userId);

	/**
	 * 用户主动退出群.<br/>
	 * 执行失败抛 WEB_GRP_QUIT（10723）异常.
	 * 
	 * @param groupId
	 *            群Id.
	 * @param userId
	 *            主动退出群的用户Id.
	 * 
	 */
	public void exitGroup(String groupId, String userId);

	/**
	 * 注销群.
	 * 
	 * @param groupId
	 *            群Id. 必填.
	 * @param userId
	 *            创建群的用户id. 必填.
	 * @return 成功被注销的群对象信息. 否则抛WEB_DISMISS_GRP（10717）异常.
	 */
	public GroupInfo destroyGroup(String groupId, String userId);

	/**
	 * 给指定用户发布主题及内容.<br/>
	 * PublishData 设置需要发布的主题、内容、用户集合及订阅的主题。其中:<br/>
	 * subject：发布的消息主题<br/>
	 * message：发布的消息内容. role：本次接收消息的用户集合，不传则是推给订阅主题下的所有用户,用逗号分隔
	 * 可以是多个用户登录名。登录名为userid. topic：订阅的主题(为空，则使用AppKey作为默认主题).
	 * 
	 * @param publishs
	 *            发布消息List. 必填.
	 * @return 成功返回状态码(0), 否则抛WEB_PUBLISH（10722）异常.
	 */
	public String publishToUsers(List<PublishData> publishs);
}