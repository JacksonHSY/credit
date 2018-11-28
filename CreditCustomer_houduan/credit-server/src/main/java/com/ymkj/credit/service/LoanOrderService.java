package com.ymkj.credit.service;



import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.HttpHostConnectException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ymkj.base.core.biz.api.message.Response;
import com.ymkj.credit.common.constants.Constants;
import com.ymkj.credit.common.entity.ApplyBaseInfo;
import com.ymkj.credit.common.entity.ApplyGroup;
import com.ymkj.credit.common.entity.ApplyGroupInfo;
import com.ymkj.credit.common.entity.Customer;
import com.ymkj.credit.common.entity.LoanOrder;
import com.ymkj.credit.common.ex.BussErrorCode;
import com.ymkj.credit.common.untils.DateUtil;
import com.ymkj.credit.common.untils.HttpUtils;
import com.ymkj.credit.config.ApplicationBean;
import com.ymkj.credit.mapper.ApplyGroupInfoMapper;
import com.ymkj.credit.mapper.ApplyGroupMapper;
import com.ymkj.credit.mapper.CustomerMapper;
import com.ymkj.credit.mapper.LoanOrderMapper;
import com.ymkj.credit.network.HttpClientUtil;
import com.ymkj.credit.service.dms.DmsService;
import com.ymkj.credit.service.redis.BasicRedisOpts;
import com.ymkj.credit.web.api.model.Result;
import com.ymkj.credit.web.api.model.base.Model_004009;
import com.ymkj.credit.web.api.model.base.Model_004010;
import com.ymkj.credit.web.api.model.base.Model_004011;
import com.ymkj.credit.web.api.model.base.Model_004016;
import com.ymkj.credit.web.api.model.base.Model_004017;
import com.ymkj.credit.web.api.model.base.Model_004018;
import com.ymkj.credit.web.api.model.base.Model_004019;
import com.ymkj.dms.api.common.base.SignResponseBO;
import com.ymkj.dms.api.vo.response.sign.ResBMSNameaAndIdAndphone;
import com.ymkj.pms.biz.api.service.IEmployeeExecuter;
import com.ymkj.pms.biz.api.vo.request.ReqEmployeeVO;
import com.ymkj.pms.biz.api.vo.response.ResEmployeeVO;
import com.ymkj.pms.biz.api.vo.response.ResRoleVO;
import com.ymkj.springside.modules.exception.BusinessException;
import com.ymkj.springside.modules.utils.StrUtils;
@Service
@Slf4j
public class LoanOrderService {
	
	@Autowired
	private CustomerService customerService;
	@Inject
	private BasicRedisOpts basicRedisOpts;
	@Autowired
	private LoanOrderMapper loanOrderMapper;
	@Autowired
	private CustomerMapper customerMapper;
	@Autowired
	private ZyService zyService;
    @Autowired
    private RuleService ruleService;
    @Autowired
    private IdCardService idCardService;
    @Autowired
    private DmsService dmsService;
    
    @Autowired
    private IEmployeeExecuter iEmployeeExecuter;
    
    @Autowired
    private ApplyBaseInfoService applybaseinfoservice;
    
    @Autowired
	ApplyGroupInfoMapper applyGroupInfoMapper;
    
