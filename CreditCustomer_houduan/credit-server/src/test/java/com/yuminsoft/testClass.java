package com.yuminsoft;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.ymkj.base.core.biz.api.message.Response;
import com.ymkj.bms.biz.api.service.zdqq.apply.ILoanInfoInputExecuter;
import com.ymkj.cms.biz.api.service.app.IZDQQExecuter;
import com.ymkj.cms.biz.api.vo.request.app.Req_VO_700001;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ymkj.credit.common.constants.Constants;
import com.ymkj.credit.common.entity.ApplyLoanInfo;
import com.ymkj.credit.common.entity.ApplyPicInfo;
import com.ymkj.credit.mapper.ApplyPicInfoMapper;
import com.ymkj.credit.service.ApplyInfoService;
import com.ymkj.credit.service.ApplyLoanInfoService;
import com.ymkj.credit.service.LoanOrderService;
import com.ymkj.credit.service.SignLcbService;
import com.ymkj.credit.service.SignService;
import com.ymkj.credit.web.api.model.Result;
import com.ymkj.credit.web.api.model.base.Model_002025;
import com.ymkj.credit.web.api.model.base.Model_004009;
import com.ymkj.credit.web.api.model.base.Model_004011;
import com.ymkj.credit.web.api.model.base.Model_005001;
import com.ymkj.credit.web.api.model.base.Model_005002;
import com.ymkj.credit.web.api.model.base.Model_005003;
import com.ymkj.credit.web.api.model.base.Model_005005;
import com.ymkj.credit.web.api.model.base.Model_005013;
import com.ymkj.credit.web.api.model.base.Model_005014;
import com.ymkj.credit.web.api.model.base.Model_005015;
import com.ymkj.credit.web.api.model.base.Model_006001;
import com.ymkj.credit.web.api.model.base.Model_006002;
import com.ymkj.dms.api.enums.EnumConstants.applyMaintainFlag;
import com.ymkj.rule.biz.api.message.MapResponse;
import com.ymkj.rule.biz.api.message.RuleEngineRequest;
import com.ymkj.rule.biz.api.service.IRuleEngineExecuter;
import com.ymkj.rule.biz.api.vo.zdqq.ZDQQApplyRuleExecVo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring/spring-context.xml"})
@ActiveProfiles("dev")
public class testClass {

	@Autowired
	public SignService signService;
	@Autowired
	public ApplyInfoService apply;
	@Autowired
	ApplyPicInfoMapper applypicinfomapper;
	@Autowired
	public IZDQQExecuter iZdqqExecuter;
	private Gson gson = new Gson();
	@Autowired
	ILoanInfoInputExecuter iloanInfoInputExecuter;
	@Autowired
	IRuleEngineExecuter iRuleEngineExecuter;
	@Autowired
	LoanOrderService loanOrderService;
	@Autowired
	SignLcbService signLcbService;
	
	@Test
	public void clientSinature(){
		Model_002025 model = new Model_002025();
		model.setLoanNo("20161115170000166690");
		signService.clientSinature(model);
	}
	
	/**
	 * 查询是否可以借款
	 */
	@Test
	public void checkLoan(){
		Model_005001 model = new Model_005001();
		model.setIdCard("12315412315");
		apply.checkLoan(model);
	}
	
	/**
	 * 查询借款申请
	 */
	@Test
	public void checkCreditInfo(){
		Model_005003 info = new Model_005003();
		info.setIdCard("110101198208048158");
		info.setFieldKey("applyInfo");
		apply.checkCreditInfo(info);
	}
	
	@Test
	public void queryNodeInfo(){
		Model_005005 model = new Model_005005();
		model.setIdCard("110101198208048158");
		model.setFieldKey(Constants.PERSIONINFO);
		model.setLoanNo("201807262B8565");
		Result r = apply.queryNodeInfo(model);
		System.out.println(gson.toJson(r));
	}
	
	/**
	 *运营平台
	 * */
	
	@Test
	public void testinit(){
		Req_VO_700001 req = new Req_VO_700001();
		req.setSysCode("zdqq");
		req.setNodeCode(Constants.APPLYINFO);
		Response response = iZdqqExecuter.initField(req);
		System.out.println("数据"+gson.toJson(response.getData()));
	}
	
