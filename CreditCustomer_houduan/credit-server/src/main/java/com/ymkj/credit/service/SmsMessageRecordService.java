package com.ymkj.credit.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ymkj.credit.common.entity.SmsMessageRecord;
import com.ymkj.credit.common.util.PageUtils;
import com.ymkj.credit.mapper.SmsMessageRecordMapper;
import com.ymkj.springside.modules.orm.PageInfo;

/**
 * @Description：对类进行描述
 * @ClassName: SmsMessageRecordService.java
 * @Author：changj
 * @Date：2017年8月24日
 * -----------------变更历史-----------------
 * 如：who  2017年8月24日  修改xx功能
 */
@Service
public class SmsMessageRecordService {
	
	@Autowired
	private SmsMessageRecordMapper smsMessageRecordMapper;
	
	/**
	 * 
	 * @TODO
	 * @param smsMessageRecord
	 * void
	 * @author changj@yuminsoft.com
	 * @date2018年6月5日
	 */
	public void saveSmsMessageRecord(SmsMessageRecord smsMessageRecord){
		smsMessageRecord.setCreateTime(new Date());
		smsMessageRecordMapper.insertTable(smsMessageRecord);
    }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PageInfo<SmsMessageRecord> getMessageListPage(PageInfo<SmsMessageRecord> pageInfo,SmsMessageRecord message) {
		PageHelper.startPage(pageInfo.getPageNo(), pageInfo.getPageSize());
		Page<SmsMessageRecord> page = (Page)smsMessageRecordMapper.selectSmsMessageRecordListByConditions(message);
        return PageUtils.convertPage(page);
	}
}