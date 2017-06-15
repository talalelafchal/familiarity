package com.objectoriented.protal.pojo;

import org.apache.shiro.authc.AuthenticationException;


import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import com.objectoriented.manager.pojo.UserBase;
import com.objectoriented.protal.service.UserService;




public class AuthRealm extends AuthorizingRealm {

	@Autowired
	private UserService userBaseService;
	
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		UsernamePasswordToken loginToken = (UsernamePasswordToken) token;
		String username = loginToken.getUsername();
		
		//根据用户名查询用户信息
		UserBase userBase = userBaseService.findOne(username);
		/**
		 * 参数1:principal    真实的用户对象
		 * 参数2:credentials  用户的真实的密码
		 * 参数3:realmName    当前realm的名称
		 */
		//返还给Shiro安全管理器 
		AuthenticationInfo info = 
			new SimpleAuthenticationInfo(userBase, userBase.getPassword(), this.getName());
		return info;
	}

}
