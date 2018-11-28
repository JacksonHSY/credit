package com.ymkj.credit.service.function;

import javax.servlet.http.HttpServletResponse;

import com.ymkj.credit.web.api.model.Result;
import com.ymkj.credit.web.api.model.req.ReqMain;

/**
 * 对接APP业务接口
 * 
 * @author Cherish
 *
 */
public interface BusinessService extends FunctionService {
	
	/**
	 * 初始化
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result initApp(ReqMain reqMain) throws Exception;
	
	/**
	 * 登录
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result login(ReqMain reqMain) throws Exception;
	
	/**
	 * 登录密码校验
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result validateLogin(ReqMain reqMain) throws Exception;
		
	/**
	 * 忘记密码
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result forgotPassword(ReqMain reqMain) throws Exception;
	
	/**
	 * 发送短信验证码
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result sendSmsCode(ReqMain reqMain) throws Exception;
	
	/**
	 * 校验短信验证码
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result validateSmsCode(ReqMain reqMain) throws Exception;
	
	/**
	 * 校验身份证号码
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result validateIdCard(ReqMain reqMain) throws Exception;
	
	/**
	 * 注册
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result register(ReqMain reqMain) throws Exception;
	
	/**
	 * 校验经理工号
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result validateManagerNo(ReqMain reqMain) throws Exception;
	
	/**
	 * 查询贷款信息
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result queryCreditInfo(ReqMain reqMain) throws Exception;
	
	/**
	 * 查询贷款信息详情
	 * @TODO
	 * @param reqMain
	 * @return
	 * @throws Exception
	 * Result
	 * @author changj@yuminsoft.com
	 * @date2018年6月22日
	 */
	public Result queryCreditInfoDetail(ReqMain reqMain) throws Exception;
	/**
	 * 个人信息查询
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result queryPersonInfo(ReqMain reqMain) throws Exception;
	
	/**
	 * 修改密码
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result modifyPassword(ReqMain reqMain) throws Exception;
	
	/**
	 * 设置手势密码
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result setGesturePassword(ReqMain reqMain) throws Exception;
	
	/**
	 * 登出
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result logout(ReqMain reqMain) throws Exception;
	
	/**
	 * 手势密码开关
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result changeGestureSwitch(ReqMain reqMain) throws Exception;
	
	/**
	 * 公告查询
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result queryNotices(ReqMain reqMain) throws Exception;
	
	/**
	 * 首页数据查询
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result queryPageInfor(ReqMain reqMain) throws Exception;
	/**
	 * 验证是否为同一人
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result isSamePerson(ReqMain reqMain) throws Exception;
	/**
	 * 借款进度查询
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result queryBorrowMoneySchedule(ReqMain reqMain) throws Exception;
	/**
	 * 提交借款申请
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	Result subLoan(ReqMain reqMain) throws Exception;
	/**
	 * 协议文本
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	Result protocolMes(ReqMain reqMain) throws Exception;
	/**
	 * 上传身份证识别信息
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	Result uploadIdCard(ReqMain reqMain) throws Exception;
	/**
	 * 费率计算
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	Result rateCalculation(ReqMain reqMain) throws Exception;
	/**
	 * 征信报告查询
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	Result queryCreditReport(ReqMain reMain) throws Exception;
	/**
	 * 申请渠道
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	Result applicationChannel(ReqMain reqMain) throws Exception;
	/**
	 *  征信报告上传
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	Result uploadCreditReport(ReqMain reqMain) throws Exception;
	/**
	 * 业务员信息查询
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	Result queryOperator(ReqMain reqMain) throws Exception;
	/**
	 * 查询是否已通过身份认证
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	Result isAuthentication(ReqMain reqMain) throws Exception;
	/**
	 * 查询银行卡信息列表
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	Result queryBankInfo(ReqMain reqMain) throws Exception;
	/**
	 * 提交银行卡信息
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	Result createBankInfo(ReqMain reqMain) throws Exception;
	/**
	 * 查询银行卡详细信息
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	Result queryInfoById(ReqMain reqMain) throws Exception;
	
	/**
	 * 删除银行卡
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	Result removeBankCard(ReqMain reqMain) throws Exception;
	
	/**
	 * 绑定银行卡
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	Result bindBankCard(ReqMain reqMain) throws Exception;
	/**
	 * 放弃注册
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
//	Result giveUpRegister(ReqMain reqMain) throws Exception;
	/**
	 * 借新还旧
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result borrowNewAndOldLogin(ReqMain reqMain) throws Exception;
	/**
	 * 参与权限
	 * @TODO
	 * @param reqMain
	 * @return
	 * @throws Exception
	 * Result
	 * @author changj@yuminsoft.com
	 * @date2018年4月9日
	 */
	public Result borrowNewAndOldCheckRole(ReqMain reqMain) throws Exception;
	/**
	 * 借新还旧订单列表
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result borrowNewAndOldOrderInfo(ReqMain reqMain) throws Exception;
	
	/**
	 * 借新还旧确认参与
	 * @TODO
	 * @param reqMain
	 * @return
	 * @throws Exception
	 * Result
	 * @author changj@yuminsoft.com
	 * @date2018年4月10日
	 */
	public Result borrowNewAndOldSign(ReqMain reqMain) throws Exception;
	
