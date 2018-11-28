package com.ymkj.credit.mapper;

import com.github.pagehelper.Page;
import com.ymkj.credit.common.entity.MessagePushLog;
import com.ymkj.credit.common.entity.OperateLog;
import com.ymkj.credit.common.entity.SmsMessageRecord;
import com.ymkj.springside.modules.orm.mybatis.JdMapper;

/**
* SmsMessageRecord
* <p/>
* Author: 
* Date: 2017-08-21 14:33:38
* Mail: 
*/
public interface SmsMessageRecordMapper extends JdMapper<SmsMessageRecord, Long> {

	Page<SmsMessageRecord> selectSmsMessageRecordListByConditions(SmsMessageRecord smsMessageRecord);
	public int insertTable(SmsMessageRecord smsMessageRecord);
}