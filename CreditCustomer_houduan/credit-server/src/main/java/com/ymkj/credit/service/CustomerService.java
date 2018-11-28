package com.ymkj.credit.service;


import com.alibaba.fastjson.JSONArray;
import com.cloopen.rest.sdk.utils.encoder.BASE64Decoder;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ymkj.credit.common.constants.Constants;
import com.ymkj.credit.common.entity.Customer;
import com.ymkj.credit.common.entity.IdCardInfo;
import com.ymkj.credit.common.entity.MessagePushLog;
import com.ymkj.credit.common.untils.DateUtil;
import com.ymkj.credit.common.untils.FileUploadUtil;
import com.ymkj.credit.common.untils.HttpKit;
import com.ymkj.credit.common.untils.MD5;
import com.ymkj.credit.common.untils.NullUtil;
import com.ymkj.credit.common.untils.NumberUtil;
import com.ymkj.credit.common.util.FileDownUtils;
import com.ymkj.credit.common.util.PageUtils;
import com.ymkj.credit.common.util.base64Utils;
import com.ymkj.credit.mapper.CustomerMapper;
import com.ymkj.credit.mapper.IdCardInfoMapper;
import com.ymkj.credit.mapper.LoanOrderMapper;
import com.ymkj.credit.network.HttpClientUtil;
import com.ymkj.credit.web.api.dto.LoanDTO;
import com.ymkj.credit.web.api.dto.MessageDto;
import com.ymkj.credit.web.api.dto.NoticeDto;
import com.ymkj.credit.web.api.dto.NoticesDto;
import com.ymkj.credit.web.api.model.Result;
import com.ymkj.credit.web.api.model.base.*;
import com.ymkj.credit.web.api.model.resp.CustomerBo;
import com.ymkj.credit.web.api.model.resp.DepartmentBo;
import com.ymkj.credit.web.api.model.resp.EmployeeBo;
import com.ymkj.credit.web.api.model.resp.LoanInfoModel;
import com.ymkj.springside.modules.exception.BusinessException;
import com.ymkj.springside.modules.orm.PageInfo;
import com.ymkj.springside.modules.utils.StrUtils;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * @Description：对类进行描述
 * @ClassName: CustomerService.java
 * @Author：tianx
 * @Date：2017年8月24日 -----------------变更历史-----------------
 * 如：who  2017年8月24日  修改xx功能
 */
@Slf4j
@Service
public class CustomerService {

    /**
     * 客服电话
     */
    @Value("${service_tel_number}")
    private String serviceTelNumber;

    @Value("${id_card_validate_switch}")
    private String idCardValidateSwitch;
    
    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private BusinessDepartService businessDepartService;
    
    @Autowired
    private MessagePushLogService messagePushLogService;
    
    @Autowired
    private IdCardInfoMapper idCardInfoMapper;
    
    @Autowired
    private LoanOrderMapper loanOrderMapper;
    
    @Autowired
    private RuleService ruleService;
    
	@Value("${urlUPIC}")
	private String urlUPIC;
	
    @Value("${scoreMin}")
    private double scoreMin;
    
    @Value("${upload_path}")
    private String uploadPath;
    
    @Value("${isLivenessOpen}")
    private String isLivenessOpen;
    
