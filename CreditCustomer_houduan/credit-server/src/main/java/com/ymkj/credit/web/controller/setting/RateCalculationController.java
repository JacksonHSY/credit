package com.ymkj.credit.web.controller.setting;


import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ymkj.credit.common.constants.Constants;
import com.ymkj.credit.common.entity.Customer;
import com.ymkj.credit.common.entity.RateCalculation;
import com.ymkj.credit.common.entity.SmsMessageRecord;
import com.ymkj.credit.common.vo.ResultVo;
import com.ymkj.credit.service.CustomerService;
import com.ymkj.credit.service.OperateLogService;
import com.ymkj.credit.service.RateCalculationService;
import com.ymkj.credit.service.SmsMessageRecordService;
import com.ymkj.credit.service.SmsService;
import com.ymkj.springside.modules.orm.PageInfo;
import com.ymkj.sso.client.ShiroUtils;


@RequestMapping(value = "/manage")
@Controller
@Slf4j
public class RateCalculationController {
	
	@Autowired
	private RateCalculationService rateCalculationService;
	@Autowired
	private OperateLogService operateLogService;
	
	@RequestMapping("/rateCalculation")
	public ModelAndView index(Model model,HttpSession httpSession) {
		RateCalculation rateCal = rateCalculationService.selectOne();
		ModelAndView modelAndView = new ModelAndView("setting/rateCalculation");
		if(rateCal!=null){
			modelAndView.addObject("product", rateCal.getProductName());
			modelAndView.addObject("term", rateCal.getTerm());
			modelAndView.addObject("channel", rateCal.getChannel());
			modelAndView.addObject("minAmount", rateCal.getMinAmount());
			modelAndView.addObject("maxAmount", rateCal.getMaxAmount());
			modelAndView.addObject("rate", rateCal.getRate());
			modelAndView.addObject("id", rateCal.getId());
		}
		return modelAndView;
	}
	
	/**
	 * 
	 * @TODO
	 * @param smsMessageRecord
	 * @param request
	 * @return
	 * @throws Exception
	 * ResultVo
	 * @author changj@yuminsoft.com
	 * @date2018年6月6日
	 */
	@RequestMapping("/rateCalculation/saveRateCalculation")
	@ResponseBody
	public ResultVo saveRateCalculation(RateCalculation rateCalculation,HttpServletRequest request) throws Exception {
		try {
			RateCalculation rateCal = rateCalculationService.selectOne();
			boolean flag = true;
			if(rateCal==null){
				rateCalculation.setCreateTime(new Date());
				rateCalculation.setUpdateTime(new Date());
				rateCalculation.setOperater(ShiroUtils.getCurrentUser().getName());
			}else{
				rateCalculation.setUpdateTime(new Date());
				rateCalculation.setId(rateCal.getId());
				flag = false;
			}
			if("0.00".equals(rateCalculation.getMinAmount())||"0.00".equals(rateCalculation.getMaxAmount())){
				return  ResultVo.returnMsg(false, "金额不能为0");
			}
			if(Double.parseDouble(rateCalculation.getMinAmount())>Double.parseDouble(rateCalculation.getMaxAmount())){
				return  ResultVo.returnMsg(false, "最大金额不能小于最小金额");
			}
			int i = rateCalculationService.saveOrUpdateRateCalculation(rateCalculation, flag);
			//记录日志
			operateLogService.saveLog(ShiroUtils.getCurrentUser().getName(), "修改费率", "操作成功");
			if(i>0){
				return  ResultVo.returnMsg(true, "操作成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return  ResultVo.returnMsg(false, "操作失败");
	}
	
}
