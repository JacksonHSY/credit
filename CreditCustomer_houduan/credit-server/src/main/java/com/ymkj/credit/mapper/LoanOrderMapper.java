package com.ymkj.credit.mapper;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.Page;
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

	public Page<LoanOrder> selectLoanOrders(Map<String, Object> map);
	public void insertLoanOrder(LoanOrder loanOrder);
//	public List<LoanOrder> selectOrderByFlows(Map<String, String> map);
	public String getBatchNumByOrderNum(Map<String, String> map);
	public void updateBankId(Map<String, Object> map);
	public LoanOrder queryByOrderNo(Map<String, String> map);
	public List<LoanOrder> getTotalByCId(Map<String, String> map);
	public int updateLoanOrder(LoanOrder order);
	public int insertTable(LoanOrder loanOrder);
}