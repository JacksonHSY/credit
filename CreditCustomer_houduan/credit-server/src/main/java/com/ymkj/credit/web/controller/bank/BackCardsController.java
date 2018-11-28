package com.ymkj.credit.web.controller.bank;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ymkj.credit.common.entity.BankCards;
import com.ymkj.credit.common.vo.ResultVo;
import com.ymkj.credit.service.BankCardsService;
import com.ymkj.credit.service.tpp.BrokerTradeService;
import com.ymkj.credit.web.api.anno.PageSolver;
import com.ymkj.credit.web.api.model.Result;
import com.ymkj.credit.web.api.model.base.Model_001002;
import com.ymkj.credit.web.api.model.base.Model_002016;
import com.ymkj.credit.web.api.model.base.Model_002028;
import com.ymkj.springside.modules.orm.PageInfo;
import com.zendaimoney.thirdpp.trade.pub.vo.resp.query.BankResponseVo;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 银行卡管理
 * @author YM10156
 *
 */
@Controller
@RequestMapping("/manage")
@Slf4j
public class BackCardsController {
	
	@Autowired
	BankCardsService bankCardsService;
	@Autowired
	BrokerTradeService brokerTradeService;

	@RequestMapping("/bankList")
	public String index(Model model,HttpSession httpSession){
		return "/bank/banklist";
	}
	
	@RequestMapping("/bank/bankPage")
	@ResponseBody
	public ResultVo bankPage(@PageSolver PageInfo<BankCards> page, BankCards bankCards){
		return ResultVo.returnPage(bankCardsService.selectPageByColumn(page,bankCards));
	}
	/**
	 * 添加银行卡
	 * @param bankCards
	 * @param request
	 * @return
	 */
	@RequestMapping("/bank/bankName")
	@ResponseBody
	public String addBank(BankCards bankCards,HttpServletRequest request){
		Model_001002 model = new Model_001002();
		Result result =  brokerTradeService.getSupportedBanks(model);
		JSONArray array = new JSONArray();
		if (result.getSuccess()) {
			List<BankResponseVo> str = (List<BankResponseVo>) result.getData();
			for (BankResponseVo bankResponseVo : str) {
				JSONObject obj = new JSONObject();
				obj.put("id", bankResponseVo.getBankCode());
				obj.put("text", bankResponseVo.getBankName());
				array.add(obj);
			}
		}
		return array.toString();
	}
	
	/**
	 * 发送验证码
	 * @param bankCards
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/bank/sendVerify")
	@ResponseBody
	public ResultVo sendVerify(BankCards bankCards,HttpServletRequest request) throws Exception{
		try {
			if(StringUtils.isNotEmpty(bankCards.getPhone())){
				Model_002016 model = new Model_002016();
				model.setAccount(bankCards.getAccounts());
				model.setBankCode(bankCards.getBankCode());
				model.setIdCard(bankCards.getCard());
				model.setBankName(bankCards.getBankName());
				model.setAccountName(bankCards.getName());
				model.setReservedMobile(bankCards.getPhone());
				Result result = brokerTradeService.signMessage(model);
				if(result.getSuccess()){
					return ResultVo.returnMsg(result.getSuccess(), result.getData().toString());
				}
				return ResultVo.returnMsg(false, result.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResultVo.returnMsg(false, "任务处理失败！");
	}
	
	/**
	 * 绑定银行卡
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/bank/updateVerify")
	@ResponseBody
	public ResultVo updateVerify(BankCards bankCards,HttpServletRequest request)throws Exception{
		try {
			if(bankCards != null){
				Model_002028 model = new Model_002028();
				model.setSrcFlowId(bankCards.getFlowId());
				model.setVerCode(bankCards.getVerCode());
				model.setIdCard(bankCards.getCard());
				model.setBankCode(bankCards.getBankCode());
				model.setAccount(bankCards.getAccounts());
				model.setBankName(bankCards.getBankName());
				model.setBankBranchName(bankCards.getBankBranchName());
				model.setReservedMobile(bankCards.getPhone());
				Result result = brokerTradeService.sign4H5(model);
				if(result.getSuccess()){
					return ResultVo.returnMsg(result.getSuccess(), result.getData().toString());
				}
				return ResultVo.returnMsg(false, result.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResultVo.returnMsg(false, "任务处理失败！");
	}
	
	/**
	 * 新增银行卡
	 * @param bankCards
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/bank/saveVerify")
	@ResponseBody
	public ResultVo saveVerify(BankCards bankCards,HttpServletRequest request) throws Exception{
		try {
			if(bankCards != null){
				Model_002028 model = new Model_002028();
				model.setSrcFlowId(bankCards.getFlowId());
				model.setVerCode(bankCards.getVerCode());
				model.setIdCard(bankCards.getIdCard());
				model.setBankCode(bankCards.getBankCode());
				model.setAccount(bankCards.getAccount());
				model.setBankName(bankCards.getBankName());
				model.setBankBranchName(bankCards.getBankBranchName());
				model.setReservedMobile(bankCards.getPhone());
				Result result = brokerTradeService.sign4H5(model);
				if(result.getSuccess()){
					return ResultVo.returnMsg(result.getSuccess(), result.getData().toString());
				}
				return ResultVo.returnMsg(false, result.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResultVo.returnMsg(false, "任务处理失败！");
	}
	/**
	 * 检查银行卡是否能绑定
	 * @param bankCards
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/bank/chackBankCode")
	@ResponseBody
	public ResultVo checkBankCode(BankCards bankCards,HttpServletRequest request) throws Exception{
		try {
			if(StringUtils.isNotEmpty(bankCards.getBankCode())){
				boolean flag = bankCardsService.checkBankCode(bankCards);
				if(flag)
					return ResultVo.returnMsg(true, "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResultVo.returnMsg(false, "该银行卡不予绑定！");
	}
}
