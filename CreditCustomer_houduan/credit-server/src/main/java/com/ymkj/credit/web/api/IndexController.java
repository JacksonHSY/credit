package com.ymkj.credit.web.api;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import com.ymkj.credit.common.constants.Constants;
import com.ymkj.credit.common.entity.Customer;
import com.ymkj.credit.common.untils.MD5;
import com.ymkj.credit.network.HttpClientUtil;
import com.ymkj.credit.service.CustomerService;
import com.ymkj.credit.service.dms.DmsService;
import com.ymkj.credit.service.redis.BasicRedisOpts;
import com.ymkj.credit.web.api.model.Result;
import com.ymkj.dms.api.common.base.AppNewLoanInfo;
import com.ymkj.springside.modules.exception.BusinessException;
import com.ymkj.springside.modules.utils.StrUtils;



@RequestMapping(value = "/index")
@Controller
@Slf4j
public class IndexController {
	
	@Autowired
	private BasicRedisOpts basicRedisOpts;
	
	@Autowired
	private CustomerService customerService;
	
    @Autowired
    private DmsService dmsService;
    /**
     * 注册协议
     * @TODO
     * @param model
     * @param httpSession
     * @return
     * String
     * @author changj@yuminsoft.com
     * @date2018年5月25日
     */
	@RequestMapping(value ={""})
	public String protocol(Model model,HttpSession httpSession){
		return "protocol";
	}
	@RequestMapping(value="/heartbeat")
	@ResponseBody
	public String healthCheck() throws Exception {
		return "OK";
	}
	
	@RequestMapping(value = { "/dataInit" })
	@ResponseBody
	public String dataInit(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse,
			@RequestParam("memo") String memo) {
		log.info("===================》》》前前活动初始化：memo=" + memo);
		httpResponse.setHeader("Access-Control-Allow-Origin", "*");
		Result result = null;
		try {
			//查询登录信息
			Example example = new Example(Customer.class);
			Criteria criteria = example.createCriteria();
			criteria.andEqualTo("memo", memo);
			criteria.andIsNull("password");
			List<Customer> list = customerService.selectByExample(example);
			if(list==null || list.size()==0){
				result = Result.success("未找到符合条件的数据");
				return JSONObject.fromObject(result).toString();
			}
			Date date = new Date();
			for(Customer customer:list){
				String idcardPassWord = customer.getIdCard().substring(12, 18).toLowerCase();
				customer.setPassword(MD5.MD5Encode(MD5.MD5Encode(idcardPassWord)));
				customer.setStatus(Constants.DATA_VALID);
				customer.setLcbAccount(customer.getPhone());
				customer.setCreateTime(date);
				customer.setFlowStatus(Constants.FLOW_STATUS_REGISTER);//已注册
				customerService.updateByPhone(customer);
			}
			result = Result.success("初始化成功,操作条数:"+list.size());
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof BusinessException) {
				String message = e.getMessage();
				result = Result.fail(message);
				log.error(MessageFormat.format("调用功能时业务异常: {0}", message));
			} else {
				result = Result.fail();
				log.error("调用功能时系统异常: ", e);
			}
		}
		return JSONObject.fromObject(result).toString();
	}
	
	@RequestMapping(value = { "/pushData2Dms" })
	@ResponseBody
	public String pushData2Dms(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse,
			@RequestParam("memo") String memo) {
		log.info("===================》》》前前活动向录单推客户数据：memo=" + memo);
		httpResponse.setHeader("Access-Control-Allow-Origin", "*");
		Result result = null;
		try {
			//查询登录信息
			Example example = new Example(Customer.class);
			Criteria criteria = example.createCriteria();
			criteria.andEqualTo("memo", memo);
			criteria.andIsNull("accountManagerNo");
			criteria.andEqualTo("status",Constants.DATA_VALID);
			List<Customer> list = customerService.selectByExample(example);
			if(list==null || list.size()==0){
				result = Result.success("未找到符合条件的数据");
				return JSONObject.fromObject(result).toString();
			}
			int i=0;
			for(Customer customer:list){
				JSONObject loanParam = new JSONObject();
				loanParam.put("idNum", customer.getIdCard());
				try {
					JSONObject loanJson = HttpClientUtil.sendHttpPostForCredit4Jxhj(Constants.CREDIT_URL_LOANINFO_JXHJ, loanParam.toString());
					if (loanJson != null) {
						String oldAppNo = loanJson.get("oldAppNo")+"";
						if(StrUtils.isNotBlank(oldAppNo)){
							AppNewLoanInfo loan = new AppNewLoanInfo();
							loan.setOldLoanNo(oldAppNo);
							loan.setIdNo(customer.getIdCard());
							loan.setSysCode("app");
							String newLoanNo = dmsService.borrowNew(loan);
							if(StrUtils.isNotEmpty(newLoanNo)){
								++i;
								customer.setAccountManagerNo(memo);
								customerService.updateByPhone(customer);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			result = Result.success("一共执行:"+list.size()+",执行成功"+i+"条");
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof BusinessException) {
				String message = e.getMessage();
				result = Result.fail(message);
				log.error(MessageFormat.format("调用功能时业务异常: {0}", message));
			} else {
				result = Result.fail();
				log.error("调用功能时系统异常: ", e);
			}
		}
		return JSONObject.fromObject(result).toString();
	}

}
