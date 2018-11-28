package com.ymkj.credit.service;

import java.util.Map;


import lombok.extern.log4j.Log4j;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.ymkj.credit.common.untils.UrlUtil;
import com.ymkj.credit.network.HttpClientUtil;
import com.ymkj.credit.network.Param;


@Log4j
@Service
public class ZyService {
	
	/**
	 * 基础数据获取
	 * @TODO
	 * @param request
	 * @param page
	 * @param id
	 * @return
	 * String
	 * @author changj@yuminsoft.com
	 * @date2018年1月8日
	 */
	public String getBaseInfo(){
		JSONObject model = new JSONObject();
		Long currentTime = System.currentTimeMillis();
		model.put("sysCode", "zdqq");
		String params = Param.getParam4Zy("600001", model,currentTime);
		String result = HttpClientUtil.sendHttpPostFZY(params);
		result = UrlUtil.paramDecoder(result);
		log.info(result);
		return result;
	}
	/**
	 *  费率计算 
	 * @TODO
	 * @param request
	 * @param page
	 * @param id
	 * @return
	 * String
	 * @author changj@yuminsoft.com
	 * @date2018年1月8日
	 */
	public String trialBeforeCredit(Map<String,String> map){
		/*JSONObject model = new JSONObject();
		model.put("name", "刘帆之");
		model.put("channelCode", "00016");
		model.put("productCd", "00012");
		model.put("applyLmt", "30000");
		model.put("applyTerm", "24");
		model.put("fristPaymentDate", "2018-01-16");*/
		JSONObject model = JSONObject.fromObject(map);
		String params = Param.getParam4Zy("600008", model,System.currentTimeMillis());
		String result = HttpClientUtil.sendHttpPostFZY(params);
		result = UrlUtil.paramDecoder(result);
		return result;
	}
	
	/**
	 * 征信查询
	 * @TODO
	 * @param request
	 * @param name
	 * @param idCard
	 * @return
	 * String
	 * @author changj@yuminsoft.com
	 * @date2018年1月8日
	 */
	public String getReportHtmlContent(Map<String,String> map){
		/*JSONObject model = new JSONObject();
		model.put("name", name);
		model.put("idCard", idCard);*/
		JSONObject model = JSONObject.fromObject(map);
		String params = Param.getParam4Zy("500002", model,System.currentTimeMillis());
		String result = HttpClientUtil.sendHttpPostFZY(params);
		result = UrlUtil.paramDecoder(result);
		log.info(result);
		return result;
	}
	/**
	 * 	 获取申请渠道接口
	 */
	public String getApplicationChannel(Map<String, String> map) {
		JSONObject model = JSONObject.fromObject(map);
		String params = Param.getParam4Zy("900004", model,System.currentTimeMillis());
		String result = HttpClientUtil.sendHttpPostFZY(params);
		result = UrlUtil.paramDecoder(result);
		log.info(result);
		return result;
	}
	/**
	 *征信报告上传
	 */
	public String getUploadCreditReport(Map<String, String> map) {
		JSONObject model = JSONObject.fromObject(map);
		
		String params = Param.getParam4Zy("500001", model,System.currentTimeMillis());
		
		String result = HttpClientUtil.sendHttpPostFZY(params);
		result = UrlUtil.paramDecoder(result);
		log.info(result);
		return result;
	}
	
	
}
