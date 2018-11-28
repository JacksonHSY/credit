package com.ymkj.credit.service.function.impl;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



import com.ymkj.credit.service.ApplyInfoService;
import com.ymkj.credit.service.BankInfoService;
import com.ymkj.credit.service.BorrowNewAndOldService;
import com.ymkj.credit.service.DictionaryService;
import com.ymkj.credit.service.LoanOrderService;
import com.ymkj.credit.service.CustomerService;
import com.ymkj.credit.service.LoginService;
import com.ymkj.credit.service.RateCalculationService;
import com.ymkj.credit.service.SignLcbService;
import com.ymkj.credit.service.SignService;
import com.ymkj.credit.service.ValidateCodeService;
import com.ymkj.credit.service.ZmscoreService;
import com.ymkj.credit.service.function.BusinessService;
import com.ymkj.credit.service.tpp.BrokerTradeService;
import com.ymkj.credit.web.api.anno.FunctionId;
import com.ymkj.credit.web.api.model.Result;
import com.ymkj.credit.web.api.model.base.Model_001001;
import com.ymkj.credit.web.api.model.base.Model_001002;
import com.ymkj.credit.web.api.model.base.Model_002009;
import com.ymkj.credit.web.api.model.base.Model_002010;
import com.ymkj.credit.web.api.model.base.Model_002011;
import com.ymkj.credit.web.api.model.base.Model_002012;
import com.ymkj.credit.web.api.model.base.Model_002013;
import com.ymkj.credit.web.api.model.base.Model_002014;
import com.ymkj.credit.web.api.model.base.Model_002015;
import com.ymkj.credit.web.api.model.base.Model_002016;
import com.ymkj.credit.web.api.model.base.Model_002017;
import com.ymkj.credit.web.api.model.base.Model_002018;
import com.ymkj.credit.web.api.model.base.Model_002019;
import com.ymkj.credit.web.api.model.base.Model_002020;
import com.ymkj.credit.web.api.model.base.Model_002021;
import com.ymkj.credit.web.api.model.base.Model_002022;
import com.ymkj.credit.web.api.model.base.Model_002023;
import com.ymkj.credit.web.api.model.base.Model_002024;
import com.ymkj.credit.web.api.model.base.Model_002025;
import com.ymkj.credit.web.api.model.base.Model_002026;
import com.ymkj.credit.web.api.model.base.Model_002027;
import com.ymkj.credit.web.api.model.base.Model_002028;
import com.ymkj.credit.web.api.model.base.Model_002029;
import com.ymkj.credit.web.api.model.base.Model_003002;
import com.ymkj.credit.web.api.model.base.Model_004011;
import com.ymkj.credit.web.api.model.base.Model_004013;
import com.ymkj.credit.web.api.model.base.Model_004014;
import com.ymkj.credit.web.api.model.base.Model_004015;
import com.ymkj.credit.web.api.model.base.Model_002001;
import com.ymkj.credit.web.api.model.base.Model_002002;
import com.ymkj.credit.web.api.model.base.Model_002003;
import com.ymkj.credit.web.api.model.base.Model_002004;
import com.ymkj.credit.web.api.model.base.Model_002005;
import com.ymkj.credit.web.api.model.base.Model_002006;
import com.ymkj.credit.web.api.model.base.Model_002007;
import com.ymkj.credit.web.api.model.base.Model_002008;
import com.ymkj.credit.web.api.model.base.Model_003001;
import com.ymkj.credit.web.api.model.base.Model_004001;
import com.ymkj.credit.web.api.model.base.Model_004002;
import com.ymkj.credit.web.api.model.base.Model_004003;
import com.ymkj.credit.web.api.model.base.Model_004005;
import com.ymkj.credit.web.api.model.base.Model_004006;
import com.ymkj.credit.web.api.model.base.Model_004007;
import com.ymkj.credit.web.api.model.base.Model_004008;
import com.ymkj.credit.web.api.model.base.Model_004009;
import com.ymkj.credit.web.api.model.base.Model_004010;
import com.ymkj.credit.web.api.model.base.Model_004012;
import com.ymkj.credit.web.api.model.base.Model_004016;
import com.ymkj.credit.web.api.model.base.Model_004017;
import com.ymkj.credit.web.api.model.base.Model_004018;
import com.ymkj.credit.web.api.model.base.Model_004019;
import com.ymkj.credit.web.api.model.base.Model_004020;
import com.ymkj.credit.web.api.model.base.Model_004021;
import com.ymkj.credit.web.api.model.base.Model_004022;
import com.ymkj.credit.web.api.model.base.Model_004023;
import com.ymkj.credit.web.api.model.base.Model_004024;
import com.ymkj.credit.web.api.model.base.Model_004025;
import com.ymkj.credit.web.api.model.base.Model_004026;
import com.ymkj.credit.web.api.model.base.Model_004027;
import com.ymkj.credit.web.api.model.base.Model_005001;
import com.ymkj.credit.web.api.model.base.Model_005002;
import com.ymkj.credit.web.api.model.base.Model_005003;
import com.ymkj.credit.web.api.model.base.Model_005004;
import com.ymkj.credit.web.api.model.base.Model_005005;
import com.ymkj.credit.web.api.model.base.Model_005006;
import com.ymkj.credit.web.api.model.base.Model_005012;
import com.ymkj.credit.web.api.model.base.Model_005013;
import com.ymkj.credit.web.api.model.base.Model_005014;
import com.ymkj.credit.web.api.model.base.Model_005015;
import com.ymkj.credit.web.api.model.base.Model_005016;
import com.ymkj.credit.web.api.model.base.Model_005017;
import com.ymkj.credit.web.api.model.base.Model_006001;
import com.ymkj.credit.web.api.model.base.Model_006002;
import com.ymkj.credit.web.api.model.base.Model_006003;
import com.ymkj.credit.web.api.model.base.Model_006004;
import com.ymkj.credit.web.api.model.base.Model_006005;
import com.ymkj.credit.web.api.model.base.Model_006006;
import com.ymkj.credit.web.api.model.base.Model_006007;
import com.ymkj.credit.web.api.model.base.Model_006008;
import com.ymkj.credit.web.api.model.base.Model_006009;
import com.ymkj.credit.web.api.model.base.Model_006010;
import com.ymkj.credit.web.api.model.base.Model_006011;
import com.ymkj.credit.web.api.model.base.Model_006013;
import com.ymkj.credit.web.api.model.base.Model_016014;
import com.ymkj.credit.web.api.model.req.ReqMain;

