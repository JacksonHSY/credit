package com.ymkj.credit.service.tpp;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ymkj.cms.biz.api.service.master.IBMSBankExecuter;
import com.ymkj.cms.biz.api.vo.request.apply.ReqBMSChannelBankVO;
import com.ymkj.cms.biz.api.vo.request.master.ReqBMSBankVO;
import com.ymkj.cms.biz.api.vo.response.master.ResBMSBankVO;
import com.ymkj.cms.biz.api.vo.response.master.ResBMSChannelCardVO;
import com.ymkj.cms.biz.api.vo.response.master.ResListVO;
import com.ymkj.credit.common.constants.Constants;
import com.ymkj.credit.common.entity.BankChannel;
import com.ymkj.credit.common.entity.Customer;
import com.ymkj.credit.common.ex.BussErrorCode;




import com.ymkj.credit.common.untils.NumberUtil;
import com.ymkj.credit.mapper.BankChannelMapper;
import com.ymkj.credit.network.HttpClientUtil;
import com.ymkj.credit.service.BankInfoService;
import com.ymkj.credit.service.CustomerService;
import com.ymkj.credit.service.redis.BasicRedisOpts;
import com.ymkj.credit.web.api.model.Result;
import com.ymkj.credit.web.api.model.base.Model_001002;
import com.ymkj.credit.web.api.model.base.Model_002016;
import com.ymkj.credit.web.api.model.base.Model_002017;
import com.ymkj.credit.web.api.model.base.Model_002028;
import com.ymkj.springside.modules.exception.BusinessException;
import com.ymkj.springside.modules.utils.StrUtils;
import com.zendaimoney.thirdpp.common.enums.BizType;
import com.zendaimoney.thirdpp.common.enums.ThirdType;
import com.zendaimoney.thirdpp.common.vo.AttachmentResponse;
import com.zendaimoney.thirdpp.trade.pub.service.IBaseService;
import com.zendaimoney.thirdpp.trade.pub.service.IBrokerTradeService;
import com.zendaimoney.thirdpp.trade.pub.vo.req.biz.RequestVo;
import com.zendaimoney.thirdpp.trade.pub.vo.req.biz.SignMsgReqVo;
import com.zendaimoney.thirdpp.trade.pub.vo.req.biz.SignReqVo;
import com.zendaimoney.thirdpp.trade.pub.vo.req.query.BankQueryReqVo;
import com.zendaimoney.thirdpp.trade.pub.vo.req.query.BaseQueryRequestVo;
import com.zendaimoney.thirdpp.trade.pub.vo.resp.query.BankResponseVo;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**	
 * tpp接口
 * 
 * @author changj@yuminsoft.com
 * @date2018年3月2日
 * @version 1.0
 */
@Slf4j
@Service
public class BrokerTradeService{

	@Autowired
	private IBrokerTradeService iBrokerTradeService;

	@Autowired
	private IBaseService iBaseService;
	
	 @Autowired
	 private CustomerService customerService;
	 
	@Inject
	private BasicRedisOpts basicRedisOpts;
	
	@Autowired
	private IBMSBankExecuter iBMSBankExecuter;
	
	@Autowired
	private BankInfoService bankInfoService;
	
	@Autowired
	private BankChannelMapper bankChannelMapper;
	
