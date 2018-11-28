package com.ymkj.credit.service.dms;



import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.ymkj.base.core.biz.api.message.Response;
import com.ymkj.credit.common.constants.Constants;
import com.ymkj.credit.common.ex.BussErrorCode;





import com.ymkj.credit.common.untils.MD5;
import com.ymkj.credit.network.HttpClientUtil;
import com.ymkj.dms.api.bank.bo.BankBO;
import com.ymkj.dms.api.common.base.AppNewLoanInfo;
import com.ymkj.dms.api.common.base.BankCardBO;
import com.ymkj.dms.api.common.base.SignResponseBO;
import com.ymkj.dms.api.laocaibao.bo.LaocaibaoFlowBO;
import com.ymkj.dms.api.laocaibao.service.LaocaibaoSignFlowService;
import com.ymkj.dms.api.laocaibao.service.LaocaibaoSignNoticeService;
import com.ymkj.dms.api.laocaibao.service.LaocaibaoSignService;
import com.ymkj.dms.api.vo.request.master.ReqBMSLoanBaseVO;
import com.ymkj.dms.api.vo.response.sign.ResBMSNameaAndIdAndphone;
import com.ymkj.springside.modules.exception.BusinessException;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**	
 * 录单接口
 * 
 * @author changj@yuminsoft.com
 * @date2018年3月2日
 * @version 1.0
 */
@Slf4j
@Service
public class DmsService{

	@Autowired
	private LaocaibaoSignFlowService laocaibaoSignFlowService;

	@Autowired
	private LaocaibaoSignService laocaibaoSignService;
	
	@Autowired
	private LaocaibaoSignNoticeService laocaibaoSignNoticeService;
	
	@Autowired
	private com.ymkj.dms.api.bank.service.BankService bankService;

