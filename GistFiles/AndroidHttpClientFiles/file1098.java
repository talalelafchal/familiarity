package com.objectoriented.protal.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.objectoriented.manager.mapper.ContactsMapper;
import com.objectoriented.manager.pojo.Contacts;
import com.objectoriented.manager.pojo.ContactsExample;
import com.objectoriented.manager.pojo.ContactsExample.Criteria;
import com.objectoriented.protal.service.ContactsService;

@Service
public class ContactsServiceImpl implements ContactsService {

	@Autowired
	private ContactsMapper contactsMapper;
	
	@Override
	public List<Contacts> findContacts() {
		ContactsExample example = new ContactsExample();
		Criteria criteria = example.createCriteria();
		criteria.andUserIdIsNotNull();
	    return contactsMapper.selectByExample(example).subList(0, 2);
	}

}
