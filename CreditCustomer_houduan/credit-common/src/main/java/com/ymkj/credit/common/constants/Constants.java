package com.ymkj.credit.common.constants;

import java.util.Arrays;
import java.util.List;

public class Constants {
	public static final String contractType_dianzi = "1";//合同类型  电子合同
	public static final String contractType_zhizhi = "0";//纸质合同
	
	public static final String AuthType_PC = "本系统";//录单认证渠道
	public static final String AuthType_APP = "证大前前";//
	
	public static final String CODE_SUCCESS = "0000";
	public static final String CODE_FAILURE = "9999";
	public static final String MSG_SUCCESS = "SUCCESS";
	
	public static final String SYS_SERVER = "server";	//server标识
	
	/**项目编码(平台)*/
	public static final String ZDQQ_SYS_CODE = "zdqq";
	public static final String ZDQQ_CHANNEL_CODE = "00023";
	
	/**平台接口成功返回码*/
	public static final String PMS_SUCCESS_CODE = "000000";
	
	/**
	 * 数据有效性
	 */
	public static final String DATA_VALID = "1";
	public static final String DATA_UNVALID = "0";
	
	//统一通信变量
	public static final String TYTX_SYS_UID = "1014" ;//系统号
	public static final String TYTX_EMP_Id = "creditsend";//工号
	public static final String TYTX_M_TYPE_SMS = "10000002";//信息类别编码(短信)
	public static final String TYTX_M_TYPE_MESSAGE = "10000013";//信息类别编码(消息推送)
	public static final String TYTX_POLICY = "0";// 发送策略 0指定通道发送
	public static final String TYTX_CHANNEL_SMS = "sms";// 发送渠道 短信
	public static final String TYTX_CHANNEL_MAIL = "mail";// 发送渠道 邮件
	public static final String TYTX_CHANNEL_INFORM = "inform";// 发送渠道 消息推送
	//客户类型
	public static final String TYTX_CTYPE_1 = "1";// 员工
	public static final String TYTX_CTYPE_3 = "3";// 交易用户
	public static final String TYTX_CTYPE_10 = "10";// 体验用户
	public static final String TYTX_CTYPE_11 = "11";// 潜在客户
	public static final String TYTX_CTYPE_0 = "-1";// 匿名用户
	//客户识别类型
	public static final String TYTX_CMARKETYPE_1 = "1";// 手机号码
	public static final String TYTX_CMARKETYPE_2 = "2";// 邮箱地址
	public static final String TYTX_CMARKETYPE_3 = "3";// 接收用户唯一标识(客户号、工号、昵称等)
	public static final String TYTX_CMARKETYPE_4 = "4";// Android手机设备号
	public static final String TYTX_CMARKETYPE_5 = "5";// IOS手机设备号
	public static final String TYTX_CMARKETYPE_6 = "6";// 微信号
	//正文内容格式类型
	public static final String TYTX_BODYFORMAT_TEXT = "text" ;
	public static final String TYTX_BODYFORMAT_HTML = "html" ;
	public static final String TYTX_BODYFORMAT_URL = "url" ;
	public static final String TYTX_LATESENDTIME = "2099-12-31 08";
	//附件发送方式
	public static final String TYTX_ATTACHMENT_FILEPATH = "1";//资源路径方式
	public static final String TYTX_ATTACHMENT_BYTEARRAY = "2";//资源字节流
	public static final String TYTX_ATTACHMENT_FTP = "3";//上传到FTP
	
	
	//借款状态
	public static final String ORDER_STATES_SHOULIZHONG = "0";//受理中
	public static final String ORDER_STATES_BANLIZHONG = "1";//办理中
	public static final String ORDER_STATES_SHENGQINGJJ = "2";//已拒绝申请
	public static final String ORDER_STATES_SHENQINGQQ = "3";//已取消申请
	public static final String ORDER_STATES_XIANXIAQIANYUE = "4";//待签约
	public static final String ORDER_STATES_MIANQIAN = "5";//待线下面签
	public static final String ORDER_STATES_FANGKUANZHONG = "6";//放款中
	public static final String SMS_RESULT_SUCCESS_CODE = "900000";//上传到FTP
	
	
	public static final String PROGECT_NO = "projectNo";
	
	public static final String REQ_RUL = "reqUrl";
	
	public static final String REQ_PARAM = "reqParam";
	
	public static final String REQ_HEAD_PARAM = "reqHeadParam";
	
	public static final String REQ_TIMESTAMP = "reqTimestamp";
	