	/**
	 * 查询签约信息
	 */
	public List<SignResponseBO> getSign(String idCardNo) {
		log.info("调用录单查询签约信息接口入参:" + idCardNo);
		Response<List<SignResponseBO>> res = laocaibaoSignFlowService
				.getSign(idCardNo);
		if (null == res) {
			throw new BusinessException(
					BussErrorCode.ERROR_CODE_6001.getErrorcode(),
					BussErrorCode.ERROR_CODE_6001.getErrordesc());
		}
		if (!res.getRepCode().equals("000000")) {
			log.info("调用录单查询签约信息失败,返回内容:" + res.getRepMsg());
			throw new BusinessException(
					BussErrorCode.ERROR_CODE_6002.getErrorcode(),
					res.getRepMsg());
		}
		log.info("调用录单查询签约信息成功,返回申请单条数:" + (res.getData()==null?0:res.getData().size()));
		return res.getData();
	}
	/**
	 * 保存银行卡信息
	 * @TODO
	 * @param bankCard
	 * @return
	 * String
	 * @author changj@yuminsoft.com
	 * @date2018年3月2日
	 */
	public String saveBankCard(BankCardBO bankCard) {
		log.info("调用录单保存银行卡接口入参:卡号" + bankCard.getApplyBankCardNo()+",支行"+bankCard.getApplyBankBranch()+",银行ID"+bankCard.getApplyBankNameId()+",预留手机号:"+bankCard.getBankPhone()+",借款编号:"+bankCard.getLoanNo());
		Response<String> res = laocaibaoSignFlowService.saveBankCard(bankCard);
		if (null == res) {
			throw new BusinessException(
					BussErrorCode.ERROR_CODE_6003.getErrorcode(),
					BussErrorCode.ERROR_CODE_6003.getErrordesc());
		}
		if (!res.getRepCode().equals("000000")) {
			log.info("调用录单保存银行卡接口失败,返回内容:" + res.getRepMsg());
			throw new BusinessException(
					BussErrorCode.ERROR_CODE_6004.getErrorcode(),
					res.getRepMsg());
		}
		log.info("调用录单保存银行卡接口成功,返回code:" + res.getRepCode()+",内容:"+res.getRepMsg());
		return res.getRepCode();
	}
	/**
	 * 签订接口
	 * @TODO
	 * @param bo
	 * @return
	 * String
	 * @author changj@yuminsoft.com
	 * @date2018年3月2日
	 */
//	public String sign(LaocaibaoFlowBO bo) {
//		log.info("调用录单签订接口入参:LoanNo==" + bo.getLoanNo());
//		Response<String> res = laocaibaoSignFlowService.sign(bo);
//		if (null == res) {
//			throw new BusinessException(
//					BussErrorCode.ERROR_CODE_6005.getErrorcode(),
//					BussErrorCode.ERROR_CODE_6005.getErrordesc());
//		}
//		if (!res.getRepCode().equals("000000")) {
//			log.info("调用录单签订接口失败,返回内容:" + res.getRepMsg());
//			throw new BusinessException(
//					BussErrorCode.ERROR_CODE_6006.getErrorcode(),
//					res.getRepMsg());
//		}
//		log.info("调用录单签订接口成功,返回code:" + res.getRepCode()+",内容:"+res.getRepMsg());
//		return res.getRepCode();
//	}
	/**
	 * 发送短信验证码
	 * @TODO
	 * @param phone
	 * @return
	 * String
	 * @author changj@yuminsoft.com
	 * @date2018年3月2日
	 */
	public String getValidateCode(String phone) {
		log.info("调用录单发送短信验证码接口入参:" + phone);
		Response<String> res = laocaibaoSignService.getValidateCode(phone);
		if (null == res) {
			throw new BusinessException(
					BussErrorCode.ERROR_CODE_6007.getErrorcode(),
					BussErrorCode.ERROR_CODE_6007.getErrordesc());
		}
		if (!res.getRepCode().equals("000000")) {
			log.info("调用录单发送短信验证码接口失败,返回内容:" + res.getRepMsg());
			throw new BusinessException(
					BussErrorCode.ERROR_CODE_6008.getErrorcode(),
					 res.getRepMsg());
		}
		log.info("调用录单发送短信验证码接口成功,返回内容:" + res.getRepMsg());
		return res.getRepMsg();
	}
	/**
	 * 电子签章通知
	 * @TODO
	 * @param loanNo
	 * @return
	 * String
	 * @author changj@yuminsoft.com
	 * @date2018年3月2日
	 */
	public String notice(String loanNo) {
		log.info("调用录单电子签章通知接口入参:" + loanNo);
		Response<String> res = laocaibaoSignNoticeService.notice(loanNo);
		if (null == res) {
			throw new BusinessException(
					BussErrorCode.ERROR_CODE_6009.getErrorcode(),
					BussErrorCode.ERROR_CODE_6009.getErrordesc());
		}
		if (!res.getRepCode().equals("000000")) {
			log.info("调用录单电子签章通知接口失败,返回内容:" + res.getRepMsg());
			throw new BusinessException(
					BussErrorCode.ERROR_CODE_6009.getErrorcode(),
					res.getRepMsg());
		}
		log.info("调用录单电子签章通知接口成功,返回内容:" + res.getRepMsg());
		return res.getData();
	}
	/**
	 * 
	 * @TODO
	 * @return
	 * List<BankBO>
	 * @author changj@yuminsoft.com
	 * @date2018年3月7日
	 */
	public Map<String,Integer> getBankList(){
		Response<List<BankBO>> res = bankService.listByChannelCode("00016");
		if (null == res) {
			throw new BusinessException(
					BussErrorCode.ERROR_CODE_6011.getErrorcode(),
					BussErrorCode.ERROR_CODE_6011.getErrordesc());
		}
		if (!res.getRepCode().equals("000000")) {
			log.info("调用录单电子签章通知接口失败,返回内容:" + res.getRepMsg());
			throw new BusinessException(
					BussErrorCode.ERROR_CODE_6012.getErrorcode(),
					res.getRepMsg());
		}
		Map<String,Integer> map = new HashMap<String, Integer>();
		List<BankBO> list = res.getData();
		if(list!=null &&list.size()>0){
			for(BankBO bo:list){
				map.put(bo.getTppCode(), bo.getId());
			}
		}
		return map;
		
	}
	/**
	 * 存管网关查询捞财宝签约状态
	 * @TODO
	 * @param loanNo
	 * @return
	 * JSONObject
	 * @author changj@yuminsoft.com
	 * @date2018年3月30日
	
	public JSONObject getTargetStatusByCg(String loanNo) {
		try {
			SortedMap<String, Object> map = new TreeMap<String, Object>();
			map.put("borrowNo", loanNo);
			String sign = getSign(map);
			map.put("sign", sign);
			return HttpClientUtil.sendHttpPostForCunGuan(map);
		} catch (Exception e) {
			e.getStackTrace();
		}
		return null;
	}
	 */
	
