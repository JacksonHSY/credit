package com.ymkj.credit.mapper;

import java.util.Map;

import com.github.pagehelper.Page;
import com.ymkj.credit.common.entity.BankInfo;
import com.ymkj.credit.common.entity.LoanOrder;
import com.ymkj.credit.web.api.dto.BankInfoDto;
import com.ymkj.credit.web.api.dto.BankListInfoDto;
import com.ymkj.springside.modules.orm.mybatis.JdMapper;


/**
* BankInfoMapper
* <p/>
* Author: 
* Date: 2018-02-28 10:23:46
* Mail: 
*/
public interface BankInfoMapper extends JdMapper<BankInfo, String> {
	public Page<BankListInfoDto> queryBankInfo(Map<String, Object> map);

	public BankInfoDto queryInfoById(Map<String, String> map);

	public BankInfo querybySerialNumber(Map<String, String> map);

	public BankInfo selectByAccNo(Map<String, String> map);
	public int insertTable(BankInfo bankInfo);
	public int updateByPrimaryKeyTable(BankInfo bankInfo);
	public BankInfo selectOneTable(BankInfo bankInfo);
}