	/**
	 *	协议支付签约短信触发接口
	 * @TODO
	 * @param bankCode
	 * @param bankName
	 * @param accountNo
	 * @param accountName
	 * @param idNum
	 * @param phone
	 * @return
	 * String
	 * @author changj@yuminsoft.com
	 * @date2018年4月13日
	 */
	public Result signMessage(Model_002016 model) {
	//	String bankCode,String bankName,String accountNo,String accountName,String idNum,String phone
		log.info("调用tpp协议支付签约短信触发接口入参:bankCode:"+model.getBankCode()+",bankName:"+model.getBankName()+",accountNo:"+model.getAccount()+",accountName:"+model.getAccountName()+",idNum:"+model.getIdCard()+",phone:"+model.getReservedMobile());
		
		Result re = bankInfoService.getAllCardBound(model.getIdCard());
		if(!re.getSuccess()){
			return re;
		}
		//获取渠道
		String str = (String) re.getData();
		if(StringUtils.isEmpty(str)){
			return Result.fail("未获取渠道");
		}
		com.zendaimoney.thirdpp.common.vo.Response res = syncSignMessge(model,str);
		if (null == res) {
			throw new BusinessException(BussErrorCode.ERROR_CODE_9001.getErrorcode(),BussErrorCode.ERROR_CODE_9001.getErrordesc());
		}
		log.info("调用tpp协议支付签约短信触发接口返回=="+JSONObject.toJSONString(res));
		if(!"000000".equals(res.getCode())){
			List<ResBMSChannelCardVO> list = bankInfoService.getResBMSChannelCardVO();
			if(list != null && !list.isEmpty()){
				for (ResBMSChannelCardVO rv : list) {
					str = rv.getAisleName();
					//当前渠道不支持进行下一个渠道绑定
					res = syncSignMessge(model,rv.getAisleName());
					if (null == res) {
						throw new BusinessException(BussErrorCode.ERROR_CODE_9001.getErrorcode(),BussErrorCode.ERROR_CODE_9001.getErrordesc());
					}
					if("000000".equals(res.getCode())){
						break;
					}
				}
			}
			//return  Result.fail(res.getMsg());
		}
		
		if(!"000000".equals(res.getCode())){
			return  Result.fail(res.getMsg());
		}
		
		//获取验证码参数保存数据库
		BankChannel bc = new BankChannel();
		bc.setIdCard(model.getIdCard());
		bc.setBankCode(model.getBankCode());
		bc.setFlowId(res.getFlowId());
		bc.setPaySysNo(com.ymkj.credit.common.constants.ThirdType.getCode(str));
		bc.setChannelName(str);
		int i = bankChannelMapper.insertTable(bc);
		if(i <= 0){
			return  Result.fail("验证码发送失败");
		}
		
		JSONObject retJson = new JSONObject();
		retJson.put("flowId", res.getFlowId());
		retJson.put("bankCode", res.getBankCode());
		retJson.put("bankName", res.getBankName());
		
		return  Result.success(retJson);
	}
	
