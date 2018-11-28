package com.ymkj.credit.service;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ymkj.credit.common.constants.Constants;
import com.ymkj.credit.common.ex.BussErrorCode;
import com.ymkj.rule.biz.api.message.MapResponse;
import com.ymkj.rule.biz.api.message.RuleEngineRequest;
import com.ymkj.rule.biz.api.service.IRuleEngineExecuter;
import com.ymkj.rule.biz.api.vo.ApplyRuleExecVo;
import com.ymkj.rule.biz.api.vo.zdqq.ZDQQApplyRuleExecVo;
import com.ymkj.springside.modules.exception.BusinessException;

@Slf4j
@Service
public class RuleService {
	@Autowired
	private IRuleEngineExecuter iRuleEngineExecuter;
	
	public com.ymkj.rule.biz.api.message.Response validate(String name,String idCardNo){
	    //调用益博睿录单接口进行录单校验
	    ApplyRuleExecVo applyRuleExecVo = new ApplyRuleExecVo();
	    applyRuleExecVo.setName(name);
	    applyRuleExecVo.setIdNo(idCardNo);
	    applyRuleExecVo.setExecuteType(Constants.CLICK_CHECK);
	    RuleEngineRequest ruleRequest = new RuleEngineRequest();
	    ruleRequest.setSysCode("zdqq");
	    ruleRequest.setBizType("loanApply");
	    ruleRequest.setData(applyRuleExecVo);
	    log.info("调用益博睿请求报文内容:"+name+","+idCardNo);
	    com.ymkj.rule.biz.api.message.Response ruleResponse = iRuleEngineExecuter.executeRuleEngine(ruleRequest);
	    if(null == ruleResponse){
	    	 throw new BusinessException(BussErrorCode.ERROR_CODE_5001.getErrorcode(),BussErrorCode.ERROR_CODE_5001.getErrordesc());
	    }
	    if(!ruleResponse.getRepCode().equals("000000")){
	        log.info("调用益博睿校验失败返回内容:"+ruleResponse.getRepMsg());
//	    	throw new BusinessException(BussErrorCode.ERROR_CODE_5002.getErrorcode(),BussErrorCode.ERROR_CODE_5002.getErrordesc());
	    }
	    log.info("调用益博睿返回内容:"+ruleResponse.getRepMsg());
		return ruleResponse;
	}
	
	public com.ymkj.rule.biz.api.message.Response validatezdqq(ZDQQApplyRuleExecVo vo){
		RuleEngineRequest rr = new RuleEngineRequest();
		rr.setData(vo);
		rr.setBizType(Constants.ZDQQAPPLY);
		rr.setSysCode(Constants.ZDQQ_SYS_CODE);
		com.ymkj.rule.biz.api.message.Response ruleResponse = iRuleEngineExecuter.executeRuleEngine(rr);
		if(null == ruleResponse){
	    	throw new BusinessException(BussErrorCode.ERROR_CODE_10000.getErrorcode(),BussErrorCode.ERROR_CODE_10000.getErrordesc());
	    }
		 if(!ruleResponse.getRepCode().equals("000000")){
	        log.info("调用校验失败返回内容:"+ruleResponse.getRepMsg());
		   	throw new BusinessException(BussErrorCode.ERROR_CODE_10001.getErrorcode(),BussErrorCode.ERROR_CODE_10001.getErrordesc());
	    }
	    log.info("调用返回内容:"+ruleResponse.getRepMsg());
	    
		return ruleResponse;
	}
	
	public Map<String,Object> getAppValidateResult(String link, String action){
	    Map<String,Object> resultMap = new HashMap<>();
	    if(action.equals("限制") || action.equals("拒绝")) resultMap.put("ifNext", "N");
	    if(action.equals("通过")) resultMap.put("ifNext", "Y");
	    return resultMap;
	}
	
	public Map<String, Object> getValidateResultIsCheck(Map<String, Object> map){
		 Map<String,Object> resultMap = new HashMap<>();
		if (map.get("action").equals("限制") && map.get("actionCode").equals("X01")) {
			resultMap.put("ifNext", "N");
			resultMap.put("hint", map.get("hint"));
		}else if(map.get("action").equals("提示")){
			resultMap.put("ifNext", "Y");
			resultMap.put("hint", map.get("hint"));
		}else{
			resultMap.put("ifNext", "Y");
		}
		return resultMap;
	}
	
	public Map<String, Object> getValidateResultSubmit(Map<String, Object> map){
		 Map<String,Object> resultMap = new HashMap<>();
		if (map.get("action").equals("限制") && map.get("actionCode").equals("X02")) {
			resultMap.put("ifNext", "N");
			resultMap.put("hint", map.get("hint"));
		}else if(map.get("action").equals("提示")){
			resultMap.put("ifNext", "Y");
			resultMap.put("hint", map.get("hint"));
		}else{
			resultMap.put("ifNext", "Y");
		}
		return resultMap;
	}
}