	/**
	 * 绑卡查询银行卡列表
	 * @TODO
	 * @param reqMain
	 * @return
	 * @throws Exception
	 * Result
	 * @author changj@yuminsoft.com
	 * @date2018年4月13日
	 */
	public Result queryBankCardList(ReqMain reqMain) throws Exception;
	
	/**
	 * 客户还款选择银行卡列表
	 * @TODO
	 * @param reqMain
	 * @return
	 * @throws Exception
	 * Result
	 * @author changj@yuminsoft.com
	 * @date2018年6月19日
	 */
	public Result queryBankCardList4Repayment(ReqMain reqMain) throws Exception;
	/**
	 * tpp	协议支付签约短信触发接口
	 * @TODO
	 * @param reqMain
	 * @return
	 * @throws Exception
	 * Result
	 * @author changj@yuminsoft.com
	 * @date2018年4月13日
	 */
	public Result signMessage(ReqMain reqMain) throws Exception;
	/**
	 * tpp协议支付签约接口
	 * @TODO
	 * @param reqMain
	 * @return
	 * @throws Exception
	 * Result
	 * @author changj@yuminsoft.com
	 * @date2018年4月13日
	 */
	public Result sign(ReqMain reqMain) throws Exception;
	/**
	 * tpp协议支付签约接口
	 * @TODO
	 * @param reqMain
	 * @return
	 * @throws Exception
	 * Result
	 * @author changj@yuminsoft.com
	 * @date2018年4月13日
	 */
	public Result sign4H5(ReqMain reqMain) throws Exception;
	/**
	 * h5判断登录
	 * @TODO
	 * @param reqMain
	 * @return
	 * @throws Exception
	 * Result
	 * @author changj@yuminsoft.com
	 * @date2018年4月16日
	 */
	public Result checkLogin4h5(ReqMain reqMain) throws Exception;
	
	/**
	 * 判断卡号是否已绑定
	 * @TODO
	 * @param reqMain
	 * @return
	 * @throws Exception
	 * Result
	 * @author changj@yuminsoft.com
	 * @date2018年4月23日
	 */
	public Result checkCardIsBind(ReqMain reqMain) throws Exception;
	/**
	 * 获取合同签名结果
	 * @TODO
	 * @param reqMain
	 * @return
	 * @throws Exception
	 * Result
	 * @author changj@yuminsoft.com
	 * @date2018年4月24日
	 */
	public Result getSignContractStatus(ReqMain reqMain) throws Exception;
	/**
	 * 查询合同列表
	 * @TODO
	 * @param reqMain
	 * @return
	 * @throws Exception
	 * Result
	 * @author changj@yuminsoft.com
	 * @date2018年4月24日
	 */
	public Result findSignContractListInfos(ReqMain reqMain) throws Exception;
	/**
	 * 加载合同文件
	 * @TODO
	 * @param reqMain
	 * @return
	 * @throws Exception
	 * Result
	 * @author changj@yuminsoft.com
	 * @date2018年4月24日
	 */
	public String loadContractFile(ReqMain reqMain) throws Exception;
	/**
	 * 发送短信验证码
	 * @TODO
	 * @param reqMain
	 * @return
	 * @throws Exception
	 * Result
	 * @author changj@yuminsoft.com
	 * @date2018年4月24日
	 */
	public Result sendValidCode(ReqMain reqMain) throws Exception;
	/**
	 * 校验短信验证码
	 * @TODO
	 * @param reqMain
	 * @return
	 * @throws Exception
	 * Result
	 * @author changj@yuminsoft.com
	 * @date2018年4月24日
	 */
	public Result checkValidCode(ReqMain reqMain) throws Exception;
	/**
	 * 客户签章
	 * @TODO
	 * @param reqMain
	 * @return
	 * @throws Exception
	 * Result
	 * @author changj@yuminsoft.com
	 * @date2018年4月24日
	 */
	public Result clientSinature(ReqMain reqMain) throws Exception;
	
	/**
	 * TPP支持银行卡列表
	 * @TODO
	 * @param reqMain
	 * @return
	 * @throws Exception
	 * Result
	 * @author changj@yuminsoft.com
	 * @date2018年4月24日
	 */
	public Result getSupportedBanks(ReqMain reqMain) throws Exception;
	/**
	 * H5绑卡新增银行卡
	 * @TODO
	 * @param reqMain
	 * @return
	 * @throws Exception
	 * Result
	 * @author changj@yuminsoft.com
	 * @date2018年4月24日
	 */
	public Result addBankCard4H5(ReqMain reqMain) throws Exception;
	