/**
 * APP业务接口实现
 * 
 * 
 */
@Service
public class BusinessServiceImpl implements BusinessService {
	
	@Autowired
	private ValidateCodeService validateCodeService;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private LoginService loginService;
	@Autowired
	private LoanOrderService loanOrderService;
	@Autowired
	private DictionaryService dictionaryService;
	@Autowired
	private BankInfoService bankInfoService;
	@Autowired
	private BorrowNewAndOldService borrowNewAndOldService;
	@Autowired
	private BrokerTradeService brokerTradeService;
	@Autowired
	private SignService signService;
	@Autowired
	private ApplyInfoService applyInfoService;
	@Autowired
	private SignLcbService signLcbService;
	@Autowired
	private RateCalculationService rateCalculationService;
	@Autowired
	private ZmscoreService zmscoreService;
	/**
	 * 初始化
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	@FunctionId(value = "001001", desc = "初始化APP")
	@Override
	public Result initApp(ReqMain reqMain) throws Exception {
		Model_001001 model = (Model_001001) reqMain.getReqParam();
		return loginService.init(model, reqMain.getReqHeadParam().getDeviceNum(),reqMain.getReqHeadParam().getPlatform(),reqMain.getReqHeadParam().getVersion());
	}
	
	/**
	 * 登录
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	@FunctionId(value = "002001", desc = "普通密码登录")
	@Override
	public Result login(ReqMain reqMain) throws Exception {
		Model_002001 model = (Model_002001) reqMain.getReqParam();
		String deviceNum = reqMain.getReqHeadParam().getDeviceNum();
		String platform = reqMain.getReqHeadParam().getPlatform();
		return loginService.login(model,deviceNum,platform);
	}
	
	/**
	 * 登录密码校验
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	@FunctionId(value = "002002", desc = "登录密码校验")
	@Override
	public Result validateLogin(ReqMain reqMain) throws Exception {
		Model_002002 model = (Model_002002) reqMain.getReqParam();
		return loginService.validateLogin(model);
	}
	
	/**
	 * 忘记密码
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	@FunctionId(value = "002003", desc = "忘记密码")
	@Override
	public Result forgotPassword(ReqMain reqMain) throws Exception {
		Model_002003 model = (Model_002003) reqMain.getReqParam();
		String deviceNum = reqMain.getReqHeadParam().getDeviceNum();
		return loginService.forgotPassword(model,deviceNum);
	}
	
	/**
	 * 发送短信验证码
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	@FunctionId(value = "002004", desc = "发送短信验证码")
	@Override
	public Result sendSmsCode(ReqMain reqMain) throws Exception {
		Model_002004 model = (Model_002004) reqMain.getReqParam();
		return validateCodeService.sendSmsCode(model);
	}
	
	/**
	 * 校验短信验证码
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	@FunctionId(value = "002005", desc = "校验短信")
	@Override
	public Result validateSmsCode(ReqMain reqMain) throws Exception {
		Model_002005 model = (Model_002005) reqMain.getReqParam();
		return validateCodeService.validateSmsCode(model);
	}
	
	/**
	 * 校验身份证号码
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	@FunctionId(value = "002006", desc = "校验身份证信息")
	@Override
	public Result validateIdCard(ReqMain reqMain) throws Exception {
		Model_002006 model = (Model_002006) reqMain.getReqParam();
		return customerService.validateIdCard(model);
	}
	
	/**
	 * 注册
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	@FunctionId(value = "002007", desc = "注册")
	@Override
	public Result register(ReqMain reqMain) throws Exception {
		Model_002007 model = (Model_002007) reqMain.getReqParam();
		String platform = reqMain.getReqHeadParam().getPlatform();
		return customerService.register(model,platform);
	}
	
	/**
	 * 校验客户经理工号
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	@FunctionId(value = "002008", desc = "校验客户经理工号")
	@Override
	public Result validateManagerNo(ReqMain reqMain) throws Exception {
		Model_002008 model = (Model_002008) reqMain.getReqParam();
		return customerService.validateAccountManagerNo(model);
	}
	/**
	 * 校验客户经理工号
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
//	@FunctionId(value = "002009", desc = "放弃注册")
//	@Override
//	public Result giveUpRegister(ReqMain reqMain) throws Exception {
//		Model_002009 model = (Model_002009) reqMain.getReqParam();
//		return customerService.giveUpRegister(model);
//	}
	
	/**
	 * 查询贷款信息
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	@FunctionId(value = "003001", desc = "贷款信息查询")
	@Override
	public Result queryCreditInfo(ReqMain reqMain) throws Exception {
		Model_003001 model = (Model_003001) reqMain.getReqParam();
		return customerService.queryCreditInfo(model);
	}
	
	/**
	 * 个人信息查询
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	@FunctionId(value = "004001", desc = "个人信息查询")
	@Override
	public Result queryPersonInfo(ReqMain reqMain) throws Exception {
		Model_004001 model = (Model_004001) reqMain.getReqParam();
		return loginService.queryPersonInfo(model);
	}
	
	/**
	 * 修改密码
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	@FunctionId(value = "004002", desc = "修改密码")
	@Override
	public Result modifyPassword(ReqMain reqMain) throws Exception {
		Model_004002 model = (Model_004002) reqMain.getReqParam();
//		String deviceNum = reqMain.getReqHeadParam().getDeviceNum();
		return loginService.modifyPassword(model);
	}
	
	/**
	 * 设置手势密码
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	@FunctionId(value = "004003", desc = "设置手势密码")
	@Override
	public Result setGesturePassword(ReqMain reqMain) throws Exception {
		Model_004003 model = (Model_004003) reqMain.getReqParam();
		return loginService.setGesturePassword(model);
	}
	
	
	/**
	 * 登出
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	@FunctionId(value = "004005", desc = "登出")
	@Override
	public Result logout(ReqMain reqMain) throws Exception {
		Model_004005 model = (Model_004005) reqMain.getReqParam();
		String deviceNum = reqMain.getReqHeadParam().getDeviceNum();
		return loginService.logout(model,deviceNum);
	}
	
	/**
	 *手势密码开关
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	@FunctionId(value = "004007", desc = "手势密码开关")
	@Override
	public Result changeGestureSwitch(ReqMain reqMain) throws Exception {
		Model_004007 model = (Model_004007) reqMain.getReqParam();
		return loginService.changeGestureSwitch(model);
	}
	
	/**
	 * 公告查询
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	@FunctionId(value = "004006", desc = "查询公告分页")
	@Override
	public Result queryNotices(ReqMain reqMain) throws Exception {
		Model_004006 model = (Model_004006) reqMain.getReqParam();
		return customerService.getNotices(model);
	}
	/**
	 * 首页轮播图片
	 * @param reqMain 
	 * @return
	 * @throws Exception
	 */
	@FunctionId(value = "004008", desc = "首页轮播图片")
	@Override
	public Result queryPageInfor(ReqMain reqMain) throws Exception {
		Model_004008 model = (Model_004008) reqMain.getReqParam();
		String platform = reqMain.getReqHeadParam().getPlatform();
		String version = reqMain.getReqHeadParam().getVersion();
		return loginService.queryPageInfor(model,platform,version);
	}
	/**
	 * 借款进度查询
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	@FunctionId(value = "004009", desc = "借款进度查询")
	@Override
	public Result queryBorrowMoneySchedule(ReqMain reqMain) throws Exception {
		Model_004009 model = (Model_004009) reqMain.getReqParam();
	    //return loanOrderService.queryBorrowMoneySchedule(model);
		return applyInfoService.selectByProcess(model);
	}
	
	/**
	 * 提交借款申请
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	@FunctionId(value = "004010", desc = "提交借款申请")
	@Override
	public Result subLoan(ReqMain reqMain) throws Exception {
		Model_004010 model = (Model_004010) reqMain.getReqParam();
	    return loanOrderService.createLoanOrder(model);
	}
	/**
	 * 业务员信息查询
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	@FunctionId(value = "004011", desc = "业务员信息查询")
	@Override
	public Result queryOperator(ReqMain reqMain) throws Exception {
		Model_004011 model = (Model_004011) reqMain.getReqParam();
		return loanOrderService.queryOperator(model);
	}
	
	/**
	 * 验证是否为同一人
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	@FunctionId(value = "004012", desc = "验证是否为同一人")
	@Override
	public Result isSamePerson(ReqMain reqMain) throws Exception {
		Model_004012 model = (Model_004012) reqMain.getReqParam();
		return customerService.isSamePerson(model);
	}
	/**
	 * 上传身份证识别信息
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	@FunctionId(value = "004013", desc = "上传身份证识别信息")
	@Override
	public Result uploadIdCard(ReqMain reqMain) throws Exception {
		Model_004013 model = (Model_004013) reqMain.getReqParam();
		return customerService.uploadIdCard(model);
	}
	/**
	 * 查询是否已通过身份认证
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	@FunctionId(value = "004014", desc = "查询是否已通过身份认证")
	@Override
	public Result isAuthentication(ReqMain reqMain) throws Exception {
		Model_004014 model = (Model_004014) reqMain.getReqParam();
		return customerService.isAuthentication(model);
	}
	/**
	 * 协议文本
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	@FunctionId(value = "004015", desc = "协议文本")
	@Override
	public Result protocolMes(ReqMain reqMain) throws Exception {
		Model_004015 model = (Model_004015) reqMain.getReqParam();
		return dictionaryService.getProtocol();
	}
	
	/**
	 * 费率计算
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	@FunctionId(value = "004016", desc = "费率计算")
	@Override
	public Result rateCalculation(ReqMain reqMain) throws Exception {
		Model_004016 model = (Model_004016) reqMain.getReqParam();
		return loanOrderService.rateCalculation(model);
	}
	
	/**
	 * 获取申请渠道
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	@FunctionId(value = "004017", desc = " 获取申请渠道")
	@Override
	public Result applicationChannel(ReqMain reqMain) throws Exception {
		Model_004017 model = (Model_004017) reqMain.getReqParam();
	    return loanOrderService.applicationChannel(model);
		
	}
	/**
	 * 征信报告上传
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	@FunctionId(value = "004018", desc = "征信报告上传")
	@Override
	public Result uploadCreditReport(ReqMain reqMain) throws Exception {
		Model_004018 model = (Model_004018) reqMain.getReqParam();
		return loanOrderService.uploadCreditReport(model);
	}
	/**
	 * 征信报告查询
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	@FunctionId(value = "004019", desc = "征信报告查询")
	@Override
	public Result queryCreditReport(ReqMain reqMain) throws Exception {
		Model_004019 model = (Model_004019) reqMain.getReqParam();
		return loanOrderService.queryCreditReport(model,true);
	}
	/**
	 * 查询银行卡信息列表
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	@FunctionId(value = "004020", desc = "查询银行卡信息列表")
	@Override
	public Result queryBankInfo(ReqMain reqMain) throws Exception {
		Model_004020 model = (Model_004020) reqMain.getReqParam();
		return bankInfoService.queryBankInfo(model);
	}
	/**
	 * 提交银行卡信息
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	@FunctionId(value = "004021", desc = "提交银行卡信息")
	@Override
	public Result createBankInfo(ReqMain reqMain) throws Exception {
		Model_004021 model = (Model_004021) reqMain.getReqParam();
		String customerId = reqMain.getReqHeadParam().getCustomerId();
		return bankInfoService.createBankInfo(model,customerId);
	}
	/**
	 * 查询银行卡详细信息接口
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	@FunctionId(value = "004022", desc = "查询银行卡详细信息")
	public Result queryInfoById(ReqMain reqMain) throws Exception {
		Model_004022 model = (Model_004022) reqMain.getReqParam();
		return bankInfoService.queryInfoById(model);
	}
	/**
	 * 删除银行卡信息
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	@FunctionId(value = "004023", desc = "查询银行卡详细信息")
	public Result removeBankCard(ReqMain reqMain) throws Exception {
		Model_004023 model = (Model_004023) reqMain.getReqParam();
		return bankInfoService.removeBankCard(model);
	}
	/**
	 * 绑定银行卡信息
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	@FunctionId(value = "004024", desc = "绑定银行卡信息")
	public Result bindBankCard(ReqMain reqMain) throws Exception {
		Model_004024 model = (Model_004024) reqMain.getReqParam();
		return bankInfoService.bindBankCard(model);
	}
	/**
	 * 借新还旧登录
	 * @TODO
	 * @param reqMain
	 * @return
	 * @throws Exception
	 * @author changj@yuminsoft.com
	 * @date2018年4月3日
	 */
	@FunctionId(value = "002010", desc = "借新还旧登录")
	public Result borrowNewAndOldLogin(ReqMain reqMain) throws Exception {
		Model_002010 model = (Model_002010) reqMain.getReqParam();
		return borrowNewAndOldService.login(model);
	}

