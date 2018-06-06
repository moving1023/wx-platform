package org.liyl.config;

import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import org.liyl.handler.TextMessageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author 李毅霖.
 * @Date 2018/6/6.
 * @description:.
 * @modified by
 */
@Configuration
public class Config {

	@Bean
	public WxMpService wxMpService(){
		return new WxService();
	}

	@Bean
	public TextMessageHandler textMessageHandler() {
		return new TextMessageHandler();
	}

	@Bean
	public WxMpMessageRouter router() {
		WxMpMessageRouter router = new WxMpMessageRouter(wxMpService());
		router
				// 4个条件必须全部匹配的路由规则
				.rule()
				.async(false)
				.msgType(WxConsts.XmlMsgType.TEXT)
//				.event(WxConsts.EventType.)
//				.eventKey("EVENT_KEY")
//				.content("CONTENT")
				//.rContent("content正则表达式")
				.handler(textMessageHandler())
				.end()
				// 只匹配1个条件的路由规则
				.rule()
				.msgType("MSG_TYPE")
				.handler(textMessageHandler())
				.end()
				// 消息经过这个规则后可以继续尝试后面的路由规则
				.rule()
				.msgType("MSG_TYPE")
				.handler(textMessageHandler())
				.next()
				// 另一个同步处理的路由规则
				.rule()
				.async(false)
				.msgType("MSG_TYPE")
				.handler(textMessageHandler())
				.end()
				// 添加了拦截器的路由规则
				.rule()
				.msgType("MSG_TYPE")
//				.interceptor(interceptor)
				.handler(textMessageHandler())
				.end()
				// 兜底路由规则，一般放到最后
				.rule()
				.async(false)
				.handler(textMessageHandler())
				.end();
		return router;
	}

}
