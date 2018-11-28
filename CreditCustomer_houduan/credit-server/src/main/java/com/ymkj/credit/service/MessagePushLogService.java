package com.ymkj.credit.service;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ymkj.credit.common.constants.Constants;
import com.ymkj.credit.common.entity.MessagePushLog;
import com.ymkj.credit.common.util.DateUtil;
import com.ymkj.credit.common.util.PageUtils;
import com.ymkj.credit.mapper.MessagePushLogMapper;
import com.ymkj.credit.web.api.dto.MessageDto;
import com.ymkj.springside.modules.orm.PageInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* MessagePushLogService
* <p/>
* Author: 
* Date: 2017-09-11 14:34:27
* Mail: 
*/
@Service
public class MessagePushLogService{

    @Autowired
    private MessagePushLogMapper messagePushLogMapper;

    public int insert(MessagePushLog log){
        return messagePushLogMapper.insertTable(log);
    }
    /**
     * 根据id_card查询推送信息
     *
     * @param phone
     * @param flowStatus
     * @return
     */
    public List<MessageDto> queryByIdCard(String idCard) {
    	Map<String,String> map = new HashMap<String,String>();
    	map.put("idCard",idCard);
        return messagePushLogMapper.queryByIdCard(map);
    }
    
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PageInfo<MessagePushLog> getMessageListPage(PageInfo<MessagePushLog> pageInfo) {
		PageHelper.startPage(pageInfo.getPageNo(), pageInfo.getPageSize());
		MessagePushLog message = (MessagePushLog) pageInfo.getQueryParam();
		Page<MessagePushLog> page = (Page)messagePushLogMapper.selectMessagePushLogListByConditions(message);
        return PageUtils.convertPage(page);
	}
}