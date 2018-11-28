package com.ymkj.credit.mapper;

import com.ymkj.credit.common.entity.ValidateCode;
import com.ymkj.springside.modules.orm.mybatis.JdMapper;

/**
* ValidateCodeMapper
* <p/>
* Author: 
* Date: 2017-08-21 14:33:38
* Mail: 
*/
public interface ValidateCodeMapper extends JdMapper<ValidateCode, Long> {
	
	public int insertTable(ValidateCode validateCode);
	public int updateByPrimaryKeyTable(ValidateCode validateCode);
	public ValidateCode selectOneTable(ValidateCode validateCode);

}