	@FunctionId(value = "002011", desc = "借新还旧借款信息列表")
	public Result borrowNewAndOldOrderInfo(ReqMain reqMain) throws Exception {
		Model_002011 model = (Model_002011) reqMain.getReqParam();
		return borrowNewAndOldService.queryCreditInfo(model);
	}
	@FunctionId(value = "002012", desc = "借新还旧客户参与权限判断")
	public Result borrowNewAndOldCheckRole(ReqMain reqMain) throws Exception {
		Model_002012 model = (Model_002012) reqMain.getReqParam();
		return borrowNewAndOldService.checkJoinRole(model);
	}

	@FunctionId(value = "002013", desc = "借新还旧客户确认参与")
	public Result borrowNewAndOldSign(ReqMain reqMain) throws Exception {
		Model_002013 model = (Model_002013) reqMain.getReqParam();
		return borrowNewAndOldService.sign(model);
	}

	@FunctionId(value = "002014", desc = "绑卡获取银行卡列表")
	public Result queryBankCardList(ReqMain reqMain) throws Exception {
		Model_002014 model = (Model_002014) reqMain.getReqParam();
		return bankInfoService.queryBankCardList4H5(model);
	}
	@FunctionId(value = "002015", desc = "绑卡")
	public Result bindBankCard4H5(ReqMain reqMain) throws Exception {
		Model_002015 model = (Model_002015) reqMain.getReqParam();
		return bankInfoService.bindBankCard4H5(model);
	}