	/**
	 * 录单
	 * 
	 * */
	@Test
	public void testLUdan(){
		
		 //录单获取借款编号接口
		/*Model_005002 model = new Model_005002();
		com.alibaba.fastjson.JSONObject  ApplyInfoVo = new com.alibaba.fastjson.JSONObject();
		ApplyInfoVo.put("applyLmt", "10000");
		ApplyInfoVo.put("applyTerm", "00003");
		ApplyInfoVo.put("branchManagerCode", "110");
		ApplyInfoVo.put("creditApplication", "00006");
		ApplyInfoVo.put("monthMaxRepay", "2000");
		model.setIdCard("320321199008186511");
		model.setFieldValue(ApplyInfoVo.toJSONString());
		apply.CreditInfo(model);*/
		/*Model_005003 model = new Model_005003();//查询基本信息接口调试
		model.setIdCard("310110198005245419");
		model.setLoanNo("20180731185CF3");
		com.alibaba.fastjson.JSONObject  ApplyInfoVo = new com.alibaba.fastjson.JSONObject();
		ApplyInfoVo.put("idNo", model.getIdCard());
		ApplyInfoVo.put("loanNo", model.getLoanNo());
		Response<com.alibaba.fastjson.JSONObject> response = iloanInfoInputExecuter.queryApplyInfo(ApplyInfoVo);
		String loanNoObj =response.getData()==null?"":String.valueOf(response.getData());*/
		/*Model_005014 model = new Model_005014();//推送录单接口调试
		model.setLoanNo("2018073149A7ED");
		apply.pushToBms(model);*/
		/*Model_005013 model = new Model_005013();//图片删除
		model.setFileId("19110982");
		apply.deletePicture(model);*/
		/*String loanNo = "20180810F8EC15";//分布查询接口
		String idNo = "110101198208048361";
		com.alibaba.fastjson.JSONObject  ApplyInfoVo = new com.alibaba.fastjson.JSONObject();
		ApplyInfoVo.put("loanNo", loanNo);
		ApplyInfoVo.put("idNo", idNo);
		Response<com.alibaba.fastjson.JSONObject> response = iloanInfoInputExecuter.queyBaseInformation(ApplyInfoVo);
		String loanNoObj =response.getData()==null?"":String.valueOf(response.getData());*/
		String loanNo = "20180810F8EC15";//进度查询
	    String idNo = "110101198208048361";
	    Model_004009 model  = new Model_004009();
	    model.setCustomerId("87");
	    apply.selectByProcess(model);
	}
	
