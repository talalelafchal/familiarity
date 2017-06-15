package com.objectoriented.protal.pojo;

import org.apache.shiro.authc.AuthenticationInfo;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;



//自定义的加密
public class AuthCredential extends  SimpleCredentialsMatcher{
	
	@Override
	public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
		
			UsernamePasswordToken token1  = (UsernamePasswordToken) token;
			
			return super.doCredentialsMatch(token1, info);
	}
}
