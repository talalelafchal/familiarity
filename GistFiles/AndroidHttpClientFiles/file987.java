package com.objectoriented.protal.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.objectoriented.manager.mapper.BoardMapper;
import com.objectoriented.manager.pojo.Board;
import com.objectoriented.manager.pojo.UserBase;
import com.objectoriented.protal.service.BoardService;
@Service
public class BoardServiceImpl implements BoardService {

	@Resource
	private BoardMapper boardMapper; 
	
	

	@Override
	public void addBoard(String message, String title, Integer anonymity, Integer better, UserBase userBase) {
		Board  board = new Board();
		board.setBoardTime(new Date());
		board.setMessage(message);
		board.setTitle(title);
		board.setAnonymity(anonymity);
		board.setBetter(better);
		
		boardMapper.addBoard(board,userBase);
	}

}