	/**
	 * 判断添加银行卡按钮
	 * @Title: addBankCard4H5 
	 * @Description: TODO
	 * @param reqMain
	 * @return
	 * @throws Exception
	 * @return: Result
	 */
	public Result addBankSwich(ReqMain reqMain) throws Exception;
	
	/**
	 * 客户还款接口
	 * @TODO
	 * @param reqMain
	 * @return
	 * @throws Exception
	 * Result
	 * @author changj@yuminsoft.com
	 * @date2018年6月14日
	 */
	public Result realTimeRepayment(ReqMain reqMain) throws Exception;
	
	/**
	 * 同意注册协议
	 * @TODO
	 * @param reqMain
	 * @return
	 * @throws Exception
	 * Result
	 * @author changj@yuminsoft.com
	 * @date2018年6月14日
	 */
	public Result updateAgreement(ReqMain reqMain) throws Exception;
	
	/**
	 * 查询是否可以借款
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result checkLoan(ReqMain reqMain) throws Exception;
	
	/**
	 * 贷款申请以及生成借款编号
	 * @author huangsy@yuminsoft.com
	 * */
	public Result CreditInfo(ReqMain reqMain)throws Exception;
	
	/**
	 * 查询是否有借款信息
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result checkCreditInfo(ReqMain reqMain)throws Exception;
	
	/**
	 * 申请信息保存or修改
	 * @param reqMain
	 * @return
	 * @throws Exception 
	 * @author huangsy@yuminsoft.com
	 * */
	public Result psersonInfo(ReqMain reqMain)throws Exception;
	
	/**
	 * 获取节点信息
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result queryNodeInfo(ReqMain reqMain)throws Exception;
	
	/**
	 * 图片上传
	 * */
	public Result upPicture(ReqMain reqMain)throws Exception;
	
	/**
	 * 图片删除
	 * */
	//public Result deletePicture(ReqMain reqMain)throws Exception;
	
	/**
	 * 提交申请到录单
	 * */
	public Result pushToBms(ReqMain reqMain)throws Exception;
	
	/**
	 * 前前借款进度
	 * */
	//public Result selectByProcess(ReqMain reqMain)throws Exception;
	
	/**
	 * 首页借款进度的提示
	 * */
	public Result selectByFlag(ReqMain reqMain)throws Exception;
	
	/**
	 * h5与捞财宝签约获取验证码
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result signVerifyCode(ReqMain reqMain)throws Exception;
	
	/**
	 * H5与捞财宝注册登录
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result signRegisterLogin(ReqMain reqMain)throws Exception;
	
	/**
	 * H5与捞财宝绑卡
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result signBankCard(ReqMain reqMain)throws Exception;
	
	/**
	 * 校验是否符合进件条件
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result ruleCheck(ReqMain reqMain)throws Exception;
	
	/**
	 * 获取征信报告
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result requestDirect(ReqMain reqMain)throws Exception;
	
	/**
	 * app端签约
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result signAppContract(ReqMain reqMain) throws Exception;
	

	/**
	 * H5查询图片
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result signContractImages(ReqMain reqMain) throws Exception;
	
	/**
	 * 从核心获取合同图片
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result getPicImages(ReqMain reqMain) throws Exception;
	
	/**
	 * 合同签约
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result signContract(ReqMain reqMain) throws Exception;
	
	/**
	 * 获取单个银行卡信息
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result getOneBankInfo(ReqMain reqMain) throws Exception;
	
	/**
	 * 捞财宝实名认证
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result realName(ReqMain reqMain) throws Exception;
	
	/**
	 * 前前费率试算对接后台配置参数获取
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result GetrateCalculationParam(ReqMain reqMain) throws Exception;
	
	/**
	 * H5查询是否可以签约
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result checkWorkflow(ReqMain reqMain) throws Exception;
	
	/**
	 * 签约捞财宝
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result contractLcb(ReqMain reqMain) throws Exception;
	
	
	/**
	 * 根据银行卡获取银行卡信息
	 * @param reqMain
	 * @return
	 * @throws Exception
	 */
	public Result GetBankMessage(ReqMain reqMain) throws Exception;
	

	/**
	 * 
	 * h5获取芝麻分
	 * @param reqMain
	 * @return
	 * @throws Exception
	 *
	 */
	public Result getScore(ReqMain reqMain) throws Exception;
	
	/**
	 * 
	 * 获取数据超市配置的productID
	 * 
	 * */
	public Result getProductid(ReqMain reqMain) throws Exception;
}