	public static final String SN = "sn";
	
	public static final String SESSIONTOKEN = "sessionToken";
	
	public static final String VERSION = "version";
	
	public static final String MECHANISM = "mechanism";
	
	public static final String PLATFORM = "platform";
	
	public static final String TOGATHERTYPE = "togatherType";
	
	public static final String OPENCHANNEL = "openchannel";
	
	public static final String TOKEN = "token";
	
	public static final String USERAGENT = "userAgent";
	
	public static final String operatorName = "operatorName";
	
	public static final String operatorCode = "operatorCode";
	
	public static final String sign = "sign";
	
	public static final String deviceId = "deviceId";
	
	
	
	/**
	 * 信贷接口url(核心)
	 */
	public static final String CREDIT_URL_CUSTOMERINFO = "/findCustomerInfo";//查询借款客户信息接口
	public static final String CREDIT_URL_EMPLOYEEINFO = "/findEmployeeInfo";//查询客户经理信息
	public static final String CREDIT_URL_NOTICELIST = "/searchNoticeList";//查询公告信息
	public static final String CREDIT_URL_LOANINFO = "/findLoanInfo";//查询借款信息
	public static final String CREDIT_URL_SECONDEMPLOYEEINFO= "/findSecondLevelEmployeeInfo";//查询二类客户经理
	public static final String CREDIT_URL_LOANINFO_JXHJ = "/historyLoanTrial";//借新还旧借款信息
	public static final String CREDIT_URL_ACTIVITINFO_JXHJ = "/findClientJoinActivityInfo";//借新还旧客户活动信息
	public static final String CREDIT_URL_FINDBACKCARDINFO = "/findBankCardInfo";//绑卡查询银行卡列表
	public static final String CREDIT_URL_BINDBANKCARD = "/bindBankCard";//绑卡
	public static final String CREDIT_URL_preOpenAccount = "/preOpenAccount";//预开户
	public static final String CREDIT_URL_realTimeRepayment = "/realTimeRepayment";//还款
	public static final String UPLOAD_CREDIT_REPORT = "/pbccrc/saveReport";//上传征信报告
	public static final String SELECT_CREDIT_REPORT = "/creditReport/getReportId";//查询征信报告
	public static final String SELECT_ZMSCORE = "/mobData/getZmScore";//查询芝麻信用分
	/**
	 *PIC接口url
	 */
	public static final String PIC_URL_UPLOAD = "/filedata/uploadfile";//图片上传
	public static final String PIC_URL_MOVE = "/picture/updateAppno";//文件夹移动
	public static final String PIC_URL_DELETE = "/picture/delete";//图片删除
	public static final String PIC_URL_REPLACE = "/filedata/replacePic";//图片修改
	public static final String PIC_URL_UPLOAD_WITH ="/filedata/uploadfileWithIdNum";//上传接口(指定上传目录)(身份证标识)
	
	/**
	 * 存管网关接口
	 */
	public static final String CG_URL_GetTargetStatus = "/getTargetStatus";//获取捞财宝电子合同签订结果
	/**
	 * 征审网关
	 */
	public static final String ZS_URL_GetSignContractStatus = "/getSignContractStatus";//征审获取合同签名结果
	public static final String ZS_URL_UserLoginToRegisterSignature = "/userLoginToRegisterSignature";//登录注册接口
	public static final String ZS_URL_FindSignContractListInfos = "/findSignContractListInfos";//查询合同列表
	public static final String ZS_URL_LoadContractFile = "/loadContractFile";//加载合同文件
	public static final String ZS_URL_ClientSinature = "/clientSinature";//客户签章
	public static final String ZS_URL_SendValidCode = "/sendValidCode";//发送短信验证码
	public static final String ZS_URL_CheckValidCode = "/checkValidCode";//校验短信验证码
	
	/**
	 * 核心地址
	 */
	public static final String hx_loadContractFile = "/loadContractFile";//获取图片
	public static final String hx_batchGetSignResult = "/api/apsPort/batchGetSignResult";//批量查询签章结果
	public static final String hx_batchClientAutoSign ="/api/contract/batchClientAutoSign";//批量签章
	public static final String hx_batchPushContract = "/api/apsPort/batchPushContract";//批量推送签章合同信息
	
	
	/**
	 * 请求返回成功状态码
	 */
	public static final String RESP_CODE_SUCCESS = "000000";
	public static final String RESP_CODE_ERROR = "900000";//错误状态码
	public static final String RESP_CODE_EXCEPTION = "800000";//异常状态码
	/**
	 * 短信类型
	 */
	public static final String SMS_TYPE_REGISTER = "1";//1:注册
	public static final String SMS_TYPE_RESET = "2";//2:重置密码
	public static final String SMS_TYPE_LOGIN = "3";//2:登录
	/**
	 * 客户业务状态
	 */
	public static final String FLOW_STATUS_NEW = "001001";//1:新建
	public static final String FLOW_STATUS_IDCARD = "001002";//2:身份证校验通过
	public static final String FLOW_STATUS_MANAGERNO = "001003";//3:经理工号通过
	public static final String FLOW_STATUS_REGISTER = "001004";//4:注册

