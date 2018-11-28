package com.ymkj.credit.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ymkj.credit.common.constants.Constants;
import com.ymkj.credit.common.entity.BusinessDepart;
import com.ymkj.credit.mapper.BusinessDepartMapper;

/**
 * @Description：对类进行描述
 * @ClassName: BusinessDepartService.java
 * @Author：tianx
 * @Date：2017年8月24日
 * -----------------变更历史-----------------
 * 如：who  2017年8月24日  修改xx功能
 */
@Service
public class BusinessDepartService{
	
	@Autowired
	private BusinessDepartMapper businessDepartMapper;
	
	public void save(BusinessDepart record){
		record.setCreateTime(new Date());
		record.setUpdateTime(new Date());
		record.setStatus(Constants.DATA_VALID);
		businessDepartMapper.insertReturnId(record);
	}
}