	private  com.zendaimoney.thirdpp.common.vo.Response syncSignMessge(Model_002016 model,String str){
		SignMsgReqVo signMsgReqVo = new SignMsgReqVo();
		signMsgReqVo.setPaySysNo(com.ymkj.credit.common.constants.ThirdType.getCode(str)); // 通联支付2/宝付
		signMsgReqVo.setBankCode(model.getBankCode()); // 银行编码
		signMsgReqVo.setBankName(model.getBankName()); // 银行名称
		signMsgReqVo.setAccountType("1"); // 银行卡类型 1.借记卡，2信用卡
		signMsgReqVo.setAccountNo(model.getAccount());
		signMsgReqVo.setAccountName(model.getAccountName());
		signMsgReqVo.setAccountProp("C");//账号熟悉  默认私人C-私人  P-公司
		signMsgReqVo.setIdType("0");//开户证件类型0-身份证
		signMsgReqVo.setIdNum(model.getIdCard());
		signMsgReqVo.setTel(model.getReservedMobile());
//		signMsgReqVo.setMerrem("这是一个保留信息");
//		signMsgReqVo.setRemark("备注");
		RequestVo vo = new RequestVo();
		vo.getList().add(signMsgReqVo);
		vo.setBizSysNo(Constants.BIZ_SYS_NO);   // todo 签约系统功能号
	    vo.setBizTypeNo(BizType.AGREEMENT_SIGN.getCode());  // 业务类型：签约
	    vo.setInfoCategoryCode(Constants.INFOCATEGORYCODE);//下个版本10085
		return iBrokerTradeService.syncSignMessage(vo);
	}
	/**
	 * tpp协议支付签约接口
	 * @TODO
	 * @param srcFlowId
	 * @param verCode
	 * @return
	 * String
	 * @author changj@yuminsoft.com
	 * @date2018年4月13日
	 */
	public Result sign(Model_002017 model ){
		log.info("调用tpp签约接口入参:srcFlowId:"+model.getSrcFlowId()+",verCode:"+model.getVerCode());
		String agrmNo =  basicRedisOpts.getSingleResult(model.getSrcFlowId());
		//防止重复提交
		JSONObject retJson = new JSONObject();
		if (StrUtils.isNotBlank(agrmNo)) {
			retJson.put("flowId", model.getSrcFlowId());
			retJson.put("agrmNo", agrmNo);
			return  Result.success(retJson);
		}
		SignReqVo signReqVo = new SignReqVo();
		signReqVo.setPaySysNo(ThirdType.ALLINPAY2.getCode()); // 通联支付2
		signReqVo.setSrcFlowId(model.getSrcFlowId());
		signReqVo.setVerCode(model.getVerCode());
		RequestVo vo = new RequestVo();
		vo.getList().add(signReqVo);
		vo.setBizSysNo(Constants.BIZ_SYS_NO);   // todo 签约系统功能号
	    vo.setBizTypeNo(BizType.AGREEMENT_SIGN.getCode());  // 业务类型：签约
	    vo.setInfoCategoryCode(Constants.INFOCATEGORYCODE);//下个版本10085
		com.zendaimoney.thirdpp.common.vo.Response res = iBrokerTradeService
				.syncSign(vo);
		if (null == res) {
			throw new BusinessException(
					BussErrorCode.ERROR_CODE_9003.getErrorcode(),
					BussErrorCode.ERROR_CODE_9003.getErrordesc());
		}
		log.info("调用tpp签约接口返回=="+JSONObject.toJSONString(res));
		if(!"000000".equals(res.getCode())){
			return  Result.fail(res.getMsg());
		}else{
			//1日内
			Calendar now = Calendar.getInstance();
			now.add(Calendar.DAY_OF_MONTH, 1);
			basicRedisOpts.persist(res.getFlowId(), res.getAgrmNo(),now.getTime());
		}
		retJson.put("flowId", res.getFlowId());
		retJson.put("agrmNo", res.getAgrmNo());
		return  Result.success(retJson);
	}
	/**
	 * 银行卡列表
	 * @TODO
	 * @param model
	 * @return
	 * Result
	 * @author changj@yuminsoft.com
	 * @date2018年4月24日
	 */
	@SuppressWarnings("rawtypes")
	public Result getSupportedBanks(Model_001002 model ){
		log.info("调用tpp支持银行卡列表接口");
		//new 
		AttachmentResponse response = getSupportedBanks();
		if(!"000000".equals(response.getCode())){
			return  Result.fail(response.getMsg());
		}else{
			return  Result.success(response.getAttachment());
		}
	}
	public AttachmentResponse getSupportedBanks(){
		//new 
		/*BankQueryReqVo vo = new BankQueryReqVo();
		vo.setBizSysNo(Constants.BIZ_SYS_NO);   // todo 签约系统功能号
		vo.setThirdPartyCode(ThirdType.ALLINPAY2.getCode()); // 通联支付2
		AttachmentResponse response = iBaseService.getSupportedBanksWithAgrm(vo);
		log.info("调用tpp支持银行卡列表接口返回=="+JSONObject.toJSONString(response));
		if (null == response) {
			throw new BusinessException(
					BussErrorCode.ERROR_CODE_9005.getErrorcode(),
					BussErrorCode.ERROR_CODE_9005.getErrordesc());
		}*/
		AttachmentResponse response = new AttachmentResponse<>();
		
		ReqBMSBankVO bank = new ReqBMSBankVO();
		bank.setChannelCode(Constants.ZDQQ_CHANNEL_CODE);
		bank.setSysCode(Constants.ZDQQ_SYS_CODE);
		ResListVO<ResBMSBankVO> bankList = iBMSBankExecuter.getBankByChannelCode(bank);
		if(bankList == null){
			throw new BusinessException(
					BussErrorCode.ERROR_CODE_9005.getErrorcode(),
					BussErrorCode.ERROR_CODE_9005.getErrordesc());
		}
		if(!Constants.RESP_CODE_SUCCESS.equals(bankList.getRepCode())){
			log.info("从平台获取支持银行列表返回结果："+bankList.getRepCode()+"|"+bankList.getRepMsg());
		}
		
		List<ResBMSBankVO> list = bankList.getCollections();
		
		List<BankResponseVo> banklist = new ArrayList<>();
		if(list != null && !list.isEmpty()){
			for (ResBMSBankVO ro : list) {
				BankResponseVo vo = new BankResponseVo();
				vo.setBankCode(ro.getBankCode());
				vo.setBankName(ro.getName());
				banklist.add(vo);
			}
		}
		response.setCode(bankList.getRepCode());
		response.setMsg(bankList.getRepMsg());
		response.setAttachment(banklist);
		
		return response;
	}
	public Result sign4H5(Model_002028 model ){
		log.info("调用tpp签约接口入参:srcFlowId:"+model.getSrcFlowId()+",verCode:"+model.getVerCode());
		String agrmNo =  basicRedisOpts.getSingleResult(model.getSrcFlowId());
		//防止重复提交
		JSONObject retJson = new JSONObject();
		if (StrUtils.isNotBlank(agrmNo)) {
			retJson.put("flowId", model.getSrcFlowId());
			retJson.put("agrmNo", agrmNo);
			return  Result.success(retJson);
		}
		//获取渠道信息
		BankChannel bc = new BankChannel();
		bc.setIdCard(model.getIdCard());
		bc.setBankCode(model.getBankCode());
		bc.setFlowId(model.getSrcFlowId());
		bc = bankChannelMapper.selectOneTable(bc);
		
		SignReqVo signReqVo = new SignReqVo();
		signReqVo.setPaySysNo(bc.getPaySysNo()); // 通联支付2
		signReqVo.setSrcFlowId(model.getSrcFlowId());
		signReqVo.setVerCode(model.getVerCode());
		RequestVo vo = new RequestVo();
		vo.getList().add(signReqVo);
		vo.setBizSysNo(Constants.BIZ_SYS_NO);   // todo 签约系统功能号
	    vo.setBizTypeNo(BizType.AGREEMENT_SIGN.getCode());  // 业务类型：签约
	    vo.setInfoCategoryCode(Constants.INFOCATEGORYCODE);//下个版本10085
		com.zendaimoney.thirdpp.common.vo.Response res = iBrokerTradeService
				.syncSign(vo);
		if (null == res) {
			throw new BusinessException(
					BussErrorCode.ERROR_CODE_9003.getErrorcode(),
					BussErrorCode.ERROR_CODE_9003.getErrordesc());
		}
		log.info("调用tpp签约接口返回=="+JSONObject.toJSONString(res));
		if(!"000000".equals(res.getCode())){
			return  Result.fail(res.getMsg());
		}else{
			//1日内
			Calendar now = Calendar.getInstance();
			now.add(Calendar.DAY_OF_MONTH, 1);
			basicRedisOpts.persist(res.getFlowId(), res.getAgrmNo(),now.getTime());
			JSONObject param = new JSONObject();
			param.put("idNum", model.getIdCard());
			param.put("bankCode", model.getBankCode());
			param.put("account", model.getAccount());
			param.put("bankName", model.getBankName());
			param.put("bankBranchName", model.getBankBranchName());
			param.put("mobile", model.getReservedMobile());
			param.put("checkCard", Constants.IS_CHECKED_CARD);//新增卡 已绑定
			param.put("bindCardChannel", bc.getChannelName());//通联2
			param.put("tacheCode", "0001");
			if(StrUtils.isNotEmpty(model.getCardImgStr())){
				Map<String, String> queryParas = new HashMap<>();
				String batchNum = NumberUtil.getNumberForPK();
				queryParas.put("appNo", batchNum);
				queryParas.put("operator", model.getIdCard());
				queryParas.put("jobNumber", model.getIdCard());
				queryParas.put("code", "S5");// 银行卡目录
				queryParas.put("nodeKey",  "contractAward");//合同签约环节
				String result = "";
				try {
					result = customerService.uploadfile(queryParas,
							model.getCardImgStr());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}// 上传银行卡图片
				net.sf.json.JSONObject json = net.sf.json.JSONObject.fromObject(result);
				if (json.getBoolean("isOk")) {
					com.alibaba.fastjson.JSONObject resultJson = com.alibaba.fastjson.JSONObject
							.parseObject(result);
					result = com.alibaba.fastjson.JSONObject
							.toJSONString(resultJson.get("result"));
					com.alibaba.fastjson.JSONObject object = com.alibaba.fastjson.JSONObject
							.parseObject(result);
					param.put("cardUrl", "http:" + object.getString("thumUrl"));
				}
				//预开户
				JSONObject paramNew = new JSONObject();
				Customer customer = customerService.queryByIdCard(model.getIdCard());
				if(customer==null){
					return Result.fail("接口异常,身份证号码不存在");
				}
				paramNew.put("idnum", model.getIdCard());
				paramNew.put("name", customer.getCustomerName());
				paramNew.put("mphone", customer.getPhone());
				try {
					paramNew.put("sex", customerService.getCarInfo(model.getIdCard()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				HttpClientUtil.sendHttpPostForCredit4BindBankCard(Constants.CREDIT_URL_preOpenAccount, paramNew.toString());
			}
			net.sf.json.JSONObject loanJson = HttpClientUtil.sendHttpPostForCredit4BindBankCard(
			Constants.CREDIT_URL_BINDBANKCARD, param.toString());
			if (("null").equals(loanJson.toString())) {
				now.add(Calendar.DAY_OF_MONTH, 1);
				basicRedisOpts.persist(model.getAccount(), "true",now.getTime());
				return Result.success(loanJson);
			}
		}
		return Result.fail("接口异常,响应为空");
	}
}