	/**
	 * 是否逾期
	 */
	public static final String IS_OVERDUE_YES = "y";//
	public static final String IS_OVERDUE_NO = "n";
	/**
	 * 贷款状态：2逾期，1 正常，0无贷款
	 */
	public static final String LOAN_STATUS_YES = "2";//
	public static final String LOAN_STATUS_NO = "1";
	public static final String LOAN_STATUS_NULL = "0";
	/**
	 * 手势密码开关
	 */
	public static final String GESTURE_SWITCH_ON = "0";//开
	public static final String GESTURE_SWITCH_OFF = "1";//关
	public static final String GESTURE_SWITCH_UNSET = "2";//未设置

	/**
	 * 身份校验开关
	 */
	public static final String SWITCH_ON = "ON";//开
	public static final String SWITCH_OFF = "OFF";//关

	/**
	 * 密码登录类型
	 */
	public static final String LOGIN_TYPE_COMMON = "1";//普通登录
	public static final String LOGIN_TYPE_GESTURE = "2";//手势登录
	public static final String LOGIN_TYPE_SMS = "3";//短信验证码登录
	public static final String MONEY_SYMBOL = "¥";//手势登录
	
	/**
	 * 平台
	 */
	public static final String[] PLATFORM_TYPE = {"IOS","ANDROID","WEB"};
	public static final String PLATFORM_TYPE_IOS = "IOS";
	public static final String PLATFORM_TYPE_ANDROID = "ANDROID";
	public static final String PLATFORM_TYPE_WEB = "WEB";
	
	//请求项目
	public static final String ProjectNo_TYPE_WebService = "webService";//h5
	/**
	 * 借款单按钮状态值
	 * 0:不显示按钮(默认)
	   1:设置银行卡
       2:设置提现密码
       3:电子签约
       4:退回
	 */
	public static final String  NOT_BUTTON= "0";
	public static final String SET_BANK_CARD = "1";
	public static final String SET_PASSWOED = "2";
	public static final String ELEC_SIGN = "3";
	public static final String RETURN = "4";
	
	/*
	 * 是否显示更改银行卡
	 */
	public static final String  IS_MODIFY_BANKCARD= "1";//显示更改银行卡按钮
	public static final String NOT_MODIFY_BANKCARD = "0";//不显示更改银行卡按钮
	/*
	 * 是否显示状态值
	 */
	public static final String  IS_SHOW_STATUS= "1";//显示状态值
	public static final String NOT_SHOW_STATUS = "0";//不显示状态值
	
	/**
	 * 身份证正反面
	 */
	public static final String IdCard_FRONT = "0";//身份证正面
	public static final String IdCard_Reverse = "1";//身份证反面
	
	 /**规则网关*/
    public static String APP_SFHC="app-SFHC";
    public static String APP_DKSQ="app-DKSQ";
    public static String APP_SQXX="app-SQXX";
    
    public static String CLICK_NEXT="LDAPP001"; //点击下一步
    public static String CLICK_SAVE="LDAPP002"; //点击保存
    public static String CLICK_SUBMIT="LDAPP003"; //点击提交
    public static String CLICK_CHECK="LDAPP004"; //身份核查
    public static String CLICK_BATCH="LDAPP005"; //日终跑批
    
    public static String JUMP_TYPE_WEB="0";//
    public static String JUMP_TYPE_LOCAL="1";
  
    public static String NOT_CHECKED_CARD="00";//未验卡
    public static String IS_CHECKED_CARD="01";//已验卡
    
    public static String Channel_TONGLIAN2="通联支付2";//通联2
    public static String BIZ_SYS_NO="026";// todo 签约系统功能号
    public static String INFOCATEGORYCODE = "10085";//版本号
    
    public static String AGREED = "Agreed";//是否同意协议
    
