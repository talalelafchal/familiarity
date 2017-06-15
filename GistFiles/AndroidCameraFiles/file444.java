package com.objectoriented.protal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.objectoriented.protal.service.ContactsService;

@Controller
public class ContactsController {

	@Autowired
	private ContactsService contactsService;
	
	
	
}
