package com.ymkj.credit.server.mapper;

import java.util.List;
import java.util.Map;

import com.ymkj.credit.common.dto.LoanOrderDto;
import com.ymkj.credit.common.entity.LoanOrder;
import com.ymkj.springside.modules.orm.mybatis.JdMapper;



/**
* LoanOrderMapper
* <p/>
* Author: 
* Date: 2018-01-04 16:54:06
* Mail: 
*/
public interface LoanOrderMapper extends JdMapper<LoanOrder, Long> {
	
	public List<LoanOrderDto> getPage(Map<String, String> map);
	public int updateLoanOrder(LoanOrder order);
	public String getBatchNumByOrderNum(Map<String, String> map);
	
}