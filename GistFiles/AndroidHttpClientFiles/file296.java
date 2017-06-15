package com.objectoriented.protal.service;

import java.util.List;

import com.objectoriented.manager.pojo.UserBase;
import com.objectoriented.manager.pojo.UserInfo;



public interface UserService {

	UserBase findOne(String username);

	List<UserBase> findUserByRequest(String sex,Integer age1,Integer age2);

	void updateUser(UserBase userbase);

	void saveUser(UserBase userbase,UserInfo userInfo);

	UserBase findOneUser(String userId);
	
	void saveUserBase(UserBase userbase);

	List<UserBase> findShowUser(String username,String sex);

	List<UserBase> findLaterUser(String username,String sex);

	UserBase userBaseInfo(String userId);

	List<UserBase> findAll();

}
