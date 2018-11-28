package com.ymkj.credit.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ymkj.credit.common.entity.LoginLog;
import com.ymkj.credit.mapper.LoginLogInfoMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LoginLogService {

	@Autowired
	private LoginLogInfoMapper loginMapper;
	
	public void insert(LoginLog log){
		
		loginMapper.insert(log);
	}
	
	public LoginLog selectCustomer(LoginLog log){
		 return loginMapper.selectCustomer(log);
	}
	
	public void update(LoginLog log){
		loginMapper.updateByPrimaryKey(log);
	}
}
