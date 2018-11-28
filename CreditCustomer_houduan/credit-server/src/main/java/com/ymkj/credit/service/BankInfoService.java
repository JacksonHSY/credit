package com.ymkj.credit.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ymkj.base.core.biz.api.message.Response;
import com.ymkj.cms.biz.api.service.master.IBMSChannelCardExecuter;
import com.ymkj.cms.biz.api.vo.request.apply.ReqBMSChannelBankVO;
import com.ymkj.cms.biz.api.vo.request.master.ReqBMSChannelCardVO;
import com.ymkj.cms.biz.api.vo.response.master.ResBMSChannelCardVO;
import com.ymkj.cms.biz.api.vo.response.master.ResListVO;
import com.ymkj.credit.common.constants.Constants;
import com.ymkj.credit.common.entity.BankInfo;
import com.ymkj.credit.common.entity.Customer;
import com.ymkj.credit.common.entity.Dictionary;
import com.ymkj.credit.common.untils.NumberUtil;
import com.ymkj.credit.mapper.BankInfoMapper;
import com.ymkj.credit.mapper.DictionaryMapper;
import com.ymkj.credit.mapper.LoanOrderMapper;
import com.ymkj.credit.network.HttpClientUtil;
import com.ymkj.credit.service.dms.DmsService;
import com.ymkj.credit.service.redis.BasicRedisOpts;
import com.ymkj.credit.service.tpp.BrokerTradeService;
import com.ymkj.credit.web.api.dto.BankInfoDto;
import com.ymkj.credit.web.api.dto.BankListInfoDto;
import com.ymkj.credit.web.api.model.Result;
import com.ymkj.credit.web.api.model.base.Model_002014;
import com.ymkj.credit.web.api.model.base.Model_002015;
import com.ymkj.credit.web.api.model.base.Model_002019;
import com.ymkj.credit.web.api.model.base.Model_002026;
import com.ymkj.credit.web.api.model.base.Model_002027;
import com.ymkj.credit.web.api.model.base.Model_004020;
import com.ymkj.credit.web.api.model.base.Model_004021;
import com.ymkj.credit.web.api.model.base.Model_004022;
import com.ymkj.credit.web.api.model.base.Model_004023;
import com.ymkj.credit.web.api.model.base.Model_004024;
import com.ymkj.credit.web.api.model.base.Model_004027;
import com.ymkj.credit.web.api.model.resp.BankInfoBo;
import com.ymkj.credit.web.api.model.resp.ChannelInfosBo;
import com.ymkj.dms.api.common.base.BankCardBO;
import com.ymkj.springside.modules.exception.BusinessException;
import com.ymkj.springside.modules.utils.StrUtils;
import com.zendaimoney.thirdpp.common.vo.AttachmentResponse;
import com.zendaimoney.thirdpp.trade.pub.vo.resp.query.BankResponseVo;

import lombok.extern.slf4j.Slf4j;



/**
* BankInfoService
* <p/>
* Author: 
* Date: 2018-02-28 10:23:46
* Mail: 
*/
@Slf4j
@Service
public class BankInfoService {
	 @Autowired
	 private BankInfoMapper bankInfoMapper;
	 @Autowired
	 private CustomerService customerService;
	 @Autowired
	 private LoanOrderService loanOrderService;
	 @Autowired
	 private LoanOrderMapper loanOrderMapper;
	 @Autowired
	 private DictionaryMapper dictionaryMapper;
	 @Autowired
	 private DmsService dmsService;
	 @Autowired
	 private BrokerTradeService brokerTradeService;
	 @Autowired
	 private IBMSChannelCardExecuter iBMSChannelCardExecuter;
	 
	 @Value("${urlUPIC}")
	 private String urlUPIC;
	 
	//银行卡按钮是否显示
	@Value("${is_add_bank}")
	private String is_add_bank;
	 
