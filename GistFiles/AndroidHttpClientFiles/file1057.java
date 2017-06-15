package com.objectoriented.protal.service.impl;

import java.util.List;



import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.objectoriented.manager.mapper.UserInfoMapper;
import com.objectoriented.manager.pojo.UserBaseExample;
import com.objectoriented.manager.pojo.UserInfo;
import com.objectoriented.protal.service.UserInfoService;
import com.objectoriented.manager.pojo.UserBaseExample.Criteria;



@Service
public class UserInfoServiceImpl  implements UserInfoService{

	@Resource
	private UserInfoMapper userInfoMapper;
	@Override
	public UserInfo findUserInfo(String userId) {
		UserBaseExample example = new UserBaseExample();
		Criteria criteria = example.createCriteria();
		UserInfo info = userInfoMapper.selectByPrimaryKey(userId);
		
		return info;
	}

}
