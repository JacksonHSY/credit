package com.ymkj.credit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ymkj.credit.common.constants.Constants;
import com.ymkj.credit.common.entity.Message;
import com.ymkj.credit.mapper.PushMsgMapper;
import com.ymkj.credit.web.api.model.Result;

import lombok.extern.slf4j.Slf4j;
/**
 * @Description：消息推送
 * @ClassName: PushMessage.java
 * @Author：huangsy
 * @Date：
 * 如：who  2017年8月24日  修改xx功能
 */
@Slf4j
@Service
public class PushMessageService {
	@Autowired
	PushMsgMapper messageMapper;
	
	/**
	 * 查询content
	 * @param model
	 * @return
	 */
	public Message selectContent(Message mes){
		mes.setStatus(Constants.DATA_VALID);
		return messageMapper.selectOne(mes);
	}

}