	@FunctionId(value = "002016", desc = "tpp协议支付签约短信触发接口")
	public Result signMessage(ReqMain reqMain) throws Exception {
		Model_002016 model = (Model_002016) reqMain.getReqParam();
		return brokerTradeService.signMessage(model);
	}

	@FunctionId(value = "002017", desc = "tpp协议支付签约")
	public Result sign(ReqMain reqMain) throws Exception {
		Model_002017 model = (Model_002017) reqMain.getReqParam();
		return brokerTradeService.sign(model);
	}

	@FunctionId(value = "002018", desc = "h5判断登录")
	public Result checkLogin4h5(ReqMain reqMain) throws Exception {
		Model_002018 model = (Model_002018) reqMain.getReqParam();
		return loginService.checkLogin4h5(model);
	}

	@FunctionId(value = "002019", desc = "绑卡判断该卡是否已绑定")
	public Result checkCardIsBind(ReqMain reqMain) throws Exception {
		Model_002019 model = (Model_002019) reqMain.getReqParam();
		return bankInfoService.checkCardIsBind(model);
	}

	@FunctionId(value = "002020", desc = "征审获取合同签名结果")
	public Result getSignContractStatus(ReqMain reqMain) throws Exception {
		Model_002020 model = (Model_002020) reqMain.getReqParam();
		return signService.getSignContractStatus(model);
	}

