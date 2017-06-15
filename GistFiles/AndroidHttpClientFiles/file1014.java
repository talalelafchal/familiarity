package com.objectoriented.protal.pojo;

import org.apache.shiro.crypto.hash.Md5Hash;

public class MD5Password {
	
	public static String getMd5Password(String password,String username){
		Md5Hash md5Hash = new Md5Hash(password, username, 3);
		
		return md5Hash.toString();
	}
	
}
