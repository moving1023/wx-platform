package org.liyl.api;

import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import me.chanjar.weixin.mp.bean.result.WxMpUserList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author 李毅霖.
 * @Date 2018/6/7 微信公众号的接口调用.
 * @description:.
 * @modified by
 */
@Component
public class WechatMpApi {

	@Autowired
	private WxMpService wxMpService;

	//获取关注用户
	public WxMpUserList getWechatUser() throws WxErrorException {
		return wxMpService.getUserService().userList(null);
	}

	//主动发送客服消息
	public boolean sendKefuMessage(String openId, String message) throws WxErrorException {
		//"oA1540_WGvGhYcukAAT8FSOAznTc"
		WxMpKefuMessage kefuMessage = WxMpKefuMessage
				.TEXT()
				.toUser(openId)
				.content(message)
				.build();
		return wxMpService.getKefuService().sendKefuMessage(kefuMessage);
	}

	//获取用户信息
	public WxMpUser getUserInfo(String openId) throws WxErrorException {
		return wxMpService.getUserService().userInfo(openId,"zh_CN");
	}

	//获取用户标签
	public List<Long> getUserTag(String openId) throws WxErrorException {
		return wxMpService.getUserTagService().userTagList(openId);
	}
}
