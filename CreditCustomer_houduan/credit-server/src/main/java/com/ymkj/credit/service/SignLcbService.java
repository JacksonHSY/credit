package com.ymkj.credit.service;

import java.io.File;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ymkj.base.core.biz.api.message.Response;
import com.ymkj.cms.biz.api.service.master.IBMSContractTemplateExecuter;
import com.ymkj.cms.biz.api.vo.request.master.ReqBMSContractTemplateVO;
import com.ymkj.cms.biz.api.vo.response.master.ResBMSContractTemplateVO;
import com.ymkj.credit.common.constants.BankCode;
import com.ymkj.credit.common.constants.Constants;
import com.ymkj.credit.common.constants.ContractEnum;
import com.ymkj.credit.common.constants.ContractInfoEnum;
import com.ymkj.credit.common.constants.ContractType;
import com.ymkj.credit.common.entity.ApplyContractInfo;
import com.ymkj.credit.common.entity.ApplyLoanInfo;
import com.ymkj.credit.common.entity.Customer;
import com.ymkj.credit.common.untils.Html2PDFUtil;
import com.ymkj.credit.common.untils.HttpKit;
import com.ymkj.credit.common.untils.HttpUtils;
import com.ymkj.credit.common.untils.MD5;
import com.ymkj.credit.common.untils.PropertiesReader;
import com.ymkj.credit.common.util.FileDownUtils;
import com.ymkj.credit.mapper.ApplyContractInfoMapper;
import com.ymkj.credit.mapper.BankCodeMapper;
import com.ymkj.credit.network.HttpClientUtil;
import com.ymkj.credit.service.dms.DmsService;
import com.ymkj.credit.web.api.model.Result;
import com.ymkj.credit.web.api.model.base.Model_006001;
import com.ymkj.credit.web.api.model.base.Model_006002;
import com.ymkj.credit.web.api.model.base.Model_006003;
import com.ymkj.credit.web.api.model.base.Model_006004;
import com.ymkj.credit.web.api.model.base.Model_006005;
import com.ymkj.credit.web.api.model.base.Model_006006;
import com.ymkj.credit.web.api.model.base.Model_006007;
import com.ymkj.credit.web.api.model.base.Model_006008;
import com.ymkj.credit.web.api.model.base.Model_006010;
import com.ymkj.credit.web.api.model.base.Model_006011;
import com.ymkj.dms.api.common.base.BankCardBO;

import lombok.extern.log4j.Log4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * h5签约捞财宝
 * @author YM10156
 *
 */
@Service
@Log4j
public class SignLcbService {
	
	@Value("${lcb.key}")
	private String creditReqKey;
	@Value("${lcbwg.url}")
	private String lcbwgUrl;
	@Value("${urlUPIC}")
	private String urlUPIC;
	@Value("${urlZs}")
	private String urlZs;
	@Value("${urlH5.contract.image}")
	private String urlH5;
	@Value("${url.hx.address}")
	private String urlHx;
	@Value("${contract.num}")
	private String num;//获取次数
	@Value("${bank.card.url}")
	private String backCardUrl;
	
	@Autowired
	private DmsService dmsService;
	
	@Autowired
	private ApplyContractInfoMapper applyContractInfoMapper;
	@Autowired
	private IBMSContractTemplateExecuter iBMSContractTemplateExecuter;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private ApplyInfoService applyInfoService;
	@Autowired
	private BankCodeMapper bankCodeMapper;
	@Autowired
	ApplyLoanInfoService applyLoanInfoService;

	
	/**
	 * 根据银行卡号码获取银行卡归属行
	 * @param model
	 * @return
	 */
	public Result getBankMessage(Model_006011 model){
		log.info("获取银行卡信息入参为："+ model.getBankCard());
		JSONObject jst = new JSONObject();
		String bankCode = "",bankName = "";
		if(model != null && StringUtils.isNotBlank(model.getBankCard())){
			HttpGet httpGet = new HttpGet(backCardUrl+"?_input_charset=utf-8&cardBinCheck=true&cardNo="+model.getBankCard());// 创建httpget
			String  result  = HttpClientUtil.sendHttpGet(httpGet);
			JSONObject object = JSONObject.fromObject(result);
			log.info("根据银行卡号码获取银行卡归属行出参："+object);
			if(object.containsKey("stat") && object.get("stat").equals("ok")){
				if(object.containsKey("bank") && StringUtils.isNotBlank(object.getString("bank")) && object.containsKey("cardType") && StringUtils.isNotBlank(object.getString("cardType"))){
					com.ymkj.credit.common.entity.BankCode bank = new com.ymkj.credit.common.entity.BankCode();
					bank.setBank(object.getString("bank"));
					bank = bankCodeMapper.queryByBankColum(bank);
					if(bank != null){
						if(StringUtils.isNotBlank(bank.getBankCode()))
							bankCode= bank.getBankCode();
						if(StringUtils.isNotBlank(bank.getBankName()))
							bankName = bank.getBankName();
					}
					jst.put("bankStatus","1");//第三方银行卡 1.正确0.有误
					jst.put("cardType", object.getString("cardType"));//银行卡类型
					jst.put("bankCode", bankCode);//tpp支持的银行卡code
					jst.put("bankName", bankName);//银行卡名称
				}else{
					jst.put("bankStatus", "0");//第三方银行卡  
					jst.put("message", "对不起，无法识别该卡片，请更换卡片");//没有获取支持的银行卡提示语
				}
			}
		}
		log.info("对应tpp支持的银行卡code："+bankCode);
		return Result.success(jst);
	}
	/**
	 * 捞财宝-签约获取验证码
	 * @param model
	 * @return
	 */
	public Result VerificationCode(Model_006001 model){
		log.info("捞财宝-签约获取验证码入参为："+model.getCellPhone());
		
		TreeMap<String, String> reqParams = new TreeMap<>();
		reqParams.put("cellPhone", model.getCellPhone());
		String sign = MD5.getSign(reqParams, creditReqKey);
		
		Map<String, String> map = new HashMap<>();
		map.put("cellPhone", model.getCellPhone());
		map.put("sign", sign);

		String  result  = HttpKit.post(lcbwgUrl+"/getVerificationCode", map);
		JSONObject js = JSONObject.fromObject(result);
		log.info("捞财宝-签约获取验证码出参结果为："+js);
		if(!Constants.RESP_CODE_SUCCESS.equals(js.getString("repCode"))){
			return Result.fail(js.getString("repMsg"));
		}
		return Result.success();
	}
	
