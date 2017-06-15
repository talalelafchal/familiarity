package com.xxx.sample.spring.cloud.consumer.ribbon.user.controller;

import com.xxx.sample.spring.cloud.consumer.ribbon.user.service.UserConsumerRibbonService;
import com.xxx.sample.spring.cloud.model.cas.user.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Orrin on 2017/2/3.
 */
@RestController
public class UserRibbonController {
	@Autowired
	private UserConsumerRibbonService userConsumerService;

	@GetMapping("/ribbon/{id}")
	public UserModel findByIdRibbon(@PathVariable Integer id) {
		return this.userConsumerService.findByIdRibbon(id);
	}
}