	@FunctionId(value = "002021", desc = "查询合同列表")
	public Result findSignContractListInfos(ReqMain reqMain) throws Exception {
		Model_002021 model = (Model_002021) reqMain.getReqParam();
		return signService.findSignContractListInfos(model);
	}

	@FunctionId(value = "002022", desc = "加载合同文件")
	public String loadContractFile(ReqMain reqMain) throws Exception {
		Model_002022 model = (Model_002022) reqMain.getReqParam();
		return signService.loadContractFile(model);
	}

	@FunctionId(value = "002023", desc = "发送短信验证码")
	public Result sendValidCode(ReqMain reqMain) throws Exception {
		Model_002023 model = (Model_002023) reqMain.getReqParam();
		return signService.sendValidCode(model);
	}

	@FunctionId(value = "002024", desc = "校验短信验证码")
	public Result checkValidCode(ReqMain reqMain) throws Exception {
		Model_002024 model = (Model_002024) reqMain.getReqParam();
		return signService.checkValidCode(model);
	}

	@FunctionId(value = "002025", desc = "客户签章")
	public Result clientSinature(ReqMain reqMain) throws Exception {
		Model_002025 model = (Model_002025) reqMain.getReqParam();
		return signService.clientSinature(model);
	}
	@FunctionId(value = "002028", desc = "tpp协议支付签约")
	public Result sign4H5(ReqMain reqMain) throws Exception {
		Model_002028 model = (Model_002028) reqMain.getReqParam();
		return brokerTradeService.sign4H5(model);
	}
	@FunctionId(value = "001002", desc = "tpp支持银行卡列表")
	public Result getSupportedBanks(ReqMain reqMain) throws Exception {
		Model_001002 model = (Model_001002) reqMain.getReqParam();
		return brokerTradeService.getSupportedBanks(model);
	}

