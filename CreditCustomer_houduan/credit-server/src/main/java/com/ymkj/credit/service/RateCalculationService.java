package com.ymkj.credit.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ymkj.credit.common.entity.RateCalculation;
import com.ymkj.credit.common.entity.SmsMessageRecord;
import com.ymkj.credit.common.util.PageUtils;
import com.ymkj.credit.mapper.RateCalculationMapper;
import com.ymkj.credit.mapper.SmsMessageRecordMapper;
import com.ymkj.credit.web.api.model.Result;
import com.ymkj.credit.web.api.model.base.Model_006009;
import com.ymkj.springside.modules.orm.PageInfo;

/**
 * @Description：对类进行描述
 * @ClassName: RateCalculationService.java
 * @Author：changj
 * @Date：2017年8月24日
 * -----------------变更历史-----------------
 * 如：who  2017年8月24日  修改xx功能
 */
@Service
public class RateCalculationService {
	
	@Autowired
	private RateCalculationMapper rateCalculationMapper;
	
	/**
	 * 
	 * @TODO
	 * @param rateCalculation
	 * @param flag
	 * void
	 * @author changj@yuminsoft.com
	 * @date2018年6月6日
	 */
	public int saveOrUpdateRateCalculation(RateCalculation rateCalculation,boolean flag){
		int i = 0;
		if(flag){
			 i = rateCalculationMapper.insertSelective(rateCalculation);
		}else{
			 i = rateCalculationMapper.updateByPrimaryKeySelective(rateCalculation);
		}
		return i;
    }
	
	/**
	 * 
	 * @TODO
	 * @return
	 * RateCalculation
	 * @author changj@yuminsoft.com
	 * @date2018年6月6日
	 */
	public RateCalculation selectOne(){
		List<RateCalculation> list = rateCalculationMapper.selectAll();
		if(list==null || list.size()<1){
			return null;
		}else{
			return list.get(0);
		}
		
	}
	
	/**
	 * 配置查询
	 * 
	 * */
	public Result selectParam(Model_006009 model){
		JSONObject obj = new JSONObject();
		List<RateCalculation> list = rateCalculationMapper.selectAll();
		if(list==null || list.size()<1){
			return null;
		}else{
			RateCalculation rate = list.get(0);
			obj.put("applyTerm", rate.getTerm().replace("期", ""));
			obj.put("floorLimit", rate.getMinAmount());
			obj.put("showProductCode", "00001");
			obj.put("ceilingLimit", rate.getMaxAmount());
		}
		return Result.success(obj);
		
	}
}