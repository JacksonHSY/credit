package com.ymkj.credit.mapper;

import com.ymkj.credit.common.entity.BankChannel;
import com.ymkj.springside.modules.orm.mybatis.JdMapper;

/**
* CustomerMapper
* <p/>
* Author: 
* Date: 2017-08-21 14:33:20
* Mail: 
*/
public interface BankChannelMapper extends JdMapper<BankChannel, Long> {
	
	public int insertTable(BankChannel bankChannel);

	public BankChannel selectOneTable(BankChannel bankChannel);
}