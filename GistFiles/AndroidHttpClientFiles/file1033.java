package com.xxx.sample.spring.cloud.api.cas.user;

import com.xxx.sample.spring.cloud.model.cas.user.UserModel;

import java.util.List;

/**
 * Created by Orrin on 2017/1/20.
 */
public interface UserService {
	List<UserModel> findUsers(UserModel userModel);
}
