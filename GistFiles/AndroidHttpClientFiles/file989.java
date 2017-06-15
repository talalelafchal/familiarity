package com.objectoriented.protal.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.objectoriented.manager.pojo.Board;
import com.objectoriented.manager.pojo.UserBase;
import com.objectoriented.protal.service.BoardService;

@Controller
public class BoardController {
	
	@Resource
	private BoardService boardService;
	
	
	@RequestMapping("/toboard")
	public String toBoard(){
		return "board";
	}
	
	@RequestMapping("/addboard")
	public String addBoard(String message,String title,Integer anonymity,Integer better,HttpSession session){
		
		UserBase userBase = (UserBase) session.getAttribute("userBase");
		
		boardService.addBoard(message,title,anonymity,better,userBase);		
		
		return "home";
	}
	
	
	
}