	@Test
	public void rrr(){
		for (int i = 0; i < 20; i++) {
			IdCardGenerator id = new IdCardGenerator();
			com.alibaba.fastjson.JSONObject  ApplyInfoVo = new com.alibaba.fastjson.JSONObject();
			String idCard = id.getRandomID();
			ApplyInfoVo.put("idNo", idCard);
			ApplyInfoVo.put("applyInfoValue","{\"applyLmt\":\"800000\",\"applyTerm\":\"24\",\"branchManagerCode\":\"hwt\",\"creditApplication\":\"00001\",\"loanNo\":\"\",\"monthMaxRepay\":\"5000\"}");
			ApplyInfoVo.put("loanNo", "");
			
			Response<com.alibaba.fastjson.JSONObject> response = iloanInfoInputExecuter.saveApplyInfo(ApplyInfoVo);
			//借款编号由录单给到
			if(response.isSuccess()){
				String loanNo =net.sf.json.JSONObject.fromObject(response.getData()).getString("loanNo");
				String str = "{\"baseInfo\":{\"applyInfo\":{\"applyLmt\":\"800000\",\"applyTerm\":\"24\",\"branchManagerCode\":\"hwt\",\"creditApplication\":\"00001\",\"loanNo\":\""+loanNo+"\",\"monthMaxRepay\":\"5000\"},\"loanNo\":\""+loanNo+"\",\"persionInfo\":{\"age\":\"12\",\"cellphone\":\"13142563789\",\"cellphoneSec\":\"13412534678\",\"email\":\"123456@qq.com\",\"gender\":\"M\",\"home\":\"530000,530500,530524\",\"homeAddress\":\"多次发出\",\"homePhone1\":\"010-1234567\",\"homeSameRegistered\":\"1\",\"homeText\":\"云南省,保山市,昌宁县\",\"idIssuerAddress\":\"对方尴尬\",\"idNo\":\""+idCard+"\",\"issuer\":\"530000,530500,530524\",\"issuerText\":\"云南省,保山市,昌宁县\",\"maritalStatus\":\"00002\",\"name\":\"刘祥一"+i+"\",\"qqNum\":\"12555\",\"qualification\":\"00002\",\"wechatNum\":\"12355\"},\"empItemInfo\":{\"corp\":\"220000,220800,220882\",\"corpAddress\":\"和 vv 成分丰富\",\"corpDepapment\":\"的方法\",\"corpName\":\"今年年内\",\"corpPayWay\":\"00002\",\"corpPayday\":\"12\",\"corpPhone\":\"010-1234567\",\"corpPhoneSec\":\"010-3214567\",\"corpPost\":\"00006\",\"corpStandFrom\":\"2015-08\",\"corpStructure\":\"00001\",\"corpText\":\"吉林省,白城市,大安市\",\"corpType\":\"00005\",\"monthAmt\":\"123\",\"occupation\":\"00006\",\"payBank\":\"中国银行\",\"priEnterpriseType\":\"00003\",\"registerFunds\":\"1234\",\"setupDate\":\"1995-08\",\"sharesScale\":\"12\",\"takeHomePay\":\"1234\",\"totalMonthSalary\":\"123\"},\"mateInfo\":{\"contactCellphone\":\"15314286780\",\"contactCorpPhone\":\"010-33565432\",\"contactEmpName\":\"哥哥哥哥\",\"contactIdNo\":\"320324199012543201\",\"contactName\":\"法国环境\",\"ifForeignPenple\":\"1\",\"ifKnowLoan\":\"1\",\"sequenceNum\":\"0\",\"unabridged\":\"Y\"},\"creditAccountInfo\":{\"creditName\":\"12346\",\"creditPassword\":\"12345\",\"verificationCode\":\"12546\",\"unabridged\":\"Y\"},\"educationInfo\":{\"area\":\"520000,520400\",\"areaText\":\"贵州省,安顺市\",\"educationExperience\":\"00001\",\"graduationDate\":\"2018-08-10\",\"qualification\":\"00002\",\"schoolName\":\"错过后悔\",\"chsiAccount\":\"1234\",\"chsiPassword\":\"123\",\"certificateNumber\":\"1524\",\"unabridged\":\"Y\"},\"merchantLoanInfo\":{\"IsOpenLoan\":\"0\",\"borrowLimit\":\"20000\",\"buyerCreditLevel\":\"A\",\"buyerCreditType\":\"A\",\"consumptionSum\":\"451\",\"costLimit\":\"450\",\"isOpenCost\":\"1\",\"memberName\":\"今天\",\"naughtyValue\":\"450\",\"sesameCreditValue\":\"450\",\"unabridged\":\"Y\"},\"assetsInfo\":{\"eatateSameRegistered\":\"1\",\"estate\":\"640000,640400,640423\",\"estateAddress\":\"今天\",\"estateBuyDate\":\"2018-08-10\",\"estateLoan\":\"ING\",\"estateLoanAmt\":\"1334\",\"estateLoanIssueDate\":\"2018-08\",\"estateText\":\"宁夏回族自治区,固原市,隆德县\",\"estateType\":\"00001\",\"isCommon\":\"1\",\"monthPaymentAmt\":\"1313\",\"unabridged\":\"Y\"},\"carInfo\":{\"carBuyDate\":\"2018-08\",\"carBuyPrice\":\"2508\",\"carLoanIssueDate\":\"2018-08\",\"carLoanOrg\":\"今天早上\",\"carLoanStatus\":\"ING\",\"monthPaymentAmt\":\"3161\",\"plateNum\":\"宁A12345\",\"tciCompany\":\"玩\",\"unabridged\":\"Y\"},\"providentInfo\":{\"accumulationFundAccount\":\"1234\",\"accumulationFundPassword\":\"11222\",\"unabridged\":\"Y\"},\"insuranceInfo\":{\"socialInsuranceAccount\":\"124884\",\"socialInsurancePassword\":\"1344\",\"unabridged\":\"Y\"},\"cardLoanInfo\":{\"unabridged\":\"N\"},\"residenceInfo\":{\"unabridged\":\"N\"},\"policyInfo\":[{\"insuranceCompany\":\"00019\",\"insuranceDate\":\"2018-08\",\"paymentMethod\":\"Y\",\"sequenceNum\":\"0\",\"tab\":\"0\",\"paymentMoney\":\"123\",\"policyAccount\":\"123\",\"policyPassword\":\"23\",\"unabridged\":\"Y\"},{\"insuranceCompany\":\"00018\",\"insuranceDate\":\"2018-08\",\"paymentMethod\":\"Y\",\"sequenceNum\":\"0\",\"tab\":\"0\",\"paymentMoney\":\"123\",\"policyAccount\":\"123\",\"policyPassword\":\"23\",\"unabridged\":\"Y\"},{\"insuranceCompany\":\"00017\",\"insuranceDate\":\"2018-08\",\"paymentMethod\":\"Y\",\"sequenceNum\":\"0\",\"tab\":\"0\",\"paymentMoney\":\"123\",\"policyAccount\":\"123\",\"policyPassword\":\"23\",\"unabridged\":\"Y\"}],\"contactPersonInfo\":[{\"contactCellphone\":\"13122321091\",\"contactCorpPhone\":\"010-3214567\",\"contactEmpName\":\"韩版包包\",\"contactName\":\"江河湖海\",\"contactRelation\":\"00001\",\"ifKnowLoan\":\"1\",\"sequenceNum\":\"0\",\"unabridged\":\"Y\"},{\"contactCellphone\":\"12345678911\",\"contactCorpPhone\":\"010-2314567\",\"contactEmpName\":\"沟沟壑壑\",\"contactName\":\"风风光光\",\"contactRelation\":\"00002\",\"ifKnowLoan\":\"1\",\"sequenceNum\":\"1\",\"unabridged\":\"Y\"},{\"contactCellphone\":\"12546789013\",\"contactCorpPhone\":\"010-1325617\",\"contactEmpName\":\"你爸爸\",\"contactName\":\"其实方法\",\"contactRelation\":\"00002\",\"ifKnowLoan\":\"1\",\"sequenceNum\":\"2\",\"unabridged\":\"Y\"},{\"contactCellphone\":\"13485796314\",\"contactCorpPhone\":\"010-65678432\",\"contactEmpName\":\"反反复复\",\"contactName\":\"黑板报\",\"contactRelation\":\"00003\",\"ifKnowLoan\":\"1\",\"sequenceNum\":\"3\",\"unabridged\":\"Y\"}]}}";
				//开始推送录单
				Response<com.alibaba.fastjson.JSONObject> response1 = iloanInfoInputExecuter.saveBaseInformation(JSONObject.parseObject(str));
				System.out.println(String.valueOf(response1.getData()));
			}
		}
	}
	