	@FunctionId(value = "002026", desc = "H5绑卡新增银行卡")
	public Result addBankCard4H5(ReqMain reqMain) throws Exception {
		Model_002026 model = (Model_002026) reqMain.getReqParam();
		return bankInfoService.addBankCard4H5(model);
	}

	@FunctionId(value = "002027", desc = "判断添加银行卡")
	public Result addBankSwich(ReqMain reqMain) throws Exception {
		Model_002027 model = (Model_002027) reqMain.getReqParam();
		return bankInfoService.addBankSwich(model);
	}

	@FunctionId(value = "004025", desc = "客户还款")
	public Result realTimeRepayment(ReqMain reqMain) throws Exception {
		Model_004025 model = (Model_004025) reqMain.getReqParam();
		return customerService.realTimeRepayment(model);
	}

	@FunctionId(value = "004026", desc = "更新同意注册协议")
	public Result updateAgreement(ReqMain reqMain) throws Exception {
		Model_004026 model = (Model_004026) reqMain.getReqParam();
		return loginService.updateAgreement(model);
	}

	@FunctionId(value = "004027", desc = "客户还款获取银行卡列表")
	public Result queryBankCardList4Repayment(ReqMain reqMain) throws Exception {
		Model_004027 model = (Model_004027) reqMain.getReqParam();
		return bankInfoService.queryBankCardList4Repayment(model);
	}