	/**
	 * 捞财宝-签约注册登录
	 * @param model
	 * @return
	 */
	public Result RegisterLogin(Model_006002 model){
		
		log.info("捞财宝-签约注册登录入参为："+model.getCellPhone()+","+model.getValidateCode());
		
		//加密
		TreeMap<String, String> reqParams = new TreeMap<>();
		reqParams.put("cellPhone", model.getCellPhone());
		reqParams.put("validateCode", model.getValidateCode());
		String sign = MD5.getSign(reqParams, creditReqKey);
		
		//传值
		Map<String, String> map = new HashMap<>();
		map.put("cellPhone", model.getCellPhone());
		map.put("validateCode", model.getValidateCode());
		map.put("sign", sign);
		
		String result = HttpKit.post(lcbwgUrl+"/register", map);
		JSONObject js = JSONObject.fromObject(result);
		log.info("捞财宝-签约注册登录出参为："+js);
		
		if(!Constants.RESP_CODE_SUCCESS.equals(js.getString("repCode"))){
			return Result.fail(js.getString("repMsg"));
		}
		JSONObject jo = new JSONObject();
		if(StringUtils.isNotEmpty(js.getString("customerId"))){
			jo.put("customerId", js.getString("customerId"));
		}
		return Result.success(jo);
	}
	
	/**
	 * 捞财宝实名认证
	 * @param model
	 * @return
	 */
	public Result realName(Model_006008 model){
		log.info("捞财宝-签约绑卡入参为："+model.getCustomerId()+","+model.getIdCard());
		Customer cust = customerService.queryByIdCard(model.getIdCard());
		//加密
		TreeMap<String, String> reqParams = new TreeMap<>();
		reqParams.put("customerId", model.getCustomerId());
		reqParams.put("idNo", model.getIdCard());
		reqParams.put("name", cust.getCustomerName());
		String sign = MD5.getSign(reqParams, creditReqKey);
		//传值
		Map<String, String> map = new HashMap<>();
		map.put("customerId", model.getCustomerId());
		map.put("idNo", model.getIdCard());
		map.put("name", cust.getCustomerName());
		map.put("sign", sign);
		
		String result = HttpKit.post(lcbwgUrl+"/realName", map);
		JSONObject js = JSONObject.fromObject(result);
		log.info("捞财宝-签约注册登录出参为："+js);
		
		if(!Constants.RESP_CODE_SUCCESS.equals(js.getString("repCode"))){
			return Result.fail(js.getString("repMsg"));
		}
		
		return Result.success("实名认证成功！");
	}
	
