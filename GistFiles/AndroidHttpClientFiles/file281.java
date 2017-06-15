package com.objectoriented.protal.controller;

import java.util.List;


import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.objectoriented.manager.pojo.UserBase;
import com.objectoriented.protal.service.UserService;


@Controller
public class MatchesController {

	@Resource
	private UserService userService;
	
	@RequestMapping("/tomatches")
	public String tomatches(String sex,Integer age1,Integer age2,Model model){
		List<UserBase> userBase = userService.findUserByRequest(sex,age1,age2);
		model.addAttribute("userBaseList", userBase);
		return "matches";
	}
}