	@FunctionId(value = "003002", desc = "查询贷款详情,还款页面")
	public Result queryCreditInfoDetail(ReqMain reqMain) throws Exception {
		Model_003002 model = (Model_003002) reqMain.getReqParam();
		return customerService.queryCreditInfoDetail(model);
	}
	
	@FunctionId(value ="005001",desc="查询是否能借款")
	public Result checkLoan(ReqMain reqMain) throws Exception {
		Model_005001 model = (Model_005001) reqMain.getReqParam();
		return applyInfoService.checkLoan(model);
	}
	
	@FunctionId(value = "005002", desc = "贷款申请，以及借款编号生成")
	public Result CreditInfo(ReqMain reqMain) throws Exception {
		Model_005002 model = (Model_005002) reqMain.getReqParam();
		return applyInfoService.CreditInfo(model);
	}

	@FunctionId(value = "005003", desc = "查询借款申请信息")
	public Result checkCreditInfo(ReqMain reqMain) throws Exception {
		Model_005003 model = (Model_005003)reqMain.getReqParam();
		return applyInfoService.checkCreditInfo(model);
	}
	
	@FunctionId(value = "005004", desc = "申请信息保存or修改")
	public Result psersonInfo(ReqMain reqMain) throws Exception {
		Model_005004 model = (Model_005004)reqMain.getReqParam();
		return applyInfoService.psersonInfo(model);
	}
	
	@FunctionId(value = "005005", desc = "根据节点获取借款信息")
	public Result queryNodeInfo(ReqMain reqMain) throws Exception {
		Model_005005 model = (Model_005005)reqMain.getReqParam();
		return applyInfoService.queryNodeInfo(model);
	}
	
	@FunctionId(value = "005012", desc = "图片上传")
	public Result upPicture(ReqMain reqMain)throws Exception {
		Model_005012 model = (Model_005012)reqMain.getReqParam();
		return null;
	}
//	@FunctionId(value = "005013", desc = "图片删除")
//	public Result deletePicture(ReqMain reqMain)throws Exception {
//		Model_005013 model = (Model_005013)reqMain.getReqParam();
//		return applyInfoService.deletePicture(model);
//	}
	@FunctionId(value = "005014", desc = "推送录单")
	public Result pushToBms(ReqMain reqMain)throws Exception {
		Model_005014 model = (Model_005014)reqMain.getReqParam();
		return applyInfoService.pushToBms(model);
	}
	
	/*@FunctionId(value = "005015", desc = "前前进件借款进度")
	public Result selectByProcess(ReqMain reqMain)throws Exception {
		Model_005015 model = (Model_005015)reqMain.getReqParam();
		return applyInfoService.selectByProcess(model);
	}*/
	
	@FunctionId(value = "005016", desc = "首页借款进度的提示")
	public Result selectByFlag(ReqMain reqMain)throws Exception {
		Model_005016 model = (Model_005016)reqMain.getReqParam();
		return applyInfoService.selectByFlag(model);
	}
	@FunctionId(value = "006001",desc = "签约h5获取验证码")
	public Result signVerifyCode(ReqMain reqMain) throws Exception {
		Model_006001 model = (Model_006001)reqMain.getReqParam();
		return signLcbService.VerificationCode(model);
	}

