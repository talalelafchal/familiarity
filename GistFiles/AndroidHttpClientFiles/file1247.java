package com.objectoriented.protal.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.objectoriented.manager.pojo.UserBase;
import com.objectoriented.protal.service.UserInfoService;
import com.objectoriented.protal.service.UserService;

@Controller
public class UserEditController {

	@Resource
	private UserService userService;
	
	
	@RequestMapping("/toedit")
	private String toedit(Model model,HttpSession session){
		UserBase userBase = userService.userBaseInfo(((UserBase) session.getAttribute("userBase")).getUserId());
		model.addAttribute("info", userBase);
		return "/edit_profile";
	}
	
	@RequestMapping("/update")
	private String saveEdit(UserBase userbase){
		
		userService.updateUser(userbase);
		return "/list_profile";
	}
	
	
}
