package com.xxx.sample.spring.cloud.service.cas.user.controller;

import com.xxx.sample.spring.cloud.api.cas.user.UserService;
import com.xxx.sample.spring.cloud.model.cas.user.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by Orrin on 2017/2/3.
 */
@RestController
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private DiscoveryClient discoveryClient;

	/**
	 * 注：@GetMapping("/{id}")是spring 4.3的新注解等价于：
	 * @RequestMapping(value = "/id", method = RequestMethod.GET)
	 * 类似的注解还有@PostMapping等等
	 * @param id
	 * @return user信息
	 */
	@GetMapping("/{id}")
	public UserModel findById(@PathVariable Integer id) {
		UserModel userModel = new UserModel();
		userModel.setId(id);
		List<UserModel> userModels = userService.findUsers(userModel);

		if(userModels!=null && userModels.size() > 0){
			return userModels.get(0);
		}else {
			return new UserModel();
		}
	}

	/**
	 * 本地服务实例的信息
	 * @return
	 */
	@GetMapping("/instance-info")
	public ServiceInstance showInfo() {
		ServiceInstance localServiceInstance = this.discoveryClient.getLocalServiceInstance();
		return localServiceInstance;
	}
}