	@FunctionId(value = "006002",desc = "签约H5与捞财宝注册登录")
	public Result signRegisterLogin(ReqMain reqMain) throws Exception {
		Model_006002 model = (Model_006002)reqMain.getReqParam();
		return signLcbService.RegisterLogin(model);
	}

	@FunctionId(value = "006003",desc = "签约H5与捞财宝绑卡")
	public Result signBankCard(ReqMain reqMain) throws Exception {
		Model_006003 model = (Model_006003)reqMain.getReqParam();
		return signLcbService.BankCard(model);
	}

	@FunctionId(value ="005006",desc = "规则引擎校验是否可以提交")
	public Result ruleCheck(ReqMain reqMain) throws Exception {
		Model_005006 model = (Model_005006)reqMain.getReqParam();
		return applyInfoService.ruleCheck(model);
	}
	
	@FunctionId(value ="005017",desc = "获取征信报告")
	public Result requestDirect(ReqMain reqMain) throws Exception {
		Model_005017 model = (Model_005017)reqMain.getReqParam();
		return applyInfoService.requestDirect(model);
	}

	@FunctionId(value = "006004",desc = "APP端合同签章")
	public Result signAppContract(ReqMain reqMain) throws Exception {
		Model_006004 model = (Model_006004)reqMain.getReqParam();
		return signLcbService.getNotSignContract(model);
	}

	@FunctionId(value = "006005",desc="H5要签章的图片")
	public Result signContractImages(ReqMain reqMain) throws Exception {
		Model_006005 model = (Model_006005)reqMain.getReqParam();
		return signLcbService.signContractImages(model);
	}

	@FunctionId(value = "006006",desc = "H5从核心获取图片")
	public Result getPicImages(ReqMain reqMain) throws Exception {
		Model_006006 model = (Model_006006)reqMain.getReqParam();
		return signLcbService.getPicImages(model);
	}

	@FunctionId(value = "006107",desc = "合同签约")
	public Result signContract(ReqMain reqMain) throws Exception {
		Model_006004 model = (Model_006004)reqMain.getReqParam();
		return signLcbService.signContract(model);
	}

	@FunctionId(value = "006007",desc = "获取银行卡信息")
	public Result getOneBankInfo(ReqMain reqMain) throws Exception {
		Model_006007 model = (Model_006007)reqMain.getReqParam();
		return signLcbService.getOneBankInfo(model);
	}
	
	@FunctionId(value = "006008",desc = "捞财宝实名认证")
	public Result realName(ReqMain reqMain) throws Exception {
		Model_006008 model = (Model_006008)reqMain.getReqParam();
		return signLcbService.realName(model);
	}
	
	@FunctionId(value = "006009",desc = "前前费率试算对接后台配置参数获取")
	public Result GetrateCalculationParam(ReqMain reqMain) throws Exception {
		Model_006009 model = (Model_006009)reqMain.getReqParam();
		return rateCalculationService.selectParam(model);
	}
	
	@FunctionId(value = "006010",desc = "查询是否可以到签约")
	public Result checkWorkflow(ReqMain reqMain) throws Exception {
		Model_006010 model = (Model_006010)reqMain.getReqParam();
		return signLcbService.checkWorkflow(model);
	}

	@FunctionId(value = "002029",desc = "捞财宝签约")
	public Result contractLcb(ReqMain reqMain) throws Exception {
		Model_002029 model = (Model_002029)reqMain.getReqParam();
		return signService.contractLcb(model);
	}

	@FunctionId(value = "006011",desc = "根据银行卡号获取银行卡信息")
	public Result GetBankMessage(ReqMain reqMain) throws Exception {
		Model_006011 model = (Model_006011)reqMain.getReqParam();
		return signLcbService.getBankMessage(model);
	}
	
	@FunctionId(value = "006013",desc = "h5获取芝麻分")
	public Result getScore(ReqMain reqMain) throws Exception {
		Model_006013 model = (Model_006013)reqMain.getReqParam();
		return zmscoreService.getScore(model);
	}

	@FunctionId(value = "016014",desc = "获取第三方数据超市的productID")
	public Result getProductid(ReqMain reqMain) throws Exception {
		Model_016014 model = (Model_016014)reqMain.getReqParam();
		return zmscoreService.getProductid(model);
	}
}
