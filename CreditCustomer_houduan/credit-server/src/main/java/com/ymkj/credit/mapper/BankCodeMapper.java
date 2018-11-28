package com.ymkj.credit.mapper;

import com.ymkj.credit.common.entity.BankCode;
import com.ymkj.springside.modules.orm.mybatis.JdMapper;

public interface BankCodeMapper extends JdMapper<BankCode, Long>{

	public BankCode queryByBankColum(BankCode bankCode);
}