    @Autowired
	ApplyGroupMapper applyGroupMapper;
    /**
     * 借新还旧h5地址
     */
    @Value("${jxhjH5Url}")
    private String jxhjH5Url;
    
    
    @Value("${channelCode}")
    private String channelCode;
    @Value("${urlUploadReport}")
	private String urlUploadReport;
	 /**
     * 借款进度查询
     *
     */
	public Result queryBorrowMoneySchedule(Model_004009 model) {
		Customer customer = new Customer();
		customer.setId(Long.parseLong(model.getCustomerId()));
		customer = customerMapper.selectOneTable(customer);
		if (null == customer) {
			return Result.fail("客户不存在");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cId", model.getCustomerId());
		PageHelper.startPage(model.getPageNo(), model.getPageSize());
		Page<LoanOrder> page = loanOrderMapper.selectLoanOrders(map);
		List<SignResponseBO> list = dmsService.getSign(customer.getIdCard());
		if (list != null) {
			Set<String> localloanNo = new HashSet<String>();
			for(LoanOrder order:page.getResult()){
				localloanNo.add(order.getOrdernum());
			}
			int i = 0;
			Set<String> setXs = new HashSet<String>();
			// 信审环节状态
			setXs.add("SQLR");
			setXs.add("LRFH");
			setXs.add("CSFP");
			setXs.add("XSCS");
			setXs.add("ZSFP");
			setXs.add("XSZS");
			setXs.add("QYFP");
			Set<String> setDms = new HashSet<String>();
			// 录单环节状态
			setDms.add("HTQY");
			setDms.add("HTQR");
			Calendar now = Calendar.getInstance();
			now.add(Calendar.DATE, 1);
			for (SignResponseBO sr : list) {
				if(localloanNo.contains(sr.getLoanNo())){
					continue;
				}
				if(sr.getContractType()!=null){
					basicRedisOpts.persist(sr.getLoanNo(), sr.getContractType()+"",now.getTime());
					log.info("redis缓存数据=="+sr.getLoanNo()+",值=="+basicRedisOpts.getSingleResult(sr.getLoanNo())); ;
				}
				LoanOrder loanOrder = new LoanOrder();
				loanOrder.setIsProject("1");
				log.info("录单返回办理中第("+i+1+")个申请单申请单编号=="+sr.getLoanNo()+",State=="+sr.getState()+",workflow编码=="+sr.getWorkflow()+",contractType=="+sr.getContractType()+",AuthType=="+sr.getAuthType());
				loanOrder.setOrdernum(sr.getLoanNo());
				loanOrder.setApplyDate(DateUtil.format(sr.getApplyDate(),"yyyy-MM-dd"));
				loanOrder.setProjectCode(sr.getApplyCode());
				loanOrder.setProjectName(sr.getApplyName());
				loanOrder.setDeadline(sr.getApplyTerm() + "");
				//办理未保存取本地额度
				if(sr.getApplyLimit()==null){
					LoanOrder	lo = queryByOrderNo(sr.getLoanNo());
					if(lo!=null){
						loanOrder.setApplylimit(lo.getApplylimit());
						loanOrder.setDeadline(lo.getDeadline());
						loanOrder.setProjectName(lo.getProjectName());
						loanOrder.setFlowstatus("办理中");
						loanOrder.setFlowstatusValue("1");
						loanOrder.setIsShowStatus(Constants.IS_SHOW_STATUS);
					}else{
						if (sr.getSignLimit() != null) {loanOrder.setApplylimit(sr.getSignLimit().toString());
						} else if (sr.getSignLimit() == null&& sr.getAuditLimit() == null) {
							loanOrder.setApplylimit(sr.getApplyLimit() + "");
						} else if (sr.getSignLimit() == null&& sr.getAuditLimit() != null) {
							loanOrder.setApplylimit(sr.getAuditLimit() + "");
						}
					}
				}else{
					if (sr.getSignLimit() != null) {loanOrder.setApplylimit(sr.getSignLimit().toString());
					
					} else if (sr.getSignLimit() == null&& sr.getAuditLimit() == null) {
						loanOrder.setApplylimit(sr.getApplyLimit() + "");
					} else if (sr.getSignLimit() == null&& sr.getAuditLimit() != null) {
						loanOrder.setApplylimit(sr.getAuditLimit() + "");
					}
					if (setXs.contains(sr.getState())) {// 办理中节点状态 在信审环节
						loanOrder.setBtnStatus(Constants.NOT_BUTTON);
						loanOrder.setFlowstatusValue(Constants.ORDER_STATES_BANLIZHONG);
						loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
						loanOrder.setIsShowStatus(Constants.IS_SHOW_STATUS);
						loanOrder.setFlowstatus("办理中");
					}else if (setDms.contains(sr.getState())) {// 录单环节
						if (StrUtils.isEmpty(sr.getWorkflow())) {
							sr.setWorkflow("0");
						}
						int flowValue = Integer.parseInt(sr.getWorkflow());// workflow编码
						if(flowValue < 1602){
							loanOrder.setBtnStatus(Constants.NOT_BUTTON);
							loanOrder.setFlowstatusValue(Constants.ORDER_STATES_BANLIZHONG);
							loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
							loanOrder.setIsShowStatus(Constants.IS_SHOW_STATUS);
							loanOrder.setFlowstatus("办理中");
						}else if (flowValue == 1602) {// 待保存银行卡StrUtils.isEmpty(loan.getBankInfoId())
							loanOrder.setBtnStatus(Constants.SET_BANK_CARD);
							loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
							loanOrder.setIsShowStatus(Constants.NOT_SHOW_STATUS);
							loanOrder.setFlowstatus("待签约");
							loanOrder.setFlowstatusValue(Constants.ORDER_STATES_XIANXIAQIANYUE);
						} else if (flowValue >= 1603 && flowValue <= 1611) {// 绑卡后到待签合同
							loanOrder.setBtnStatus(Constants.NOT_BUTTON);
							loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
							loanOrder.setFlowstatus("待线下签约");
							loanOrder.setIsShowStatus(Constants.IS_SHOW_STATUS);
							loanOrder.setFlowstatusValue(Constants.ORDER_STATES_XIANXIAQIANYUE);
						} else if (flowValue == 1612 || flowValue == 1613) {// 01612:签约电子合同
							loanOrder.setBtnStatus(Constants.ELEC_SIGN);
							loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
							loanOrder.setIsShowStatus(Constants.NOT_SHOW_STATUS);
							loanOrder.setFlowstatus("");
							loanOrder.setFlowstatusValue(Constants.ORDER_STATES_XIANXIAQIANYUE);
							loanOrder.setJumpUrl(jxhjH5Url+"/custom.html?loanNo="+ loanOrder.getOrdernum()+"&source=app&from=normal&idCard="+customer.getIdCard());
						} else if (flowValue >= 01614 || "HTQR".equals(sr.getState())) {// 01614:签订确认
//							loanOrder.setBtnStatus(Constants.NOT_BUTTON);
//							loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
//							loanOrder.setFlowstatus("待线下面签");
//							loanOrder.setIsShowStatus(Constants.IS_SHOW_STATUS);
//							loanOrder.setFlowstatusValue(Constants.ORDER_STATES_MIANQIAN);
							log.info("==================flowvalue:"+flowValue+"statue:"+sr.getState());
							if("HTQR".equals(sr.getState())){
								loanOrder.setBtnStatus(Constants.NOT_BUTTON);
								loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
								loanOrder.setFlowstatus("待线下面签");
								loanOrder.setIsShowStatus(Constants.IS_SHOW_STATUS);
								loanOrder.setFlowstatusValue(Constants.ORDER_STATES_MIANQIAN);
								log.info("flowStaute状态值："+loanOrder.getFlowstatus()+"对应的value："+loanOrder.getFlowstatusValue());
							}else if(flowValue== 1615){
								loanOrder.setBtnStatus(Constants.SET_BANK_CARD);
								loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
								loanOrder.setIsShowStatus(Constants.NOT_SHOW_STATUS);
								loanOrder.setFlowstatus("待签约");
								loanOrder.setFlowstatusValue(Constants.ORDER_STATES_XIANXIAQIANYUE);
							}
						}
//						else if (flowValue == 01615) {// 01615:放款审核
//							loanOrder.setBtnStatus(Constants.NOT_BUTTON);
//							loanOrder.setFlowstatusValue(Constants.ORDER_STATES_FANGKUANZHONG);
//							loanOrder.setIsShowStatus(Constants.IS_SHOW_STATUS);
//							loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
//							loanOrder.setFlowstatus("放款中");
//						}
					} else if (sr.getState() != null&& sr.getState().startsWith("FK")) {// FKSH:放款审核
						loanOrder.setBtnStatus(Constants.NOT_BUTTON);
						loanOrder.setFlowstatusValue(Constants.ORDER_STATES_FANGKUANZHONG);
						loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
						loanOrder.setFlowstatus("放款中");
					}
				}
				if(Constants.AuthType_PC.equals(sr.getAuthType())){//认证渠道为pc 
					loanOrder.setBtnStatus(Constants.NOT_BUTTON);// 钱钱借款进度不显示按钮
				}
				page.add(i, loanOrder);
				i++;
			}
		}
		map.clear();
		map.put("loanOrderList", page);
		map.put("total", page.size());
		return Result.success(map);
	}
    /**
     * 提交借款信息申请
     *
     */
	public Result createLoanOrder(Model_004010 model) {
		com.ymkj.rule.biz.api.message.Response ruleResponse = ruleService.validate(model.getName(), model.getIdNo());
		if(null == ruleResponse){
   		 return Result.fail("调用益博睿录单接口失败！返回null");
 	    }
 	    if(!ruleResponse.getRepCode().equals("000000")){
 	    	return Result.fail("账号未通过审核");
 	    }
 	    //查询受理中的借
 	    Map<String,String> map  = new HashMap<String,String>();
 	    map.put("cId", model.getCustomerId());
 	    List<LoanOrder> list=  loanOrderMapper.getTotalByCId(map);
 	    if(list!=null  && list.size()>0){ 
 	    	return Result.fail("已有一笔借款在办理中");
 	    }
		LoanOrder loanOrder = new LoanOrder();
		loanOrder.setId(UUID.randomUUID().toString());
		loanOrder.setOrdernum(createAppNo(new Date()));
		loanOrder.setCid(Long.parseLong(model.getCustomerId()));
		loanOrder.setCity(model.getCity());
		loanOrder.setCityName(model.getCityName());
		loanOrder.setProfession(model.getProfession());
		loanOrder.setBeforetaxsalary(model.getBeforeTaxSalary());;
		loanOrder.setApplylimit(model.getApplyLimit());
		loanOrder.setDeadline(model.getDeadline());
		loanOrder.setPurpose(model.getPurpose());
		loanOrder.setPurposeName(model.getPurposeName());
		loanOrder.setMonthliabilities(model.getMonthLiabilities());
		loanOrder.setOperatorcode(model.getOperatorCode());
		loanOrder.setProperty(model.getProperty());
		loanOrder.setCreatetime(new Date());
		loanOrder.setStatus(Constants.DATA_VALID);
		loanOrder.setFlowstatus(Constants.ORDER_STATES_SHOULIZHONG);
		
		
		loanOrderMapper.insertTable(loanOrder);	
		
		
		Map<String,String> query= new HashMap<String,String>();
		query.put("orderNum",loanOrder.getOrdernum());
		String batchNum= loanOrderMapper.getBatchNumByOrderNum(query);//身份证信息表的批次号
		Map<String,String> queryparams= new HashMap<String,String>();
		queryparams.put("appNo", batchNum);
		queryparams.put("newAppNo",loanOrder.getOrdernum() );
		queryparams.put("operator", "zdqqSystem");
		queryparams.put("jobNumber", "zdqqSystem");
		idCardService.moveFile(queryparams);
		
		return Result.success("借款申请已经提交");
	}
	/**
     * 业务员信息查询
     *
     */
   public Result queryOperator(Model_004011 model) {
	   JSONObject object = new JSONObject();
      //获取客户经理
	   ReqEmployeeVO managerParam = new ReqEmployeeVO();
	   managerParam.setUsercode(model.getOperatorCode());
	   managerParam.setSysCode(Constants.ZDQQ_SYS_CODE);
	   Response<List<ResRoleVO>> res = iEmployeeExecuter.findRolesByAccount(managerParam);
	   List<ResRoleVO> resrolevo =  res.getData();
	   boolean flag = false;
	   if(resrolevo != null && !resrolevo.isEmpty()){
		   for (ResRoleVO rrv : resrolevo) {
			   if(rrv.getCode().equals("customerManager")){
				   ReqEmployeeVO r = new ReqEmployeeVO();
				   r.setUsercode(model.getOperatorCode());
				   r.setSysCode(Constants.ZDQQ_SYS_CODE);
				   Response<ResEmployeeVO> emp = iEmployeeExecuter.findByAccount(r);
				   if(emp.isSuccess()){
					   ResEmployeeVO employee = emp.getData();
					   object.put("operatorName", employee.getName());
					   flag = true;
					   break;
				   	}
			   }
		   }
	   }
	   if(flag)
		   return Result.success(object);
	   else
		   return Result.fail("未找到对应的客户经理");
   }
   /**
    * 费率计算
    *
    */
	 public Result rateCalculation(Model_004016 model) {
		 Map<String,String> map = new HashMap<String,String>();
		 map.put("money", model.getApplyLmt());
		 map.put("time", model.getApplyTerm());
		 map.put("fundsSources", "00016");
		 map.put("custStarLevel", "4");
		 map.put("loanType", model.getProductCd());
		 map.put("isRatePreferLoan", "N");
		 JSONObject jsonObject=JSONObject.fromObject(map);
		 com.alibaba.fastjson.JSONObject result = HttpClientUtil.sendHttpPostForCore(jsonObject.toString());
		 if(null!=result&&result.containsKey("repaymentDetail")){
			 Map<String,Object> data = new HashMap<String,Object>();
			 JSONArray trialBeforeCreditListData = new JSONArray(); 
			 com.alibaba.fastjson.JSONArray trialBeforeCreditListDatas = com.alibaba.fastjson.JSONArray.parseArray(result.get("repaymentDetail").toString());
			 for(int i=0;i<trialBeforeCreditListDatas.size();i++){
				 JSONObject object = new JSONObject();
				 com.alibaba.fastjson.JSONObject ob  =  com.alibaba.fastjson.JSONObject.parseObject(trialBeforeCreditListDatas.get(i).toString());
				 if(ob.containsKey("currentTerm")){
					 String currentTerm = String.valueOf(ob.get("currentTerm"));
					 object.put("applyTermNo", currentTerm);
				 }
				 if(ob.containsKey("repaymentAll")){
					 String repaymentAll = String.valueOf(ob.get("repaymentAll"));
					 object.put("paymentAllMoney", repaymentAll);
				 }
				 if(ob.containsKey("returneterm")){
					 String returneterm = String.valueOf(ob.get("returneterm"));
					 object.put("paymentMoney", returneterm);
				 }
				 if(ob.containsKey("returnDate")){
					 String returnDate = String.valueOf(ob.get("returnDate"));
					 object.put("paymentDate", returnDate);
				 }
				 trialBeforeCreditListData.add(object);
			 }
			 data.put("trialBeforeCreditListData", trialBeforeCreditListData);
			 return Result.success(data);
		 }
		
		return Result.fail("费率计算接口异常");
   }
	/**
	  * 申请渠道
	  *
	  */
	public Result applicationChannel(Model_004017 model) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("productCode", model.getProductCd());
		map.put("applyLmt", model.getApplyLmt());
		map.put("applyTerm", model.getApplyTerm());
		String resultString = zyService.getApplicationChannel(map);
		com.alibaba.fastjson.JSONObject resultJson = com.alibaba.fastjson.JSONObject.parseObject(resultString);
		if(null != resultJson){
			if("0000".equals(resultJson.get("code"))){
				resultString = com.alibaba.fastjson.JSONObject.toJSONString(resultJson.get("msgEx"));
				com.alibaba.fastjson.JSONObject object = com.alibaba.fastjson.JSONObject.parseObject(resultString);
				if(null != object){
					resultString = com.alibaba.fastjson.JSONObject.toJSONString(object.get("infos"));
					object = com.alibaba.fastjson.JSONObject.parseObject(resultString);
					if (null != object) {
						JSONArray channelList = JSONArray.fromObject(object.get("channelList"));
						Map<String,Object> data = new HashMap<String,Object>();
						data.put("channelList", channelList);
						return Result.success(data);
					}
				}
			}else{
				resultString = com.alibaba.fastjson.JSONObject.toJSONString(resultJson.get("msgEx"));
				com.alibaba.fastjson.JSONObject object = com.alibaba.fastjson.JSONObject.parseObject(resultString);
				if(null != object){
					resultString = com.alibaba.fastjson.JSONObject.toJSONString(object.get("respDesc"));
					return Result.fail(resultString);
				}
			}
		}else{
			return Result.fail("展业申请渠道接口异常");
		}
		return Result.fail("申请渠道接口异常");
	}
	/**
	  * 征信报告上传
	  *
	  */
	public Result uploadCreditReport(Model_004018 model) {

		String name = model.getName();
		String idCard = model.getIdCard();
		String htmlContent = model.getHtmlContent();
		Map<String,String> map = new HashMap<String,String>();
		map.put("name", name);
		map.put("idCard", idCard);
		map.put("htmlContent", htmlContent);
		JSONObject jsonObject=JSONObject.fromObject(map);
		com.alibaba.fastjson.JSONObject object = HttpClientUtil.sendHttpPostSaveReport(jsonObject.toString());
		log.info("征信报告录入.返回结果" + object.toJSONString());

		if (object.containsKey("resCode")) {
			if ("000000".equals(object.getString("resCode"))) {
				String reportId = object.getString("reportId");
				//更新到网关本地
				JSONObject obj = new JSONObject();
				obj.put("hasCreditReport", "1");
				obj.put("reportId", reportId);
				ApplyBaseInfo apl = new ApplyBaseInfo();
				apl.setFieldObjValue(obj.toString());
				apl.setLoanNo(model.getAppNo());
				apl.setFieldKeyParent(Constants.NODEINFO);
				apl.setFieldKey(Constants.CREDITACCOUNTINFO);
				apl.setState(Constants.DATA_VALID);
				//先查询，有就更新，没有就新建
				ApplyBaseInfo abi = applybaseinfoservice.selectByappNo(apl);
				if(abi!=null){
					applybaseinfoservice.updateByappNo(apl);
				}else{
					applybaseinfoservice.insertByLoanAndKey(apl);
				}
				//更新第四步信用账户信息绿色状态
				if(!StringUtils.isEmpty(reportId)){
					ApplyGroup group = new ApplyGroup();
					ApplyGroupInfo groupInfo = new ApplyGroupInfo();
					group.setGroupCode(Constants.CREDITACCOUNTINFO);
					ApplyGroup cord = applyGroupMapper.selectByGroupCode(group);
					groupInfo.setApplyGroupId(cord.getId());
					groupInfo.setState("1");
					groupInfo.setIsEdit("1");
					groupInfo.setIsRollback("0");
					groupInfo.setLoanNo(model.getAppNo());
					applyGroupInfoMapper.updateByGroupId(groupInfo);
				}
				return Result.success(object);
			} else {
				return Result.fail(object.getString("resMsg"));
			}
		}
		return Result.success();
	
	
	}
	 /**
	  * 
	  * @param model
	  * @param status true.更新，false不更新
	  * @return
	  */
	public Result queryCreditReport(Model_004019 model,boolean status) {

		String name = model.getName();
		String idCard = model.getIdCard();
		JSONObject map = new JSONObject();
		map.put("customerName", name);
		map.put("customerIdCard", idCard);
		map.put("appNo", model.getAppNo());
		map.put("sources", "ZDQQ");
		map.put("timestamp", String.valueOf(System.currentTimeMillis()));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		map.put("queryDate", sdf.format(new Date()));
		Map<String,Object> maps = new HashMap<String,Object>();
		maps.put("param", map);
		log.info("征信报告查询入参" + maps.toString());
		JSONObject obj = new JSONObject();
		String rusult = "";
		try {
			rusult = HttpUtils.doPost(urlUploadReport+Constants.SELECT_CREDIT_REPORT, maps);
			log.info("征信报告查询出参" + rusult);
		} catch (HttpHostConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(StringUtils.isNotEmpty(rusult)){
			JSONObject js = JSONObject.fromObject(rusult);
			if(js.containsKey("reportId")){
				js.put("reportId", String.valueOf(js.get("reportId")));
				//更新本地各人信用信息
				JSONObject jbk = new JSONObject();
				ApplyBaseInfo base = new ApplyBaseInfo();
				base.setLoanNo(model.getAppNo());
				base.setFieldKey(Constants.CREDITACCOUNTINFO);
				base.setFieldKeyParent(Constants.NODEINFO);
				base.setState(Constants.DATA_VALID);
				if(StringUtils.isEmpty(String.valueOf(js.get("reportId")))){
					jbk.put("hasCreditReport", "0");
				}else{
					jbk.put("hasCreditReport", "1");
				}
				jbk.put("reportId", String.valueOf(js.get("reportId")));
				base.setFieldObjValue(jbk.toString());
				//先查询，有就更新，没有就新建
				ApplyBaseInfo abi = applybaseinfoservice.selectByappNo(base);
				if(abi!=null){
					applybaseinfoservice.updateByappNo(base);
				}else{
					applybaseinfoservice.insertByLoanAndKey(base);
				}
				//更新节点状态
				ApplyGroup group = new ApplyGroup();
				group.setGroupCode(Constants.CREDITACCOUNTINFO);
				ApplyGroup cord = applyGroupMapper.selectByGroupCode(group);
				//查询节点信息
				ApplyGroupInfo groupInfo = new ApplyGroupInfo();
				groupInfo.setLoanNo(model.getAppNo());
				groupInfo.setApplyGroupId(cord.getId());
				groupInfo  = applyGroupInfoMapper.queryById(groupInfo);
				
				//更新第四步信用账户信息绿色状态
				if(!StringUtils.isEmpty(String.valueOf(js.get("reportId"))) && status){
					groupInfo.setState("1");
					groupInfo.setIsEdit("1");
					groupInfo.setIsRollback("0");
					groupInfo.setLoanNo(model.getAppNo());
					applyGroupInfoMapper.updateByGroupId(groupInfo);
				}else if(status){//更新第四步信用账户信息为灰色状态
					groupInfo.setState("0");
					groupInfo.setIsEdit("1");
					groupInfo.setIsRollback("0");
					groupInfo.setLoanNo(model.getAppNo());
					applyGroupInfoMapper.updateByGroupId(groupInfo);
				}
			}
			obj.put("rusult", js);
		}
		return Result.success(obj);
	
	
	}
	/**
	   * 生成  借款编号
	   * 8位创建日期+6位  16进制随机生成数
	   * @param date
	   * @return
	   */
	  public static String createAppNo(Date date){
	    String dateStr = DateUtil.format(date,DateUtil.DATAFORMAT_YYYYMMDD);
	    return dateStr + randomHexString(6);
	  } 
	  /** 
	     * 获取16进制随机数 
	     * @param len 长度
	     * @return 
	     * @throws CoderException 
	     */  
    public static String randomHexString(int len)  {  
      
        try {  
            StringBuffer result = new StringBuffer();  
            for(int i=0;i<len;i++) {  
                result.append(Integer.toHexString(new Random().nextInt(16)));  
            }  
            return result.toString().toUpperCase();  
        } catch (Exception e) {  
            return null;
        }  
          
    }
  /*
   * 根据借款编号获取借款信息
   */
   public LoanOrder queryByOrderNo(String loanNo){
	   	Map<String,String> map = new HashMap<String,String>();
	   	map.put("orderNum", loanNo);
	   	LoanOrder loanOrder = loanOrderMapper.queryByOrderNo(map);
	    return loanOrder;
   }
   /**
    * 
    * @TODO
    * @param order
    * void
    * @author changj@yuminsoft.com
    * @date2018年3月14日
    */
   public void updateLoanOrder(LoanOrder order){
		order.setUpdatetime(new Date());
		int count = loanOrderMapper.updateLoanOrder(order);
		if(count!=1){
			 throw new BusinessException(BussErrorCode.ERROR_CODE_0103.getErrorcode(),"状态更新失败");
		}
   }
   
   public static void main(String[] agr){
	   String code = "01603";
	   System.out.println(Integer.parseInt(code));
   }
}
