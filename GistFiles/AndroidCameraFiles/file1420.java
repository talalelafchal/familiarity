package com.objectoriented.protal.controller;


import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.objectoriented.manager.pojo.UserBase;
import com.objectoriented.manager.pojo.UserInfo;
import com.objectoriented.protal.service.UserInfoService;
import com.objectoriented.protal.service.UserService;


@Controller
public class ViewCreateController {

	@Resource
	private UserService userService;
	
	
	@Resource 
	private UserInfoService userInfoService;
	
	
	@RequestMapping("/tocreate")
	public String toCreate(String userId,Model model){
		UserInfo info = userInfoService.findUserInfo(userId);
		model.addAttribute("info", info);
		return "/view_profile";
	}
	
	@RequestMapping("/save")
	public String tosave(UserInfo userInfo,UserBase userbase,String userId,Model model){
		UserInfo info = userInfoService.findUserInfo(userId);
		model.addAttribute("info", info);
		model.addAttribute("userbase", userbase);
		userService.saveUser(userbase,userInfo);
		
		return "/list_profile";
	}
	
	
	
	@RequestMapping("/listprofile.action")
	public String listprofile(Model model,HttpSession session){
		UserBase userBase = userService.userBaseInfo(((UserBase) session.getAttribute("userBase")).getUserId());
		model.addAttribute("info", userBase);
		return "/list_profile";
	}
}
