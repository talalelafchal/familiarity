package com.objectoriented.protal.controller;




import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.objectoriented.manager.pojo.Contacts;
import com.objectoriented.manager.pojo.SucceedCase;
import com.objectoriented.manager.pojo.UserBase;
import com.objectoriented.protal.service.ContactsService;
import com.objectoriented.protal.service.SucceedCaseService;
import com.objectoriented.protal.service.UserService;

@Controller
public class LoginController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private SucceedCaseService succeedCaseService;
	
	@Autowired
	private ContactsService contactsService;
	
	@RequestMapping("/tologin")
	public String toLogin(){
		
		
		return "login";
	}
	
	@RequestMapping("/login")
	public String login(String username,String password,Model model){
		
		UsernamePasswordToken token = new UsernamePasswordToken(username, password);
		
		Subject subject = SecurityUtils.getSubject();
		
		try {	
			subject.login(token);
			UserBase userBase = (UserBase) subject.getPrincipal();
			subject.getSession().setAttribute("userBase", userBase);
			List<UserBase> showList =userService.findShowUser(userBase.getUsername(),userBase.getSex());
		
			List<UserBase> laterList=userService.findLaterUser(userBase.getUsername(),userBase.getSex());
			List<SucceedCase> caseList = succeedCaseService.findAll();
			model.addAttribute("laterList", laterList);
			model.addAttribute("showList", showList);
			model.addAttribute("caseList", caseList);
			
			return "home";
		} catch (AuthenticationException e) {
			e.printStackTrace();
		}
		
		return "redirect:tologin";
	}
	
	@RequestMapping("/tohome.action")
	public String home(Model model){
		
		List<UserBase> showList =userService.findShowUser("优雅的叶子","女");
	
		List<UserBase> laterList=userService.findLaterUser("优雅的叶子","女");
		
		List<SucceedCase> caseList = succeedCaseService.findAll();
		
		List<Contacts> contactsList = contactsService.findContacts();
		
		model.addAttribute("laterList", laterList);
		model.addAttribute("showList", showList);
		model.addAttribute("caseList", caseList);
		model.addAttribute("contactsList", contactsList);
		return "home";
	}
	
	@RequestMapping("/test")
	public String lt(){
		
		return "_head";
	}
	
	
}
