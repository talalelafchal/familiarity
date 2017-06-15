package com.objectoriented.protal.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.objectoriented.manager.pojo.UserBase;
import com.objectoriented.protal.service.UserService;

@Controller
public class RegisterController {

	@Autowired
	private UserService userService;
	
	@RequestMapping("/register")
	public String register(UserBase userBase,HttpSession session) {
		userService.saveUserBase(userBase);
		session.setAttribute("userBase", userBase);
		return "redirect:/tologin.action";
	}
}