	public boolean getTargetStatusByDms(String loanNo) {
		try {
			ReqBMSLoanBaseVO bv = new ReqBMSLoanBaseVO();
			bv.setLoanNo(loanNo);
			bv.setSysCode(Constants.CONTRACT_APP);
			Response<String> res  = 	laocaibaoSignFlowService.newContractSigningLcb(bv);
			if (null == res) {
				throw new BusinessException(
						BussErrorCode.ERROR_CODE_6013.getErrorcode(),
						BussErrorCode.ERROR_CODE_6013.getErrordesc());
			}
			log.info("查询录单捞财宝合同签订状态接口返回res.getRepCode()=="+res.getRepCode()+",res.getData()=="+res.getData()+",res.getRepMsg()=="+res.getRepMsg());
			if (!res.getRepCode().equals("000000")) {
				throw new BusinessException(
						BussErrorCode.ERROR_CODE_6014.getErrorcode(),
						res.getRepMsg());
			}else{
				if(	"2A".equals(res.getData())){ //待捞财宝签约
					return false;
				}
				if("3".equals(res.getData())){//捞财宝已经签约
					return true;
				}
			}
		} catch (Exception e) {
			e.getStackTrace();
		}
		return false;
	}
  /**
   * 生成签名
   * @TODO
   * @param parameters
   * @return
   * @throws Exception
   * String
   * @author changj@yuminsoft.com
   * @date2018年3月2日
 
	  protected static String getSign( SortedMap<String, Object> parameters) throws Exception{
	    StringBuilder signBuilder = new StringBuilder();
	    for (Entry<String, Object> entry : parameters.entrySet()) {
	      String k = entry.getKey();
	      Object v = entry.getValue();
	      if(null != v && !"".equals(v)  && !"sign".equals(k) && !"key".equals(k)) {
	        signBuilder.append(k + "=" + v + "&");
	      }
	    }
	    log.info("传递的参数为-->{}",signBuilder.toString());
	    //拼接key进行MD5签名
	    signBuilder.append("key=" + HttpClientUtil.getCgKey());
	    log.info("cgKey-->{}",HttpClientUtil.getCgKey());
	    String sign = MD5.md5(signBuilder.toString(), "UTF-8").toUpperCase();
	    log.info("前置系统sign签名为-->{}",sign);
	    return sign;
	  }
	    */
	 /**
	  * 根据借款编号查询客户信息
	  * @TODO
	  * @param loanNo
	  * @return
	  * ResBMSNameaAndIdAndphone
	  * @author changj@yuminsoft.com
	  * @date2018年4月2日
	  */
	public ResBMSNameaAndIdAndphone getCustomerInfoByDms(String loanNo) {
		try {
			ReqBMSLoanBaseVO bv = new ReqBMSLoanBaseVO();
			bv.setLoanNo(loanNo);
			bv.setSysCode(Constants.CONTRACT_APP);
			Response<ResBMSNameaAndIdAndphone> res = laocaibaoSignFlowService.findNameAndIdAndPhoneBrrowNOByBrrowNO(bv);
			if (null == res) {
				throw new BusinessException(
						BussErrorCode.ERROR_CODE_6015.getErrorcode(),
						BussErrorCode.ERROR_CODE_6015.getErrordesc());
			}
			if (!res.getRepCode().equals("000000")) {
				throw new BusinessException(
						BussErrorCode.ERROR_CODE_6016.getErrorcode(),
						res.getRepMsg());
			} else {
				return res.getData();
			}
		} catch (Exception e) {
			e.getStackTrace();
		}
		return null;
	}
	/**
	 * 借新还旧录单确认参与接口
	 * @TODO
	 * @param loanNo
	 * @return
	 * String
	 * @author changj@yuminsoft.com
	 * @date2018年4月10日
	 */
	public String borrowNew(AppNewLoanInfo loanNo) {
		log.info("调用录单借新还旧确认参与接口入参:OldLoanNo=" + loanNo.getOldLoanNo()+",IdNo="+loanNo.getIdNo()+",SysCode="+loanNo.getSysCode());
		Response<String> res = 	laocaibaoSignFlowService.borrowNew(loanNo);
		if (null == res) {
			throw new BusinessException(
					BussErrorCode.ERROR_CODE_6017.getErrorcode(),
					BussErrorCode.ERROR_CODE_6017.getErrordesc());
		}
		if (res.getRepCode().equals("400001")) {
			log.info("调用录单借新还旧确认参与接口,返回内容:" + res.getRepMsg());
			throw new BusinessException(
					"400001","已参与过该活动");
		}
		if (!res.getRepCode().equals("000000")) {
			log.info("调用录单借新还旧确认参与接口,返回内容:" + res.getRepMsg());
			throw new BusinessException(
					BussErrorCode.ERROR_CODE_6018.getErrorcode(),
					res.getRepMsg());
		}
		log.info("调用录单借新还旧确认参与接口,返回内容:" + res.getData());
		return res.getData();
	}
}