    /**
     * 功能描述：校验身份证信息
     * 输入参数：
     *
     * @param model
     * @return 返回类型：Result
     * 创建人：tianx
     * 日期：2017年8月24日
     */
    public Result validateIdCard(Model_002006 model) {
        if (Constants.SWITCH_ON.equals(idCardValidateSwitch)) {
            if (!NullUtil.is18ByteIdCard(model.getIdCard())) {
                return Result.fail("身份证不合法");
            }
        }

        Customer query = new Customer();
        query.setFlowStatus(Constants.FLOW_STATUS_REGISTER);
        query.setIdCard(model.getIdCard());
        List<Customer> records = select(query);
        if(null != records && records.size()>0){
            return Result.fail("身份证已被注册");
        }
        Customer customer = findByPhone(model.getPhone());
        if (null == customer) {
            return Result.fail("手机号不存在");
        }
        if (null != customer && Constants.FLOW_STATUS_REGISTER.equals(customer.getFlowStatus())) {
            return Result.fail("手机号已注册");
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("idNum", model.getIdCard().toUpperCase());
        jsonObject.put("userName", model.getCustomerName());
        JSONObject attchment = HttpClientUtil.sendHttpPostForCredit(Constants.CREDIT_URL_CUSTOMERINFO, jsonObject.toString());

        //获取
        if (!attchment.containsKey("customer")) {
            return Result.fail("接口响应信息异常");
        }
        CustomerBo customerBo = (CustomerBo) JSONObject.toBean(JSONObject.fromObject(attchment.get("customer")), CustomerBo.class);
        if (!customerBo.getTelList().contains(model.getPhone())) {
            return Result.fail("用户手机号与身份证不匹配");
        }
        //保存并返回客户经理工号
        EmployeeBo employeeBo = (EmployeeBo) JSONObject.toBean(JSONObject.fromObject(attchment.get("employee")), EmployeeBo.class);
        //customer.setBusiId(depart.getId());
        customer.setAccountManagerNo(employeeBo.getUserCode());
        customer.setIdCard(model.getIdCard().toUpperCase());
        customer.setFlowStatus(Constants.FLOW_STATUS_IDCARD);
        customer.setCustomerName(model.getCustomerName());
        customerMapper.updateByPrimaryKeyTable(customer);
        JSONObject userCode = new JSONObject();
        userCode.put("userCode", employeeBo.getUserCode());
        return Result.success(userCode);
    }

    /**
     * 功能描述：校验客户经理工号
     * 输入参数：
     *
     * @param model
     * @return 返回类型：Result
     * 创建人：tianx
     * 日期：2017年8月25日
     */
    public Result validateAccountManagerNo(Model_002008 model) {
        Customer customer = findByPhone(model.getPhone());
        if (null == customer) {
            return Result.fail("手机号不存在");
        }
        if (null != customer && Constants.FLOW_STATUS_REGISTER.equals(customer.getFlowStatus())) {
            return Result.fail("手机号已注册");
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userCode", model.getAccountManagerNo());
        JSONObject attach = HttpClientUtil.sendHttpPostForCredit(Constants.CREDIT_URL_EMPLOYEEINFO, jsonObject.toString());
        if (!attach.containsKey("userCode")) {
            return Result.fail("客户经理不存在");
        }
        customer.setAccountManagerNo(model.getAccountManagerNo());
        customer.setUpdateTime(new Date());
        customer.setFlowStatus(Constants.FLOW_STATUS_MANAGERNO);
        customerMapper.updateByPrimaryKeyTable(customer);
        return Result.success();
    }

    /**
     * 功能描述：注册
     * 输入参数：
     *
     * @param model
     * @return 返回类型：Result
     * 创建人：tianx
     * 日期：2017年8月25日
     */
    public Result register(Model_002007 model,String platform) {
        Customer customer = findByPhone(model.getPhone());
        if (null != customer && Constants.FLOW_STATUS_REGISTER.equals(customer.getFlowStatus())) {
            return Result.fail("手机号已注册");
        }
        log.info("推送id："+model.getPushId());
        log.info("平台类型："+platform);
        customer.setPlatform(platform);
        customer.setPushId(model.getPushId());
        customer.setPassword(MD5.MD5Encode(model.getPassword()));
        customer.setPhone(model.getPhone());
        customer.setGestureSwitch(Constants.GESTURE_SWITCH_UNSET);
        customer.setFlowStatus(Constants.FLOW_STATUS_REGISTER);
        customer.setStatus(Constants.DATA_VALID);
        customer.setCreateTime(new Date());
        customer.setMemo(Constants.AGREED);
        customerMapper.updateByPrimaryKeyTable(customer);
        /**注册成功推送手机消息*/
        /*提交成功推送手机消息,type必传*/
		JSONObject objects = new JSONObject();
		JSONObject param = new JSONObject();
		param.put("loanNo", "");
		param.put("cusName", "");
		param.put("banPhone", "");
		param.put("repaidNum", "");
		param.put("bankCard", "");
		param.put("repaidTotal", "");
		param.put("idNum", "");
		param.put("gender", "");
		param.put("realPaid", "");
		param.put("phone", model.getPhone());
		objects.put("type", Constants.REGIEST);
		objects.put("param", param);
		com.alibaba.fastjson.JSONObject objMes;
		try {
			objMes = HttpClientUtil.sendHttpPostPushMes(objects.toString());
			log.info("提交借款消息推送结果："+objMes.getString("message"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return Result.success();
    }
    
    /**
     * 新建用户
     * @param customer
     * @return
     */
    public int insertCustomer(Customer customer){
    	customer.setGestureSwitch(Constants.GESTURE_SWITCH_UNSET);
        customer.setFlowStatus(Constants.FLOW_STATUS_REGISTER);
        customer.setStatus(Constants.DATA_VALID);
        customer.setCreateTime(new Date());
        return customerMapper.insertTable(customer);
    }
//    /**
//     * 功能描述：根据身份证号和状态查找
//     * 输入参数：
//     *
//     */
//    public List<Customer> queryByIdCardAndflowStatus(Map map){
//    	
//		return null;
//    	
//    }
    /**
     * 功能描述：获取用户公告
     * 输入参数：
     *
     * @param model
     * @return 返回类型：Result
     * 创建人：tianx
     * 日期：2017年8月28日
     */
    public Result getNotices(Model_004006 model) {
    		Integer type = model.getType();
    		JSONObject jsonObject = new JSONObject();
            if(type==1){
            	jsonObject.put("pagerNum", model.getPageNo());
                jsonObject.put("pagerMax", model.getPageSize());
            	JSONObject attach = HttpClientUtil.sendHttpPostForCredit(Constants.CREDIT_URL_NOTICELIST, jsonObject.toString());
                if (attach.containsKey("noticeList")) {
                    NoticesDto notices = (NoticesDto) JSONObject.toBean(attach, NoticesDto.class);
                    int totalPageNum = (notices.getTotal() + notices.getMax() - 1) / notices.getMax();
                    if (model.getPageNo() > totalPageNum) {
                        notices.setNoticeList(null);
                        return Result.success(notices);
                    }
                    List<NoticeDto> list = JSONArray.parseArray(attach.getString("noticeList"), NoticeDto.class);
                    for (NoticeDto dto : list) {
                        String weekOfDate = DateUtil.getWeekOfDate(DateUtil.getCommStyleTime(dto.getCreateTime(), DateUtil.DATAFORMAT_YYYY_MM_DD));
                        dto.setCreateTime(dto.getCreateTime().substring(0, 10) + " " + weekOfDate);
                    }
                    notices.setNoticeList(list);
                    return Result.success(notices);
                }
            }else{
            	if(model.getCustomerId()!=null){
            		Customer customer = findCustomerMes(model.getCustomerId());
                	String idCard =customer.getIdCard(); 
                	PageHelper.startPage(model.getPageNo(), model.getPageSize());
            		Page<MessageDto> pageList = (Page<MessageDto>) messagePushLogService.queryByIdCard(idCard);
            		for (MessageDto dto : pageList) {
                         String weekOfDate = DateUtil.getWeekOfDate(DateUtil.getCommStyleTime(dto.getCreateTime(), DateUtil.DATAFORMAT_YYYY_MM_DD));
                         dto.setCreateTime(dto.getCreateTime().substring(0, 10) + " " + weekOfDate);
                     }
            		Map<String,Object> map = new HashMap<String,Object>();
            		map.put("messageList", pageList);
            		map.put("total",pageList.getTotal());
            		return Result.success(map);
            	}
            }
		return Result.success();
    }

    /**
     * 功能描述：根据客户ID获取客户贷款信息
     * 输入参数：
     *
     * @param model
     * @return 返回类型：Result
     * 创建人：tianx
     * 日期：2017年8月28日
     */
    public Result queryCreditInfo(Model_003001 model) {

        Customer query = new Customer();
        query.setId(model.getCustomerId());
        Customer customer = customerMapper.selectOneTable(query);
        if (null == customer) {
        	return  Result.fail("用户不存在");
        }
	    JSONObject data = new JSONObject();
	    List<LoanDTO> resultLoans = new ArrayList<LoanDTO>(2);
		data.put("serviceTelNumber", serviceTelNumber);
		 /**
         * 获取贷款信息
         */
		 JSONObject loanParam = new JSONObject();
	     loanParam.put("idNum", customer.getIdCard());
	     JSONObject loanJson = HttpClientUtil.sendHttpPostForCreditCustomerInfo(Constants.CREDIT_URL_LOANINFO, loanParam.toString());
    	if(loanJson!=null){
    		 /**
             * 获取营业部信息
             */
            JSONObject customerParam = new JSONObject();
            customerParam.put("idNum", customer.getIdCard());
            customerParam.put("userName", customer.getCustomerName());
            JSONObject departAttch = HttpClientUtil.sendHttpPostForCredit(Constants.CREDIT_URL_CUSTOMERINFO, customerParam.toString());
        	if(departAttch == null){///债权取消 或者 拒绝不提示异常
        	   LoanDTO dto = new LoanDTO();
       		   dto.setLoanState(Constants.LOAN_STATUS_NULL);
       		   resultLoans.add(dto);
       		   resultLoans.add(dto);
       		   data.put("loans", resultLoans);
       		   return Result.success(data);
        	}
            
            
            if (!departAttch.containsKey("department")) {
                return Result.fail("接口响应信息异常");
            }
            DepartmentBo departBo = (DepartmentBo) JSONObject.toBean(JSONObject.fromObject(departAttch.getString("department")), DepartmentBo.class);
            data.put("salesOffice", departBo.getDeptName());
            data.put("officePhone", departBo.getTelephone());
            data.put("longitude", departBo.getLongitude());
            data.put("latitude", departBo.getLatitude());
            data.put("address", departBo.getSite());
            /**
             * 获取客户经理
             */
//            JSONObject managerParam = new JSONObject();
////          managerParam.put("userCode", customer.getAccountManagerNo());
//            JSONObject attach = HttpClientUtil.sendHttpPostForCredit(Constants.CREDIT_URL_EMPLOYEEINFO, managerParam.toString());
//            data.put("accountManagerName", attach.getString("empName"));
            
            /**
             * 获取贷款信息
             */
//            JSONObject loanParam = new JSONObject();
//            loanParam.put("idNum", customer.getIdCard());
//            JSONObject loanJson = HttpClientUtil.sendHttpPostForCredit(Constants.CREDIT_URL_LOANINFO, loanParam.toString());
            if (!loanJson.containsKey("list")) {
                Result.fail("查询贷款信息异常");
            }
            BigDecimal amountDue = BigDecimal.ZERO;
         
            List<LoanInfoModel> loans = JSONArray.parseArray(loanJson.getString("list"), LoanInfoModel.class);
            for (int i = 0; i < 2; i++) {
                LoanDTO dto = new LoanDTO();
                if (null == loans || loans.isEmpty() || loans.size() < i + 1) {
                    dto.setLoanState(Constants.LOAN_STATUS_NULL);
                    resultLoans.add(dto);
                    continue;
                }
                LoanInfoModel loan = loans.get(i);
                dto.setContractNo(loan.getContractNum());
                
                /*-------1.5.1--------*/
                dto.setRepayCorpus(loan.getRepayCorpus());
                dto.setRepayInterest(loan.getRepayInterest());
                dto.setRepayFine(loan.getRepayFine());
                dto.setLoanState(loan.getLoanState());
                dto.setIsOffer(loan.getIsOffer());
                dto.setTerm(loan.getTerm());
                dto.setProductName(loan.getProductName());
                dto.setDeptName(loan.getDeptName());
                dto.setDeptTel(loan.getDeptTel());
                dto.setIsValid(loan.getIsValid());
                dto.setLongitude(departBo.getLongitude());
                dto.setLatitude(departBo.getLatitude());
                dto.setAddress(departBo.getSite());
                /*--------1.5.1---------*/
                
//                dto.setContractAmount(Constants.MONEY_SYMBOL + loan.getPactMoney().setScale(2).toString());
                dto.setContractAmount(loan.getPactMoney().setScale(2,BigDecimal.ROUND_DOWN).toString());
                dto.setRemainTerm(loan.getResidualTerm());
                if (Constants.IS_OVERDUE_YES.equals(loan.getIsOverdue())) {
                    dto.setLoanStatus(Constants.LOAN_STATUS_YES);
                } else {
                    dto.setLoanStatus(Constants.LOAN_STATUS_NO);
                }
                dto.setRepayDate(loan.getCurRepayDate());
                if(loan.getRepayMoney().compareTo(BigDecimal.ZERO) != 0){
//                    dto.setRepayAmount(Constants.MONEY_SYMBOL + loan.getRepayMoney().setScale(2).toString());
            	  if (loan.getIsOverdue().equals(Constants.IS_OVERDUE_YES)) {
            		  dto.setRepayAmount(loan.getRepayMoney().add(loan.getCurTermMoney()).setScale(2,BigDecimal.ROUND_DOWN).toString());
            	  }else{
            			dto.setRepayAmount(loan.getRepayMoney().setScale(2,BigDecimal.ROUND_DOWN).toString());
            	  }
                }else{
                    dto.setRepayAmount("0");
                }
                if (StringUtils.isNotBlank(loan.getCurRepayDate())) {
                    Date repayTime = DateUtil.getCommStyleTime(loan.getCurRepayDate(), DateUtil.DATAFORMAT_YYYY_MM_DD);
                    dto.setRemainDays(DateUtil.getIntervalDays(repayTime,DateUtil.getCommStyleTime(DateUtil.getDateFormatString(new Date(),DateUtil.DATAFORMAT_YYYY_MM_DD))));
                }
                if (loan.getIsOverdue().equals(Constants.IS_OVERDUE_YES)) {
                    dto.setOverDueDays(loan.getOverdueDays());
                 
                    amountDue = amountDue.add(loan.getCurTermMoney());
                }
                amountDue = amountDue.add(loan.getRepayMoney());
                resultLoans.add(dto);
            }
           
            if(amountDue.compareTo(BigDecimal.ZERO) != 0){
                data.put("amountDue", amountDue.setScale(2,BigDecimal.ROUND_DOWN).toString());
            }else{
                data.put("amountDue", "0");
            }
    	}else{
    		   LoanDTO dto = new LoanDTO();
    		   dto.setLoanStatus(Constants.LOAN_STATUS_NULL);
    		   resultLoans.add(dto);
    		   resultLoans.add(dto);
    	}
    	 data.put("loans", resultLoans);
        return Result.success(data);
    }
    
    /**
     * 还款页面详情
     * @TODO
     * @param model
     * @return
     * Result
     * @author changj@yuminsoft.com
     * @date2018年6月22日
     */
    public Result queryCreditInfoDetail(Model_003002 model) {

        Customer query = new Customer();
        query.setId(model.getCustomerId());
        Customer customer = customerMapper.selectOneTable(query);
        if (null == customer) {
        	return  Result.fail("用户不存在");
        }
	    JSONObject data = new JSONObject();
	    List<LoanDTO> resultLoans = new ArrayList<LoanDTO>(2);
//		data.put("serviceTelNumber", serviceTelNumber);
		 /**
         * 获取贷款信息
         */
		 JSONObject loanParam = new JSONObject();
	     loanParam.put("idNum", customer.getIdCard());
	     JSONObject loanJson = HttpClientUtil.sendHttpPostForCreditCustomerInfo(Constants.CREDIT_URL_LOANINFO, loanParam.toString());
    	if(loanJson!=null){
    		 /**
             * 获取营业部信息
             */
            JSONObject customerParam = new JSONObject();
            customerParam.put("idNum", customer.getIdCard());
            customerParam.put("userName", customer.getCustomerName());
            JSONObject departAttch = HttpClientUtil.sendHttpPostForCredit(Constants.CREDIT_URL_CUSTOMERINFO, customerParam.toString());
        	if(departAttch == null){///债权取消 或者 拒绝不提示异常
        	   LoanDTO dto = new LoanDTO();
       		   dto.setLoanState(Constants.LOAN_STATUS_NULL);
       		   resultLoans.add(dto);
       		   resultLoans.add(dto);
       		   data.put("loans", resultLoans);
       		   return Result.success(data);
        	}
            if (!departAttch.containsKey("department")) {
                return Result.fail("接口响应信息异常");
            }
//            DepartmentBo departBo = (DepartmentBo) JSONObject.toBean(JSONObject.fromObject(departAttch.getString("department")), DepartmentBo.class);
//            data.put("salesOffice", departBo.getDeptName());
//            data.put("officePhone", departBo.getTelephone());
//            data.put("longitude", departBo.getLongitude());
//            data.put("latitude", departBo.getLatitude());
//            data.put("address", departBo.getSite());
            /**
             * 获取客户经理
             */
//            JSONObject managerParam = new JSONObject();
////          managerParam.put("userCode", customer.getAccountManagerNo());
//            JSONObject attach = HttpClientUtil.sendHttpPostForCredit(Constants.CREDIT_URL_EMPLOYEEINFO, managerParam.toString());
//            data.put("accountManagerName", attach.getString("empName"));
            
            /**
             * 获取贷款信息
             */
//            JSONObject loanParam = new JSONObject();
//            loanParam.put("idNum", customer.getIdCard());
//            JSONObject loanJson = HttpClientUtil.sendHttpPostForCredit(Constants.CREDIT_URL_LOANINFO, loanParam.toString());
            if (!loanJson.containsKey("list")) {
                Result.fail("查询贷款信息异常");
            }
            BigDecimal amountDue = BigDecimal.ZERO;
         
            List<LoanInfoModel> loans = JSONArray.parseArray(loanJson.getString("list"), LoanInfoModel.class);
            for (int i = 0; i < 2; i++) {
                LoanDTO dto = new LoanDTO();
                if (null == loans || loans.isEmpty() || loans.size() < i + 1) {
                    dto.setLoanStatus(Constants.LOAN_STATUS_NULL);
                    resultLoans.add(dto);
                    continue;
                }
                LoanInfoModel loan = loans.get(i);
                dto.setContractNo(loan.getContractNum());
                
                /*-------1.5.1--------*/
                dto.setRepayCorpus(loan.getRepayCorpus());
                dto.setRepayInterest(loan.getRepayInterest());
                dto.setRepayFine(loan.getRepayFine());
                dto.setLoanState(loan.getLoanState());
                dto.setIsOffer(loan.getIsOffer());
                dto.setTerm(loan.getTerm());
                dto.setProductName(loan.getProductName());
                dto.setDeptName(loan.getDeptName());
                dto.setDeptTel(loan.getDeptTel());
                dto.setIsValid(loan.getIsValid());
                /*--------1.5.1---------*/
//                dto.setContractAmount(Constants.MONEY_SYMBOL + loan.getPactMoney().setScale(2).toString());
                dto.setContractAmount(loan.getPactMoney().setScale(2,BigDecimal.ROUND_DOWN).toString());
                dto.setRemainTerm(loan.getResidualTerm());
                if (Constants.IS_OVERDUE_YES.equals(loan.getIsOverdue())) {
                    dto.setLoanStatus(Constants.LOAN_STATUS_YES);
                } else {
                    dto.setLoanStatus(Constants.LOAN_STATUS_NO);
                }
                dto.setRepayDate(loan.getCurRepayDate());
                if(loan.getRepayMoney().compareTo(BigDecimal.ZERO) != 0){
//                    dto.setRepayAmount(Constants.MONEY_SYMBOL + loan.getRepayMoney().setScale(2).toString());
                	 dto.setRepayAmount(loan.getRepayMoney().setScale(2,BigDecimal.ROUND_DOWN).toString());
                }else{
                    dto.setRepayAmount("0");
                }
                if (StringUtils.isNotBlank(loan.getCurRepayDate())) {
                    Date repayTime = DateUtil.getCommStyleTime(loan.getCurRepayDate(), DateUtil.DATAFORMAT_YYYY_MM_DD);
                    dto.setRemainDays(DateUtil.getIntervalDays(repayTime,DateUtil.getCommStyleTime(DateUtil.getDateFormatString(new Date(),DateUtil.DATAFORMAT_YYYY_MM_DD))));
                }
                if (loan.getIsOverdue().equals(Constants.IS_OVERDUE_YES)) {
                    dto.setOverDueDays(loan.getOverdueDays());
                }
                amountDue = amountDue.add(loan.getRepayMoney());
                resultLoans.add(dto);
            }
           
            if(amountDue.compareTo(BigDecimal.ZERO) != 0){
                data.put("amountDue", amountDue.setScale(2,BigDecimal.ROUND_DOWN).toString());
            }else{
                data.put("amountDue", "0");
            }
    	}else{
    		   LoanDTO dto = new LoanDTO();
    		   dto.setLoanState(Constants.LOAN_STATUS_NULL);
    		   resultLoans.add(dto);
    		   resultLoans.add(dto);
    	}
    	 data.put("loans", resultLoans);
        return Result.success(data);
    }
	/**
	 * 还款
	 * @TODO
	 * @param model
	 * @return
	 * Result
	 * @author changj@yuminsoft.com
	 * @date2018年6月14日
	 */
    public Result realTimeRepayment(Model_004025 model){
    	  JSONObject param = new JSONObject();
    	  param.put("contractNum", model.getContractNum());
    	  param.put("account", model.getAccount());
    	  param.put("amount", model.getAmount());
    	  JSONObject retJson = HttpClientUtil.sendHttpPostForCreditCustomerInfo4All(Constants.CREDIT_URL_realTimeRepayment, param.toString());
    	  if(!Constants.RESP_CODE_SUCCESS.equals(retJson.get("resCode"))){
    	 	  return Result.fail(retJson.get("resMsg").toString());
          }else{
        	  return Result.success();
          }
    }
    
    
    
    
    /**
     * 功能描述：根据手机号查询客户
     * 输入参数：
     *
     * @param phone
     * @return 返回类型：Customer
     * 创建人：tianx
     * 日期：2017年8月23日
     */
    public Customer findByPhone(String phone) {
        Customer query = new Customer();
        query.setPhone(phone);
        query.setStatus(Constants.DATA_VALID);
        return customerMapper.selectOneTable(query);
    }

    /**
     * 功能描述：查询已注册用户
     * 输入参数：
     *
     * @param phone
     * @param flowStatus
     * @return 返回类型：Customer
     * 创建人：tianx
     * 日期：2017年8月25日
     */
    public Customer findByPhone(String phone, String flowStatus) {
        Customer query = new Customer();
        query.setPhone(phone);
        query.setFlowStatus(flowStatus);
        query.setStatus(Constants.DATA_VALID);
        return customerMapper.selectOneTable(query);
    }

    /**
     * 功能描述：根据手机号保存用户
     * 输入参数：
     *
     * @param phone 返回类型：void
     *              创建人：tianx
     *              日期：2017年8月25日
     */
    public void insertByPhone(String phone) {
        Customer insert = new Customer();
        insert.setPhone(phone);
        insert.setFlowStatus(Constants.FLOW_STATUS_NEW);
        insert.setStatus(Constants.DATA_VALID);
        insert.setCreateTime(new Date());
        insert.setUpdateTime(new Date());
        customerMapper.insertTable(insert);
    }

    /**
     * @param customer
     */
    public void updateByPhone(Customer customer) {
        customer.setUpdateTime(new Date());
        customerMapper.updateByPrimaryKeyTable(customer);
    }

    /**
     * 根据ID查询客户
     *
     * @param phone
     * @param flowStatus
     * @return
     */
    public Customer findById(Long id) {
        Customer query = new Customer();
        query.setId(id);
        query.setFlowStatus(Constants.FLOW_STATUS_REGISTER);
        query.setStatus(Constants.DATA_VALID);
        return customerMapper.selectOneTable(query);
    }
    /**
     * 根据ID查询客户信息(查询条件不包括客户业务状态)
     *
     * @param phone
     * @param flowStatus
     * @return
     */
    public Customer findCustomerMes(Long id) {
        Customer query = new Customer();
        query.setId(id);
        query.setStatus(Constants.DATA_VALID);
        return customerMapper.selectOneTable(query);
    }
    /**
     * 更新
     *
     * @param customer
     * @return
     */
    public int updateByPrimaryKeySelective(Customer customer) {
        customer.setUpdateTime(new Date());
        return customerMapper.updateByPrimaryKeyTable(customer);
    }

    public int updateByPrimaryKey(Customer customer) {
        customer.setUpdateTime(new Date());
        return customerMapper.updateByPrimaryKeyTable(customer);
    }

    /**
     * 查询客户信息
     *
     * @param customer
     * @return
     */
    public Customer selectOne(Customer customer) {
        customer.setStatus(Constants.DATA_VALID);
        return customerMapper.selectOneTable(customer);
    }
    /**
     *根据身份证号查询客户信息
     *
     * @param example
     * @return
     */
    public Customer queryByIdCard(String idCard) {
    	  Customer query = new Customer();
    	  query.setIdCard(idCard);
          query.setStatus(Constants.DATA_VALID);
          return customerMapper.selectOneTable(query);
    }
    /**
     * 查询列表
     *
     * @param example 
     * @return
     */
    public List<Customer> selectByExample(Customer example) {
    	return customerMapper.selectByExampleTable(example);
    }
    public List<Customer> selectByExample(Example example) {
    	return customerMapper.selectByExample(example);
    }
    public List<Customer> selectByExampleByPhone(Customer cus) {
    	return customerMapper.selectByExampleByPhone(cus);
    }

    /**
     * 查询列表
     *
     * @param example
     * @return
     */
    public List<Customer> select(Customer query) {
        query.setStatus(Constants.DATA_VALID);
        return customerMapper.selectByExampleTable(query);
    }
    /**
     * 查询身份证照片列表
     *
     * @param example
     * @return
     */
    public List<IdCardInfo> selectIdCardInfo(IdCardInfo query) {
        query.setStatus(Constants.DATA_VALID);
        return idCardInfoMapper.select(query);
    }
    /**
     *验证是否为同一个人
     *
     * @param example
     * @return
     */
    public Result isSamePerson(Model_004012 model) {
    	double score =  Double.parseDouble(model.getScore());
        if(score >=scoreMin){
        	return Result.success("验证通过");
        }else{
        	return Result.fail("验证未通过");
        }
    }
    /**
     *身份信息验证
     *
     * @param example
     * @return
     * @throws Exception 
     */
    @Transactional
    public Result uploadIdCard(com.ymkj.credit.web.api.model.base.Model_004013 model) throws Exception {
    	com.ymkj.credit.common.entity.Customer customer = findCustomerMes(Long.parseLong(model.getCustomerId()));
    	if(null==customer){
			return Result.fail("用户不存在");
		}
    	if(Constants.FLOW_STATUS_IDCARD.equals(customer.getFlowStatus())&& !customer.getCustomerName().trim().equals(model.getCustomerName().trim())){
    		return Result.fail("认证失败,非本人认证");
    	}
    	//查询该身份证是否已被认证
    	String idCard  = model.getIdCard();
    	Customer c = queryByIdCard(idCard);
    	if(c!=null){
    		//if(!model.getCustomerId().equals(c.getId().toString())){
    			if(Constants.FLOW_STATUS_IDCARD.equals(c.getFlowStatus())||Constants.FLOW_STATUS_MANAGERNO.equals(c.getFlowStatus())){
    				return Result.fail("该身份证已被其他账户认证,请不要重复认证");
    			}
    		//}
    	}
//    	com.ymkj.rule.biz.api.message.Response ruleResponse = ruleService.validate(model.getCustomerName(), idCard);
//    	 if(null == ruleResponse){
//    		 return Result.fail("调用益博睿录单接口失败！返回null");
//  	    }
//  	    if(!ruleResponse.getRepCode().equals("000000")){
//  	    	return Result.fail("账号未通过审核");
//  	    }
  	    String idCardBeginDate = "";
  	    if(customer.getIdCardBeginDate()!=null){
  	    	idCardBeginDate = DateUtil.format(customer.getIdCardBeginDate(), DateUtil.DATAFORMAT_YYYYMMDD);
  	    }
  	    //StrUtils.isEmpty(customer.getIdCard())||( (idCard.equals(customer.getIdCard()) && !Constants.FLOW_STATUS_IDCARD.equals(customer.getFlowStatus()))) &&
    	log.info("身份认证入参开始时间:"+model.getBeginDate()+",客户表存量开始时间:"+idCardBeginDate);
    	String flowStatus="";
    	if(c!=null){
    		flowStatus=c.getFlowStatus();
    	}
  	    if(!model.getBeginDate().equals(idCardBeginDate) || !(Constants.FLOW_STATUS_IDCARD.equals(flowStatus)||Constants.FLOW_STATUS_MANAGERNO.equals(flowStatus))){
  	    	//之前的设为无效
  	    	IdCardInfo old = new IdCardInfo();
  	    	old.setCustomerId(Long.parseLong(model.getCustomerId()));
  	    	old.setStatus(Constants.DATA_UNVALID);
  	    	idCardInfoMapper.updateIdCardInfoByCId(old);
  	    	
    		Map<String,  String>  queryParas  =  new  HashMap<>();
        	String batchNum = NumberUtil.getNumberForPK();
        	queryParas.put("appNo",batchNum);
        	queryParas.put("operator",customer.getId().toString());
        	queryParas.put("jobNumber",customer.getId().toString());
        	queryParas.put("nodeKey",  "loanApplication");//录单环节
        	if(model.getIdNoFrontImg() != null){ 
        		queryParas.put("code","B");//身份证 正面:s41  反面:s42
        		//queryParas.put("imgeInfo",model.getIdNoFrontImg()); {"isOk":false,"isFail":true,"errorcode":"111111","errormsg":"参数(nodeKey)不能为空"}
        		String imageInfo = model.getIdNoFrontImg();
        	    String result =  uploadfile(queryParas,imageInfo);
        	    JSONObject json = JSONObject.fromObject(result);
        	    if(json.getBoolean("isOk")){
        			com.alibaba.fastjson.JSONObject resultJson = com.alibaba.fastjson.JSONObject.parseObject(result);
        			result = com.alibaba.fastjson.JSONObject.toJSONString(resultJson.get("result"));
        			com.alibaba.fastjson.JSONObject object = com.alibaba.fastjson.JSONObject.parseObject(result);
        	    	IdCardInfo idCardInfo = new IdCardInfo();
        		  	idCardInfo.setId(UUID.randomUUID().toString());
        	    	idCardInfo.setCustomerId((Long.parseLong(model.getCustomerId())));
        	    	idCardInfo.setScore(model.getScore());
        	    	idCardInfo.setCreateTime(new Date());
        	    	idCardInfo.setStatus(Constants.DATA_VALID);
        	        idCardInfo.setUrl(object.getString("url"));
            	    idCardInfo.setMemo(object.getString("id"));
//            	    idCardInfo.setName(customerName);
            	    idCardInfo.setType(Constants.IdCard_FRONT);//正面
            	    idCardInfo.setInfo(model.getIdNoFrontInfo());
            	    idCardInfo.setBatchNum(batchNum);
            	    idCardInfo.setIsLivenessOpen(isLivenessOpen);
            	    idCardInfoMapper.insertSelective(idCardInfo);
            	    
        	    }else{
        	    	throw new BusinessException(json.getString("errormsg"));
        	    }
    	     }
        	if(model.getIdNoReverseSideImg() != null){
    	    	 queryParas.put("code","B");//身份证 正面:s41  反面:s42
    	    	 String imageInfo = model.getIdNoReverseSideImg();
    	    	 String result =  uploadfile(queryParas,imageInfo);
    	    	 JSONObject json = JSONObject.fromObject(result);
    	    	  if(json.getBoolean("isOk")){
    	    			com.alibaba.fastjson.JSONObject resultJson = com.alibaba.fastjson.JSONObject.parseObject(result);
    	    			result = com.alibaba.fastjson.JSONObject.toJSONString(resultJson.get("result"));
    	    			 com.alibaba.fastjson.JSONObject object = com.alibaba.fastjson.JSONObject.parseObject(result);
    	    		     IdCardInfo idCardInfo2 = new IdCardInfo();
    	    			 idCardInfo2.setId(UUID.randomUUID().toString());
    	        	     idCardInfo2.setCustomerId((Long.parseLong(model.getCustomerId())));
    	        	     idCardInfo2.setScore(model.getScore());
    	        	     idCardInfo2.setCreateTime(new Date());
    	        	     idCardInfo2.setStatus(Constants.DATA_VALID);
    	    	         idCardInfo2.setUrl(object.getString("url"));
    	    	         idCardInfo2.setMemo(object.getString("id"));
    	    	         idCardInfo2.setType(Constants.IdCard_Reverse);//反面
//    	    	         idCardInfo2.setName(customerName);
//    	         	     idCardInfo2.setIdCardNo(idCard);
    	    	    	 idCardInfo2.setInfo(model.getIdNoReverseSideInfo());
    	    	    	 idCardInfo2.setBatchNum(batchNum);
    	    	    	 idCardInfo2.setIsLivenessOpen(isLivenessOpen);
    	    	    	 idCardInfoMapper.insertSelective(idCardInfo2);
    	    	    }else{
    	    	    	throw new BusinessException(json.getString("errormsg"));
    	    	    }
    	     }
        	 customer.setFlowStatus(Constants.FLOW_STATUS_IDCARD);
        	 customer.setIdCard(model.getIdCard());
        	 customer.setCustomerName(model.getCustomerName());
        	 customer.setIdCardBeginDate(DateUtil.strToDate(model.getBeginDate(),DateUtil.DATAFORMAT_YYYYMMDD));
        	 customer.setIdCardEndDate(DateUtil.strToDate(model.getEndDate(),DateUtil.DATAFORMAT_YYYYMMDD));
     	     updateByPrimaryKey(customer);
     	     //查询是否存在身份证手机号相等未认证的其他账户
     	     Customer cus = new Customer();
     	     cus.setId(Long.parseLong(model.getCustomerId()));
     	     cus.setIdCard(model.getIdCard());
     	     cus.setFlowStatus(Constants.FLOW_STATUS_IDCARD);
     	     List<Customer> list = customerMapper.selectListColumn(cus);
     	     for (Customer ct : list) {
				ct.setStatus(Constants.DATA_UNVALID);
				updateByPrimaryKey(ct);
     	     }
     	     //预开户
 			JSONObject paramNew = new JSONObject();
 			paramNew.put("idnum", model.getIdCard());
 			paramNew.put("name", customer.getCustomerName());
 			paramNew.put("mphone", customer.getPhone());
 			paramNew.put("sex", getCarInfo(model.getIdCard()));
 			HttpClientUtil.sendHttpPostForCredit4BindBankCard(Constants.CREDIT_URL_preOpenAccount, paramNew.toString());
    		return Result.success("身份信息已提交");
    	}else{
    		 return Result.fail("请不要重复认证同一张身份证");
    	}
    }
    /**
	 * 文件上传
	 * @throws Exception 
	 */
	public String uploadfile(Map<String,String> queryParas,String imageInfo) throws IOException {
		// byte[] buffer = new BASE64Decoder().decodeBuffer(imageInfo);  
		 String fileName = "./"+UUID.randomUUID()+".jpg";
		 base64Utils.GenerateImage(imageInfo,fileName);
	     queryParas.put("sysName",  "aps");
	     queryParas.put("dataSources", "0");
		 File  file  =  new  File(fileName);//参数file	 
		 String  result  =  HttpKit.post(urlUPIC+Constants.PIC_URL_UPLOAD, queryParas, file);
		 if(file != null && file.exists()){
			 FileDownUtils.deleteDir(file);
		 }
		 return result;
	}
    /**
	 * 查询是否已通过身份认证
	 * @throws Exception 
	 */
	public Result isAuthentication(Model_004014 model) {
		Customer cus = new Customer();
		cus.setId(Long.parseLong(model.getCustomerId()));
		Customer customer = customerMapper.selectOneTable(cus);
		Date currDate = new Date(); 
		if(customer.getIdCardEndDate()!=null && customer.getIdCardEndDate().getTime()<currDate.getTime()){
			return Result.fail("账号未通过审核");
		}else if(StrUtils.isEmpty(customer.getIdCard())||!Constants.FLOW_STATUS_IDCARD.equals(customer.getFlowStatus())){
			return Result.fail("账户未认证");
	    }else{
	    	return Result.success("已通过认证");
	    }
	}
	/*
	 * 放弃注册
	 */
	public void giveUpRegister(String phone) {
//		String phone = model.getPhone();
		Customer customer = findByPhone(phone);
		customer.setStatus(Constants.DATA_UNVALID);
		customer.setMemo("用户放弃注册,已置为无效数据");
		customerMapper.updateByPrimaryKeyTable(customer);
	}
	/**
	 * 
	 * @TODO
	 * @param CardCode
	 * @return
	 * @throws Exception
	 * @author changj@yuminsoft.com
	 * @date2018年5月18日
	 */
   public static String getCarInfo(String CardCode)  
            throws Exception {  
//        Map<String, Object> map = new HashMap<String, Object>();  
//        String year = CardCode.substring(6).substring(0, 4);// 得到年份  
//        String yue = CardCode.substring(10).substring(0, 2);// 得到月份  
        // String day=CardCode.substring(12).substring(0,2);//得到日  
        String sex;  
        if (Integer.parseInt(CardCode.substring(16).substring(0, 1)) % 2 == 0) {// 判断性别  
            sex = "女";  
        } else {  
            sex = "男";  
        }  
//        Date date = new Date();// 得到当前的系统时间  
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");  
//        String fyear = format.format(date).substring(0, 4);// 当前年份  
//        String fyue = format.format(date).substring(5, 7);// 月份  
        // String fday=format.format(date).substring(8,10);  
//        int age = 0;  
//        if (Integer.parseInt(yue) <= Integer.parseInt(fyue)) { // 当前月份大于用户出身的月份表示已过生  
//            age = Integer.parseInt(fyear) - Integer.parseInt(year) + 1;  
//        } else {// 当前用户还没过生  
//            age = Integer.parseInt(fyear) - Integer.parseInt(year);  
//        }  
//        map.put("sex", sex);  
//        map.put("age", age);  
        return sex;  
    }
   
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public PageInfo<Customer> selectPageByColumn(PageInfo<Customer> pageInfo,Customer customer) {
		PageHelper.startPage(pageInfo.getPageNo(), pageInfo.getPageSize());
		if(!isAllFieldNull(customer)){
			customer.setStatus(Constants.DATA_VALID);
			Page<Customer> page = (Page)customerMapper.selectPageByColumn(customer);
	        return PageUtils.convertPage(page);
		}else{
			return PageUtils.convertPage(new Page(0,-1));
		}	
	}
   
  //判断该对象是否: 返回ture表示所有属性为null  返回false表示不是所有属性都是null
    private boolean isAllFieldNull(Object obj){
    	try {
    		Class stuCla = (Class) obj.getClass();// 得到类对象
            Field[] fs = stuCla.getDeclaredFields();//得到属性集合
            boolean flag = true;
            for (Field f : fs) {//遍历属性
                f.setAccessible(true); // 设置属性是可以访问的(私有的也可以)
                Object val = f.get(obj);// 得到此属性的值
                if(val!=null) {//只要有1个属性不为空,那么就不是所有的属性值都为空
                    flag = false;
                    break;
                }
            }
            return flag;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
    }
    
   public static void main(String sagr[]) throws Exception{
	  System.out.println(getCarInfo("510623198807145328")); 
   }
}