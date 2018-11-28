package com.ymkj.credit.mapper;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.Page;
import com.ymkj.credit.common.entity.ApplyLoanInfo;
import com.ymkj.credit.common.entity.BankInfo;
import com.ymkj.springside.modules.orm.mybatis.JdMapper;

public interface ApplyLoanInfoMapper extends JdMapper<ApplyLoanInfo, Long>{
	
	public ApplyLoanInfo selectByIdCard(ApplyLoanInfo applyloaninfo);
	public ApplyLoanInfo selectByLoanNo(ApplyLoanInfo applyloaninfo);
	public void updateByLoanNo(ApplyLoanInfo applyloaninfo);
	public List<ApplyLoanInfo> selectLoanInfo(Map<String, Object> map);  
	public List<ApplyLoanInfo> queryByApplyLoanInfo(ApplyLoanInfo info);
	public int insertTable(ApplyLoanInfo info);
	public ApplyLoanInfo selectLoanOne(ApplyLoanInfo info);
	public void updateToTable(ApplyLoanInfo info);
	public List<ApplyLoanInfo> selectLoanAll(ApplyLoanInfo info);
	public void updateByLoan(ApplyLoanInfo applyloaninfo);
}