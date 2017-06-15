package com.objectoriented.protal.service;

import com.objectoriented.manager.pojo.Board;

import com.objectoriented.manager.pojo.UserBase;

public interface BoardService {

	

	public void addBoard(String message, String title, Integer anonymity, Integer better, UserBase userBase);

}
