package com.ymkj.credit.service;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;

import com.ymkj.credit.common.ex.BussErrorCode;
import com.ymkj.springside.modules.exception.BusinessException;
import com.ymkj.springside.modules.utils.StrUtils;

import lombok.extern.log4j.Log4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ymkj.credit.common.constants.Constants;
import com.ymkj.credit.common.entity.Customer;
import com.ymkj.credit.common.entity.OperateLog;
import com.ymkj.credit.common.entity.ValidateCode;
import com.ymkj.credit.common.untils.NumberUtil;
import com.ymkj.credit.common.untils.PropertiesReader;
import com.ymkj.credit.mapper.OperateLogMapper;
import com.ymkj.credit.mapper.ValidateCodeMapper;
import com.ymkj.credit.web.api.model.Result;
import com.ymkj.credit.web.api.model.base.Model_002004;
import com.ymkj.credit.web.api.model.base.Model_002005;

import net.sf.json.JSONObject;

/**
 * @Description：对类进行描述
 * @ClassName: OperateLogService.java
 * @Author：changj
 * @Date：2017年8月24日
 * -----------------变更历史-----------------
 * 如：who  2017年8月24日  修改xx功能
 */
@Service
public class OperateLogService {
	
	@Autowired
	private OperateLogMapper operateLogMapper;
	
	/**
	 * 
	 * @TODO
	 * @param operater
	 * @param operateType
	 * @param content
	 * void
	 * @author changj@yuminsoft.com
	 * @date2018年5月30日
	 */
	public void saveLog(String operater,String operateType,String content){
		OperateLog log = new OperateLog();
		log.setOperater(operater);
		log.setOperateType(operateType);
		log.setContent(content);
		log.setCreateTime(new Date());
		operateLogMapper.insertSelective(log);
    }
}