    public static String SYSCODE = "000001";//系统编码
    public static String INFOCATEGORY = "10000008";//信息类别

    
    /**进件节点常量*/
    public static String APPLYINFO ="applyInfo"; //申请信息
    public static String PERSIONINFO = "persionInfo";//个人信息
    public static String EMPITEMINFO = "empItemInfo";//工作信息
    public static String FRIENDSINFO = "friendsInfo";//第三步节点
    public static String CONTACTPERSONINFO = "contactPersonInfo";//联系人信息
    public static String MATEINFO = "mateInfo";//婚配信息
    public static String NODEINFO = "nodeInfo";//第四步包含以下信息
    public static String ASSETSINFO = "assetsInfo";//房产信息
    public static String CARINFO = "carInfo";//	车辆信息
    public static String MERCHANTLOANINFO = "merchantLoanInfo";//	淘宝账户信息表
    public static String SOCIALSECURITYINFO ="socialSecurityInfo";//社保/公积金信息
    public static String PROVIDENTINFO = "providentInfo";//公积金信息表
    public static String INSURANCEINFO = "insuranceInfo";//社保信息表
    public static String CARDLOANINFO = "cardLoanInfo";//	信用卡信息
    public static String EDUCATIONINFO = "educationInfo";//学历信息表
    //public static String EDUCATIONCHSI = "educationChsi";//学历信息中的学信网
    //public static String EDUCATIONMARK = "educationMark";//学历信息中的认证书
    public static String POLICYINFO = "policyInfo";//	寿险投保信息
    //public static String POLICYACCOUNT = "policyAccount";//投保认证
    public static String CREDITACCOUNTINFO = "creditAccountInfo";//	信用账户信息
    public static String RESIDENCEINFO = "residenceInfo";//居住证信息
    public static String OTHERINFO = "otherInfo";//其他信息
    public static String ZMSCORE="sesameCreditInfo";//芝麻信用
    
    /**是否提交*/
    public static String IS_SUBMIT="1";//已提交
    public static String IS_NOT_SUBMIT = "0";//未提交
    public static String IS_REBACK_SUBMIT = "2";//回退
    public static String IS_CANCEL_SUBMIT = "3";//取消
    public static String IS_REFUSE_SUBMIT = "4";//拒绝
    public static String IS_COMPLETE_SUBMIT = "5";//完成

    /**是否能借款*/
    public static String RESULT_YES ="1";//可以借款
    public static String REUSLT_NO ="0";//不可以借款
    
    /**规则引擎环节*/
    public static String RULE_NODE_SQ = "ZDQQ02";//我要借款
    public static String RULE_NODE_TJ = "ZDQQ03";//提交
    public static String ZDQQAPPLY = "zdqqApply";
    
    /**前前app提示信息*/
    public static String RULE_HINT_0="0";
    public static String RULE_HINT_1="1";
    public static String RULE_HINT_2="2";
    
    public static final String  HTTP_STR="http:";
    
    /**前前业务节点*/
    public static String REGIEST = "4";//注册
    public static String SUBMIT = "5";//提交申请
    public static String REBACK = "6";//审核退回
    public static String PASS = "7";//审核通过待签约
    public static String PENDINGMONEY = "8";//待放款
    public static String LOAN = "9";//放款，渠道捞财宝
    public static String REPAYMENT = "10";//还款提醒
    public static String OVERDUE = "11";//还款日前未还款
    public static String HAVEREPALY = "12";//还款日前还款入账成功（包含客户提前还款）
    public static String REFUSE = "13";//审核拒绝
    public static String REPLAY = "14";//实际还款
    public static String SETTLE = "15";//结清提示
    public static String OUIT = "16";//客户经理离职提醒
    public static String LOANNOTLCB = "17";//放款，渠道费捞财宝
    

    
    public static String CONTRACT_SYS_CODE = "000004"; //前前app
    public static String CONTRACT_INFO_TYPE_01 = "10000010";//前前APP电子授权（授权委托、电子认证服务协议）
    public static String CONTRACT_INFO_TYPE_02 = "10000011";//前前APP电子授权（个人征信查询）
    public static String CONTRACT_IS_NOT_SIGN_01 = "0";//不要签章
    public static String CONTRACT_IS_NOT_SIGN_02 = "1";//要签章
    
    public static String CONTRACT_APP = "app";//系统code
    //签章参数
    //public static String[] contract  = {ContractType.contract_type_00208.getKey(),ContractType.contract_type_00209.getKey(),ContractType.contract_type_00210.getKey()};

    /**
     * 请求核心
     * */
    public static String APPCORE = "/createLoanTrial";//核心费率试算

}
