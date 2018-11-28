package com.ymkj.credit.mapper;


import java.util.List;
import java.util.Map;

import com.github.pagehelper.Page;
import com.ymkj.credit.common.entity.MessagePushLog;
import com.ymkj.credit.common.entity.ValidateCode;
import com.ymkj.credit.web.api.dto.MessageDto;
import com.ymkj.springside.modules.orm.mybatis.JdMapper;

/**
* MessagePushLogMapper
* <p/>
* Author: 
* Date: 2017-09-11 14:34:27
* Mail: 
*/
public interface MessagePushLogMapper extends JdMapper<MessagePushLog, Long> {
	List<MessageDto> queryByIdCard(Map map);
	
	Page<MessagePushLog> selectMessagePushLogListByConditions(MessagePushLog messagePushLog);
	public int insertTable(MessagePushLog messagePushLog);
}