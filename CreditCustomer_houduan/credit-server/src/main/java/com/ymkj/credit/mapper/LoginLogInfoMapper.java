package com.ymkj.credit.mapper;

import com.ymkj.credit.common.entity.LoginLog;
import com.ymkj.springside.modules.orm.mybatis.JdMapper;

public interface LoginLogInfoMapper extends JdMapper<LoginLog, String>{

	public LoginLog selectCustomer(LoginLog log);
}
