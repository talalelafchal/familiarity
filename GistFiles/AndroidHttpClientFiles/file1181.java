package com.xxx.sample.spring.cloud.service.cas.user.mapper;

import com.xxx.sample.spring.cloud.model.cas.user.UserModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by Orrin on 2017/1/20.
 */
@Mapper
public interface UserMapper {
	List<UserModel> findUsers(UserModel userModel);
}