	/**
	 * H5与捞财宝绑卡
	 * @param model
	 * @return
	 */
	public Result BankCard(Model_006003 model){
		log.info("捞财宝-签约绑卡入参为："+model.getCustomerId()+","+model.getBankCard()+","+model.getBankCode()+","+model.getCellPhone()+","+model.getLoanNo());
		//捞财宝绑卡
		String value = BankCode.getValue(model.getBankCode());
		if(StringUtils.isEmpty(value)){
			 return Result.fail("不支持该银行卡");//暂不支持该银行,请更换其他银行卡
		}
		//加密
		TreeMap<String, String> reqParams = new TreeMap<>();
		reqParams.put("cellPhone", model.getCellPhone());
		reqParams.put("customerId", model.getCustomerId());
		reqParams.put("bankCard", model.getBankCard());
		reqParams.put("bankCode", BankCode.getValue(model.getBankCode()));
		String sign = MD5.getSign(reqParams, creditReqKey);
		
		//传值
		Map<String, String> map = new HashMap<>();
		map.put("cellPhone", model.getCellPhone());
		map.put("customerId", model.getCustomerId());
		map.put("bankCard", model.getBankCard());
		map.put("bankCode", BankCode.getValue(model.getBankCode()));
		map.put("sign", sign);
		
		String result = HttpKit.post(lcbwgUrl+"/bindBankCard", map);
		JSONObject js = JSONObject.fromObject(result);
		log.info("捞财宝-签约注册登录出参为："+js);
		
		if(!Constants.RESP_CODE_SUCCESS.equals(js.getString("repCode"))){
			return Result.fail(js.getString("repMsg"));
		}
		//录单绑卡
		Map<String,Integer> bankMap = dmsService.getBankList();
		BankCardBO bo = new BankCardBO();
		bo.setSysCode(Constants.CONTRACT_APP);
		bo.setLoanNo(model.getLoanNo());
		bo.setBankPhone(model.getCellPhone());
		bo.setApplyBankBranch(model.getApplyBankBranch());
		bo.setApplyBankCardNo(model.getBankCard());
		if(bankMap.containsKey(model.getBankCode())){
			bo.setApplyBankNameId(bankMap.get(model.getBankCode()));
			String code = dmsService.saveBankCard(bo);
			if("999990".equals(code)){
				return Result.fail("银行卡卡号格式不正确,请检查后重新填写");
			}
		}else{
			 return Result.fail("不支持该银行卡");//暂不支持该银行,请更换其他银行卡
		}
		return Result.success("银行卡绑定成功");
	}
	
	/**
	 * 从平台拿到html转pdf上传到pic给到核心地址拿取签章结果
	 * @param model
	 * @return
	 */
	public Result getSignContract(Model_006004 model){
		//入参是身份证号，借款编号
		//从平台获取html数据并转pdf上传到pic并且更新本地数据
		Result resultIdCard = idCardContract(model.getIdCard());
		if(!resultIdCard.getSuccess()){
			return resultIdCard;
		}
		//先删除原有的pdf文件
		/*ApplyContractInfo aci = new ApplyContractInfo();
		aci.setIdNo(model.getLoanNo());
		List<ApplyContractInfo> acilist = applyContractInfoMapper.selectContactTable(aci);
		if(acilist != null && !acilist.isEmpty()){
			for (ApplyContractInfo ac : acilist) {
				boolean flag = deletePicture(ac.getPdfId());
				if(!flag){
					log.info("pdf删除失败！pdf所对应的id= "+ac.getPdfId());
					return Result.fail("合同签章处理失败！");
				}
			}
		}*/
		
		Result resultLoanNo = loanNoContract(model.getLoanNo());
		if(!resultLoanNo.getSuccess()){
			return resultLoanNo;
		}
		
		//将地址发送给核心拿取是否签章修改数据库变成 已签章、未签章 并且将image图片保存到本地
		ApplyContractInfo a = new ApplyContractInfo();
		a.setIsNotSign(Constants.DATA_UNVALID);
		a.setIdNos(new String[]{model.getIdCard(),model.getLoanNo()});
		List<ApplyContractInfo> listaci = applyContractInfoMapper.queryByIdNos(a);
		if(listaci != null && !listaci.isEmpty()){
			Result re = contractTS(listaci, model.getIdCard(), model.getPhone(), model.getUserName(), model.getLoanNo());
			if(!re.getSuccess()){
				return re;
			}
		}
		return Result.success();
	}
	
