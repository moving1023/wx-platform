package org.liyl.controller;

import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUserList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author 李毅霖.
 * @Date 2018/6/6.
 * @description:.
 * @modified by
 */
@RestController
@RequestMapping("/")
public class HelloController {

	@Autowired
	private WxMpService wxMpService;

	@Autowired
	private WxMpMessageRouter router;

	@RequestMapping("/say")
	public String say() {
		System.out.println("hello");
		return "hello world";
	}

	@RequestMapping("/check")
	public String check(HttpServletRequest request, HttpServletResponse response, String signature, String timestamp, String nonce, String echostr) throws Exception {
		System.out.println("执行到位");
		System.out.println(signature);
		System.out.println(timestamp);
		System.out.println(nonce);
		System.out.println(echostr);
		WxMpXmlMessage message = WxMpXmlMessage.fromXml(request.getInputStream());
		String messageType=message.getMsgType();   //消息类型
		String fromUser=message.getFromUser();
		String touser=message.getToUser();
		String content=message.getContent();
		String event = message.getEvent();
		System.out.println("消息类型：" + messageType);
		System.out.println("from用户：" + fromUser);
		System.out.println("to用户：" + touser);
		System.out.println("内容：" + content);
		System.out.println("事件：" + event);
		WxMpXmlOutMessage outMessage = router.route(message);
		if (outMessage != null) {
			// 说明是同步回复的消息
			// 将xml写入HttpServletResponse
			response.getWriter().write(outMessage.toXml());
		} else {
			// 说明是异步回复的消息，直接将空字符串写入HttpServletResponse
			System.out.println("这是异步回答消息");
		}
		return echostr;
	}

	@RequestMapping("/user-list")
	public WxMpUserList getUserList() throws WxErrorException {
		return wxMpService.getUserService().userList(null);
	}
}
