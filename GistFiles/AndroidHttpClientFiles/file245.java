package com.xxx.sample.spring.cloud.consumer.feign.user.service;

import com.xxx.sample.spring.cloud.model.cas.user.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 使用@FeignClient("serviceId")注解绑定serviceId服务，还可以使用url参数指定一个URL。
 * @author Orrin
 */
@FeignClient(name = "scs-producer-cas", fallback = UserConsumerFeignServiceClient.UserConsumerFeignServiceClientFallback.class)
public interface UserConsumerFeignServiceClient {
	@RequestMapping("/{id}")
	public UserModel findByIdFeign(@RequestParam("id") Integer id);


	/**
	 * 这边采取了和Spring Cloud官方文档相同的做法，将fallback类作为内部类放入Feign的接口中，当然也可以单独写一个fallback类。
	 * @author eacdy
	 */
	@Component
	static class UserConsumerFeignServiceClientFallback implements UserConsumerFeignServiceClient {
		private static final Logger LOGGER = LoggerFactory.getLogger(UserConsumerFeignServiceClientFallback.class);

		/**
		 * hystrix fallback方法
		 * @param id id
		 * @return 默认的用户
		 */
		@Override
		public UserModel findByIdFeign(Integer id) {
			LOGGER.info("异常发生，进入fallback方法，接收的参数：id = {}", id);
			UserModel user = new UserModel();
			user.setId(-2);
			user.setUserName("default username");
			return user;
		}
	}
}
