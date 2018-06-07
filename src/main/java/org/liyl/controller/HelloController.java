package org.liyl.controller;

import com.google.common.collect.Lists;
import me.chanjar.weixin.common.bean.menu.WxMenu;
import me.chanjar.weixin.common.bean.menu.WxMenuButton;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import me.chanjar.weixin.mp.bean.menu.WxMpMenu;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import me.chanjar.weixin.mp.bean.result.WxMpUserList;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;

/**
 * @Author 李毅霖.
 * @Date 2018/6/6.
 * @description:.
 * @modified by
 */
@RestController
@RequestMapping("/")
public class HelloController {

	private Logger logger = LoggerFactory.getLogger(HelloController.class);

	@Autowired
	private WxMpService wxMpService;

	@Autowired
	private WxMpMessageRouter router;

	@RequestMapping("/say")
	public String say() {
		System.out.println("hello");
		return "hello world";
	}

	// 参考修改https://github.com/Wechat-Group/weixin-java-tools/wiki/MP_%E6%B6%88%E6%81%AF%E7%9A%84%E5%8A%A0%E8%A7%A3%E5%AF%86
	@RequestMapping("/check")
	public void check(HttpServletRequest request, HttpServletResponse response, String signature, String timestamp, String nonce, String echostr) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		if (!wxMpService.checkSignature(timestamp, nonce, signature)) {
			// 消息不合法
			System.out.println("消息不合法");
			response.getWriter().println("非法请求");
			return;
		}
		if(echostr != null) {
			response.getWriter().println(echostr);
			return;
		}
		System.out.println(signature);
		System.out.println(timestamp);
		System.out.println(nonce);
		System.out.println(echostr);
		WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(request.getInputStream());
		String encryptType = "raw";
//		String encryptType = StringUtils.isBlank(request.getParameter("encrypt_type")) ?
//				"raw" :
//				request.getParameter("encrypt_type");
		if ("raw".equals(encryptType)) {
			// 明文传输的消息
			inMessage = WxMpXmlMessage.fromXml(request.getInputStream());
		} else if ("aes".equals(encryptType)) {
			// 是aes加密的消息
			String msgSignature = request.getParameter("msg_signature");
//			inMessage = WxMpXmlMessage.fromEncryptedXml(request.getInputStream(), wxMpConfigStorage, timestamp, nonce, msgSignature);
		} else {
			response.getWriter().println("不可识别的加密类型");
			return;
		}

		String messageType=inMessage.getMsgType();   //消息类型
		String fromUser=inMessage.getFromUser();
		String touser=inMessage.getToUser();
		String content=inMessage.getContent();
		String event = inMessage.getEvent();
		System.out.println("消息类型：" + messageType);
		System.out.println("from用户：" + fromUser);
		System.out.println("to用户：" + touser);
		System.out.println("内容：" + content);
		System.out.println("事件：" + event);
		WxMpXmlOutMessage outMessage = router.route(inMessage);
		if (outMessage != null) {
			// 说明是同步回复的消息
			// 将xml写入HttpServletResponse
			response.getWriter().write(outMessage.toXml());
		} else {
			// 说明是异步回复的消息，直接将空字符串写入HttpServletResponse
			System.out.println("这是异步回答消息");
		}
		return ;
	}

	@RequestMapping("/user-list")
	public WxMpUserList getUserList() throws WxErrorException {
		return wxMpService.getUserService().userList(null);
	}

	@RequestMapping("/send-message")
	public String sendMessage() throws WxErrorException {
		WxMpKefuMessage message = WxMpKefuMessage
				.TEXT()
				.toUser("oA1540_WGvGhYcukAAT8FSOAznTc")
				.content("这是一条主动推送的消息")
				.build();
		wxMpService.getKefuService().sendKefuMessage(message);
		return "SUCCESS";
	}

	@RequestMapping("/user-info/{openId}")
	public WxMpUser getUserinfo(@PathVariable String openId) throws WxErrorException {
		String lang = "zh_CN"; //语言
		WxMpUser user = wxMpService.getUserService().userInfo(openId,lang);
		return user;
	}
	@RequestMapping("/user-tag/{openId}")
	public List<Long> getUserTag(@PathVariable String openId) throws WxErrorException {
		return wxMpService.getUserTagService().userTagList(openId);
	}

	@RequestMapping("/menu/create")
	public void createMenu() throws WxErrorException {
		wxMpService.getMenuService().menuDelete();
		WxMenu wxMenu = new WxMenu();
//		wxMenu.setButtons();
		WxMenuButton wxMenuButton = new WxMenuButton();
		wxMenuButton.setName("百度");
		wxMenuButton.setType("click");
		wxMenuButton.setKey("V1001_BD");

		WxMenuButton wxMenuButton1 = new WxMenuButton();
		wxMenuButton1.setUrl("https://spring.io/");
		wxMenuButton1.setName("Spring官网");
		wxMenuButton1.setType("view");

		WxMenuButton btnGroup = new WxMenuButton();
		btnGroup.setName("按钮组");
		btnGroup.setSubButtons(Lists.newArrayList(wxMenuButton, wxMenuButton1));

		wxMenu.setButtons(Lists.newArrayList(btnGroup, wxMenuButton, wxMenuButton1));
		wxMpService.getMenuService().menuCreate(wxMenu);
	}

	@RequestMapping("/menu/get")
	public WxMpMenu getMenu() throws WxErrorException {
		return wxMpService.getMenuService().menuGet();
	}
	@RequestMapping("/qrcode")
	public File getQrcode() throws WxErrorException {
		WxMpQrCodeTicket ticket = null;//wxMpService.getQrcodeService().qrCodeCreateTmpTicket(scene, expire_seconds);
		File file = wxMpService.getQrcodeService().qrCodePicture(ticket);
		return file;
	}

	@RequestMapping("/short-url")
	public String shortUrl() throws WxErrorException {
		return wxMpService.shortUrl("www.baidu.com");
	}

	@RequestMapping("/send-template-message/{templateId}")
	public String sendTemplateMessage(@PathVariable String templateId) throws WxErrorException {
		WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
				.toUser("oA1540_WGvGhYcukAAT8FSOAznTc")
		.templateId(templateId)
		.url("http://www.baidu.com")
		.build();
		templateMessage.addData(new WxMpTemplateData("product", "烧仙草奶茶", ""));
		templateMessage.addData(new WxMpTemplateData("amt", "19.87", ""));
		templateMessage.addData(new WxMpTemplateData("date", "2018-06-06", ""));
		return wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
	}
}
