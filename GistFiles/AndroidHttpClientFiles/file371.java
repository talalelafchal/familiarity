package com.xxx.sample.spring.cloud.consumer.feign.user.controller;

import com.xxx.sample.spring.cloud.consumer.feign.user.service.UserConsumerFeignServiceClient;
import com.xxx.sample.spring.cloud.model.cas.user.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Orrin on 2017/2/3.
 */
@RestController
public class UserFeignController {
	@Autowired
	private UserConsumerFeignServiceClient userConsumerFeignServiceClient;

	@GetMapping("feign/{id}")
	public UserModel findByIdFeign(@PathVariable Integer id) {
		UserModel user = this.userConsumerFeignServiceClient.findByIdFeign(id);
		return user;
	}
}
