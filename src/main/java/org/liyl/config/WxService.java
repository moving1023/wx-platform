package org.liyl.config;

import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Author 李毅霖.
 * @Date 2018/6/6.
 * @description:.
 * @modified by
 */
@Component
public class WxService extends WxMpServiceImpl implements WxMpService{

	@Value("${wx.mp.app-id}")
	private String appId;

	@Value("${wx.mp.secret}")
	private String secret;

	@Value("${wx.mp.token}")
	private String token;

	@Value("${wx.mp.aes-key}")
	private String aesKey;


	public WxMpInMemoryConfigStorage getWxConfig() {
		WxMpInMemoryConfigStorage config;
		config = new WxMpInMemoryConfigStorage();
		config.setAppId(appId); // 设置微信公众号的appid
		config.setSecret(secret); // 设置微信公众号的app corpSecret
		config.setToken(token); // 设置微信公众号的token
		config.setAesKey(aesKey); // 设置微信公众号的EncodingAESKey
		return config;
	}


	public WxService() {
	}

	@PostConstruct
	public void init() {
		super.setWxMpConfigStorage(getWxConfig());
	}

}
