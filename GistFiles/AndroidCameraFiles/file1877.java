package com.objectoriented.protal.service.impl;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.objectoriented.manager.mapper.SucceedCaseMapper;
import com.objectoriented.manager.pojo.SucceedCase;

import com.objectoriented.protal.service.SucceedCaseService;
@Service
public class SucceedCaseServiceImpl implements SucceedCaseService {

	@Autowired
	private SucceedCaseMapper succeedCaseMapper;
	@Override
	public List<SucceedCase> findAll() {
		
		List<SucceedCase>  succList = succeedCaseMapper.findSucceedCase();
		return succList;
		
	}

}
