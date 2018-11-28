package com.ymkj.credit.service;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ymkj.credit.common.entity.ApplyLoanInfo;
import com.ymkj.credit.web.api.model.Result;
import com.ymkj.credit.web.api.model.base.Model_006013;
import com.ymkj.credit.web.api.model.base.Model_016014;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
@Slf4j
@Service
public class ZmscoreService {
	/**
	 * 获取芝麻分
	 * 
	 * */
	@Value("${mobProductId}")
    private String mobProductId;
	
	@Autowired
	ApplyLoanInfoService applyLoanInfoService;
	public Result getScore(Model_006013 model){
		 String score="";
   	 	 String loanNo = model.getLoanNo();
   	 	 log.info("借款编号："+loanNo);
		 ApplyLoanInfo info = new ApplyLoanInfo();
		 info.setLoanNo(loanNo);
		 ApplyLoanInfo a = applyLoanInfoService.selectOne(info);
		 if(a!=null){
			score = a.getZmScore();
		 }
		 log.info("芝麻分："+score);
		 JSONObject obj = new JSONObject();
		 SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd");
		 obj.put("score", score);
		 obj.put("time", sim.format(new Date()));
		 return Result.success(obj);
	}
	/**
	 * 获取数据超市productid
	 * */
	public Result getProductid(Model_016014 model){
		JSONObject obj = new JSONObject();
		obj.put("mobProductId", mobProductId);
		return Result.success(obj);
	}
}
