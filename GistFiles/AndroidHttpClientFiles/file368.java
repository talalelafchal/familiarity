package com.objectoriented.protal.service.impl;

import java.util.Calendar;
import java.util.Date;



import java.util.GregorianCalendar;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.objectoriented.manager.mapper.UserBaseMapper;
import com.objectoriented.manager.mapper.UserInfoMapper;

import com.objectoriented.manager.pojo.UserBase;
import com.objectoriented.manager.pojo.UserBaseExample;
import com.objectoriented.manager.pojo.UserBaseExample.Criteria;
import com.objectoriented.manager.pojo.UserInfo;
import com.objectoriented.protal.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserBaseMapper userBaseMapper;
	
	@Resource
	private UserInfoMapper userInfoMapper;
	
	@Override
	public UserBase findOne(String username) {
		UserBaseExample example = new UserBaseExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		List<UserBase> list = userBaseMapper.selectByExample(example);
		
		return list.get(0);
	}



	

	@Override
	public void updateUser(UserBase userbase) {
		Date updateTime = new Date();
		userbase.setUpdateTime(updateTime);
		UserBaseExample example = new UserBaseExample();
		userBaseMapper.updateByExample(userbase, example);
		
	}

	@Override
	public void saveUser(UserBase userbase,UserInfo userInfo) {
		
		String uuid = UUID.randomUUID().toString();
		
		userbase.setUserId(uuid);
		
		userbase.setState(1);
		
		userInfo.setUserInfoId(uuid);
		Date createTime = new Date();
		userbase.setCreateTime(createTime);
		userBaseMapper.insert(userbase);
		userInfoMapper.insert(userInfo);
	}
	
	@Override
	public void saveUserBase(UserBase userbase) {
		
		String uuid = UUID.randomUUID().toString();
		
		userbase.setUserId(uuid);
		
		userbase.setState(1);
		
		Date createTime = new Date();
		userbase.setCreateTime(createTime);
		
		Date updateTime = new Date();
		userbase.setUpdateTime(updateTime);
		
		userBaseMapper.insert(userbase);
		
	}

	@Override
	public UserBase findOneUser(String userId) {
		
		return null;
	}

	@Override
	public List<UserBase> findUserByRequest(String sex,Integer age1,Integer age2 ) {
		
		UserBaseExample example = new UserBaseExample();
		Criteria criteria = example.createCriteria();
		criteria.andSexEqualTo(sex);
		criteria.andAgeBetween(age1, age2);
		List<UserBase> userlist = userBaseMapper.selectByExample(example);
		for (UserBase userBase : userlist) {
			UserInfo userInfo = userInfoMapper.selectByPrimaryKey(userBase.getUserId());
			userBase.setUserInfo(userInfo);
		}
		return userlist;
	}

	/**
	 * 主页轮播显示对象 不带本人信息
	 * 不带自己  与自己性别相反
	 * 10条随机
	 */
	@Override
	public List<UserBase> findShowUser(String username,String sex) {

		UserBaseExample example = new UserBaseExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameNotEqualTo(username);
		criteria.andSexNotEqualTo(sex);
		List<UserBase> list = userBaseMapper.selectByExample(example);
		return list;
	}


	/**
	 * 显示最近添加的会员
	 * 不带自己
	 * 6条
	 */

	@Override
	public List<UserBase> findLaterUser(String username,String sex) {
		 Date date=new Date();//取时间 
	     Calendar   calendar  =  new GregorianCalendar(); 
	     calendar.setTime(date); 
	     calendar.add(calendar.DATE,-30);//把日期往后增加一天.整数往后推,负数往前移动 
	     date=calendar.getTime(); 
		
		UserBaseExample example = new UserBaseExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameNotEqualTo(username);
		criteria.andSexNotEqualTo(sex);
		criteria.andCreateTimeGreaterThanOrEqualTo(date);
		List<UserBase> laterList =  userBaseMapper.selectByExample(example);
		return laterList.subList(0, 3);
	}





	@Override
	public UserBase userBaseInfo(String userId) {
		UserBase userBase = userBaseMapper.selectByPrimaryKey(userId);
		UserInfo userInfo = userInfoMapper.selectByPrimaryKey(userId);
		userBase.setUserInfo(userInfo);
		return userBase;
	}





	@Override
	public List<UserBase> findAll() {
		UserBaseExample example = new UserBaseExample();
		Criteria criteria = example.createCriteria();
		criteria.andSexEqualTo("女");
		List<UserBase> laList =  userBaseMapper.selectByExample(example);
		return laList;
	}

}