	@Test
	public void insert(){
		ApplyPicInfo pic = new ApplyPicInfo();
		pic.setFieldKey("444");
		pic.setPicId("111");
		pic.setStatus("1");
		pic.setPicUrl("wertwert");
		pic.setImgName("sdfsdf");
		applypicinfomapper.insert(pic);
	}
	
	@Test
	public void gz(){
		//ruleAppSevice.validate(name, idCardNo);
		ZDQQApplyRuleExecVo vo = new ZDQQApplyRuleExecVo();
		vo.setIdNo("110101198208048158");
		vo.setExecuteType(Constants.RULE_NODE_SQ);
		RuleEngineRequest rr = new RuleEngineRequest();
		rr.setData(vo);
		rr.setBizType(Constants.ZDQQAPPLY);
		rr.setSysCode(Constants.ZDQQ_SYS_CODE);
		com.ymkj.rule.biz.api.message.Response ruleResponse = iRuleEngineExecuter.executeRuleEngine(rr);
		//MapResponse map = (MapResponse) ruleResponse;
		Map<String, Object> map = ((MapResponse) ruleResponse).getMap();
		System.out.println(map.get("action"));
		
	}
	
	@Test
	public void code(){
		Model_004011 model = new Model_004011(); 
		model.setOperatorCode("hwt");
		loanOrderService.queryOperator(model);
		
	}

	@Test
	public void ruleCheck(){
		//apply.ruleCheck("2018080899B7FF");
	}
	//-Dglobal.config.path="D:\yumin_object\tomcat7.0\CreditCustomer_houduan\credit-server\src\main\resources\resource"
	
	/**
	 * 验证码校验
	 */
	@Test
	public void verification(){
		Model_006001 model = new Model_006001();
		model.setCellPhone("13122301151");
		signLcbService.VerificationCode(model);
	}
	
	/**
	 * 注册登录
	 */
	@Test
	public void RegisterLogin(){
		Model_006002 model = new Model_006002();
		model.setCellPhone("13122301091");
		model.setValidateCode("1234");
		signLcbService.RegisterLogin(model);
	}
	
	@Test
	public void isCheckLoan(){
		//apply.isCheckLoan("320321199105163210");
	}
	//-Dglobal.config.path="D:\yumin_object\tomcat7.0\CreditCustomer_houduan\credit-server\src\main\resources\resource"
}