	 @Inject
	 private BasicRedisOpts basicRedisOpts;
	/*
	 * 根据银行卡卡号查询列表
	 */
	public BankInfo queryByAccNO(String accNo,String cId){
		Map<String,String> map = new HashMap<String,String>();
		map.put("accNo",accNo);
		map.put("cId",cId);
		return bankInfoMapper.selectByAccNo(map);
		
	}
	/*
	 * 根据id查找银行卡信息
	 */
	public BankInfo queryById(String id){
		BankInfo bankInfo = new BankInfo();
		bankInfo.setId(id);
		bankInfo.setStatus(Constants.DATA_VALID);
		return bankInfoMapper.selectOneTable(bankInfo);
	}
	/*
	 * 查询银行卡列表
	 */
	public Result queryBankInfo(Model_004020 model) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("CID",model.getCustomerId());
		PageHelper.startPage(model.getPageNo(), model.getPageSize());
		Page<BankListInfoDto> page = bankInfoMapper.queryBankInfo(map);
		for(BankListInfoDto bankinfo :page){
			String bankName = bankinfo.getBankName();
			//根据银行卡姓名获取银行卡图标地址
			map.clear();
			map.put("bankName", bankName);
			Dictionary dictionary = dictionaryMapper.getImageUrl(map);
			if(dictionary!=null){
				bankinfo.setIconUrl(dictionary.getDataValue());
			}else {
				bankinfo.setIconUrl("/static/images/bank_icon_zw.png");//银行卡统一一图标
			}
		}
		map.clear();
		map.put("bankCardList",page);
		map.put("total",page.getTotal());
		return Result.success(map);
	}
	/*
	 * 添加和修改银行卡
	 */
	public Result createBankInfo(Model_004021 model, String cusromerId)
			throws IOException {
		com.ymkj.credit.common.entity.Customer customer = customerService
				.findCustomerMes(Long.parseLong(cusromerId));
		// BankInfo querybankInfo = queryByAccNO(model.getCardNo());

		Map<String, String> queryParas = new HashMap<>();

		String batchNum = NumberUtil.getNumberForPK();
		queryParas.put("appNo", batchNum);
		queryParas.put("operator", customer.getCustomerName());
		queryParas.put("jobNumber", customer.getPhone());
		queryParas.put("code", "S5");// 银行卡目录
		if (model.getType() == 0) {// 添加银行卡信息
			BankInfo querybankInfo = queryByAccNO(model.getCardNo(),cusromerId);
			if(querybankInfo!=null){
				return Result.fail("请不要重复提交银行卡");
			}
			queryParas.put("nodeKey",  "contractAward");//合同签约环节
			String result = customerService.uploadfile(queryParas,
					model.getCardImgStr());// 上传银行卡图片
			JSONObject json = JSONObject.fromObject(result);
			if (json.getBoolean("isOk")) {
				BankInfo bankInfo = new BankInfo();
				bankInfo.setAccName(model.getOwnerName());
				bankInfo.setAccNo(model.getCardNo());
				bankInfo.setBankName(model.getBankName());
				bankInfo.setSubbranch(model.getBranchBankName());
				bankInfo.setPhone(model.getPhoneNum());
				com.alibaba.fastjson.JSONObject resultJson = com.alibaba.fastjson.JSONObject
						.parseObject(result);
				result = com.alibaba.fastjson.JSONObject
						.toJSONString(resultJson.get("result"));
				com.alibaba.fastjson.JSONObject object = com.alibaba.fastjson.JSONObject
						.parseObject(result);
				bankInfo.setSimageUrl("http:" + object.getString("thumUrl"));// 上传文件返回缩略图地址
				bankInfo.setGimageUrl("http:" + object.getString("url"));// 上传文件返回高清图地址
				bankInfo.setCid(Long.parseLong(cusromerId));
				bankInfo.setId(UUID.randomUUID().toString());
				bankInfo.setCreateTime(new Date());
				bankInfo.setStatus(Constants.DATA_VALID);
				bankInfoMapper.insertTable(bankInfo);
			} else {
				throw new BusinessException(json.getString("errormsg"));
			}
		} else {// 修改银行卡
			BankInfo bankInfo = queryById(model.getId());
			bankInfo.setAccName(model.getOwnerName());
			bankInfo.setAccNo(model.getCardNo());
			if(!bankInfo.getAccNo().equals(model.getCardNo())){
				BankInfo querybankInfo = queryByAccNO(model.getCardNo(),cusromerId);
				if(querybankInfo!=null){
					return Result.fail("请不要提交卡号重复的银行卡");
				}
			}
			bankInfo.setBankName(model.getBankName());
			bankInfo.setSubbranch(model.getBranchBankName());
			bankInfo.setPhone(model.getPhoneNum());
			if (model.getCardImgStr() != null) {
				String result = customerService.uploadfile(queryParas,
						model.getCardImgStr());// 上传银行卡图片
				JSONObject json = JSONObject.fromObject(result);
				if (json.getBoolean("isOk")) {
					com.alibaba.fastjson.JSONObject resultJson = com.alibaba.fastjson.JSONObject
							.parseObject(result);
					result = com.alibaba.fastjson.JSONObject
							.toJSONString(resultJson.get("result"));
					com.alibaba.fastjson.JSONObject object = com.alibaba.fastjson.JSONObject
							.parseObject(result);
					bankInfo.setSimageUrl("http:" + object.getString("thumUrl"));// 上传文件返回缩略图地址
					bankInfo.setGimageUrl("http:" + object.getString("url"));// 上传文件返回高清图地址
				} else {
					throw new BusinessException(json.getString("errormsg"));
				}
			}
			bankInfo.setUpdateTime(new Date());
			bankInfoMapper.updateByPrimaryKeyTable(bankInfo);

		}
		return Result.success("银行卡信息提交成功");
	}
	
	/*
	 * 查询银行卡详细信息
	 */
	public Result queryInfoById(Model_004022 model) {
		String id = model.getId();
		Map<String,String> map = new HashMap<String,String>();
		map.put("id",id);
		BankInfoDto bankInfo = bankInfoMapper.queryInfoById(map);
		Map<String,Object> m = new HashMap<String, Object>();
		m.put("bankInfo", bankInfo);
		return Result.success(m);
	}
	/*
	 * 删除银行卡信息
	 */
	public Result removeBankCard(Model_004023 model) {
		String id = model.getId();
		BankInfo b = new BankInfo();
		b.setId(id);
		BankInfo bankInfo = bankInfoMapper.selectOneTable(b);
		bankInfo.setStatus(Constants.DATA_UNVALID);
		bankInfoMapper.updateByPrimaryKeyTable(bankInfo);
		return Result.success("删除成功");
	}
	/*
	 * 绑定银行卡
	 */
	@Transactional
	public Result bindBankCard(Model_004024 model) {
			Map<String,Integer> bankMap = dmsService.getBankList();
			BankCardBO bo = new BankCardBO();
			bo.setSysCode("app");
			bo.setLoanNo(model.getLoanNo());
			bo.setBankPhone(model.getReservedMobile());
			bo.setApplyBankCardNo(model.getAccount());
			bo.setApplyBankBranch(model.getBankBranchName());
			if(bankMap.containsKey(model.getBankCode())){
				bo.setApplyBankNameId(bankMap.get(model.getBankCode()));
				String code = dmsService.saveBankCard(bo);
				if("999990".equals(code)){
					return Result.fail("银行卡卡号格式不正确,请检查后重新填写");
				}
//				Map<String,Object> map = new HashMap<String,Object>();
//				map.put("id", model.getId());
//				map.put("accNo",bankInfo.getAccNo());
//				map.put("bankName", bankInfo.getBankName());
//				map.put("phone", bankInfo.getPhone());
//				map.put("subbranch", bankInfo.getSubbranch());
//				map.put("serialNumber", model.getSerialNumber());
//				map.put("gimageUrl", bankInfo.getGimageUrl());
//				map.put("updateTime", new Date());
//			    loanOrderMapper.updateBankId(map);
				//签订
//				LaocaibaoFlowBO lo = new LaocaibaoFlowBO();
//	        	lo.setSysCode("app");
//	        	lo.setLoanNo(model.getSerialNumber());
//	        	String msg = dmsService.sign(lo);
//	        	log.info("DMS保存银行卡接口返回code=="+code+",签订返回msg=="+msg);
			}else{
				 return Result.fail("不支持该银行卡");//暂不支持该银行,请更换其他银行卡
			}
			return Result.success("银行卡绑定成功");
	}
	
	/**
	 * 绑卡
	 * @TODO
	 * @param model
	 * @return
	 * Result
	 * @author changj@yuminsoft.com
	 * @date2018年4月13日
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Result queryBankCardList4H5(Model_002014 model) {
		/**
		 * 获取银行卡信息
		 */
		JSONObject param = new JSONObject();
		param.put("idNum", model.getIdCard());
		JSONObject loanJson = HttpClientUtil.sendHttpPostForCredit4BindBankCard(
				Constants.CREDIT_URL_FINDBACKCARDINFO, param.toString());
		if (loanJson != null ) {
			if(!loanJson.containsKey("bankCards")){
				return Result.success();
			}
			AttachmentResponse response = brokerTradeService.getSupportedBanks();
			List<BankResponseVo> bankList = (List<BankResponseVo>) response.getAttachment();
			StringBuffer sb = new StringBuffer();
			for(BankResponseVo bank:bankList){
				sb.append(bank.getBankCode()).append("|");
			}
			loanJson.put("supportBankCodes", sb.toString());
			return Result.success(loanJson);
		}
		return Result.fail("接口异常,响应为空");
	}
	
	public Result bindBankCard4H5(Model_002015 model) {
		// 查询登录信息
//		if (null == basicRedisOpts.getSingleResult(model.getPhone())) {
//			Result.fail("会话已过期,请重新登录");
//		}
		JSONObject param = new JSONObject();
		param.put("idNum", model.getIdCard());
		param.put("bankCode", model.getBankCode());
		param.put("account", model.getAccount());
		
		param.put("bankName", model.getBankName());
		param.put("bankBranchName", model.getBankBranchName());
		
		param.put("mobile", model.getReservedMobile());

//		
//		param.put("bankName", "农业银行");
//		param.put("bankBranchName", "大拇指支行");
		param.put("checkCard", Constants.IS_CHECKED_CARD);//已绑卡
		Result re = getAllCardBound(model.getIdCard());
		if(!re.getSuccess()){
			return re;
		}
		//获取渠道
		String str = (String) re.getData();
		if(StringUtils.isEmpty(str)){
			return Result.fail("未获取渠道");
		}
		param.put("bindCardChannel", str);
		param.put("tacheCode", "0001");
		JSONObject loanJson = HttpClientUtil.sendHttpPostForCredit4BindBankCard(
				Constants.CREDIT_URL_BINDBANKCARD, param.toString());
		if (loanJson != null) {
			Calendar now = Calendar.getInstance();
			now.add(Calendar.DAY_OF_MONTH, 1);
			basicRedisOpts.persist(model.getAccount(), "true",now.getTime());
			return Result.success(loanJson);
		}
		return Result.fail("接口异常,响应为空");
	}
	
	//多渠道绑卡
	public Result getAllCardBound(String idCard){
		String channel = "";
		log.info("获取银行卡列表信息入参为："+idCard);
		JSONObject param = new JSONObject();
		param.put("idNum", idCard);
		//获取该用户绑卡列表
		JSONObject loanJson = HttpClientUtil.sendHttpPostForCredit4BindBankCard(Constants.CREDIT_URL_FINDBACKCARDINFO, param.toString());
		//JSONObject loanJson = JSONObject.fromObject("{\"bankCards\":[{\"account\":\"6228480088055777562\",\"bankBranchName\":\"南京\",\"bankCode\":\"00080004\",\"bankName\":\"中国农业银行\",\"channelInfos\":[{\"bindChannel\":\"通联支付2\",\"cardUrl\":\"http://172.16.230.24/aps/2018102611525400007/S5/220x220_3f258b29ed9749d7b5114aef8941e0bc.jpg\"}],\"checkCard\":\"01\",\"generalBankCode\":\"103\",\"masterCard\":\"01\",\"mobile\":\"13200000000\"},{\"account\":\"6222023602035848555\",\"bankBranchName\":\"无语\",\"bankCode\":\"00080003\",\"bankName\":\"中国工商银行\",\"channelInfos\":[{\"bindChannel\":\"通联支付2\",\"cardUrl\":\"http://172.16.230.24/aps/2018102611203000006/S5/220x220_3bcf6d3f4efc45a78fec5e444f8a1fa4.jpg\"}],\"checkCard\":\"01\",\"generalBankCode\":\"102\",\"masterCard\":\"01\",\"mobile\":\"13200000000\"},{\"account\":\"6228483760038218554\",\"bankBranchName\":\"看\",\"bankCode\":\"00080004\",\"bankName\":\"中国农业银行\",\"channelInfos\":[{\"bindChannel\":\"通联支付2\"}],\"checkCard\":\"01\",\"generalBankCode\":\"103\",\"masterCard\":\"01\",\"mobile\":\"13200000000\"}],\"name\":\"兰兰杨\"}");
		//JSONObject loanJson = new JSONObject();
		
		//获取所有渠道
		ArrayList<String> arrlist = new ArrayList<>();
		List<ResBMSChannelCardVO> list = getResBMSChannelCardVO();
		if(list != null && !list.isEmpty()){
			//取出所有渠道名称
			for (ResBMSChannelCardVO vo : list) {
				arrlist.add(vo.getAisleName());
			}
		}else{
			log.info("从平台获取渠道内容为空");
			return Result.fail("从平台获取渠道失败");
		}
		if(loanJson != null && loanJson.size() > 0){
			if(loanJson.containsKey("bankCards")){ 
				JSONArray ja = loanJson.getJSONArray("bankCards");
				if(ja != null && ja.size() >0){
					//获取最后一个卡片信息
					JSONObject jb = ja.getJSONObject(0).getJSONArray("channelInfos").getJSONObject(0);
					if(jb.containsKey("bindChannel") && StringUtils.isNotBlank(jb.getString("bindChannel"))){
						//获取当前渠道的下标
						int index = arrlist.indexOf(jb.getString("bindChannel"))+1;
						//根据下标去取值
						if(index < arrlist.size()){
							channel = arrlist.get(index);
						}else if(index == arrlist.size()){
							channel = arrlist.get(0);
						}
					}
				}else{
					channel = arrlist.get(0);
				}
			}
		}else{
			channel = arrlist.get(0);
		}
		return Result.success("",channel);
	}
	
	//从平台获取绑卡渠道
	public List<ResBMSChannelCardVO> getResBMSChannelCardVO(){
		ReqBMSChannelCardVO bankVo = new ReqBMSChannelCardVO();
		bankVo.setSysCode(Constants.ZDQQ_SYS_CODE);
		bankVo.setChannelId(Constants.ZDQQ_CHANNEL_CODE);
		ResListVO<ResBMSChannelCardVO> response = iBMSChannelCardExecuter.getAllCardBound(bankVo);
		if(!Constants.RESP_CODE_SUCCESS.equals(response.getRepCode())){
			log.info("从平台获取渠道借口返回结果："+response.getRepCode()+"|"+response.getRepMsg());
		}
		log.info("从平台获取渠道参数为："+ JSON.toJSONString(response.getCollections()));
		return response.getCollections();
	}
	/**
	 * 绑卡判断该卡是否已绑定
	 * @TODO
	 * @param model
	 * @return
	 * Result
	 * @author changj@yuminsoft.com
	 * @date2018年4月23日
	 */
	public Result checkCardIsBind(Model_002019 model) {
		String isBind = basicRedisOpts.getSingleResult(model.getAccount());
		JSONObject param = new JSONObject();
		param.put("isBind", false);
		if(StrUtils.isNotBlank(isBind)){
			param.put("isBind", true);
		}
		return Result.success(param);
	}
	/**
	 * 新增卡
	 * @TODO
	 * @param model
	 * @return
	 * Result
	 * @author changj@yuminsoft.com
	 * @throws Exception 
	 * @date2018年4月24日
	 */
	@SuppressWarnings("static-access")
	public Result addBankCard4H5(Model_002026 model) throws Exception {
		// 查询登录信息
//		if (null == basicRedisOpts.getSingleResult(model.getPhone())) {
//			Result.fail("会话已过期,请重新登录");
//		}
		/**
		 * 获取银行卡信息
		 */
		JSONObject param = new JSONObject();
		param.put("idNum", model.getIdCard());
		param.put("bankCode", model.getBankCode());
		param.put("account", model.getAccount());
		param.put("bankName", model.getBankName());
		param.put("bankBranchName", model.getBankBranchName());
		param.put("mobile", model.getReservedMobile());
		param.put("checkCard", Constants.IS_CHECKED_CARD);//新增卡 已绑定
		
		Result re = getAllCardBound(model.getIdCard());
		if(!re.getSuccess()){
			return re;
		}
		//获取渠道
		String str = (String) re.getData();
		if(StringUtils.isEmpty(str)){
			return Result.fail("未获取渠道");
		}
		param.put("bindCardChannel", str);
		
		param.put("tacheCode", "0001");
		if(StrUtils.isNotEmpty(model.getCardImgStr())){
			Map<String, String> queryParas = new HashMap<>();
			String batchNum = NumberUtil.getNumberForPK();
			queryParas.put("appNo", batchNum);
			queryParas.put("operator", model.getIdCard());
			queryParas.put("jobNumber", model.getIdCard());
			queryParas.put("code", "S5");// 银行卡目录
			queryParas.put("nodeKey",  "contractAward");//合同签约环节
			String result = customerService.uploadfile(queryParas,
					model.getCardImgStr());// 上传银行卡图片
			JSONObject json = JSONObject.fromObject(result);
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
			paramNew.put("idnum", model.getIdCard());
			paramNew.put("name", customer.getCustomerName());
			paramNew.put("mphone", customer.getPhone());
			paramNew.put("sex", customerService.getCarInfo(model.getIdCard()));
			HttpClientUtil.sendHttpPostForCredit4BindBankCard(Constants.CREDIT_URL_preOpenAccount, paramNew.toString());
			
		}
		JSONObject loanJson = HttpClientUtil.sendHttpPostForCredit4BindBankCard(
				Constants.CREDIT_URL_BINDBANKCARD, param.toString());
		if (loanJson != null) {
			return Result.success(loanJson);
		}
		return Result.fail("接口异常,响应为空");
	}
	/**
	 * 新增银行卡开关
	 * @Title: addBankSwich 
	 * @Description: TODO
	 * @param model
	 * @return
	 * @return: Result
	 */
	public Result addBankSwich(Model_002027 model){
		JSONObject param = new JSONObject();
		param.put("addBank", is_add_bank);
		return Result.success(param);
	}
	
	/**
	 * 客户还款获取银行卡列表
	 * @TODO
	 * @param model
	 * @return
	 * Result
	 * @author changj@yuminsoft.com
	 * @date2018年6月19日
	 */
	public Result queryBankCardList4Repayment(Model_004027 model) {
		/**
		 * 获取银行卡信息
		 */
		JSONObject param = new JSONObject();
		param.put("idNum", model.getIdCard());
		param.put("contractNum", model.getLoanNo());
		JSONObject loanJson = HttpClientUtil.sendHttpPostForCredit4BindBankCard(
				Constants.CREDIT_URL_FINDBACKCARDINFO, param.toString());
		if (loanJson != null ) {
			if(!loanJson.containsKey("bankCards")){
				return Result.success();
			}
			List<BankInfoBo> list = com.alibaba.fastjson.JSONObject.parseArray(loanJson.get("bankCards").toString(), BankInfoBo.class);
//			 Iterator<BankInfoBo> it = list.iterator();
//			  while (it.hasNext()){
//		            if(!Constants.IS_CHECKED_CARD.equals(it.next().getCheckCard())){
//		                it.remove();
//		            }
//		        }
			AttachmentResponse response = brokerTradeService.getSupportedBanks();
			List<BankResponseVo> bankList = (List<BankResponseVo>) response.getAttachment();
			StringBuffer sb = new StringBuffer();
			for(BankResponseVo bank:bankList){
				sb.append(bank.getBankCode()).append("|");
			}
			List<BankInfoBo> retList = new ArrayList<BankInfoBo>();
			for(BankInfoBo bo:list){
				if(Constants.IS_CHECKED_CARD.equals(bo.getCheckCard())){
					for(ChannelInfosBo cBo:bo.getChannelInfos()){
						if(Constants.Channel_TONGLIAN2.equals(cBo.getBindChannel())){
							if(sb.toString().contains(bo.getBankCode())){
								retList.add(bo);
							}
						}
					}
				}
			}
			list.clear();
			return Result.success(retList);
		}
		return Result.fail("接口异常,响应为空");
	}
}