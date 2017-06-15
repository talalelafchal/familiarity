package com.objectoriented.protal.controller;

import javax.servlet.http.HttpSession;


import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LogoutController {

	@RequestMapping("/logout")
	public String  logout(){
		Subject sub =SecurityUtils.getSubject();
		sub.getSession().stop();
		return "home";
	}
	@RequestMapping("/notfound")
	public String  notfound(){
		
		return "notfound";
	}
	
}
