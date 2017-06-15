package com.objectoriented.protal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.objectoriented.manager.pojo.UserBase;
import com.objectoriented.protal.service.UserService;



@Controller
public class ToIndex {
	
	@Autowired
	private UserService userService;

	@RequestMapping("/toindex")
	public String toIndex(Model model){
		List<UserBase> userBaseListM=userService.findAll();
		model.addAttribute("userBaseListM", userBaseListM);
		return "start";
	}
}
