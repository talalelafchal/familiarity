package com.xxx.sample.spring.cloud.service.cas.user.service.impl;

import com.xxx.sample.spring.cloud.api.cas.user.UserService;
import com.xxx.sample.spring.cloud.model.cas.user.UserModel;
import com.xxx.sample.spring.cloud.service.cas.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Orrin on 2017/1/20.
 */
@Service("userService")
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper userMapper;

	@Override
	public List<UserModel> findUsers(UserModel userModel) {
		return userMapper.findUsers(userModel);
	}
}