	/**
	 * 推送合同
	 * @param listaci
	 * @return
	 */
	public Result contractTS(List<ApplyContractInfo> listaci,String idCard,String phone,String userName,String loanNo){
		if(listaci != null && !listaci.isEmpty()){
			JSONObject jo = new JSONObject();
			jo.put("idNum", idCard);
			jo.put("mobile", phone);
			jo.put("userName", userName);
			jo.put("sysCode", Constants.CONTRACT_SYS_CODE);
			
			//判断是否含有已人维度，借款编号维度
			boolean card = false,loan = false;
			for (ApplyContractInfo aaf : listaci) {
				if(aaf.getStatus().equals(String.valueOf(ContractEnum.contract_type_01.getType()))){
					card = true;
				}
				if(aaf.getStatus().equals(String.valueOf(ContractEnum.contract_type_02.getType()))){
					loan = true;
				}
			}
			JSONArray infoc = new JSONArray();
			if(card){
				JSONObject jt = new JSONObject();
				jt.put("businessId", idCard);
				JSONArray jaa = new JSONArray();
				JSONObject cont = new JSONObject();
				JSONArray cs = new JSONArray();
				for (ApplyContractInfo ac : listaci) {
					if(ac.getStatus().equals(String.valueOf(ContractEnum.contract_type_01.getType()))){
						JSONObject jj = new JSONObject();
						jj.put("contractType", ContractType.getType(ac.getCode()));
						jj.put("fileName", ac.getFileName());
						jj.put("saveDirectory", ac.getSaveDirectory());
						cs.add(jj);
					}
				}
				cont.put("contractInfos", cs);
				cont.put("infoCategory", Constants.CONTRACT_INFO_TYPE_01);
				jaa.add(cont);
				jt.put("infoCategoryContractVos", jaa);
				infoc.add(jt);
			}
			
			if(loan){
				JSONObject jt = new JSONObject();
				jt.put("businessId", loanNo);
				JSONArray jaa = new JSONArray();
				JSONObject cont = new JSONObject();
				JSONArray cs = new JSONArray();
				for (ApplyContractInfo ac : listaci) {
					if(ac.getStatus().equals(String.valueOf(ContractEnum.contract_type_02.getType()))){
						JSONObject jj = new JSONObject();
						jj.put("contractType", ContractType.getType(ac.getCode()));
						jj.put("fileName", ac.getFileName());
						jj.put("saveDirectory", ac.getSaveDirectory());
						cs.add(jj);
					}
				}
				cont.put("contractInfos", cs);
				cont.put("infoCategory", Constants.CONTRACT_INFO_TYPE_02);
				jaa.add(cont);
				jt.put("infoCategoryContractVos", jaa);
				infoc.add(jt);
			}
			jo.put("batchContractFlagVos", infoc);
			log.info("合同签章传入核心入参为："+jo);
			log.info("合同签章传入核地址为:"+urlHx+ Constants.hx_batchPushContract);
			String result = HttpUtils.doJsonPost(urlHx + Constants.hx_batchPushContract, jo.toString());
			JSONObject jb = JSONObject.fromObject(result);
			log.info("合同出参数据为："+jb.getString("resCode")+"|"+jb.getString("resMsg"));
			if(!Constants.RESP_CODE_SUCCESS.equals(jb.getString("resCode"))){
				return Result.fail("合同签章推送失败！");
			}
			return Result.success();
		}
		return Result.fail();
	}
	
