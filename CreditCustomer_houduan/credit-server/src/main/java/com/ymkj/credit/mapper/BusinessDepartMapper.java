package com.ymkj.credit.mapper;

import com.ymkj.credit.common.entity.BusinessDepart;
import com.ymkj.springside.modules.orm.mybatis.JdMapper;

/**
* BusinessDepartMapper
* <p/>
* Author: 
* Date: 2017-08-24 08:41:13
* Mail: 
*/
public interface BusinessDepartMapper extends JdMapper<BusinessDepart, Long> {

  int insertReturnId(BusinessDepart record);
}