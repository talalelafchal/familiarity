package com.xxx.sample.spring.cloud.consumer.ribbon.user.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.xxx.sample.spring.cloud.model.cas.user.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Orrin on 2017/2/3.
 */
@Service
public class UserConsumerRibbonService {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserConsumerRibbonService.class);

	@Autowired
	private RestTemplate restTemplate;

	/**
	 * 使用@HystrixCommand注解指定当该方法发生异常时调用的方法
	 * @param id id
	 * @return 通过id查询到的用户
	 */
	@HystrixCommand(fallbackMethod = "fallback")
	public UserModel findByIdRibbon(Integer id) {
		// http://服务提供者的serviceId/url
		return this.restTemplate.getForObject("http://scs-producer-cas/" + id, UserModel.class);
	}

	/**
	 * hystrix fallback方法
	 * @param id id
	 * @return 默认的用户
	 */
	public UserModel fallback(Integer id) {
		LOGGER.info("异常发生，进入fallback方法，接收的参数：id = {}", id);
		UserModel user = new UserModel();
		user.setId(-1);
		user.setUserName("default username");
		return user;
	}
}
