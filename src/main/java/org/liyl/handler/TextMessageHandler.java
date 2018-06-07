package org.liyl.handler;

import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

import java.util.Map;

/**
 * @Author 李毅霖.
 * @Date 2018/6/6.
 * @description:.
 * @modified by
 */
public class TextMessageHandler implements WxMpMessageHandler{
	@Override
	public WxMpXmlOutMessage handle(WxMpXmlMessage wxMpXmlMessage, Map<String, Object> map, WxMpService wxMpService, WxSessionManager wxSessionManager) throws WxErrorException {
		System.out.println("消息处理");
		System.out.println("用户：" + wxMpXmlMessage.getFromUser());
		System.out.println(wxMpXmlMessage);
		return WxMpXmlOutMessage.TEXT()
				.content("欢迎关注此公众号")
				.fromUser(wxMpXmlMessage.getToUser())
				.toUser(wxMpXmlMessage.getFromUser())
				.build();
	}
}