	public Result idCardContract(String idCard){
		//人为维度
		ApplyContractInfo acf = new ApplyContractInfo();
		acf.setIdNo(idCard);
		List<ApplyContractInfo> idclist = applyContractInfoMapper.selectContactTable(acf);
		if(idclist.isEmpty()){
			String[] strperson = ContractEnum.contract_type_01.getCode();
			for (int i =0; i < strperson.length; i++) {
				ReqBMSContractTemplateVO rbtl = new ReqBMSContractTemplateVO();
				rbtl.setCode(strperson[i]);
				rbtl.setSysCode(Constants.ZDQQ_SYS_CODE);
				Response<ResBMSContractTemplateVO> response = iBMSContractTemplateExecuter.findByVO(rbtl);
				log.info("调用平台获取html出参："+response.getRepCode()+" " +response.getRepMsg());
				if(!"000000".equals(response.getRepCode())){
					return Result.fail("合同签章处理失败");
				}
				Customer cust = customerService.queryByIdCard(idCard);
				Map<String, Object> values = new HashMap<>();
				if(cust != null ){
					values.put("borrowerName", cust.getCustomerName());
					values.put("idnum", idCard);
					String msgBody = PropertiesReader.readAsString("zdmoney.adress");
					values.put("address", msgBody);
				}
				Calendar calendar = Calendar.getInstance();
				values.put("year", String.valueOf(calendar.get(Calendar.YEAR)));
				values.put("month", String.valueOf(calendar.get(Calendar.MARCH) +1));
				values.put("day", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
				String contract = (String) response.getData().getTemplateContent();
				boolean flag = getHtmlContractPDF(idCard,strperson[i],contract,ContractEnum.contract_type_01.getType(),values);
				if(!flag){
					return Result.fail("合同签章处理异常！");
				}
			}
		}
		return Result.success();
	}
	
	public Result loanNoContract(String loanNo){
		ApplyContractInfo acf = new ApplyContractInfo();
		acf.setIdNo(loanNo);
		List<ApplyContractInfo> nolist = applyContractInfoMapper.selectContactTable(acf);
		if(nolist.isEmpty()){
			//借款为维度
			String[] strloanNo = ContractEnum.contract_type_02.getCode();
			for (int i = 0; i < strloanNo.length; i++) {
				ReqBMSContractTemplateVO rbtl = new ReqBMSContractTemplateVO();
				rbtl.setCode(strloanNo[i]);
				rbtl.setSysCode(Constants.ZDQQ_SYS_CODE);
				Response<ResBMSContractTemplateVO> response = iBMSContractTemplateExecuter.findByVO(rbtl);
				log.info("调用平台获取html出参："+response.getRepCode()+" " +response.getRepMsg());
				if(!"000000".equals(response.getRepCode())){
					return Result.fail("合同签章处理失败");
				}
				String idcard="";
				ApplyLoanInfo info = new ApplyLoanInfo();
				info.setLoanNo(loanNo);
				ApplyLoanInfo a = applyLoanInfoService.selectOne(info);
				if(a!=null){
					idcard = a.getIdCard();
				}
				SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd");
				Map<String, Object> values = new HashMap<>();
				values.put("idnum", idcard);
				values.put("authDate", sim.format(new Date()));
				String contract = (String) response.getData().getTemplateContent();
				boolean flag = getHtmlContractPDF(loanNo,strloanNo[i],contract,ContractEnum.contract_type_02.getType(),values);
				if(!flag){
					return Result.fail("合同签章处理异常！");
				}
			}
		}
		return Result.success();
	}
	
	/**
	 * 查看未签章合同
	 * @param model
	 * @return
	 */
	public Result getNotSignContract(Model_006004 model){
		//入参：身份证、借款编号、未签章的合同编号
		log.info("查询未签章合同入参："+model.getIdCard()+"|"+model.getLoanNo()+"|"+model.getPhone()+"|"+model.getUserName());
		Result re = getSignContract(model);
		if(!re.getSuccess()){
			return re;
		}
		Result tt = getSignResult(model);
		if(!tt.getSuccess()){
			return tt;
		}
		JSONObject result = new JSONObject();
		ApplyContractInfo a = new ApplyContractInfo();
		a.setIsNotSign(Constants.DATA_UNVALID);
		a.setIdNos(new String[]{model.getIdCard(),model.getLoanNo()});
		List<ApplyContractInfo> listaci = applyContractInfoMapper.queryByIdNos(a);
		if(listaci != null && !listaci.isEmpty()){
			result.put("isNotSign", Constants.CONTRACT_IS_NOT_SIGN_02);
			String msgBody = PropertiesReader.readAsString("contract.filling.in.info");
			String title = "";
			for (ApplyContractInfo aco : listaci) {
				title += "《"+ContractType.getValue(aco.getCode())+"》、";
			}
			result.put("title", MessageFormat.format(msgBody, title.substring(0, title.length()-1)));
			result.put("urlHtml", urlH5);
			return Result.success(result);
		}else{
			result.put("isNotSign", Constants.CONTRACT_IS_NOT_SIGN_01);
			return Result.success(result);
		}
	}
	
	/**
	 * 点击签约合同
	 * @param model
	 * @return
	 */
	public Result signContract(Model_006004 model){
		//入参：身份证、借款编号、未签章的合同编号
		log.info("查询未签章合同入参："+model.getIdCard()+"|"+model.getLoanNo()+"|"+model.getPhone()+"|"+model.getUserName());
		ApplyContractInfo a = new ApplyContractInfo();
		a.setIsNotSign(Constants.DATA_UNVALID);
		a.setIdNos(new String[]{model.getIdCard(),model.getLoanNo()});
		List<ApplyContractInfo> listaci = applyContractInfoMapper.queryByIdNos(a);
		if(listaci != null && !listaci.isEmpty()){
			JSONObject jb = new JSONObject();
			jb.put("sysCode", Constants.CONTRACT_SYS_CODE);
			Set<String> set = new TreeSet<>();
			for (ApplyContractInfo aci : listaci) {
				set.add(aci.getIdNo());	
			}
			JSONArray ja = new JSONArray();
			if(set != null && !set.isEmpty()){
				for (String string : set) {
					JSONObject jbt = new JSONObject();
					jbt.put("businessId", string);
					List<String> list = new ArrayList<>();
					if(model.getIdCard().equals(string)){
						list.add(Constants.CONTRACT_INFO_TYPE_01);
					}
					if(model.getLoanNo().equals(string)){
						list.add(Constants.CONTRACT_INFO_TYPE_02);
					}
					jbt.put("infoCategorys", list);
					ja.add(jbt);
				}
			}
			jb.put("batchSigVos", ja);
			log.info("批量合同签章传入核心入参为："+jb);
			String result = HttpUtils.doJsonPost(urlHx + Constants.hx_batchClientAutoSign, jb.toString());
			JSONObject jbt = JSONObject.fromObject(result);
			log.info("批量合同签章出参数据为："+jbt.getString("resCode")+"|"+jbt.getString("resMsg"));
			if(!Constants.RESP_CODE_SUCCESS.equals(jbt.getString("resCode"))){
				return Result.fail(jbt.getString("resMsg"));
			}
			Result res = getSignResult(model);
			if(!res.getSuccess()){
				return res;
			}
		}
		return Result.success();
	}
	
	/**
	 * 批量查询签章结果
	 * @param model
	 * @return
	 */
	public Result getSignResult(Model_006004 model){
		log.info("批量查询签章结果入参："+model.getIdCard()+"|"+model.getLoanNo()+"|"+model.getPhone()+"|"+model.getUserName());
		ApplyContractInfo a = new ApplyContractInfo();
		a.setIsNotSign(Constants.DATA_UNVALID);
		a.setIdNos(new String[]{model.getIdCard(),model.getLoanNo()});
		List<ApplyContractInfo> listaci = applyContractInfoMapper.queryByIdNos(a);
		if(listaci != null && !listaci.isEmpty()){
			JSONObject jb = new JSONObject();
			jb.put("sysCode", Constants.CONTRACT_SYS_CODE);
			Set<String> set = new TreeSet<>();
			for (ApplyContractInfo aci : listaci) {
				set.add(aci.getIdNo());	
			}
			JSONArray ja = new JSONArray();
			if(set != null && !set.isEmpty()){
				for (String string : set) {
					JSONObject jbt = new JSONObject();
					jbt.put("businessId", string);
					List<String> list = new ArrayList<>();
					if(model.getIdCard().equals(string)){
						list.add(Constants.CONTRACT_INFO_TYPE_01);
					}
					if(model.getLoanNo().equals(string)){
						list.add(Constants.CONTRACT_INFO_TYPE_02);
					}
					jbt.put("infoCategorys", list);
					ja.add(jbt);
				}
			}
			jb.put("signResultVos", ja);
			log.info("批量查询签章结果给核心入参为："+jb);
			String result = HttpUtils.doJsonPost(urlHx + Constants.hx_batchGetSignResult, jb.toString());
			JSONObject jbt = JSONObject.fromObject(result);
			log.info("批量查询签章结果出参数据为："+jbt.getString("resCode")+"|"+jbt.getString("resMsg"));
			if(!Constants.RESP_CODE_SUCCESS.equals(jbt.getString("resCode"))){
				return Result.fail(jbt.getString("resMsg"));
			}
			JSONArray jay  = jbt.getJSONArray("attachment");
			if(jay != null && jay.size() > 0){
				for (int i = 0; i < jay.size(); i++) {
					JSONObject jtt = jay.getJSONObject(i);
					String idNo = jtt.getString("businessId");
					JSONArray ics = jtt.getJSONArray("infoCategorySignInfoVos");
					for (int j = 0; j < ics.size(); j++) {
						JSONObject jny = ics.getJSONObject(j);
						String signStatus = String.valueOf(jny.get("signStatus"));
						String infoCategory = jny.getString("infoCategory");
						if(jny.containsKey("signContractVos")){
							JSONArray jy = jny.getJSONArray("signContractVos");
							if(jy != null && jy.size() > 0){
								for (int k = 0; k < jy.size(); k++) {
									JSONObject jjj = jy.getJSONObject(k);
									String contractType = jjj.getString("contractType");
									ApplyContractInfo af = new ApplyContractInfo();
									af.setIdNo(idNo);
									af.setCode(ContractType.getKey(contractType));			
									af = applyContractInfoMapper.selectContactTableOne(af);
									af.setImageUrl(jjj.getString("fileNum"));
									af.setIsNotSign(signStatus);
									int tttt = applyContractInfoMapper.updateByPrimaryKeyTable(af);
									if(tttt <= 0){
										return Result.fail("更新本地图片失败！");
									}
								}
							}
						}else{
							String[] str = ContractInfoEnum.getKey(infoCategory);
							if(str != null && str.length > 0){
								for (String st : str) {
									ApplyContractInfo af = new ApplyContractInfo();
									af.setIdNo(idNo);
									af.setCode(st);
									af = applyContractInfoMapper.selectContactTableOne(af);
									af.setIsNotSign(signStatus);
									int tttt = applyContractInfoMapper.updateByPrimaryKeyTable(af);
									if(tttt <= 0){
										return Result.fail("更新本地图片失败！");
									}
								}
							}
						}
					}
				}
			}
		}
		return Result.success();
	}
	
	/**
	 * h5获取图片合同
	 * @param model
	 * @return
	 */
	public Result signContractImages(Model_006005 model){
		
		ApplyContractInfo aci = new ApplyContractInfo();
		aci.setIsNotSign(Constants.DATA_UNVALID);
		aci.setIdNos(new String[]{model.getIdCard(),model.getLoanNo()});
		List<ApplyContractInfo> listaci = applyContractInfoMapper.queryByIdNos(aci);
		JSONArray ja = new JSONArray();
		if(listaci != null && !listaci.isEmpty()){
			for (ApplyContractInfo af : listaci) {
				JSONObject jb = new JSONObject();
				jb.put("title", ContractType.getValue(af.getCode()));
				jb.put("fileName", af.getImageUrl());
				ja.add(jb);
			}
		}
		return Result.success(ja);
	}
	/**
	 * H5从核心获取图片
	 * @param model
	 * @return
	 */
	public Result getPicImages(Model_006006 model){
		log.info("H5从核心获取图片入参："+model.getFileName());
		JSONObject object = new JSONObject();
		object.put("sysCode", Constants.CONTRACT_SYS_CODE);
		object.put("fileNum", model.getFileName());
		log.info("H5从核心获取图片核心入参为："+object);
		String result = HttpUtils.doJsonPost(urlZs + Constants.hx_loadContractFile, object.toString());
		JSONObject jbt = JSONObject.fromObject(result);
		log.info("H5从核心获取图片出参数据为："+jbt.getString("resCode")+"|"+jbt.getString("resMsg"));
		if(!Constants.RESP_CODE_SUCCESS.equals(jbt.getString("resCode"))){
			return Result.fail(jbt.getString("resMsg"));
		}
		JSONObject jb = jbt.getJSONObject("attachment");
		JSONObject jct = new JSONObject();
		jct.put("image", jb.getString("contractContent"));
		return Result.success(jct);
	}
	/**
	 * 图片删除接口
	 * @param model
	 * @return
	 * */
	private boolean deletePicture(String fileId){
		String param = "ids="+fileId+"&operator=zdqq&jobNumber=zdqq";
		String obj = HttpClientUtil.sendHttpPost1(param);
		if(StringUtils.isNotEmpty(obj)){
			com.alibaba.fastjson.JSONObject objs = com.alibaba.fastjson.JSONObject.parseObject(obj);
			if(!"000000".equals(objs.get("errorcode"))) {
				log.info(Constants.RESP_CODE_ERROR+"PIC图片系统删除调用失败");
				return false;
			}
			log.info("图片删除入参为："+fileId +"出参为："+obj);
		}else{
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * @param idNo 身份证和借款编号
	 * @param code	合同编号
	 * @param contract	合同内容
	 * @param status 1.身份证，2.借款编号
	 * @param values 参数
	 * @return
	 */
	private boolean getHtmlContractPDF(String idNo,String code,String contract,Integer status,Map<String, Object> values){
		//html合同文件
		try {
			String targetFileName ="./" + UUID.randomUUID()+".pdf";
			File file = new File(targetFileName);
			Html2PDFUtil.generatePdf(contract, values, file);
			Map<String,  String>  queryParas  =  new  HashMap<>();
			if(status.equals(ContractEnum.contract_type_01.getType())){
				queryParas.put("idNo",idNo);
			}
			if(status.equals(ContractEnum.contract_type_02.getType())){
				queryParas.put("appNo",idNo);
			}
	    	queryParas.put("operator","zdqq");
	    	queryParas.put("jobNumber","zdqq");
	    	queryParas.put("nodeKey",  "loanApplication");//录单环节
	    	queryParas.put("sysName",  "app");
		    queryParas.put("dataSources", "0"); //1：pc端，0：app端
		    if(code.equals(ContractType.contract_type_00208.getKey())){
		    	queryParas.put("code", "S45");
		    }
		    if(code.equals(ContractType.contract_type_00210.getKey())){
		    	queryParas.put("code", "S46");
		    }
		    if(code.equals(ContractType.contract_type_00209.getKey())){
		    	queryParas.put("code", "Q");
		    }
			if(!file.exists()){
				throw new Exception(Constants.RESP_CODE_ERROR);
			}
			String  result  = "";
			if(status.equals(ContractEnum.contract_type_01.getType())){
				result = HttpKit.post(urlUPIC+Constants.PIC_URL_UPLOAD_WITH, queryParas, file);
			}
			if(status.equals(ContractEnum.contract_type_02.getType())){
				result = HttpKit.post(urlUPIC+Constants.PIC_URL_UPLOAD, queryParas, file);
			}
		    String httpResult = URLDecoder.decode(result, "UTF-8");
		   //删除生成的pdf
			if(file != null && file.exists()){
				FileDownUtils.deleteDir(file);
			}
			com.alibaba.fastjson.JSONObject objs = com.alibaba.fastjson.JSONObject.parseObject(httpResult);
			
			if(!"000000".equals(objs.get("errorcode"))) {
				log.info(Constants.RESP_CODE_ERROR+"pdf系统上传调用失败");
				return false;
			}
			//保存在本地方便查询
			com.alibaba.fastjson.JSONObject j = (com.alibaba.fastjson.JSONObject) objs.get("result");
			//Long picId = Long.valueOf(Integer.parseInt(j.get("id").toString()));
			String picId = j.get("id").toString();
			String url = (String) j.get("url");//文件地址
			String[] urls = url.split("/");
			String regex="//[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}/";
			String ip = url.replaceAll(regex, "");
			
			ApplyContractInfo ant = new ApplyContractInfo();
			ant.setIdNo(idNo);
			ant.setCode(code);
			ant = applyContractInfoMapper.selectContactTableOne(ant);
			if(ant != null && ant.getId() != null){
				ant.setPdfId(picId);
				ant.setPdfUrl(url);
				ant.setFileName(urls[urls.length - 1]);
				ant.setSaveDirectory(ip.substring(0, ip.lastIndexOf("/")+1));
				ant.setIsNotSign(Constants.DATA_UNVALID); //0未签章，1.已签章
				int i = applyContractInfoMapper.updateByPrimaryKeyTable(ant);
				if(i > 0)
					return true;
				return false;
			}else{
				ApplyContractInfo aci = new ApplyContractInfo();
				aci.setPdfId(picId);
				aci.setIdNo(idNo);
				aci.setCode(code);
				aci.setStatus(String.valueOf(status));
				aci.setPdfUrl(url);
				aci.setFileName(urls[urls.length - 1]);
				aci.setSaveDirectory(ip.substring(0, ip.lastIndexOf("/")+1));
				aci.setIsNotSign(Constants.DATA_UNVALID); //0未签章，1.已签章
				aci.setContractName(ContractType.getValue(code));
				aci.setCreateTime(new Date());
				aci.setUpdateTime(new Date());
				int i = applyContractInfoMapper.insertTable(aci);
				if(i > 0)
					return true;
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 获取银行卡详情
	 * @param model
	 * @return
	 */
	public Result getOneBankInfo(Model_006007 model){
		JSONObject jt = new JSONObject();
		Customer cust = customerService.queryByIdCard(model.getIdCard());
		if(cust == null){
			return Result.fail("该用户不存在！");
		}
		jt.put("name", cust.getCustomerName());
		jt.put("idCard", cust.getIdCard());
		jt.put("bankcard", model.getBankCard());
		JSONObject param = new JSONObject();
		param.put("idNum", model.getIdCard());
		JSONObject loanJson = HttpClientUtil.sendHttpPostForCredit4BindBankCard(
				Constants.CREDIT_URL_FINDBACKCARDINFO, param.toString());
		if(loanJson != null && loanJson.size() > 0){
			if(loanJson.containsKey("bankCards")){
				JSONArray ja = loanJson.getJSONArray("bankCards");
				for (int i = 0; i < ja.size(); i++) {
					JSONObject jj = ja.getJSONObject(i);
					if(model.getBankCard().equals(jj.getString("account"))){
						jt.put("bankBranchName", jj.getString("bankBranchName"));
						jt.put("bankCode", jj.getString("bankCode"));
						jt.put("bankName", jj.getString("bankName"));
						jt.put("mobile", jj.getString("mobile"));
					}
					
				}
			}
		}
		return Result.success(jt);
	}
	/**
	 * 查询是否到签约步骤
	 * @param model
	 * @return
	 */
	public Result checkWorkflow(Model_006010 model){
		JSONObject jb = new JSONObject();
		Integer contract = Integer.valueOf(num);
		log.info("循环次数："+contract);
		boolean re = queryLoanNo(model.getLoanNo());
		if(!re){
			for (int i = 0; i < contract; i++) {
				try {
					Thread.sleep(10000);
					boolean res = queryLoanNo(model.getLoanNo());
					if(res){
						jb.put("result", "0");
						log.info("第"+(i+1)+"次获取结果："+jb);
						return Result.success(jb);
					}
				} catch (InterruptedException e) {
					jb.put("result", "1");
					jb.put("errmsg", e.getMessage());
					return Result.success(jb);
				}
			}
		}else{
			jb.put("result", "0");
			log.info("第一次获取结果："+jb);
			return Result.success(jb);
		}
		jb.put("result", "1");
		log.info("获取结果："+jb);
		return Result.success(jb);
	}
	
	private boolean queryLoanNo(String loanNo){
		com.alibaba.fastjson.JSONObject objAll = new com.alibaba.fastjson.JSONObject();
		objAll.put("loanNo", loanNo);
		log.info("借款进度录单入参值："+ JSONObject.fromObject(objAll));
		Result re = applyInfoService.queryLoanAndIdCard(objAll);
		if(!re.getSuccess()){
			log.info("签约流程-签约环节出现错误");
			return false;
		}
		JSONObject obj = JSONObject.fromObject(re.getData());
		if(obj.containsKey("processList")){
			JSONArray js = JSONArray.fromObject(obj.get("processList"));
			if(js!=null && js.size() >0){
				JSONObject ob = JSONObject.fromObject(js.get(0));
				log.info("借款进度出参："+ob);
				if(ob.containsKey("workFlow")){
					String workflow = ob.getString("workFlow");
					if("01612".equals(workflow) || "01613".equals(workflow)){//到签约环节
						log.info("签约流程-到签约环节");
						return true;
					}
				}	
			}
		}
		log.info("签约流程-未到签约环节");
		return false;
	}
}
