package com.ymkj.credit.service;

import java.io.File;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import oracle.jdbc.Const;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.StringUtil;
import com.ymkj.base.core.biz.api.message.Response;
import com.ymkj.bms.biz.api.service.zdqq.apply.ILoanInfoInputExecuter;
import com.ymkj.cms.biz.api.service.app.IZDQQExecuter;
import com.ymkj.cms.biz.api.vo.request.app.Req_VO_700001;
import com.ymkj.credit.common.constants.ConstantStatus;
import com.ymkj.credit.common.constants.Constants;
import com.ymkj.credit.common.dto.InsuranceInfoDto;
import com.ymkj.credit.common.dto.ProvidentInfoDto;
import com.ymkj.credit.common.entity.ApplyBaseInfo;
import com.ymkj.credit.common.entity.ApplyGroup;
import com.ymkj.credit.common.entity.ApplyGroupInfo;
import com.ymkj.credit.common.entity.ApplyLoanInfo;
import com.ymkj.credit.common.entity.ApplyPicInfo;
import com.ymkj.credit.common.entity.Customer;
import com.ymkj.credit.common.entity.IdCardInfo;
import com.ymkj.credit.common.untils.AesUtil;
import com.ymkj.credit.common.untils.HttpKit;
import com.ymkj.credit.common.untils.PicUtil;
import com.ymkj.credit.common.untils.PropertiesReader;
import com.ymkj.credit.common.util.FileDownUtils;
import com.ymkj.credit.common.util.base64Utils;
import com.ymkj.credit.mapper.ApplyBaseInfoMapper;
import com.ymkj.credit.mapper.ApplyGroupInfoMapper;
import com.ymkj.credit.mapper.ApplyGroupMapper;
import com.ymkj.credit.mapper.ApplyLoanInfoMapper;
import com.ymkj.credit.mapper.ApplyPicInfoMapper;
import com.ymkj.credit.mapper.CustomerMapper;
import com.ymkj.credit.mapper.IdCardInfoMapper;
import com.ymkj.credit.network.HttpClientUtil;
import com.ymkj.credit.service.dms.DmsService;
import com.ymkj.credit.service.redis.BasicRedisOpts;
import com.ymkj.credit.web.api.model.Result;
import com.ymkj.credit.web.api.model.base.Model_004009;
import com.ymkj.credit.web.api.model.base.Model_004014;
import com.ymkj.credit.web.api.model.base.Model_004019;
import com.ymkj.credit.web.api.model.base.Model_005001;
import com.ymkj.credit.web.api.model.base.Model_005002;
import com.ymkj.credit.web.api.model.base.Model_005003;
import com.ymkj.credit.web.api.model.base.Model_005004;
import com.ymkj.credit.web.api.model.base.Model_005005;
import com.ymkj.credit.web.api.model.base.Model_005006;
import com.ymkj.credit.web.api.model.base.Model_005012;
import com.ymkj.credit.web.api.model.base.Model_005014;
import com.ymkj.credit.web.api.model.base.Model_005016;
import com.ymkj.credit.web.api.model.base.Model_005017;
import com.ymkj.credit.web.api.model.base.Model_006004;
import com.ymkj.rule.biz.api.message.MapResponse;
import com.ymkj.rule.biz.api.service.IRuleEngineExecuter;
import com.ymkj.rule.biz.api.vo.zdqq.ZDQQApplyRuleExecVo;
import com.ymkj.rule.biz.api.vo.zdqq.ZDQQCarVO;
import com.ymkj.rule.biz.api.vo.zdqq.ZDQQCardVO;
import com.ymkj.rule.biz.api.vo.zdqq.ZDQQContactVO;
import com.ymkj.rule.biz.api.vo.zdqq.ZDQQEducationVO;
import com.ymkj.rule.biz.api.vo.zdqq.ZDQQEstateVO;
import com.ymkj.rule.biz.api.vo.zdqq.ZDQQMasterVO;
import com.ymkj.rule.biz.api.vo.zdqq.ZDQQPersonVO;
import com.ymkj.rule.biz.api.vo.zdqq.ZDQQPolicyVO;
import com.ymkj.rule.biz.api.vo.zdqq.ZDQQPrivateOwnerVO;
import com.ymkj.rule.biz.api.vo.zdqq.ZDQQWorkVO;
/**
 * @Description：贷款申请信息
 * @ClassName: ApplyLoanInfo.java
 * @Author：huangsy
 * @Date：
 * 如：who  2017年8月24日  修改xx功能
 */
@Slf4j
@Service
public class ApplyInfoService {
	
	@Autowired
	ApplyLoanInfoService applyLoanInfoService;
	
	@Autowired
	ApplyLoanInfoMapper applyloaninfomapper;
	
	@Autowired
	ApplyBaseInfoMapper applybaseinfomapper;
	
	@Value("${upload_path}")
    private String uploadPath;
	@Value("${urlUPIC}")
	private String urlUPIC;
	@Value("${sys.appKey}")
	private String appKey;
	@Value("${sys.appPicGateWayUrl}")
	private String appPicGateWayUrl;
	@Value("${urlH5.contract.url}")
	private String urlH5url;
	
	@Autowired
	IRuleEngineExecuter iruleengineexecuter;
	
	@Autowired
	ApplyBaseInfoService applyBaseInfoService;
	
	@Autowired
	RuleService ruleAppSevice;
	
	@Autowired
	public IZDQQExecuter iZdqqExecuter;
	
	@Autowired
	ILoanInfoInputExecuter iloanInfoInputExecuter;
	
	@Autowired
	ApplyPicInfoMapper applypicinfomapper;
	
	@Autowired
	CustomerService customerService;
	@Autowired
	IdCardInfoMapper idCardInfoMapper;
	
	@Autowired
	ApplyGroupMapper applyGroupMapper;
	
	@Autowired
	SignLcbService signLcbService;
	
	@Autowired
	ApplyGroupInfoMapper applyGroupInfoMapper;
	@Inject
	private BasicRedisOpts basicRedisOpts;
	/**
     * 借新还旧h5地址
     */
    @Value("${jxhjH5Url}")
    private String jxhjH5Url;
    @Autowired
	private CustomerMapper customerMapper;
    @Autowired
    private DmsService dmsService;
    @Autowired
	private SignService signService;
    @Autowired
    private LoanOrderService loanOrderService;

	/**
	 * 校验是否能借款
	 * @param model
	 * @return
	 */
	public Result checkLoan(Model_005001 model){
		log.info("是否借款入参："+model.getIdCard()+"|"+model.getCustomerId());
		//调规则网关校验是否可以申请贷款
		JSONObject object = new JSONObject();
		//判断该用户是否进行过实名认证
		Model_004014 m = new Model_004014();
		m.setCustomerId(model.getCustomerId());
		Result re = customerService.isAuthentication(m);
		if(re.getSuccess()){
			//校验规则引擎
			Map<String, Object> map = isCheckLoan(model.getIdCard());
			if(map.get("ifNext").equals("Y")){
				object.put("isLaon", Constants.RESULT_YES);
				object.put("message", map.get("hint"));
			}else{
				object.put("isLaon", Constants.REUSLT_NO);
				object.put("message", map.get("hint"));
			}	
			return Result.success(object);
		}else{
			return re;
		}
	}
	
	//根据身份证校验
	private Map<String,Object> isCheckLoan(String idCard){
		//查询是否有未完成的借款进件
		ApplyLoanInfo ali = new ApplyLoanInfo();
		ali.setIdCard(idCard);
		ali.setApplyStatus(Constants.IS_NOT_SUBMIT);
		List<ApplyLoanInfo> list = applyloaninfomapper.selectLoanAll(ali);
		//校验规则引擎
		if(list != null && !list.isEmpty()){
			String loan = list.get(0).getLoanNo();
			log.info("规则引擎入参："+ loan);
			ZDQQApplyRuleExecVo vo = ruleCheck(loan,Constants.RULE_NODE_SQ);
			com.ymkj.rule.biz.api.message.Response ruleResponse = ruleAppSevice.validatezdqq(vo);
			Map<String, Object> map = ((MapResponse) ruleResponse).getMap();
			return ruleAppSevice.getValidateResultIsCheck(map);
		}else{
			Customer tt = customerService.queryByIdCard(idCard);
			ZDQQApplyRuleExecVo vo = new ZDQQApplyRuleExecVo();
			log.info("规则引擎入参：" + idCard);
			if(tt != null){
				vo.setName(tt.getCustomerName());
			}
			vo.setIdNo(idCard);
			vo.setExecuteType(Constants.RULE_NODE_SQ);
			com.ymkj.rule.biz.api.message.Response ruleResponse = ruleAppSevice.validatezdqq(vo);
			Map<String, Object> map = ((MapResponse) ruleResponse).getMap();
			return ruleAppSevice.getValidateResultIsCheck(map);
		}
	}
	
	/**
	 * 点击保存借款申请
	 */
	public Result CreditInfo(Model_005002 model){
		
		log.info("申请借款保存到录单数据：身份证号"+model.getIdCard()+" 申请信息"+model.getFieldValue()+" 借款编号"+model.getLoanNo());
		//调规则网关校验是否可以申请贷款
		//获取姓名
		Customer custs = customerService.queryByIdCard(model.getIdCard());
		Map<String, String> retrunMap =new HashMap<String, String>();
  		com.alibaba.fastjson.JSONObject  ApplyInfoVo = new com.alibaba.fastjson.JSONObject();
		ApplyInfoVo.put("idNo", model.getIdCard());
		JSONObject jb = JSONObject.fromObject(model.getFieldValue());
		if(custs != null){
			jb.put("name", custs.getCustomerName());
		}
		ApplyInfoVo.put("applyInfoValue",jb);
		ApplyInfoVo.put("loanNo", model.getLoanNo());
		
		Response<com.alibaba.fastjson.JSONObject> response = iloanInfoInputExecuter.saveApplyInfo(ApplyInfoVo);
		log.info("推送借款返回："+response.isSuccess()+"|"+response.getRepMsg());
  		//借款编号由录单给到
		if(!response.isSuccess()){
			return Result.fail(response.getRepMsg());
		}
		String loanNo =JSONObject.fromObject(response.getData()).getString("loanNo");
		log.info("贷款申请借款编号为："+loanNo);
		//根据借款编号查询是否有记录
  		ApplyLoanInfo applyInfo = new ApplyLoanInfo();
  		applyInfo.setIdCard(model.getIdCard());
  		applyInfo.setLoanNo(loanNo);
  		ApplyLoanInfo ap = applyloaninfomapper.selectByIdCard(applyInfo);
  		
  		if(null != ap){//修改
  			log.info("借款申请修改信息");
  			ap.setFieldValue(model.getFieldValue());
  			ap.setUpdateTime(new Date());
  			applyloaninfomapper.updateToTable(ap);
  			retrunMap.put("loanNo", ap.getLoanNo());
  		}else{
  			log.info("借款申请新增");
  			applyInfo.setIdCard(model.getIdCard());
  			applyInfo.setLoanNo(loanNo);
  			applyInfo.setFieldValue(model.getFieldValue());
  			applyInfo.setFieldKey(Constants.APPLYINFO);
  			applyInfo.setStatus("1");
  			applyInfo.setApplyStatus(Constants.IS_NOT_SUBMIT);
  			applyInfo.setUpdateTime(new Date());
  			applyInfo.setCreateTime(new Date());
  			
  			int i = applyloaninfomapper.insertTable(applyInfo);
  			if(i > 0){
  				retrunMap.put("loanNo", loanNo);
  	  			//初始化第四步步骤状态
  	  			insertNodeInfo(loanNo);
  	  			Customer cust = customerService.queryByIdCard(model.getIdCard());
  	  			IdCardInfo idcard = new IdCardInfo();
	  			idcard.setCustomerId(cust.getId());
	  			idcard.setStatus(Constants.DATA_VALID);
	  			List<IdCardInfo> list = idCardInfoMapper.select(idcard);
	  			if(list != null && !list.isEmpty()){
		  	  		boolean bool = PicUtil.uploadfile(urlUPIC, list.get(0).getBatchNum(),loanNo,"B");
					if(!bool){
						return Result.fail("借款进件失败");
					}
	  			}
  			}else
  				return Result.fail("借款进件失败");
  			
  		}
  		return Result.success(retrunMap);
	}
	
	/**
	 * 获取借款申请数据
	 * @param model
	 * @return
	 */
	public Result checkCreditInfo(Model_005003 model){
		log.info("获取借款申请入参为："+model.getIdCard()+"," +model.getLoanNo()+","+model.getFieldKey());
		//从运营平台拿初始化数据
		JSONObject data = base(Constants.APPLYINFO);
		log.info("获取借款申请-从运营平台获取初始化数据为："+data);
		JSONObject object = new JSONObject();
		object.put("fieldKey", Constants.APPLYINFO);
		if (data.containsKey(Constants.APPLYINFO)) {
			object.put("fieldValue", data.get(Constants.APPLYINFO));
		}
		if(data.containsKey("listData")){
			object.put("listData", data.get("listData"));
		}
		ApplyLoanInfo info = new ApplyLoanInfo();
		info.setIdCard(model.getIdCard());
		info.setApplyStatus(Constants.IS_NOT_SUBMIT);
		info = applyLoanInfoService.selectOne(info);
		
		if(null == info){
			//运营平台初始化数据+拼接上其他参数
			object.put("loanNo", "");
			return Result.success(object);
		}else if(StringUtils.isEmpty(info.getLoanNo())){
			return Result.fail("获取贷款申请失败");
		}else if(StringUtils.isNotEmpty(info.getLoanNo())){//判断借款信息是否推送到录单系统
			//借款进度数据回显
			if(StringUtils.isNotEmpty(info.getLoanNo())){
				info = updateApplyStatus(info.getLoanNo());
			}
			if(info.getApplyStatus().equals(Constants.IS_CANCEL_SUBMIT) ||
					info.getApplyStatus().equals(Constants.IS_REFUSE_SUBMIT) || 
					info.getApplyStatus().equals(Constants.IS_COMPLETE_SUBMIT)){
				object.put("loanNo", "");
				return Result.success(object);
			}
			//从录单那边拿取文本框输入值
			com.alibaba.fastjson.JSONObject  ApplyInfoVo = new com.alibaba.fastjson.JSONObject();
			ApplyInfoVo.put("idNo", model.getIdCard());
			ApplyInfoVo.put("loanNo",info.getLoanNo());
			
			Response<com.alibaba.fastjson.JSONObject> response = iloanInfoInputExecuter.queryApplyInfo(ApplyInfoVo);
			log.info("获取借款申请-从录单获取借款结果返回："+response.isSuccess()+"|"+response.getRepMsg());
			//借款申请信息
			if(!response.isSuccess()){
				return Result.fail(response.getRepMsg());
			}
			log.info("获取借款申请-从录单获取借款内容返回："+response.getData());
			object.put("loanNo", info.getLoanNo());
			object.put(Constants.APPLYINFO+"Value", JSONObject.fromObject(response.getData()));
			return Result.success(object);
		}
		return Result.fail("查询贷款信息异常");
	}
	/**
	 * 获取节点信息
	 * @return
	 */
	public Result queryNodeInfo(Model_005005 model){
		log.info("获取节点信息入参为："+model.getIdCard()+","+model.getFieldKey()+","+model.getLoanNo());
		ApplyLoanInfo info = new ApplyLoanInfo();
		info.setIdCard(model.getIdCard());
		info.setLoanNo(model.getLoanNo());
		info = applyLoanInfoService.selectOne(info);
		if(null == info){
			return Result.fail("该客户没有填借款申请");
		}
		JSONObject ob = new JSONObject();
		ob.put("loanNo", info.getLoanNo());
		//规则引擎推送回来的提示信息
		//只有退回的单子第一步、第二步、第三步、第四步是可以切换
		if(info.getApplyStatus().equals(Constants.IS_REBACK_SUBMIT) && (model.getFieldKey().equals(Constants.NODEINFO) || model.getFieldKey().equals(Constants.PERSIONINFO) || model.getFieldKey().equals(Constants.EMPITEMINFO) || model.getFieldKey().equals(Constants.FRIENDSINFO))){
			ob.put("isRollback", "1");
		}
		//信用账户信息等于2才显示图片0未填写，1填写，2.有误
//		ApplyGroupInfo api = new ApplyGroupInfo();
//		api.setLoanNo(model.getLoanNo());
//		api.setGroupCode(Constants.CREDITACCOUNTINFO);
//		String caState = applyGroupInfoMapper.queryByApplyGroupInfoState(api);
		//房产信息等于2才显示图片0未填写，1填写，2.有误
		ApplyGroupInfo ap = new ApplyGroupInfo();
		ap.setLoanNo(model.getLoanNo());
		ap.setGroupCode(Constants.ASSETSINFO);
		String aState = applyGroupInfoMapper.queryByApplyGroupInfoState(ap);
		
		if(model.getFieldKey().equals(Constants.FRIENDSINFO)){
			//获取初始化数据
			JSONArray arrayc = new JSONArray();
			JSONObject contactPerson = base(Constants.CONTACTPERSONINFO);
			JSONObject jbc =  JSONObject.fromObject(contactPerson.get(Constants.CONTACTPERSONINFO));
			jbc.put("sectionKey", Constants.CONTACTPERSONINFO);
			jbc.put("typeName", "联系人信息");
			jbc.put("sectionLabel", "");			
			arrayc.add(jbc);
			JSONObject mate = base(Constants.MATEINFO);
			JSONObject jbm = JSONObject.fromObject(mate.get(Constants.MATEINFO));
			jbm.put("sectionKey", Constants.MATEINFO);
			jbm.put("typeName", "配偶信息表");
			jbm.put("sectionLabel", "");			
			arrayc.add(jbm);
			//获取下拉信息
			JSONObject jsonOne = new JSONObject();
			if(contactPerson.containsKey("listData")){
				jsonOne.putAll((Map) contactPerson.get("listData"));
			}
			if(mate.containsKey("listData")){
				jsonOne.putAll((Map) mate.get("listData"));
			}
			ob.put("fieldKey", model.getFieldKey());
			ob.put("fieldValue", arrayc);
			ob.put("listData", jsonOne);
		}else if(model.getFieldKey().equals(Constants.NODEINFO)){
			String msgBody = PropertiesReader.readAsString("information.filling.in.information");
			ob.put("message", msgBody);
			//获取初始化状态
			JSONArray ar = JSONObject.fromObject(base(Constants.NODEINFO)).getJSONObject(Constants.NODEINFO).getJSONArray("fieldList");
			JSONArray node = new JSONArray();
			for (int i = 0; i < ar.size(); i++) {
				String fieldKey = ar.getJSONObject(i).getString("fieldKey");
				JSONObject jt = new JSONObject();
				jt.put("label", ar.getJSONObject(i).get("label"));
				jt.put("fieldKey", fieldKey);
				//获取第四步状态信息
				ApplyGroupInfo agi = new ApplyGroupInfo();
				agi.setGroupCode(Constants.NODEINFO);
				agi.setState(Constants.DATA_VALID);
				agi.setLoanNo(model.getLoanNo());
				List<ApplyGroupInfo> ailist = applyGroupInfoMapper.queryByApplyGroupParentCode(agi);
				if(ailist != null && !ailist.isEmpty()){
					for (ApplyGroupInfo applyGroupInfo : ailist) {
						if(fieldKey.equals(applyGroupInfo.getGroupCode())){
							jt.put("state", applyGroupInfo.getState());//0未填写，1填写，2.有误
							jt.put("isEdit", applyGroupInfo.getIsEdit());//0不可编辑1可编辑
							jt.put("isRollback", applyGroupInfo.getIsRollback());//回滚回来的0没有提示，1有提示
							jt.put("tipMsg", applyGroupInfo.getTipMsg());//提示内容
							jt.put("isRequested", applyGroupInfo.getIsRequested());//是否是必填
						}
					}
				}
				node.add(jt);
			}
			JSONObject jt = new JSONObject();
			jt.put("fieldList", node);
			ob.put("fieldKey", model.getFieldKey());
			ob.put("fieldValue", jt);
		}
		/*else if(model.getFieldKey().equals(Constants.SOCIALSECURITYINFO)){
			
			ApplyGroupInfo app = new ApplyGroupInfo();
			app.setLoanNo(model.getLoanNo());
			app.setIsRequested("0");
			app.setApplyGroupId(Long.valueOf("17"));
			applyGroupInfoMapper.updateByPrimaryKeySelective(app);
			
			JSONObject jt = new JSONObject();
			jt.put("state", applyGroupInfo.getState());//0未填写，1填写，2.有误
			jt.put("isEdit", applyGroupInfo.getIsEdit());//0不可编辑1可编辑
			jt.put("isRollback", applyGroupInfo.getIsRollback());//回滚回来的0没有提示，1有提示
			jt.put("tipMsg", applyGroupInfo.getTipMsg());//提示内容
			jt.put("isRequested", applyGroupInfo.getIsRequested());//是否是必填
		}*/
		
		else if(model.getFieldKey().equals(Constants.EDUCATIONINFO)){
			JSONArray arrayc = new JSONArray();
			JSONObject ed = base(Constants.EDUCATIONINFO);
			JSONObject jbc =  JSONObject.fromObject(ed.get(Constants.EDUCATIONINFO));
			jbc.put("sectionKey", Constants.EDUCATIONINFO);
			jbc.put("typeName", "");
			jbc.put("sectionLabel", "学历学籍");	
			arrayc.add(jbc);
//			JSONObject chsi = base(Constants.EDUCATIONCHSI);
//			JSONObject cjbc =  JSONObject.fromObject(chsi.get(Constants.EDUCATIONCHSI));
//			cjbc.put("sectionKey", Constants.EDUCATIONCHSI);
//			cjbc.put("typeName", "");
//			cjbc.put("sectionLabel", "学籍认证");			
//			arrayc.add(cjbc);
//			JSONObject mark = base(Constants.EDUCATIONMARK);
//			JSONObject mjbc =  JSONObject.fromObject(mark.get(Constants.EDUCATIONMARK));
//			mjbc.put("sectionKey", Constants.EDUCATIONMARK);
//			mjbc.put("typeName", "");
//			mjbc.put("sectionLabel", "学籍认证");
//			arrayc.add(mjbc);
			//获取下拉信息
			JSONObject jsonOne = new JSONObject();
			if(ed.containsKey("listData")){
				jsonOne.putAll((Map) ed.get("listData"));
			}
//			if(chsi.containsKey("listData")){
//				jsonOne.putAll((Map) chsi.get("listData"));
//			}
//			if(mark.containsKey("listData")){
//				jsonOne.putAll((Map) mark.get("listData"));
//			}
			ob.put("fieldKey", model.getFieldKey());
			ob.put("fieldValue", arrayc);
			ob.put("listData", jsonOne);
		}else if(model.getFieldKey().equals(Constants.POLICYINFO)){
			JSONArray arrayc = new JSONArray();
			JSONObject po = base(Constants.POLICYINFO);
			JSONObject pjbc =  JSONObject.fromObject(po.get(Constants.POLICYINFO));
			pjbc.put("sectionKey", Constants.POLICYINFO);
			pjbc.put("typeName", "");
			pjbc.put("sectionLabel", "寿险投保记录");	
			arrayc.add(pjbc);
			
//			JSONObject pa = base(Constants.POLICYACCOUNT);
//			JSONObject pajbc =  JSONObject.fromObject(pa.get(Constants.POLICYACCOUNT));
//			pajbc.put("sectionKey", Constants.POLICYINFO);
//			pajbc.put("typeName", "");
//			pajbc.put("sectionLabel", "投保认证");	
//			arrayc.add(pajbc);
			//获取下拉信息
			JSONObject jsonOne = new JSONObject();
			if(po.containsKey("listData")){
				jsonOne.putAll((Map) po.get("listData"));
			}
//			if(pa.containsKey("listData")){
//				jsonOne.putAll((Map) pa.get("listData"));
//			}
			String msgBody = PropertiesReader.readAsString("information.filling.in.policyInfo");
			ob.put("message", msgBody);//文案待修改
			ob.put("fieldKey", model.getFieldKey());
			ob.put("fieldValue", arrayc);
			ob.put("listData", jsonOne);
		}else if(model.getFieldKey().equals(Constants.ASSETSINFO)){
			ob.put("fieldKey", model.getFieldKey());
			JSONArray carr = new JSONArray();
			JSONObject base = base(model.getFieldKey());
			JSONObject car = base.getJSONObject(model.getFieldKey());
			car.put("sectionKey", Constants.ASSETSINFO);
			car.put("typeName", "");
			car.put("sectionLabel", "房产信息填写");
			carr.add(car);
			//查询是否有图片
			ApplyPicInfo pic = new ApplyPicInfo();
			pic.setLoanNo(model.getLoanNo());
			pic.setFieldKey(Constants.ASSETSINFO);
			List<ApplyPicInfo> picList = applypicinfomapper.select(pic);
			if((aState != null && aState.equals("2")) || (picList != null && !picList.isEmpty())){
				String msgBody = PropertiesReader.readAsString("information.filling.in.assetsInfo");
				JSONObject pjbc =  new JSONObject();
				pjbc.put("sectionKey", Constants.ASSETSINFO);
				pjbc.put("typeName", "");
				pjbc.put("sectionLabel", "房产认证");
				pjbc.put("message", msgBody);//文案待修改
				carr.add(pjbc);
			}
			ob.put("fieldValue", carr);
			if(base.containsKey("listData")){
				ob.put("listData", base.get("listData"));
			}
		}else if(model.getFieldKey().equals(Constants.CREDITACCOUNTINFO)){
			ob.put("fieldKey", model.getFieldKey());
			JSONArray carr = new JSONArray();
			JSONObject base = base(model.getFieldKey());
			JSONObject car = base.getJSONObject(model.getFieldKey());
			car.put("sectionKey", Constants.CREDITACCOUNTINFO);
			car.put("typeName", "");
			car.put("sectionLabel", "信用账户信息");
			carr.add(car);
			
			//if(caState != null && caState.equals("2")){
			JSONObject pjbc =  new JSONObject();
			pjbc.put("sectionKey", Constants.CREDITACCOUNTINFO);
			pjbc.put("typeName", "");
			pjbc.put("sectionLabel", "信用账户认证");
			pjbc.put("message", "");//文案待修改
			carr.add(pjbc);
			//}
			ob.put("fieldValue", carr);
			if(base.containsKey("listData")){
				ob.put("listData", base.get("listData"));
			}
		}else if(model.getFieldKey().equals(Constants.CARINFO)){
			ob.put("fieldKey", model.getFieldKey());
			JSONArray carr = new JSONArray();
			JSONObject base = base(model.getFieldKey());
			JSONObject car = base.getJSONObject(model.getFieldKey());
			car.put("sectionKey", Constants.CARINFO);
			car.put("typeName", "");
			car.put("sectionLabel", "车产信息填写");
			carr.add(car);
			
			String msgBody = PropertiesReader.readAsString("information.filling.in.carInfo");
			JSONObject pjbc =  new JSONObject();
			pjbc.put("sectionKey", Constants.CARINFO);
			pjbc.put("typeName", "");
			pjbc.put("message", msgBody);//文案待修改
			pjbc.put("sectionLabel", "车产认证");
			carr.add(pjbc);
			ob.put("fieldValue", carr);
			if(base.containsKey("listData")){
				ob.put("listData", base.get("listData"));
			}
		}else if(model.getFieldKey().equals(Constants.RESIDENCEINFO)){
			String msgBody = PropertiesReader.readAsString("information.filling.in.residenceInfo");
			ob.put("fieldKey", model.getFieldKey());
			JSONObject pjbc =  new JSONObject();
			pjbc.put("sectionKey", Constants.RESIDENCEINFO);
			pjbc.put("typeName", "");
			pjbc.put("sectionLabel", "居住证明");
			pjbc.put("message", msgBody);//文案待修改
			ob.put("fieldValue", pjbc);
		}else if(model.getFieldKey().equals(Constants.MERCHANTLOANINFO)){
			ob.put("fieldKey", model.getFieldKey());
			JSONArray carr = new JSONArray();
			JSONObject base = base(model.getFieldKey());
			JSONObject car = base.getJSONObject(model.getFieldKey());
			car.put("sectionKey", Constants.MERCHANTLOANINFO);
			car.put("typeName", "");
			car.put("sectionLabel", "淘宝账户信息填写");
			carr.add(car);
			String msgBody = PropertiesReader.readAsString("information.filling.in.merchantLoanInfo");
			JSONObject pjbc =  new JSONObject();
			pjbc.put("sectionKey", Constants.MERCHANTLOANINFO);
			pjbc.put("typeName", "");
			pjbc.put("sectionLabel", "淘宝认证");
			pjbc.put("message", msgBody);//文案待修改
			carr.add(pjbc);
			ob.put("fieldValue", carr);
			if(base.containsKey("listData")){
				ob.put("listData", base.get("listData"));
			}
		}else if(model.getFieldKey().equals(Constants.CARDLOANINFO)){
			String msgBody = PropertiesReader.readAsString("information.filling.in.cardLoanInfo");
			ob.put("fieldKey", model.getFieldKey());
			JSONObject pjbc =  new JSONObject();
			pjbc.put("sectionKey", Constants.CARDLOANINFO);
			pjbc.put("typeName", "");
			pjbc.put("sectionLabel", "信用卡认证");
			pjbc.put("message", msgBody);//文案待修改
			ob.put("fieldValue", pjbc);
		}else if(model.getFieldKey().equals(Constants.ZMSCORE)){//芝麻分

			ob.put("fieldKey", model.getFieldKey());
			//JSONArray carr = new JSONArray();
			JSONObject base = base(model.getFieldKey());
			JSONObject car = base.getJSONObject(model.getFieldKey());
			//carr.add(car);
			ob.put("fieldValue", car);
			if(base.containsKey("listData")){
				ob.put("listData", base.get("listData"));
			}
		
		}
		else if(model.getFieldKey().equals(Constants.OTHERINFO)){
			ob.put("fieldKey", model.getFieldKey());
			JSONObject pjbc =  new JSONObject();
			pjbc.put("sectionKey", Constants.OTHERINFO);
			pjbc.put("typeName", "");
			pjbc.put("sectionLabel", "其他信息");
			pjbc.put("message", "");//文案待修改
			ob.put("fieldValue", pjbc);
		}else{
			//从运营平台获取初始化数据
			ob.put("fieldKey", model.getFieldKey());
			JSONObject base = base(model.getFieldKey());
			if (base.containsKey(model.getFieldKey())) {
				ob.put("fieldValue", base.get(model.getFieldKey()));
			}
			if(base.containsKey("listData")){
				ob.put("listData", base.get("listData"));
			}
		}
		//判断是否从已经提交
		if(info.getApplyStatus().equals(Constants.IS_SUBMIT)){
			//图片信息
			ApplyPicInfo pic = new ApplyPicInfo();
			pic.setFieldKey(model.getFieldKey());
			pic.setLoanNo(model.getLoanNo());
			List<ApplyPicInfo> list =  applypicinfomapper.select(pic);
			if(list != null && !list.isEmpty()){
				//图片地址
				JSONArray ja = new JSONArray();
				for (ApplyPicInfo applyPicInfo : list) {
					JSONObject jb = new JSONObject();
					jb.put("picId", applyPicInfo.getPicId());
					jb.put("picUrl", picUrlStr(applyPicInfo.getPicUrl()));
					jb.put("picName", applyPicInfo.getImgName());
					ja.add(jb);
				}
				ob.put(model.getFieldKey()+"Pic", ja);
			}
			JSONObject ldbase= ldBase(model.getIdCard(),model.getLoanNo()).getJSONObject("baseInfo");
			//运营平台初始化数据+录单文本框数据
			if(model.getFieldKey().equals(Constants.FRIENDSINFO)){
				JSONArray js = ldbase.getJSONArray(Constants.CONTACTPERSONINFO);
				JSONObject contactPerson = new JSONObject();
				if(js != null && js.size() > 0){
					for (int i = 0; i < js.size(); i++) {
						JSONObject jj = js.getJSONObject(i);
						contactPerson.put((jj.getInt("sequenceNum")-1),jj);
					}
				}
				ob.put(Constants.CONTACTPERSONINFO+"Value", contactPerson);
				JSONObject jo = ldbase.getJSONObject(Constants.MATEINFO);
				JSONObject mate = new JSONObject();
				if(jo != null && jo.size() > 0){
					mate.put("0",jo);
				}
				ob.put(Constants.MATEINFO+"Value", mate);
			}else if(model.getFieldKey().equals(Constants.POLICYINFO)){
				JSONArray js = ldbase.getJSONArray(Constants.POLICYINFO);
				JSONObject policy = new JSONObject();
				if(js != null && js.size() > 0){
					for (int i = 0; i < js.size(); i++) {
						policy.put(i,js.getJSONObject(i));
					}
				}
				ob.put(Constants.POLICYINFO+"Value", policy);
			}else{
				JSONObject jo = ldbase.getJSONObject(model.getFieldKey());
				if(jo != null && jo.size() >0){
					ob.put(model.getFieldKey()+"Value",jo);
				}	
			}
			return Result.success(ob);
		}else{
			//从本地 运营平台初始化数据+本地数据
			ApplyBaseInfo applyBase = new ApplyBaseInfo();
			applyBase.setLoanNo(model.getLoanNo());
			if(model.getFieldKey().equals(Constants.NODEINFO)){
				applyBase.setFieldKey(model.getFieldKey());
			}else{
				if(ConstantStatus.getFieldKey(model.getFieldKey()).equals(Constants.NODEINFO)){
					applyBase.setFieldKey(model.getFieldKey());
				}else{
					applyBase.setFieldKeyParent(model.getFieldKey());
				}
			}
			
			List<ApplyBaseInfo> baseList = applyBaseInfoService.applyBaseInfoList(applyBase);
			
			//图片信息
			ApplyPicInfo pic = new ApplyPicInfo();
			pic.setFieldKey(model.getFieldKey());
			pic.setLoanNo(model.getLoanNo());
			List<ApplyPicInfo> list =  applypicinfomapper.select(pic);
			if(list != null && !list.isEmpty()){
				//图片地址
				JSONArray ja = new JSONArray();
				for (ApplyPicInfo applyPicInfo : list) {
					JSONObject jb = new JSONObject();
					jb.put("picId", applyPicInfo.getPicId());
					jb.put("picUrl", picUrlStr(applyPicInfo.getPicUrl()));
					jb.put("picName", applyPicInfo.getImgName());
					ja.add(jb);
				}
				ob.put(model.getFieldKey()+"Pic", ja);
			}
			if(null != baseList && !baseList.isEmpty()){
				//判断是否是第四步，如果是第四步拼接值格式+运营平台初始化数据，否则直接获取节点+初始化数据
				if(ConstantStatus.getFieldKey(model.getFieldKey()).equals(Constants.NODEINFO)){
					if(model.getFieldKey().equals(Constants.POLICYINFO)){
						JSONObject policy = new JSONObject();
						for (ApplyBaseInfo applyBaseInfo : baseList) {
							policy.put(applyBaseInfo.getTab(),applyBaseInfo.getFieldObjValue());
						}
						ob.put(Constants.POLICYINFO+"Value", policy);
						return Result.success(ob);
					}else{
						ob.put(model.getFieldKey()+"Value", baseList.get(0).getFieldObjValue());
						return Result.success(ob);
					}
				}else if(model.getFieldKey().equals(Constants.FRIENDSINFO)){
					JSONObject contactPerson = new JSONObject();
					JSONObject mate = new JSONObject();
					for (ApplyBaseInfo applyBaseInfo : baseList) {
						if(applyBaseInfo.getFieldKey().equals(Constants.CONTACTPERSONINFO)){
							contactPerson.put((applyBaseInfo.getTab()-1),applyBaseInfo.getFieldObjValue());
						}
						if(applyBaseInfo.getFieldKey().equals(Constants.MATEINFO)){
							mate.put("0",applyBaseInfo.getFieldObjValue());
						}
					}
					ob.put(Constants.CONTACTPERSONINFO+"Value", contactPerson);
					ob.put(Constants.MATEINFO+"Value", mate);
					return Result.success(ob);
				}else{
					ob.put(model.getFieldKey()+"Value",baseList.get(0).getFieldObjValue());
					return Result.success(ob);
				}
			}else{
				//从初始化数据中获取相应的节点数据
				return Result.success(ob);
			}
		}
	}
	

	/**
	 * 申请信息保存
	 * @param model
	 * @return
	 */
	public Result psersonInfo(Model_005004 model) throws Exception{
		if(model.getFieldKey().equals(Constants.PERSIONINFO)){//第一步个人信息操作
			//根据appNo先查询库中是否有值，有表示修改，没有表示刚进件
			insertToLocal(model,Constants.PERSIONINFO);
			//如果之前有配偶信息，现在个人信息修改成未婚就要删除配偶信息
			if(StringUtils.isNotEmpty(model.getFieldValue())){
				JSONObject object = JSONObject.fromObject(model.getFieldValue());
				if(object.containsKey("maritalStatus") && StringUtils.isNotEmpty(object.getString("maritalStatus"))){
					if(!"00002".equals(object.getString("maritalStatus"))){
						ApplyBaseInfo mate = new ApplyBaseInfo();
						mate.setLoanNo(model.getLoanNo());
						mate.setFieldKey(Constants.MATEINFO);
						applybaseinfomapper.deleteByFiledKey(mate);
					}
				}
				//房产同住址地址选择是，住宅地址变化后房产地址未变化
				ApplyBaseInfo abi = new ApplyBaseInfo();
				abi.setLoanNo(model.getLoanNo());
				abi.setFieldKey(Constants.ASSETSINFO);
				abi = applybaseinfomapper.selectBaseOne(abi);
				if(abi != null && StringUtils.isNotEmpty(abi.getFieldObjValue())){
					JSONObject js = JSONObject.fromObject(abi.getFieldObjValue());
					if(js.containsKey("eatateSameRegistered") && StringUtils.isNotEmpty(js.getString("eatateSameRegistered")) && "1".equals(js.getString("eatateSameRegistered"))){
						js.put("estate", object.getString("home"));
						js.put("estateText", object.getString("homeText"));
						js.put("estateAddress", object.getString("homeAddress"));
						abi.setFieldObjValue(js.toString());
						applybaseinfomapper.updateToTable(abi);
					}
				}
				//联系人判断
				ApplyBaseInfo abf = new ApplyBaseInfo();
				abf.setLoanNo(model.getLoanNo());
				abf.setFieldKey(Constants.CONTACTPERSONINFO);
				List<ApplyBaseInfo> listabf = applybaseinfomapper.selectBaseTable(abf);
				if(listabf != null && !listabf.isEmpty()){
					if(object.containsKey("cellphone") && StringUtils.isNotEmpty(object.getString("cellphone"))){
						for (ApplyBaseInfo apply : listabf) {
							JSONObject jt =  JSONObject.fromObject(apply.getFieldObjValue());
							if(jt.containsKey("contactCellphone") && StringUtils.isNotEmpty(jt.getString("contactCellphone")) 
									&& object.containsKey("cellphone") &&  StringUtils.isNotEmpty(object.getString("cellphone")) 
									&& object.getString("cellphone").equals(jt.getString("contactCellphone"))){
								return Result.fail("本人手机号不能与联系人手机号一致");
							}
							if(jt.containsKey("contactCellphone") && StringUtils.isNotEmpty(jt.getString("contactCellphone"))
								&& object.containsKey("cellphoneSec") && StringUtils.isNotEmpty(object.getString("cellphoneSec"))
								&& object.getString("cellphoneSec").equals(jt.getString("contactCellphone"))){
								return Result.fail("本人备用手机号不能与联系人手机号一致");
							}
						}
					}
				}
				//配偶判断
				ApplyBaseInfo ab = new ApplyBaseInfo();
				ab.setLoanNo(model.getLoanNo());
				ab.setFieldKey(Constants.MATEINFO);
				ab = applybaseinfomapper.selectBaseOne(ab);
				if(ab != null && StringUtils.isNotEmpty(ab.getFieldObjValue())){
					if(object.containsKey("cellphone") && StringUtils.isNotEmpty(object.getString("cellphone"))){
						JSONObject jt =  JSONObject.fromObject(ab.getFieldObjValue());
						if(jt.containsKey("contactCellphone") && StringUtils.isNotEmpty(jt.getString("contactCellphone")) 
								&& object.getString("cellphone").equals(jt.getString("contactCellphone"))){
							return Result.fail("本人手机号不能与配偶手机号一致");
						}
						if(jt.containsKey("contactCellphone") && StringUtils.isNotEmpty(jt.getString("contactCellphone"))
								&& object.containsKey("cellphoneSec") && StringUtils.isNotEmpty(object.getString("cellphoneSec"))
								&& object.getString("cellphoneSec").equals(jt.getString("contactCellphone"))){
								return Result.fail("本人备用手机号不能与配偶手机号一致");
							}
					}
				}
			}
		}else if(model.getFieldKey().equals(Constants.EMPITEMINFO)){//第二步工作信息操作
			//根据appNo先查询库中是否有值，有表示修改，没有表示刚进件
			insertToLocal(model,Constants.EMPITEMINFO);
		}else if(model.getFieldKey().equals(Constants.CONTACTPERSONINFO)){//第三步联系人信息
			JSONObject js = JSONObject.fromObject(model.getFieldValue());
			//校验当前联系人与个人信息中的手机号、备用号码是否重复
			ApplyBaseInfo abi = new ApplyBaseInfo();
			abi.setLoanNo(model.getLoanNo());
			abi.setFieldKey(Constants.PERSIONINFO);
			abi = applybaseinfomapper.selectBaseOne(abi);
			if(abi != null){
				JSONObject jb = JSONObject.fromObject(abi.getFieldObjValue());
				if(js.containsKey("contactCellphone") && StringUtils.isNotEmpty(js.getString("contactCellphone"))){
					if(abi != null && StringUtils.isNotEmpty(abi.getFieldObjValue())){
						if(jb.containsKey("cellphone") && StringUtils.isNotEmpty(jb.getString("cellphone")) && jb.getString("cellphone").equals(js.getString("contactCellphone"))){
							return Result.fail("联系人手机号不能与本人信息手机号一致");
						}
						if(jb.containsKey("cellphoneSec") && StringUtils.isNotEmpty(jb.getString("cellphoneSec")) && jb.getString("cellphoneSec").equals(js.getString("contactCellphone"))){
							return Result.fail("联系人手机号不能与本人信息备用手机号一致");
						}
					}
				}
				if(jb.containsKey("name") && js.containsKey("contactName") && js.getString("contactName").equals(jb.getString("name"))){
					return Result.fail("联系人姓名不能与本人姓名一致");
				}
			}
			//根据appNo先查询库中是否有值，有表示修改，没有表示刚进件
			insertToLocal(model,Constants.FRIENDSINFO);
		}else if(model.getFieldKey().equals(Constants.MATEINFO)){
			JSONObject js = JSONObject.fromObject(model.getFieldValue());
			//校验当前联系人与个人信息中的手机号、备用号码是否重复
			ApplyBaseInfo abi = new ApplyBaseInfo();
			abi.setLoanNo(model.getLoanNo());
			abi.setFieldKey(Constants.PERSIONINFO);
			abi = applybaseinfomapper.selectBaseOne(abi);
			if(abi != null){
				JSONObject jb = JSONObject.fromObject(abi.getFieldObjValue());
				if(js.containsKey("contactCellphone") && StringUtils.isNotEmpty(js.getString("contactCellphone"))){
					if(abi != null && StringUtils.isNotEmpty(abi.getFieldObjValue())){
						if(jb.containsKey("cellphone") && StringUtils.isNotEmpty(jb.getString("cellphone")) && js.getString("contactCellphone").equals(jb.getString("cellphone"))){
							return Result.fail("配偶手机号不能与个人信息手机号重复");
						}
						if(jb.containsKey("cellphoneSec") && StringUtils.isNotEmpty(jb.getString("cellphoneSec")) && js.getString("contactCellphone").equals(jb.getString("cellphoneSec"))){
							return Result.fail("配偶手机号不能与个人信息备用手机号重复");
						}
					}
					if(jb.containsKey("name") && js.containsKey("contactName") && js.getString("contactName").equals(jb.getString("name"))){
						return Result.fail("配偶姓名不能与本人姓名一致");
					}
				}
			}
			//根据appNo先查询库中是否有值，有表示修改，没有表示刚进件
			insertToLocal(model,Constants.FRIENDSINFO);
		}else if(model.getFieldKey().equals(Constants.ASSETSINFO)){//房产信息
			if(StringUtils.isNotEmpty(model.getPictureValue()))
				pictureValue(model.getPictureValue(),model.getFieldKey(),model.getLoanNo());
			
			if(StringUtils.isNotEmpty(model.getFieldValue())){
				JSONObject jo = JSONObject.fromObject(model.getFieldValue());
				ApplyBaseInfo abi = new ApplyBaseInfo();
				abi.setLoanNo(model.getLoanNo());
				abi.setFieldKey(Constants.PERSIONINFO);
				abi = applybaseinfomapper.selectBaseOne(abi);
				if(abi != null && StringUtils.isNotEmpty(abi.getFieldObjValue()) && jo.containsKey("eatateSameRegistered") && StringUtils.isNotEmpty(jo.getString("eatateSameRegistered"))){
					JSONObject jbc = JSONObject.fromObject(abi.getFieldObjValue());
					String home = jbc.getString("home").concat(jbc.getString("homeAddress"));
					String astate = jo.getString("estate").concat(jo.getString("estateAddress"));
					if("1".equals(jo.getString("eatateSameRegistered"))){
						if(!home.equals(astate)){
							return Result.fail("房产同住宅地址选择是，必须房产地址与住宅地址一致");
						}
					}else{
						if(home.equals(astate)){
							return Result.fail("房产同住宅地址选择否，必须房产地址与住宅地址不一致");
						}
					}
				}
				//根据appNo先查询库中是否有值，有表示修改，没有表示刚进件
				insertToLocal(model,Constants.NODEINFO);
			}
		}else if(model.getFieldKey().equals(Constants.CARINFO)){//车辆信息
			if(StringUtils.isNotEmpty(model.getPictureValue()))
				pictureValue(model.getPictureValue(),model.getFieldKey(),model.getLoanNo());
			//根据appNo先查询库中是否有值，有表示修改，没有表示刚进件
			insertToLocal(model,Constants.NODEINFO);
		}else if(model.getFieldKey().equals(Constants.MERCHANTLOANINFO)){//淘宝账户信息
			if(StringUtils.isNotEmpty(model.getPictureValue()))
				pictureValue(model.getPictureValue(),model.getFieldKey(),model.getLoanNo());
			//根据appNo先查询库中是否有值，有表示修改，没有表示刚进件
			insertToLocal(model,Constants.NODEINFO);
		}else if(model.getFieldKey().equals(Constants.SOCIALSECURITYINFO)){//公积金/社保信息
			//根据appNo先查询库中是否有值，有表示修改，没有表示刚进件
			insertToLocal(model,Constants.NODEINFO);
			//先查询
			ApplyGroupInfo ap = new ApplyGroupInfo();
			ap.setLoanNo(model.getLoanNo());
			ap.setState("1");
			ap.setApplyGroupId(Long.valueOf("17"));
			List<ApplyGroupInfo> apl = applyGroupInfoMapper.select(ap);
			if(apl.size()==0){
				ApplyGroupInfo app = new ApplyGroupInfo();
				app.setLoanNo(model.getLoanNo());
				app.setState("1");
				app.setIsEdit("1");
				app.setIsRollback("0");
				app.setIsRequested("0");
				app.setApplyGroupId(Long.valueOf("17"));
				applyGroupInfoMapper.insert(app);
			}
			
			
			/*jt.put("state", applyGroupInfo.getState());//0未填写，1填写，2.有误
			jt.put("isEdit", applyGroupInfo.getIsEdit());//0不可编辑1可编辑
			jt.put("isRollback", applyGroupInfo.getIsRollback());//回滚回来的0没有提示，1有提示
			jt.put("tipMsg", applyGroupInfo.getTipMsg());//提示内容
			jt.put("isRequested", applyGroupInfo.getIsRequested());//是否是必填
*/			
			//如果是公积金社保信息填写则更新为绿色打钩
		}/*else if(model.getFieldKey().equals(Constants.INSURANCEINFO)){//社保信息
			//根据appNo先查询库中是否有值，有表示修改，没有表示刚进件
			insertToLocal(model,Constants.NODEINFO);
		}*/else if(model.getFieldKey().equals(Constants.CARDLOANINFO)){//信用卡信息
			if(StringUtils.isNotEmpty(model.getPictureValue()))
				pictureValue(model.getPictureValue(),model.getFieldKey(),model.getLoanNo());
			//根据appNo先查询库中是否有值，有表示修改，没有表示刚进件
			insertToLocal(model,Constants.NODEINFO);
		}else if(model.getFieldKey().equals(Constants.EDUCATIONINFO)){//学历信息
			//根据appNo先查询库中是否有值，有表示修改，没有表示刚进件
			insertToLocal(model,Constants.NODEINFO);
		}else if(model.getFieldKey().equals(Constants.POLICYINFO)){//寿险信息
			//根据appNo先查询库中是否有值，有表示修改，没有表示刚进件
			insertToLocal(model,Constants.NODEINFO);
		}else if(model.getFieldKey().equals(Constants.CREDITACCOUNTINFO)){//信用账户信息
			if(StringUtils.isNotEmpty(model.getPictureValue()))
				pictureValue(model.getPictureValue(),model.getFieldKey(),model.getLoanNo());
			//根据appNo先查询库中是否有值，有表示修改，没有表示刚进件
			//insertToLocal(model,Constants.NODEINFO);
		}else if(model.getFieldKey().equals(Constants.RESIDENCEINFO)){//居住证信息
			if(StringUtils.isNotEmpty(model.getPictureValue()))
				pictureValue(model.getPictureValue(),model.getFieldKey(),model.getLoanNo());
		}else if(model.getFieldKey().equals(Constants.OTHERINFO)){//其他
			if(StringUtils.isNotEmpty(model.getPictureValue()))
				pictureValue(model.getPictureValue(),model.getFieldKey(),model.getLoanNo());
		}else if(model.getFieldKey().equals(Constants.ZMSCORE)){//芝麻信用分
			
			insertToLocal(model,Constants.NODEINFO);
		}
	
		return Result.success("保存成功");
	}
	
	public void insertToLocal(Model_005004 model,String code){
		ApplyBaseInfo baseInfo = new ApplyBaseInfo();
		//根据appNo先查询库中是否有值，有表示修改，没有表示刚进件
		baseInfo.setLoanNo(model.getLoanNo());
		baseInfo.setFieldKey(model.getFieldKey());
		baseInfo.setState(Constants.DATA_VALID);
		if(StringUtils.isNotEmpty(model.getTab())){
			if(code.equals(Constants.FRIENDSINFO))
				baseInfo.setTab(Integer.parseInt(model.getTab()) + 1);
			else
				baseInfo.setTab(Integer.parseInt(model.getTab()));
		}
		ApplyBaseInfo base = applybaseinfomapper.selectBaseOne(baseInfo);
		if(base!=null){
			JSONObject jb = JSONObject.fromObject(model.getFieldValue());
			if(jb.containsKey("sequenceNum") && StringUtils.isNotEmpty(jb.getString("sequenceNum")) && code.equals(Constants.FRIENDSINFO)){
				jb.put("sequenceNum", String.valueOf(jb.getInt("sequenceNum")+1));
			}
			if(jb.containsKey("delete") && StringUtils.isNotEmpty(jb.getString("delete"))){
				applybaseinfomapper.deleteByLoan(base);
			}else{
				base.setFieldObjValue(jb.toString());
				base.setUpdateDate(new Date());
				int i = applybaseinfomapper.updateToTable(base);
			}
		}else{
			JSONObject jb = JSONObject.fromObject(model.getFieldValue());
			if(jb.containsKey("sequenceNum") && StringUtils.isNotEmpty(jb.getString("sequenceNum")) && code.equals(Constants.FRIENDSINFO)){
				jb.put("sequenceNum", String.valueOf(jb.getInt("sequenceNum")+1));
			}
			baseInfo.setFieldKey(model.getFieldKey());
			baseInfo.setFieldObjValue(jb.toString());
			if(StringUtils.isNotEmpty(model.getTab())){
				if(code.equals(Constants.FRIENDSINFO))
					baseInfo.setTab(Integer.parseInt(model.getTab()) + 1);
				else
					baseInfo.setTab(Integer.parseInt(model.getTab()));
			}
			baseInfo.setFieldKeyParent(code);
			baseInfo.setState("1");
			baseInfo.setCreateDate(new Date());
			baseInfo.setUpdateDate(new Date());
			applybaseinfomapper.insertByLoanAndKey(baseInfo);
		}
		updateNodeInfo(model.getLoanNo(),model.getFieldKey());
	}
	
	/**
	 * 图片删除接口
	 * @param model
	 * @return
	 * */
	public boolean deletePicture(String fileId){
		JSONObject param = new JSONObject();
		param.put("ids", fileId);
		param.put("operator", "zdqq");
		param.put("jobNumber", "zdqq");
		String param1 = "ids="+fileId+"&operator=zdqq&jobNumber=zdqq";

		String obj = HttpClientUtil.sendHttpPost1(param1);
		com.alibaba.fastjson.JSONObject objs = com.alibaba.fastjson.JSONObject.parseObject(obj);
		if(!"000000".equals(objs.get("errorcode"))) {
			log.info(Constants.RESP_CODE_ERROR+"PIC图片系统删除调用失败");
			return false;
		}
		log.info("图片删除入参为："+fileId +"出参为："+obj);
		ApplyPicInfo info = new ApplyPicInfo();
		info.setPicId(fileId);
		int id = applypicinfomapper.delete(info);
		if(id > 0)
			return true;
		return false;
	}
	
	/**
	 * 图片处理
	 * @param pictureValue
	 * @throws Exception 
	 */
	public Result pictureValue(String pictureValue,String fieldKey,String loanNo ) throws Exception{
		JSONArray object = JSONArray.fromObject(pictureValue);
		if(object != null && object.size() > 0){
			for (int i = 0; i < object.size(); i++) {
				JSONObject jo = object.getJSONObject(i);
				if(jo != null && jo.size() >0){
					Integer imageStatus = jo.getInt("picStatus");
					if(imageStatus == 1){
						String imageByte = (String) jo.get("picByte");
						String imageDesc = (String) jo.get("picName");
						Model_005012 model = new Model_005012();
						model.setFieldKey(fieldKey);
						model.setLoanNo(loanNo);
						boolean flag  = upPicture(model,imageByte,imageDesc);
						if(!flag){
							return Result.fail("图片保存失败");
						}
						//updateNodeInfo(loanNo,fieldKey);
					}else if(imageStatus == 2){
						String imageByte = (String) jo.get("picByte");
						String imageDesc = (String) jo.get("picName");
						String imageId = (String) jo.get("picId");
						boolean flag = replace(imageByte,imageDesc,imageId);
						if(!flag){
							return Result.fail("图片修改失败");
						}
//						ApplyGroupInfo apg = new ApplyGroupInfo();
//						apg.setState("1");
						//updateNodeInfo(loanNo,fieldKey);
					}else if(imageStatus == 3){
						String imageId = (String) jo.get("picId");
						boolean flag = deletePicture(imageId);
						if(!flag){
							return Result.fail("图片删除失败");
						}
					}
				}
			}
		}
		//更新节点
		updateNodeInfo(loanNo,fieldKey);
		return Result.success("图片保存成功");
	}
	
	/**
	 * 图片修改接口
	 * @param model
	 * @return
	 * @throws Exception 
	 * */
	public boolean replace(String image,String name,String imageId)throws Exception{
		Map<String,  String>  queryParas  =  new  HashMap<>();
    	queryParas.put("id",imageId);
    	queryParas.put("operator","zdqq");
    	queryParas.put("jobNumber","zdqq");
    	String targetFileName ="./" + UUID.randomUUID()+".jpg";
  		//生成图片
  		base64Utils.GenerateImage(image,targetFileName);
  		File  file  =  new  File(targetFileName);//参数file
	    if(!file.exists()){
			throw new Exception(Constants.RESP_CODE_ERROR );
		}
		String  result  =  HttpKit.post(urlUPIC+Constants.PIC_URL_REPLACE, queryParas, file);
		String httpResult = URLDecoder.decode(result, "UTF-8");
	    //删除生成的图片
		if(file != null && file.exists()){
			FileDownUtils.deleteDir(file);
		}
		com.alibaba.fastjson.JSONObject objs = com.alibaba.fastjson.JSONObject.parseObject(httpResult);
		
		if(!"000000".equals(objs.get("errorcode"))) {
			log.info(Constants.RESP_CODE_ERROR+"PIC图片系统上传调用失败");
			return false;
		}
		return true;
	}
	
	/**
	 * 图片转换
	 * @param url
	 * @return
	 */
	private String picUrlStr(String url){
		String picUrlStr = null;
		if(null != url){
			try {
				String urlParam = AesUtil.encryptAES(Constants.HTTP_STR+url,appKey);
				picUrlStr=appPicGateWayUrl+urlParam;
			} catch (Exception e) {
				log.info("图片加密出错！");
			}
		}
		return picUrlStr;
	}
	
	/**
	 * 图片上传接口
	 * @param model
	 * @return
	 * @throws Exception 
	 * */
	public boolean upPicture(Model_005012 model,String image,String name) throws Exception{
		log.info("图片上传入参为："+ model.getLoanNo()+","+model.getUploadFileList()+","+model.getFieldKey()+","+image+","+name);
		String loanNo=model.getLoanNo(); 
		String imgKey = model.getFieldKey();
		//List<ApplyPicInfo> list = new ArrayList<ApplyPicInfo>();
		Map<String,  String>  queryParas  =  new  HashMap<>();
    	queryParas.put("appNo",loanNo);
    	queryParas.put("operator","zdqq");
    	queryParas.put("jobNumber","zdqq");
    	queryParas.put("nodeKey",  "loanApplication");//录单环节
    	queryParas.put("sysName",  "app");
	    queryParas.put("dataSources", "0"); //1：pc端，0：app端
	    if(model.getFieldKey().equals(Constants.CARINFO)){
	    	queryParas.put("code", "G");
	    }
	    if(model.getFieldKey().equals(Constants.ASSETSINFO)){
	    	queryParas.put("code", "F");
	    }
	    if(model.getFieldKey().equals(Constants.MERCHANTLOANINFO)){
	    	queryParas.put("code", "N");
	    }
	    if(model.getFieldKey().equals(Constants.CARDLOANINFO)){
	    	queryParas.put("code", "S43");
	    }
	    if(model.getFieldKey().equals(Constants.RESIDENCEINFO)){
	    	queryParas.put("code", "S44");
	    }
	    if(model.getFieldKey().equals(Constants.OTHERINFO)){
	    	queryParas.put("code", "M");
	    }
	    if(model.getFieldKey().equals(Constants.CREDITACCOUNTINFO)){
	    	queryParas.put("code", "L");
	    }
	    String targetFileName ="./" + UUID.randomUUID()+".jpg";
		//生成图片
		base64Utils.GenerateImage(image,targetFileName);
		
	    File  file  =  new  File(targetFileName);//参数file
	    if(!file.exists()){
			throw new Exception(Constants.RESP_CODE_ERROR );
		}
	    String  result  =  HttpKit.post(urlUPIC+Constants.PIC_URL_UPLOAD, queryParas, file);
	    String httpResult = URLDecoder.decode(result, "UTF-8");
	   //删除生成的图片
		if(file != null && file.exists()){
			FileDownUtils.deleteDir(file);
		}
		com.alibaba.fastjson.JSONObject objs = com.alibaba.fastjson.JSONObject.parseObject(httpResult);
		
		if(!"000000".equals(objs.get("errorcode"))) {
			log.info(Constants.RESP_CODE_ERROR+"PIC图片系统上传调用失败");
			return false;
		}
		//保存在本地方便查询
		com.alibaba.fastjson.JSONObject j = (com.alibaba.fastjson.JSONObject) objs.get("result");
		//Long picId = Long.valueOf(Integer.parseInt(j.get("id").toString()));
		String picId = j.get("id").toString();
		String url = (String) j.get("url");//文件地址
		//String thumUrl = (String) j.get("thumUrl");//缩略图地址
		ApplyPicInfo pic = new ApplyPicInfo();
		pic.setFieldKey(imgKey);
		pic.setPicId(picId);
		pic.setStatus("1");
		pic.setPicUrl(url);
		pic.setImgName(name);
		pic.setLoanNo(loanNo);
		pic.setCreateTime(new Date());
		pic.setUpdateTime(new Date());
		int i = applypicinfomapper.insert(pic);
		if(i > 0)
			return true;
		return false;
	}
	
	/**
	 * 规则引擎校验
	 * @param model
	 * @return
	 */
	public Result ruleCheck(Model_005006 model){
		log.info("规则引擎校验入参为："+model.getLoanNo());
		ZDQQApplyRuleExecVo vo = ruleCheck(model.getLoanNo(),Constants.RULE_NODE_TJ);
		com.ymkj.rule.biz.api.message.Response ruleResponse = ruleAppSevice.validatezdqq(vo);
		Map<String, Object> map = ruleAppSevice.getValidateResultSubmit(((MapResponse) ruleResponse).getMap());
		JSONObject object = new JSONObject();
		if(map.get("ifNext").equals("Y")){
			object.put("isLaon", Constants.RESULT_YES);
			object.put("message", map.get("hint"));
		}else{
			object.put("isLaon", Constants.REUSLT_NO);
			object.put("message", map.get("hint"));
		}
		return Result.success(object);
	}

	/**
	 * 借款推送至录单
	 * @param model
	 * @return
	 * */
	@Transactional
	public Result pushToBms(Model_005014 model){
 		log.info("借款推送至录单入参为："+model.getLoanNo());
		String loanNo = model.getLoanNo();
		
		//判断征信报告是否填写
		ApplyBaseInfo cred = new ApplyBaseInfo();
		cred.setLoanNo(loanNo);
		cred.setFieldKey(Constants.CREDITACCOUNTINFO);
		cred = applybaseinfomapper.selectByappNo(cred);
		if(cred == null){
			return Result.fail("信用账户信息必填");
		}
		if(StringUtils.isEmpty(cred.getFieldObjValue())){
			return Result.fail("信用账户信息必填");
		}
		JSONObject jb = JSONObject.fromObject(cred.getFieldObjValue());
		if(!jb.containsKey("reportId")){
			return Result.fail("信用账户信息必填");
		}
		if(StringUtils.isEmpty(jb.getString("reportId"))){
			return Result.fail("信用账户信息必填");
		}
		//判断芝麻信用是否填写
		ApplyBaseInfo cred1 = new ApplyBaseInfo();
		cred1.setLoanNo(loanNo);
		cred1.setFieldKey(Constants.ZMSCORE);
		cred1 = applybaseinfomapper.selectByappNo(cred1);
		if(cred1 == null){
			return Result.fail("芝麻分信息必填");
		}
		if(StringUtils.isEmpty(cred1.getFieldObjValue())){
			return Result.fail("芝麻分信息必填");
		}
		JSONObject jbs = JSONObject.fromObject(cred1.getFieldObjValue());
		if(!jbs.containsKey("sesameCreditValue")){
			return Result.fail("芝麻分信息必填");
		}
		if(StringUtils.isEmpty(jbs.getString("sesameCreditValue"))){
			return Result.fail("芝麻分信息必填");
		}
		//3-8中至少选填一项
		ApplyGroupInfo agi = new ApplyGroupInfo();
		agi.setLoanNo(loanNo);
		agi.setState("1");
		agi.setGroupCodes(new String[]{Constants.EDUCATIONINFO,Constants.POLICYINFO,
										Constants.CARDLOANINFO,Constants.ASSETSINFO,Constants.CARINFO,
										Constants.MERCHANTLOANINFO});
		List<ApplyGroupInfo> appgilist = applyGroupInfoMapper.queryApplyGroupInfoAll(agi);
		if(appgilist.isEmpty()){
			return Result.fail("3-8中至少选填一项");
		}
		
		//根据借款编号查询所有信息、
		com.alibaba.fastjson.JSONObject objAll = new com.alibaba.fastjson.JSONObject();
		
		ApplyLoanInfo apply = new ApplyLoanInfo();
		apply.setLoanNo(loanNo);
		ApplyLoanInfo selectApply = applyloaninfomapper.selectByLoanNo(apply);
		selectApply.getFieldKey();
		selectApply.getFieldValue();
		JSONObject obj = new JSONObject();
		obj.put("loanNo", loanNo);//借款编号
		
		ApplyBaseInfo baseInfo = new ApplyBaseInfo();
		baseInfo.setLoanNo(loanNo);
		List<ApplyBaseInfo> baseInfos = applybaseinfomapper.selectByFiledKeyAndTab(baseInfo);
		List<String> list = new ArrayList<>();
		//征信报告id
		String reportId = "";
		
		if(baseInfos!=null && !baseInfos.isEmpty()){
			JSONArray contact = new JSONArray();
			JSONArray policy = new JSONArray();
			for(ApplyBaseInfo base:baseInfos){
				//获取有几个节点填写数据
				list.add(base.getFieldKey());
				//添加是否填写
				JSONObject jj = JSONObject.fromObject(base.getFieldObjValue());	
				if(jj != null && jj.size() > 0 )
					jj.put("unabridged", "Y");
				else{
					jj = new JSONObject();
					if(!base.getFieldKey().equals(Constants.POLICYINFO)){
						jj.put("unabridged", "N");
					}
				}
				if(base.getFieldKey().equals(Constants.CREDITACCOUNTINFO)){
					JSONObject js = JSONObject.fromObject(base.getFieldObjValue());
					if(js.containsKey("reportId")){
						reportId = js.getString("reportId");
					}
				}
				if(base.getFieldKey().equals(Constants.CONTACTPERSONINFO)){//联系人信息
					contact.add(jj);
				}else if(base.getFieldKey().equals(Constants.POLICYINFO)){//保单信息
					policy.add(jj);
				}else if(base.getFieldKey().equals(Constants.SOCIALSECURITYINFO)){//公积金/社保信息
					ProvidentInfoDto pro = (ProvidentInfoDto)JSONObject.toBean(jj, ProvidentInfoDto.class);
					InsuranceInfoDto ins = (InsuranceInfoDto)JSONObject.toBean(jj,InsuranceInfoDto.class);
					JSONObject jbtpro = new JSONObject();
					if(pro != null && (StringUtils.isNotBlank(pro.getAccumulationFundAccount())||StringUtils.isNotBlank(pro.getAccumulationFundPassword()))){
						jbtpro.put("accumulationFundAccount", pro.getAccumulationFundAccount());
						jbtpro.put("accumulationFundPassword", pro.getAccumulationFundPassword());
						jbtpro.put("unabridged", "Y");
						obj.put(Constants.PROVIDENTINFO, jbtpro);//其他所有信息
					}else{
						jbtpro.put("unabridged", "N");
						obj.put(Constants.PROVIDENTINFO, jbtpro);//其他所有信息
					}
					JSONObject jbtins = new JSONObject();
					if(ins != null && (StringUtils.isNotBlank(ins.getSocialInsuranceAccount())||StringUtils.isNotBlank(ins.getSocialInsurancePassword()))){
						jbtins.put("socialInsuranceAccount", ins.getSocialInsuranceAccount());
						jbtins.put("socialInsurancePassword", ins.getSocialInsurancePassword());
						jbtins.put("unabridged", "Y");
						obj.put(Constants.INSURANCEINFO, jbtins);//其他所有信息
					}else{
						jbtins.put("unabridged", "N");
						obj.put(Constants.INSURANCEINFO, jbtins);//其他所有信息
					}
				}else{
					obj.put(base.getFieldKey(), jj);//其他所有信息
				}
			}
			obj.put(Constants.POLICYINFO, policy);//保单信息
			obj.put(Constants.CONTACTPERSONINFO, contact);//联系人信息
			if(obj.containsKey(Constants.ZMSCORE)){
				if(obj.containsKey(Constants.MERCHANTLOANINFO)){
					JSONObject ob = JSONObject.fromObject(obj.get(Constants.MERCHANTLOANINFO));
					//ob.put("SESAME_CREDIT_VALUE", value);//芝麻分
					//ob.put("SESAME_CREDIT_ACQUIRE_WAY", value);//手输入|第三方
					obj.put(Constants.MERCHANTLOANINFO, ob);
				}else{
					JSONObject ob = new JSONObject();
					//ob.put("SESAME_CREDIT_VALUE", value);//芝麻分
					//ob.put("SESAME_CREDIT_ACQUIRE_WAY", value);//手输入|第三方
					obj.put(Constants.MERCHANTLOANINFO, ob);
				}
			}
		}
		
		//查询节点数据
		ApplyGroup ag = new ApplyGroup();
		ag.setGroupParentCode(Constants.NODEINFO);
		ag.setState(Constants.DATA_VALID);
		List<String> codelist = applyGroupMapper.queryByApplyCode(ag);
		codelist.removeAll(list);
		if(codelist != null && !codelist.isEmpty()){
			for (String string : codelist) {
				if(!string.equals(Constants.CARDLOANINFO) && !string.equals(Constants.RESIDENCEINFO)){
					JSONObject jo = new JSONObject();
					jo.put("unabridged", "N");
					obj.put(string, jo);
				}
				if(string.equals(Constants.POLICYINFO)){
					JSONArray jn = new JSONArray();
					obj.put(string, jn);
				}
				
			}
		}
		
		JSONObject jo1 = JSONObject.fromObject(selectApply.getFieldValue());
		jo1.put("loanNo", loanNo);
		jo1.put("reportId", reportId);
		obj.put(selectApply.getFieldKey(), jo1);//申请信息
		
		//查询信用卡图片是否上传
		ApplyPicInfo ap = new ApplyPicInfo();
		ap.setLoanNo(loanNo);
		ap.setFieldKey(Constants.CARDLOANINFO);
		ap.setStatus(Constants.DATA_VALID);
		List<ApplyPicInfo> aplist = applypicinfomapper.select(ap);
		JSONObject k = new JSONObject();
		if(aplist != null && !aplist.isEmpty())
			k.put("unabridged", "Y");
		else
			k.put("unabridged", "N");
		obj.put(Constants.CARDLOANINFO, k);
		
		ApplyPicInfo ap1 = new ApplyPicInfo();
		ap1.setLoanNo(loanNo);
		ap1.setFieldKey(Constants.RESIDENCEINFO);
		ap1.setStatus(Constants.DATA_VALID);
		List<ApplyPicInfo> ap1list = applypicinfomapper.select(ap1);
		JSONObject k1 = new JSONObject();
		if(ap1list != null && !ap1list.isEmpty())
			k1.put("unabridged", "Y");
		else
			k1.put("unabridged", "N");
		obj.put(Constants.RESIDENCEINFO, k1);
		
		if(StringUtils.isEmpty(reportId)){
			ApplyPicInfo ap2 = new ApplyPicInfo();
			ap2.setLoanNo(loanNo);
			ap2.setFieldKey(Constants.CREDITACCOUNTINFO);
			ap2.setStatus(Constants.DATA_VALID);
			List<ApplyPicInfo> ap2list = applypicinfomapper.select(ap2);
			JSONObject k2 = new JSONObject();
			if(ap2list != null && !ap2list.isEmpty())
				k2.put("unabridged", "Y");
			else
				k2.put("unabridged", "N");
			obj.put(Constants.RESIDENCEINFO, k2);
		}
		
		
		log.info("提交推送给录单入参为："+obj);
		//统一封装
		objAll.put("baseInfo", obj);
		//开始推送录单
		Response<com.alibaba.fastjson.JSONObject> response = iloanInfoInputExecuter.saveBaseInformation(objAll);
		
		log.info("提交推送录单结果返回："+response.isSuccess()+"|"+response.getRepMsg());
		if(!response.isSuccess()){
			return Result.fail(response.getRepMsg());
		}
		
		/**
		 * 提交要签章的合同
		 */
		if(Constants.CONTRACT_IS_NOT_SIGN_02.equals(model.getIsNotSign())){
			Model_006004 m = new Model_006004();
			m.setIdCard(selectApply.getIdCard());
			m.setLoanNo(model.getLoanNo());
			Customer cust = customerService.queryByIdCard(selectApply.getIdCard());
			m.setUserName(cust.getCustomerName());
			m.setPhone(cust.getPhone());
			Result re = signLcbService.signContract(m);
			if(!re.getSuccess()){
				return re;
			}
		}
		
		String data = String.valueOf(response.getData());
		
		//推送成功会写数据库
		if(data != null){
			selectApply.setApplyStatus(Constants.IS_SUBMIT);
			selectApply.setUpdateTime(new Date());
			applyloaninfomapper.updateToTable(selectApply);
		}
		
		/*提交成功推送手机消息,type必传*/
		JSONObject objects = new JSONObject();
		JSONObject param = new JSONObject();
		param.put("loanNo", "");
		param.put("cusName", "");
		param.put("banPhone", "");
		param.put("repaidNum", "");
		param.put("bankCard", "");
		param.put("repaidTotal", "");
		param.put("idNum", selectApply.getIdCard());
		param.put("gender", "");
		param.put("realPaid", "");
		objects.put("type", Constants.SUBMIT);
		objects.put("param", param);
		com.alibaba.fastjson.JSONObject objMes;
		try {
			objMes = HttpClientUtil.sendHttpPostPushMes(objects.toString());
			log.info("提交借款消息推送结果："+objMes.getString("message"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Result.success(data);
		
	}
	
	/**
	 * 前前借款进度查询
	 * @param modle
	 * @return
	 * */
	public Result selectByProcess(Model_004009 model){
		Customer customer = new Customer();
		customer.setId(Long.parseLong(model.getCustomerId()));
		customer = customerMapper.selectOneTable(customer);
		if (null == customer) {
			return Result.fail("客户不存在");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("idCard", customer.getIdCard());
		//PageHelper.startPage(model.getPageNo(), model.getPageSize());
		//List<ApplyLoanInfo> page = applyloaninfomapper.selectLoanInfo(map);
		List page = new ArrayList();
		//Set<String> localloanNo = new HashSet<String>();
		String idNo = customer.getIdCard();
		//JSONObject object = new JSONObject();
		com.alibaba.fastjson.JSONObject objAll = new com.alibaba.fastjson.JSONObject();
		//objAll.put("loanNo", loanNo);
		objAll.put("idNo", idNo);
		log.info("借款进度录单入参值："+ JSONObject.fromObject(objAll));
		Result re = queryLoanAndIdCard(objAll);
		if(!re.getSuccess()){
			return re;
		}
		JSONObject obj = JSONObject.fromObject(re.getData());
		log.info("获取借款进度出参："+obj);
		if(obj.containsKey("processList")){
			JSONArray js = JSONArray.fromObject(obj.get("processList"));
			if(js!=null){
				for(int i=0;i<js.size();i++){
					JSONObject ob = JSONObject.fromObject(js.get(i));
					Calendar now = Calendar.getInstance();
					now.add(Calendar.DATE, 1);
					String contractType = (String)ob.get("contractType");
					String loanN = (String)ob.get("loanNo");
					String applyTime = (String)ob.get("applyTime");
					String productName = (String)ob.get("productName");
					String PRODUCT_CD = (String)ob.get("applyCode");
					//String productAmount = (String)ob.get("productAmount");
					String applyLimit = (String)ob.get("applyLimit");
					String applyTerm = (String)ob.get("applyTerm");
					String auditLimit = (String)ob.get("auditLimit");
					//String auditTerm = (String)ob.get("auditTerm");
					//String signTerm = (String)ob.get("signTerm");
					String signLimit = (String)ob.get("signLimit");
					String state = (String)ob.get("state");
					String rtfNodeState = (String)ob.get("rtfNodeState");
					String rtfState = (String)ob.get("rtfState");
					String authType = String.valueOf(ob.get("authType"));
					/**判断是否是放款完成的案件*/
					
					if(rtfNodeState.equals("FKQR-SUBMIT")){//放款完成
						continue;
					}
					//更新首页提示
			        basicRedisOpts.persist("type"+loanN, rtfNodeState);
					String workflow = "";
					if(ob.containsKey("workFlow") && StringUtils.isNotEmpty(ob.getString("workFlow"))){
						workflow = ob.getString("workFlow");
					}
					if(contractType!=null){
						basicRedisOpts.persist(loanN, contractType+"",now.getTime());
						log.info("redis缓存数据=="+loanN+",值=="+basicRedisOpts.getSingleResult(loanN));
					}
					ApplyLoanInfo loanOrder = new ApplyLoanInfo();
					if(StringUtils.isEmpty(productName)||productName.equals("证大前前")){
						loanOrder.setIsProject("0");
					}else{
						loanOrder.setIsProject("1");
					}
					loanOrder.setOrdernum(loanN);
					loanOrder.setApplyDate(applyTime);
					loanOrder.setProjectCode(PRODUCT_CD);
					loanOrder.setProjectName(productName);
					loanOrder.setDeadline(applyTerm);
					//办理未保存取本地额度
					//if(applyLimit==null){
						/*ApplyLoanInfo apply = new ApplyLoanInfo();
						apply.setLoanNo(loanN);
						ApplyLoanInfo selectApply = applyloaninfomapper.selectByLoanNo(apply);
						if(selectApply!=null){
							loanOrder.setApplylimit(selectApply.getApplylimit());
							loanOrder.setDeadline(lo.getDeadline());
							loanOrder.setProjectName(productName);
							loanOrder.setFlowstatus("办理中");
							loanOrder.setFlowstatusValue("1");
							loanOrder.setIsShowStatus(Constants.IS_SHOW_STATUS);
						}else{
							if (signLimit != null) {loanOrder.setApplylimit(signLimit);
							} else if (signLimit == null&& auditLimit == null) {
								loanOrder.setApplylimit(applyLimit);
							} else if (signLimit == null&& auditLimit != null) {
								loanOrder.setApplylimit(auditLimit);
							}
						}*/
					//}else{
						if (StringUtils.isNotEmpty(signLimit)) {
							loanOrder.setApplylimit(signLimit);						
						} else if (StringUtils.isEmpty(signLimit)&& StringUtils.isEmpty(auditLimit)) {
							loanOrder.setApplylimit(applyLimit);
						} else if ((StringUtils.isEmpty(signLimit)&&StringUtils.isNotEmpty(auditLimit))) {
							loanOrder.setApplylimit(auditLimit);
						}
					//}
					if(Constants.AuthType_PC.equals(authType)){//认证渠道为pc 
						loanOrder.setBtnStatus(Constants.NOT_BUTTON);// 钱钱借款进度不显示按钮
					}
					
					if(ob.containsKey("rtfNodeState")){
						if(rtfNodeState.equals("ZDQQAPPSQLR-CREATE")||rtfNodeState.equals("ZDQQAPPSQLR-SUBMIT")||rtfNodeState.equals("ZDQQAPPSQLR-RESUBMIT")){//受理中
							//object.put("rtfNodeState", "受理中");
							//object.put("State", "1");
							loanOrder.setBtnStatus(Constants.NOT_BUTTON);
							loanOrder.setFlowstatusValue(Constants.ORDER_STATES_SHOULIZHONG);
							loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
							loanOrder.setIsShowStatus(Constants.IS_SHOW_STATUS);
							loanOrder.setFlowstatus("受理中");
						}else if(rtfNodeState.equals("CSFP-SUBMIT")||rtfNodeState.equals("XSCS-ASSIGN")||rtfNodeState.equals("XSCS-HANGUP")||rtfNodeState.equals("XSCS-PASS")||rtfNodeState.equals("HIGH-PASS")||rtfNodeState.equals("XSCS-SUBMIT")||rtfNodeState.equals("XSZS-ASSIGN")
								||rtfNodeState.equals("XSZS-HANGUP")||rtfNodeState.equals("XSZS-RTNCS")||rtfNodeState.equals("XSZS-SUBMIT-HIGH")||rtfNodeState.equals("XSZS-SUBMIT-BACK")||rtfNodeState.equals("XSZS-SUBMIT-APPROVAL")
								){//办理中
							//object.put("rtfNodeState", "办理中");
							loanOrder.setBtnStatus(Constants.NOT_BUTTON);
							loanOrder.setFlowstatusValue(Constants.ORDER_STATES_BANLIZHONG);
							loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
							loanOrder.setIsShowStatus(Constants.IS_SHOW_STATUS);
							loanOrder.setFlowstatus("办理中");
						}else if(rtfNodeState.equals("XSZS-PASS")||rtfNodeState.equals("HTQR-RETURN")){//待签约
							if(!StringUtils.isEmpty(workflow)&&workflow.equals("01602")){
								loanOrder.setBtnStatus(Constants.SET_BANK_CARD);
				                loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
				                loanOrder.setIsShowStatus(Constants.NOT_SHOW_STATUS);
				                loanOrder.setFlowstatus("待签约");
				                loanOrder.setFlowstatusValue(Constants.ORDER_STATES_XIANXIAQIANYUE);
				                log.info("跳转到H5绑卡地址:" + urlH5url);
				                loanOrder.setJumpUrl(urlH5url);
							}else{
								loanOrder.setBtnStatus(Constants.NOT_BUTTON);
								loanOrder.setFlowstatusValue(Constants.ORDER_STATES_XIANXIAQIANYUE);
								loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
								loanOrder.setIsShowStatus(Constants.IS_SHOW_STATUS);
								loanOrder.setFlowstatus("待签约");
							}
							//object.put("rtfNodeState", "待签约");
							//object.put("State", "3");
						}else if(rtfState.equals("HTQY") && ("01608".equals(workflow) || "01609".equals(workflow) || "01610".equals(workflow) || "01611".equals(workflow))){
							if((rtfNodeState.equals("QYCS-CANCEL")||rtfNodeState.equals("HTQY-CANCEL"))&&(!ob.containsKey("checkNodeState")||ob.getString("checkNodeState").equals("NO_CHECK")||ob.getString("checkNodeState").equals("CHECK_PASS"))){//签约取消的
		                        loanOrder.setBtnStatus(Constants.NOT_BUTTON);
		                        loanOrder.setFlowstatusValue(Constants.ORDER_STATES_SHENQINGQQ);
		                        loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
		                        loanOrder.setFlowstatus("已撤销");
		                        loanOrder.setIsShowStatus(Constants.IS_SHOW_STATUS);
		                        loanOrder.setIsRed("1");
		                      }else if(rtfNodeState.equals("HTQY-REJECT")&&(!ob.containsKey("checkNodeState")||ob.getString("checkNodeState").equals("NO_CHECK")||ob.getString("checkNodeState").equals("CHECK_PASS"))){//签约拒绝
		                        loanOrder.setBtnStatus(Constants.NOT_BUTTON);
		                        loanOrder.setFlowstatusValue(Constants.ORDER_STATES_SHENGQINGJJ);
		                        loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
		                        loanOrder.setFlowstatus("已拒绝");
		                        loanOrder.setIsShowStatus(Constants.IS_SHOW_STATUS);
		                        loanOrder.setIsRed("1");
		                        }else if(rtfNodeState.equals("FKSH-RETURN")||rtfNodeState.equals("FKQR-RETURN")){
		                          loanOrder.setBtnStatus(Constants.NOT_BUTTON);
		                          loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
		                          loanOrder.setFlowstatus("重新签约");
		                          loanOrder.setIsShowStatus(Constants.IS_SHOW_STATUS);
		                          loanOrder.setFlowstatusValue(Constants.ORDER_STATES_XIANXIAQIANYUE);
		                        }else{
		                        	loanOrder.setBtnStatus(Constants.NOT_BUTTON);
			                        loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
			                        loanOrder.setFlowstatus("待签约");
			                        loanOrder.setIsShowStatus(Constants.IS_SHOW_STATUS);
			                        loanOrder.setFlowstatusValue(Constants.ORDER_STATES_XIANXIAQIANYUE);
		                        }
						}else if((!"HTQY-REJECT".equals(rtfNodeState) || !"QYCS-CANCEL".equals(rtfNodeState) ||!"HTQY-CANCEL".equals(rtfNodeState) ) && rtfState.equals("HTQY") && "01602".equals(workflow)){
							//签约分派办理 显示设置银行卡按钮
				              if(rtfNodeState.equals("QYCS-CANCEL")||rtfNodeState.equals("HTQY-CANCEL")){//签约取消的
				                loanOrder.setBtnStatus(Constants.NOT_BUTTON);
				                loanOrder.setFlowstatusValue(Constants.ORDER_STATES_SHENQINGQQ);
				                loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
				                loanOrder.setFlowstatus("已撤销");
				                loanOrder.setIsShowStatus(Constants.IS_SHOW_STATUS);
				                loanOrder.setIsRed("1");
				              }else if(rtfNodeState.equals("HTQY-REJECT")){//签约拒绝
				                loanOrder.setBtnStatus(Constants.NOT_BUTTON);
				                loanOrder.setFlowstatusValue(Constants.ORDER_STATES_SHENGQINGJJ);
				                loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
				                loanOrder.setFlowstatus("已拒绝");
				                loanOrder.setIsShowStatus(Constants.IS_SHOW_STATUS);
				                loanOrder.setIsRed("1");
				              }else{
				                loanOrder.setBtnStatus(Constants.SET_BANK_CARD);
				                loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
				                loanOrder.setIsShowStatus(Constants.NOT_SHOW_STATUS);
				                loanOrder.setFlowstatus("待签约");
				                loanOrder.setFlowstatusValue(Constants.ORDER_STATES_XIANXIAQIANYUE);
				                log.info("跳转到H5绑卡地址:" + urlH5url);
				                loanOrder.setJumpUrl(urlH5url);
				              }
						}else if(rtfState.equals("HTQY") && "01612".equals(workflow)&&(!"HTQY-CANCEL".equals(rtfNodeState))&&(!"QYCS-CANCEL".equals(rtfNodeState))){//合同签约办理签订 显示合同签订按钮
							//HTQY-REJECT可能也会返回workflow01612要做判断
							if("HTQY-REJECT".equals(rtfNodeState)){
								loanOrder.setBtnStatus(Constants.NOT_BUTTON);
		                        loanOrder.setFlowstatusValue(Constants.ORDER_STATES_SHENGQINGJJ);
		                        loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
		                        loanOrder.setFlowstatus("已拒绝");
		                        loanOrder.setIsShowStatus(Constants.IS_SHOW_STATUS);
		                        loanOrder.setIsRed("1");
		                        
							}else{
								
								//查询核心是否签约
								boolean hx = signService.queryHxContract(loanN);
								//查询捞财宝签约结果
								boolean flag = dmsService.getTargetStatusByDms(loanN);
								if(hx && flag){//核心与捞财宝都签订
									loanOrder.setBtnStatus(Constants.NOT_BUTTON);
									loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
									loanOrder.setFlowstatus("待签约");
									loanOrder.setIsShowStatus(Constants.IS_SHOW_STATUS);
									loanOrder.setFlowstatusValue(Constants.ORDER_STATES_XIANXIAQIANYUE);
								}else if(hx && !flag){//核心签捞财宝未签
									loanOrder.setBtnStatus(Constants.ELEC_SIGN);
									loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
									loanOrder.setIsShowStatus(Constants.NOT_SHOW_STATUS);
									loanOrder.setFlowstatus("");
									loanOrder.setFlowstatusValue(Constants.ORDER_STATES_XIANXIAQIANYUE);
									loanOrder.setJumpUrl("");
								}else if(!hx){
									loanOrder.setBtnStatus(Constants.ELEC_SIGN);
									loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
									loanOrder.setIsShowStatus(Constants.NOT_SHOW_STATUS);
									loanOrder.setFlowstatus("");
									loanOrder.setFlowstatusValue(Constants.ORDER_STATES_XIANXIAQIANYUE);
									String jxhjH5url = jxhjH5Url+"/custom-lcb.html?loanNo="+ loanOrder.getOrdernum()+"&source=app&from=normal&idCard="+customer.getIdCard();
									log.info("跳转到H5签约地址："+jxhjH5url);
									loanOrder.setJumpUrl(jxhjH5url);
								}
							}
						}else if(rtfState.equals("HTQY") &&"01613".equals(workflow)){//签捞财宝
							boolean flag = dmsService.getTargetStatusByDms(loanN);
							if(flag){
								loanOrder.setBtnStatus(Constants.NOT_BUTTON);
								loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
								loanOrder.setFlowstatus("待签约");
								loanOrder.setIsShowStatus(Constants.IS_SHOW_STATUS);
								loanOrder.setFlowstatusValue(Constants.ORDER_STATES_XIANXIAQIANYUE);
							}else{
								loanOrder.setBtnStatus(Constants.ELEC_SIGN);
								loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
								loanOrder.setIsShowStatus(Constants.NOT_SHOW_STATUS);
								loanOrder.setFlowstatus("");
								loanOrder.setFlowstatusValue(Constants.ORDER_STATES_XIANXIAQIANYUE);
								loanOrder.setJumpUrl("");
							}
						}else if(rtfState.equals("HTQY") && ("01614".equals(workflow) || "01615".equals(workflow))){//待签约
							loanOrder.setBtnStatus(Constants.ELEC_SIGN);
							loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
							loanOrder.setIsShowStatus(Constants.NOT_SHOW_STATUS);
							loanOrder.setFlowstatus("待签约");
							loanOrder.setFlowstatusValue(Constants.ORDER_STATES_XIANXIAQIANYUE);
						}else if(rtfNodeState.equals("HTQY-SUBMIT") && rtfState.equals("HTQR")){//待签约
							loanOrder.setBtnStatus(Constants.NOT_BUTTON);
							loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
							loanOrder.setFlowstatus("待签约");
							loanOrder.setIsShowStatus(Constants.IS_SHOW_STATUS);
							loanOrder.setFlowstatusValue(Constants.ORDER_STATES_XIANXIAQIANYUE);
						}else if(rtfNodeState.equals("HTQY-ASSIGN")&& StringUtils.isEmpty(workflow)){
							//显示待签约
							loanOrder.setBtnStatus(Constants.NOT_BUTTON);
							loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
							loanOrder.setFlowstatus("待签约");
							loanOrder.setIsShowStatus(Constants.IS_SHOW_STATUS);
							loanOrder.setFlowstatusValue(Constants.ORDER_STATES_XIANXIAQIANYUE);
						}else if((rtfNodeState.equals("HTQY-CANCEL")&&StringUtils.isEmpty(workflow))||((rtfNodeState.equals("QYCS-CANCEL")||rtfNodeState.equals("HTQY-CANCEL"))&&workflow.equals("01612"))){
							//显示撤销
							loanOrder.setBtnStatus(Constants.NOT_BUTTON);
							loanOrder.setFlowstatusValue(Constants.ORDER_STATES_SHENQINGQQ);
							loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
							loanOrder.setFlowstatus("已撤销");
							loanOrder.setIsShowStatus(Constants.IS_SHOW_STATUS);
							loanOrder.setIsRed("1");
						}else if(rtfNodeState.equals("HTQR-SUBMIT")||rtfNodeState.equals("FKSH-SUBMIT")){//放款中
							//object.put("rtfNodeState", "放款中");
							//object.put("State", "4");
							loanOrder.setBtnStatus(Constants.NOT_BUTTON);
							loanOrder.setFlowstatusValue(Constants.ORDER_STATES_FANGKUANZHONG);
							loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
							loanOrder.setIsShowStatus(Constants.IS_SHOW_STATUS);
							loanOrder.setFlowstatus("募集中");
						}else if(rtfNodeState.equals("LRCS-CANCEL")||rtfNodeState.equals("QYCS-CANCEL")||rtfNodeState.equals("HTQY-CANCEL")){//已撤销取消
							//object.put("rtfNodeState", "已撤销");
							//object.put("State", "5");
							loanOrder.setBtnStatus(Constants.NOT_BUTTON);
							loanOrder.setFlowstatusValue(Constants.ORDER_STATES_SHENQINGQQ);
							loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
							loanOrder.setFlowstatus("已撤销");
							loanOrder.setIsShowStatus(Constants.IS_SHOW_STATUS);
							loanOrder.setIsRed("1");
						}else if(rtfNodeState.equals("LRCS-REJECT")||rtfNodeState.equals("CSFP-REJECT")||rtfNodeState.equals("XSCS-REJECT")||rtfNodeState.equals("ZSFP-REJECT")||rtfNodeState.equals("XSZS-REJECT")
								||rtfNodeState.equals("SQJWH-REJECT")||rtfNodeState.equals("HTQY-REJECT")
								){//已拒绝
							//object.put("rtfNodeState", "已拒绝");
							//object.put("State", "6");
							if(rtfNodeState.equals("XSCS-REJECT")&&ob.containsKey("checkNodeState")&&(ob.getString("checkNodeState").equals("NO_CHECK")||ob.getString("checkNodeState").equals("CHECK_PASS"))){
								loanOrder.setBtnStatus(Constants.NOT_BUTTON);
								loanOrder.setFlowstatusValue(Constants.ORDER_STATES_SHENGQINGJJ);
								loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
								loanOrder.setFlowstatus("已拒绝");
								loanOrder.setIsShowStatus(Constants.IS_SHOW_STATUS);
								loanOrder.setIsRed("1");
							}else if(rtfNodeState.equals("XSCS-REJECT")&&ob.containsKey("checkNodeState")&&(!ob.getString("checkNodeState").equals("NO_CHECK")||!ob.getString("checkNodeState").equals("CHECK_PASS"))){
								loanOrder.setBtnStatus(Constants.NOT_BUTTON);
								loanOrder.setFlowstatusValue(Constants.ORDER_STATES_BANLIZHONG);
								loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
								loanOrder.setIsShowStatus(Constants.IS_SHOW_STATUS);
								loanOrder.setFlowstatus("办理中");
							}else{
								loanOrder.setBtnStatus(Constants.NOT_BUTTON);
								loanOrder.setFlowstatusValue(Constants.ORDER_STATES_SHENGQINGJJ);
								loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
								loanOrder.setFlowstatus("已拒绝");
								loanOrder.setIsShowStatus(Constants.IS_SHOW_STATUS);
								loanOrder.setIsRed("1");
							}
						}else if(rtfNodeState.equals("FKQR-SUBMIT")){//放款完成
							
						}else if(rtfNodeState.equals("FKSH-RETURN")||rtfNodeState.equals("FKQR-RETURN")){//重新签约
							loanOrder.setBtnStatus(Constants.NOT_BUTTON);
							loanOrder.setFlowstatusValue(Constants.ORDER_STATES_XIANXIAQIANYUE);
							loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
							loanOrder.setIsShowStatus(Constants.IS_SHOW_STATUS);
							loanOrder.setFlowstatus("重新签约");
						}else if(rtfNodeState.equals("CSFP-ZDQQRETURN")||rtfNodeState.equals("XSCS-ZDQQRETURN")||rtfNodeState.equals("XSZS-ZDQQRETURN")){
							//案件回退
							//object.put("rtfNodeState", "案件退回");
							//object.put("State", "1");
							if(rtfNodeState.equals("XSCS-ZDQQRETURN")){
								
								if(ob.containsKey("checkNodeState")){
									String checkNodeState = (String)ob.get("checkNodeState");
									if("NO_CHECK".equals(checkNodeState)||"CHECK_PASS".equals(checkNodeState)){
										
										loanOrder.setBtnStatus(Constants.RETURN);
										loanOrder.setFlowstatusValue(Constants.ORDER_STATES_BANLIZHONG);
										loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
										loanOrder.setIsShowStatus(Constants.IS_SHOW_STATUS);
										loanOrder.setFlowstatus("案件回退");
										//调录单查询接口获取信息，更新本地数据
										JSONObject jayb = ldBase(customer.getIdCard(),loanN);
										JSONObject jay = JSONObject.fromObject(jayb.get("baseInfo"));
										log.info("进件退回获取录单数据："+jay);
										//1.更新主表状态为2
										ApplyLoanInfo app = new ApplyLoanInfo();
										app.setLoanNo(loanN);
										//判断是否为修改未提交
										ApplyLoanInfo Ainfo = applyloaninfomapper.selectByLoanNo(app);
										if(!Ainfo.getApplyStatus().equals("2")){
											boolean status = true;
											JSONObject newjson=new JSONObject();
											app.setApplyStatus(Constants.IS_REBACK_SUBMIT);
											app.setUpdateTime(new Date());
											applyloaninfomapper.updateByLoanNo(app);
											//2.更新信息表
											ApplyBaseInfo apl = new ApplyBaseInfo();
											apl.setLoanNo(loanN);
											if(jay.containsKey(Constants.PERSIONINFO)){//个人信息
												apl.setFieldKey(Constants.PERSIONINFO);
												apl.setFieldObjValue(jay.getString(Constants.PERSIONINFO));
												applybaseinfomapper.updateByloanNoAndFiledKey(apl);
											}
											if(jay.containsKey(Constants.EMPITEMINFO)){//工作信息
												apl.setFieldKey(Constants.EMPITEMINFO);
												apl.setFieldObjValue(jay.getString(Constants.EMPITEMINFO));
												applybaseinfomapper.updateByloanNoAndFiledKey(apl);
											}
											if(jay.containsKey(Constants.CONTACTPERSONINFO)){//联系人信息
												//先删除网关库的联系人
												apl.setFieldKey(Constants.CONTACTPERSONINFO);
												apl.setLoanNo(loanN);
												applybaseinfomapper.deleteByFiledKey(apl);
												//在新增联系人
												JSONArray array = JSONArray.fromObject(jay.getString(Constants.CONTACTPERSONINFO));
												for(int k=0;k<array.size();k++){
													JSONObject contactobj = JSONObject.fromObject(array.get(k).toString());
													String tab = String.valueOf(contactobj.get("sequenceNum"));
													apl.setFieldObjValue(array.get(k).toString());
													apl.setTab(Integer.valueOf(tab));
													apl.setLoanNo(loanN);
													apl.setFieldKey(Constants.CONTACTPERSONINFO);
													apl.setFieldKeyParent(Constants.FRIENDSINFO);
													apl.setState("1");
													applybaseinfomapper.insertByLoanAndKey(apl);
													//applybaseinfomapper.updateByloanNoAndFiledKeyForContact(apl);
												}
											}
											if(jay.containsKey(Constants.MATEINFO)){//配偶信息
												//先删除网关库的联系人
												apl.setFieldKey(Constants.MATEINFO);
												apl.setLoanNo(loanN);
												applybaseinfomapper.deleteByFiledKey(apl);
												
												JSONObject jj = JSONObject.fromObject(jay.getString(Constants.MATEINFO));
												apl.setTab(Integer.valueOf(jj.getString("sequenceNum")));
												apl.setLoanNo(loanN);
												apl.setState("1");
												apl.setFieldKey(Constants.MATEINFO);
												apl.setFieldObjValue(jj.toString());
												apl.setFieldKeyParent(Constants.FRIENDSINFO);
												applybaseinfomapper.insertByLoanAndKey(apl);
											}
											if(jay.containsKey(Constants.ASSETSINFO)){//房产信息
												apl.setFieldKey(Constants.ASSETSINFO);
												apl.setFieldObjValue(jay.getString(Constants.ASSETSINFO));
												applybaseinfomapper.updateByloanNoAndFiledKey(apl);
											}
											if(jay.containsKey(Constants.CARINFO)){//车辆信息
												apl.setFieldKey(Constants.CARINFO);
												apl.setFieldObjValue(jay.getString(Constants.CARINFO));
												applybaseinfomapper.updateByloanNoAndFiledKey(apl);
											}
											if(jay.containsKey(Constants.MERCHANTLOANINFO)){//淘宝账户信息
												apl.setFieldKey(Constants.MERCHANTLOANINFO);
												apl.setFieldObjValue(jay.getString(Constants.MERCHANTLOANINFO));
												applybaseinfomapper.updateByloanNoAndFiledKey(apl);
											}
											if(jay.containsKey(Constants.PROVIDENTINFO)){//公积金信息
												newjson.putAll(jay.getJSONObject(Constants.PROVIDENTINFO));
//												apl.setFieldKey(Constants.PROVIDENTINFO);
//												apl.setFieldObjValue(jay.getString(Constants.PROVIDENTINFO));
//												applybaseinfomapper.updateByloanNoAndFiledKey(apl);
											}
											if(jay.containsKey(Constants.INSURANCEINFO)){//社保信息
												newjson.putAll(jay.getJSONObject(Constants.INSURANCEINFO));
//												apl.setFieldKey(Constants.INSURANCEINFO);
//												apl.setFieldObjValue(jay.getString(Constants.INSURANCEINFO));
//												applybaseinfomapper.updateByloanNoAndFiledKey(apl);
											}
											if(newjson != null && newjson.size() > 0){
												apl.setFieldKey(Constants.SOCIALSECURITYINFO);
												apl.setFieldObjValue(newjson.toString());
												applybaseinfomapper.updateByloanNoAndFiledKey(apl);
											}
											if(jay.containsKey(Constants.CARDLOANINFO)){//信用卡信息
												apl.setFieldKey(Constants.CARDLOANINFO);
												apl.setFieldObjValue(jay.getString(Constants.CARDLOANINFO));
												applybaseinfomapper.updateByloanNoAndFiledKey(apl);
											}
											if(jay.containsKey(Constants.EDUCATIONINFO)){//学历信息
												apl.setFieldKey(Constants.EDUCATIONINFO);
												apl.setFieldObjValue(jay.getString(Constants.EDUCATIONINFO));
												applybaseinfomapper.updateByloanNoAndFiledKey(apl);
											}
											if(jay.containsKey(Constants.POLICYINFO)){//保单信息
												//先删除网关库的
												apl.setFieldKey(Constants.POLICYINFO);
												apl.setLoanNo(loanN);
												applybaseinfomapper.deleteByFiledKey(apl);
												JSONArray array = JSONArray.fromObject(jay.getString(Constants.POLICYINFO));
												for(int g=0;g<array.size();g++){
													apl.setFieldObjValue(array.get(g).toString());
													apl.setTab(g);
													apl.setFieldKey(Constants.POLICYINFO);
													apl.setFieldKeyParent("nodeInfo");
													apl.setState("1");
													applybaseinfomapper.insertByLoanAndKey(apl);
													//applybaseinfomapper.updateByloanNoAndFiledKeyForContact(apl);
												}
											}
											if(jay.containsKey(Constants.CREDITACCOUNTINFO)){//信用账户信息
												apl.setFieldKey(Constants.CREDITACCOUNTINFO);
												apl.setFieldObjValue(jay.getString(Constants.CREDITACCOUNTINFO));
												applybaseinfomapper.updateByloanNoAndFiledKey(apl);
											}
											if(jay.containsKey(Constants.RESIDENCEINFO)){//居住证信息
												apl.setFieldKey(Constants.RESIDENCEINFO);
												apl.setFieldObjValue(jay.getString(Constants.RESIDENCEINFO));
												applybaseinfomapper.updateByloanNoAndFiledKey(apl);
											}
											//3：更新apply_group_info表报错信息。
											if(ob.containsKey("reasonList")){
												//表示非第一次退回，先把本地数据库中的原因制空
												ApplyGroupInfo groupInfos = new ApplyGroupInfo();
												groupInfos.setLoanNo(loanN);
												groupInfos.setTipMsg("");
												applyGroupInfoMapper.updateMsg(groupInfos);
												JSONArray reason = JSONArray.fromObject(ob.get("reasonList"));
												if(reason!=null){
													for(int j=0;j<reason.size();j++){
														JSONObject objs = JSONObject.fromObject(reason.get(j));
														ApplyGroup record = new ApplyGroup();
														ApplyGroupInfo groupInfo = new ApplyGroupInfo();
														if(objs.containsKey("primaryReasonCode")){
															String reasonCode = objs.getString("primaryReasonCode");//表示第几步的错误
															if("RK001".equals(reasonCode)){//征信
																record.setGroupCode(Constants.CREDITACCOUNTINFO);
																ApplyGroup cord = applyGroupMapper.selectByGroupCode(record);
																groupInfo.setApplyGroupId(cord.getId());
																groupInfo.setLoanNo(loanN);
																ApplyGroupInfo gro = applyGroupInfoMapper.queryById(groupInfo);
																groupInfo.setState("2");
																groupInfo.setIsEdit("1");
																groupInfo.setIsRollback("1");
																if(objs.containsKey("secondReason")){
																	String secondreason = objs.getString("secondReason");//错误原因
																	//根据groupcode判断是否已经存在原因。否直接插入，有，获取拼接并插入。
																	if(StringUtils.isEmpty(gro.getTipMsg())){
																		groupInfo.setTipMsg(secondreason);
																	}else{
																		if(!secondreason.equals(gro.getTipMsg())){
																			groupInfo.setTipMsg(secondreason+","+gro.getTipMsg());
																		}
																	}
																}
																status = false;
																applyGroupInfoMapper.updateByGroupId(groupInfo);
															}else if("RM0010".equals(reasonCode)){//学历
																record.setGroupCode(Constants.EDUCATIONINFO);
																ApplyGroup cord = applyGroupMapper.selectByGroupCode(record);
																groupInfo.setApplyGroupId(cord.getId());
																groupInfo.setLoanNo(loanN);
																ApplyGroupInfo gro = applyGroupInfoMapper.queryById(groupInfo);
																groupInfo.setState("2");
																groupInfo.setIsEdit("1");
																groupInfo.setIsRollback("1");
																if(objs.containsKey("secondReason")){
																	String secondreason = objs.getString("secondReason");//错误原因
																	//根据groupcode判断是否已经存在原因。否直接插入，有，获取拼接并插入。
																	if(StringUtils.isEmpty(gro.getTipMsg())){
																		groupInfo.setTipMsg(secondreason);
																	}else{
																		if(!secondreason.equals(gro.getTipMsg())){
																			groupInfo.setTipMsg(secondreason+","+gro.getTipMsg());
																		}
																	}
																}
																applyGroupInfoMapper.updateByGroupId(groupInfo);
															}else if("RM0007".equals(reasonCode)){//保单
																record.setGroupCode(Constants.POLICYINFO);
																ApplyGroup cord = applyGroupMapper.selectByGroupCode(record);
																groupInfo.setApplyGroupId(cord.getId());
																groupInfo.setLoanNo(loanN);
																ApplyGroupInfo gro = applyGroupInfoMapper.queryById(groupInfo);
																groupInfo.setState("2");
																groupInfo.setIsEdit("1");
																groupInfo.setIsRollback("1");
																if(objs.containsKey("secondReason")){
																	String secondreason = objs.getString("secondReason");//错误原因
																	//根据groupcode判断是否已经存在原因。否直接插入，有，获取拼接并插入。
																	if(StringUtils.isEmpty(gro.getTipMsg())){
																		groupInfo.setTipMsg(secondreason);
																	}else{
																		if(!secondreason.equals(gro.getTipMsg())){
																			groupInfo.setTipMsg(secondreason+","+gro.getTipMsg());
																		}
																	}
																}
																applyGroupInfoMapper.updateByGroupId(groupInfo);
															}else if("RT00037".equals(reasonCode)||"RM0006".equals(reasonCode)){//房产
																record.setGroupCode(Constants.ASSETSINFO);
																ApplyGroup cord = applyGroupMapper.selectByGroupCode(record);
																groupInfo.setLoanNo(loanN);
																groupInfo.setApplyGroupId(cord.getId());
																ApplyGroupInfo gro = applyGroupInfoMapper.queryById(groupInfo);
																groupInfo.setState("2");
																groupInfo.setIsEdit("1");
																groupInfo.setIsRollback("1");
																if(objs.containsKey("secondReason")){
																	String secondreason = objs.getString("secondReason");//错误原因
																	//根据groupcode判断是否已经存在原因。否直接插入，有，获取拼接并插入。
																	if(StringUtils.isEmpty(gro.getTipMsg())){
																		groupInfo.setTipMsg(secondreason);
																	}else{
																		if(!secondreason.equals(gro.getTipMsg())){
																			groupInfo.setTipMsg(secondreason+","+gro.getTipMsg());
																		}
																	}
																}
																applyGroupInfoMapper.updateByGroupId(groupInfo);
															}else if("RM0008".equals(reasonCode)){//车产
																record.setGroupCode(Constants.CARINFO);
																ApplyGroup cord = applyGroupMapper.selectByGroupCode(record);
																groupInfo.setApplyGroupId(cord.getId());
																groupInfo.setLoanNo(loanN);
																ApplyGroupInfo gro = applyGroupInfoMapper.queryById(groupInfo);
																groupInfo.setState("2");
																groupInfo.setIsEdit("1");
																groupInfo.setIsRollback("1");
																if(objs.containsKey("secondReason")){
																	String secondreason = objs.getString("secondReason");//错误原因
																	//根据groupcode判断是否已经存在原因。否直接插入，有，获取拼接并插入。
																	if(StringUtils.isEmpty(gro.getTipMsg())){
																		groupInfo.setTipMsg(secondreason);
																	}else{
																		if(!secondreason.equals(gro.getTipMsg())){
																			groupInfo.setTipMsg(secondreason+","+gro.getTipMsg());
																		}
																	}
																}
																applyGroupInfoMapper.updateByGroupId(groupInfo);
															}else if("RM0009".equals(reasonCode)){//网购
																record.setGroupCode(Constants.MERCHANTLOANINFO);
																ApplyGroup cord = applyGroupMapper.selectByGroupCode(record);
																groupInfo.setApplyGroupId(cord.getId());
																groupInfo.setLoanNo(loanN);
																ApplyGroupInfo gro = applyGroupInfoMapper.queryById(groupInfo);
																groupInfo.setState("2");
																groupInfo.setIsEdit("1");
																groupInfo.setIsRollback("1");
																if(objs.containsKey("secondReason")){
																	String secondreason = objs.getString("secondReason");//错误原因
																	//根据groupcode判断是否已经存在原因。否直接插入，有，获取拼接并插入。
																	if(StringUtils.isEmpty(gro.getTipMsg())){
																		groupInfo.setTipMsg(secondreason);
																	}else{
																		if(!secondreason.equals(gro.getTipMsg())){
																			groupInfo.setTipMsg(secondreason+","+gro.getTipMsg());
																		}
																	}
																}
																applyGroupInfoMapper.updateByGroupId(groupInfo);
															}else if("RM0012".equals(reasonCode)){//公积金/社保信息
																record.setGroupCode(Constants.SOCIALSECURITYINFO);
																ApplyGroup cord = applyGroupMapper.selectByGroupCode(record);
																if(cord != null){
																	groupInfo.setApplyGroupId(cord.getId());
																	groupInfo.setLoanNo(loanN);
																	//删除之前的信息
																	
																	List<ApplyGroupInfo> gro1 = applyGroupInfoMapper.queryByLoan(groupInfo);
																	if(gro1.size()!=0&&gro1.size()>2){
																		ApplyGroupInfo a = new ApplyGroupInfo();
																		a.setLoanNo(loanN);
																		a.setApplyGroupId(cord.getId());
																		a.setState("1");
																		applyGroupInfoMapper.delete(a);
																	}
																	ApplyGroupInfo gro=null;
																	if(gro1.size()!=0){
																		 gro = gro1.get(0);
																	}
																	groupInfo.setState("2");
																	groupInfo.setIsEdit("1");
																	groupInfo.setIsRollback("1");
																	if(objs.containsKey("secondReason")){
																		String secondreason = objs.getString("secondReason");//错误原因
																		//根据groupcode判断是否已经存在原因。否直接插入，有，获取拼接并插入。
																		if(StringUtils.isEmpty(gro.getTipMsg())){
																			groupInfo.setTipMsg(secondreason);
																		}else{
																			if(!secondreason.equals(gro.getTipMsg())){
																				groupInfo.setTipMsg(secondreason+","+gro.getTipMsg());
																			}
																		}
																	}
																	applyGroupInfoMapper.updateByGroupId(groupInfo);
																}
															}/*else if("RM0012".equals(reasonCode)){//社保
																record.setGroupCode(Constants.INSURANCEINFO);
																ApplyGroup cord = applyGroupMapper.selectByGroupCode(record);
																groupInfo.setApplyGroupId(cord.getId());
																groupInfo.setLoanNo(loanN);
																ApplyGroupInfo gro = applyGroupInfoMapper.queryById(groupInfo);
																groupInfo.setState("2");
																groupInfo.setIsEdit("1");
																groupInfo.setIsRollback("1");
																if(objs.containsKey("secondReason")){
																	String secondreason = objs.getString("secondReason");//错误原因
																	//根据groupcode判断是否已经存在原因。否直接插入，有，获取拼接并插入。
																	if(StringUtils.isEmpty(gro.getTipMsg())){
																		groupInfo.setTipMsg(secondreason);
																	}else{
																		if(!secondreason.equals(gro.getTipMsg())){
																			groupInfo.setTipMsg(secondreason+","+gro.getTipMsg());
																		}
																	}
																}
																applyGroupInfoMapper.updateByGroupId(groupInfo);
															}*/else if("RM0002".equals(reasonCode)){//工作证明
																
															}else if("RK002".equals(reasonCode)){//信用卡资料
																record.setGroupCode(Constants.CARDLOANINFO);
																ApplyGroup cord = applyGroupMapper.selectByGroupCode(record);
																groupInfo.setApplyGroupId(cord.getId());
																groupInfo.setLoanNo(loanN);
																ApplyGroupInfo gro = applyGroupInfoMapper.queryById(groupInfo);
																groupInfo.setState("2");
																groupInfo.setIsEdit("1");
																groupInfo.setIsRollback("1");
																if(objs.containsKey("secondReason")){
																	String secondreason = objs.getString("secondReason");//错误原因
																	//根据groupcode判断是否已经存在原因。否直接插入，有，获取拼接并插入。
																	if(StringUtils.isEmpty(gro.getTipMsg())){
																		groupInfo.setTipMsg(secondreason);
																	}else{
																		if(!secondreason.equals(gro.getTipMsg())){
																			groupInfo.setTipMsg(secondreason+","+gro.getTipMsg());
																		}
																	}
																}
																applyGroupInfoMapper.updateByGroupId(groupInfo);
															}else if("RM0015".equals(reasonCode)){//居住证明
																record.setGroupCode(Constants.RESIDENCEINFO);
																ApplyGroup cord = applyGroupMapper.selectByGroupCode(record);
																groupInfo.setApplyGroupId(cord.getId());
																groupInfo.setLoanNo(loanN);
																ApplyGroupInfo gro = applyGroupInfoMapper.queryById(groupInfo);
																groupInfo.setState("2");
																groupInfo.setIsEdit("1");
																groupInfo.setIsRollback("1");
																if(objs.containsKey("secondReason")){
																	String secondreason = objs.getString("secondReason");//错误原因
																	//根据groupcode判断是否已经存在原因。否直接插入，有，获取拼接并插入。
																	if(StringUtils.isEmpty(gro.getTipMsg())){
																		groupInfo.setTipMsg(secondreason);
																	}else{
																		if(!secondreason.equals(gro.getTipMsg())){
																			groupInfo.setTipMsg(secondreason+","+gro.getTipMsg());
																		}
																	}
																}
																applyGroupInfoMapper.updateByGroupId(groupInfo);
															}else if("RM99990003".equals(reasonCode)){//客户取消申请
																
															}else{//其他
																record.setGroupCode(Constants.OTHERINFO);
																ApplyGroup cord = applyGroupMapper.selectByGroupCode(record);
																groupInfo.setApplyGroupId(cord.getId());
																groupInfo.setLoanNo(loanN);
																ApplyGroupInfo gro = applyGroupInfoMapper.queryById(groupInfo);
																groupInfo.setState("2");
																groupInfo.setIsEdit("1");
																groupInfo.setIsRollback("1");
																if(objs.containsKey("secondReason")){
																	String secondreason = objs.getString("secondReason");//错误原因
																	//根据groupcode判断是否已经存在原因。否直接插入，有，获取拼接并插入。
																	if(StringUtils.isEmpty(gro.getTipMsg())){
																		groupInfo.setTipMsg(secondreason);
																	}else{
																		if(!secondreason.equals(gro.getTipMsg())){
																			groupInfo.setTipMsg(secondreason+","+gro.getTipMsg());
																		}
																	}
																}
																applyGroupInfoMapper.updateByGroupId(groupInfo);
															}
														}
													}
												}	
											}
											//调用查询征信报告接口，如果没查到，更新第四步信用账户为灰色，并更新本地信用数据reportid为空
											Model_004019 models = new Model_004019();
											models.setAppNo(loanN);
											models.setIdCard(idNo);
											models.setName(customer.getCustomerName());
											loanOrderService.queryCreditReport(models,status);
										}
									}else{
										loanOrder.setBtnStatus(Constants.NOT_BUTTON);
										loanOrder.setFlowstatusValue(Constants.ORDER_STATES_BANLIZHONG);
										loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
										loanOrder.setIsShowStatus(Constants.IS_SHOW_STATUS);
										loanOrder.setFlowstatus("办理中");
									}
								}
							}else{
								//其他退回
								loanOrder.setBtnStatus(Constants.RETURN);
								loanOrder.setFlowstatusValue(Constants.ORDER_STATES_BANLIZHONG);
								loanOrder.setIsShowModifyCard(Constants.NOT_MODIFY_BANKCARD);
								loanOrder.setIsShowStatus(Constants.IS_SHOW_STATUS);
								loanOrder.setFlowstatus("案件回退");
								//调录单查询接口获取信息，更新本地数据
								JSONObject jayb = ldBase(customer.getIdCard(),loanN);
								JSONObject jay = JSONObject.fromObject(jayb.get("baseInfo"));
								
								ApplyLoanInfo app = new ApplyLoanInfo();
								app.setLoanNo(loanN);
								//判断是否是退回修改没提交数据
								ApplyLoanInfo AInfo = applyloaninfomapper.selectByLoanNo(app);
								boolean status = true;
								if(!AInfo.getApplyStatus().equals("2")){//修改未提交，不做同步
									JSONObject newjson=new JSONObject();
									//1.更新主表状态为2
									app.setApplyStatus(Constants.IS_REBACK_SUBMIT);
									app.setUpdateTime(new Date());
									applyloaninfomapper.updateByLoanNo(app);
									
									//2.更新信息表
									ApplyBaseInfo apl = new ApplyBaseInfo();
									apl.setLoanNo(loanN);
									if(jay.containsKey(Constants.PERSIONINFO)){//个人信息
										apl.setFieldKey(Constants.PERSIONINFO);
										apl.setFieldObjValue(jay.getString(Constants.PERSIONINFO));
										applybaseinfomapper.updateByloanNoAndFiledKey(apl);
									}
									if(jay.containsKey(Constants.EMPITEMINFO)){//工作信息
										apl.setFieldKey(Constants.EMPITEMINFO);
										apl.setFieldObjValue(jay.getString(Constants.EMPITEMINFO));
										applybaseinfomapper.updateByloanNoAndFiledKey(apl);
									}
									if(jay.containsKey(Constants.CONTACTPERSONINFO)){//联系人信息
										//先删除网关库的联系人
										apl.setFieldKey(Constants.CONTACTPERSONINFO);
										apl.setLoanNo(loanN);
										applybaseinfomapper.deleteByFiledKey(apl);
										//在新增联系人
										JSONArray array = JSONArray.fromObject(jay.getString(Constants.CONTACTPERSONINFO));
										for(int k=0;k<array.size();k++){
											JSONObject contactobj = JSONObject.fromObject(array.get(k).toString());
											String tab = String.valueOf(contactobj.get("sequenceNum"));
											apl.setFieldObjValue(array.get(k).toString());
											apl.setTab(Integer.valueOf(tab));
											apl.setLoanNo(loanN);
											apl.setFieldKey(Constants.CONTACTPERSONINFO);
											apl.setFieldKeyParent("friendsInfo");
											apl.setState("1");
											applybaseinfomapper.insertByLoanAndKey(apl);
											//applybaseinfomapper.updateByloanNoAndFiledKeyForContact(apl);
										}
									}
									if(jay.containsKey(Constants.MATEINFO)){//配偶信息
										//先删除网关库的联系人
										apl.setFieldKey(Constants.MATEINFO);
										apl.setLoanNo(loanN);
										applybaseinfomapper.deleteByFiledKey(apl);
										
										JSONObject jj = JSONObject.fromObject(jay.getString(Constants.MATEINFO));
										apl.setTab(Integer.valueOf(jj.getString("sequenceNum")));
										apl.setLoanNo(loanN);
										apl.setState("1");
										apl.setFieldKey(Constants.MATEINFO);
										apl.setFieldObjValue(jj.toString());
										apl.setFieldKeyParent(Constants.FRIENDSINFO);
										applybaseinfomapper.insertByLoanAndKey(apl);
									}
									if(jay.containsKey(Constants.ASSETSINFO)){//房产信息
										apl.setFieldKey(Constants.ASSETSINFO);
										apl.setFieldObjValue(jay.getString(Constants.ASSETSINFO));
										applybaseinfomapper.updateByloanNoAndFiledKey(apl);
									}
									if(jay.containsKey(Constants.CARINFO)){//车辆信息
										apl.setFieldKey(Constants.CARINFO);
										apl.setFieldObjValue(jay.getString(Constants.CARINFO));
										applybaseinfomapper.updateByloanNoAndFiledKey(apl);
									}
									if(jay.containsKey(Constants.MERCHANTLOANINFO)){//淘宝账户信息
										apl.setFieldKey(Constants.MERCHANTLOANINFO);
										apl.setFieldObjValue(jay.getString(Constants.MERCHANTLOANINFO));
										applybaseinfomapper.updateByloanNoAndFiledKey(apl);
									}
									if(jay.containsKey(Constants.PROVIDENTINFO)){//公积金信息
										newjson.putAll(jay.getJSONObject(Constants.PROVIDENTINFO));
//										apl.setFieldKey(Constants.PROVIDENTINFO);
//										apl.setFieldObjValue(jay.getString(Constants.PROVIDENTINFO));
//										applybaseinfomapper.updateByloanNoAndFiledKey(apl);
									}
									if(jay.containsKey(Constants.INSURANCEINFO)){//社保信息
										newjson.putAll(jay.getJSONObject(Constants.INSURANCEINFO));
//										apl.setFieldKey(Constants.INSURANCEINFO);
//										apl.setFieldObjValue(jay.getString(Constants.INSURANCEINFO));
//										applybaseinfomapper.updateByloanNoAndFiledKey(apl);
									}
									if(newjson != null && newjson.size() > 0){
										apl.setFieldKey(Constants.SOCIALSECURITYINFO);
										apl.setFieldObjValue(newjson.toString());
										applybaseinfomapper.updateByloanNoAndFiledKey(apl);
									}
									if(jay.containsKey(Constants.CARDLOANINFO)){//信用卡信息
										apl.setFieldKey(Constants.CARDLOANINFO);
										apl.setFieldObjValue(jay.getString(Constants.CARDLOANINFO));
										applybaseinfomapper.updateByloanNoAndFiledKey(apl);
									}
									if(jay.containsKey(Constants.EDUCATIONINFO)){//学历信息
										apl.setFieldKey(Constants.EDUCATIONINFO);
										apl.setFieldObjValue(jay.getString(Constants.EDUCATIONINFO));
										applybaseinfomapper.updateByloanNoAndFiledKey(apl);
									}
									if(jay.containsKey(Constants.POLICYINFO)){//保单信息
										//先删除网关库的
										apl.setFieldKey(Constants.POLICYINFO);
										apl.setLoanNo(loanN);
										applybaseinfomapper.deleteByFiledKey(apl);
										JSONArray array = JSONArray.fromObject(jay.getString(Constants.POLICYINFO));
										for(int g=0;g<array.size();g++){
											apl.setFieldObjValue(array.get(g).toString());
											apl.setTab(g);
											apl.setFieldKey(Constants.POLICYINFO);
											apl.setFieldKeyParent("nodeInfo");
											apl.setState("1");
											applybaseinfomapper.insertByLoanAndKey(apl);
											//applybaseinfomapper.updateByloanNoAndFiledKeyForContact(apl);
										}
									}
									if(jay.containsKey(Constants.CREDITACCOUNTINFO)){//信用账户信息
										apl.setFieldKey(Constants.CREDITACCOUNTINFO);
										apl.setFieldObjValue(jay.getString(Constants.CREDITACCOUNTINFO));
										applybaseinfomapper.updateByloanNoAndFiledKey(apl);
									}
									if(jay.containsKey(Constants.RESIDENCEINFO)){//居住证信息
										apl.setFieldKey(Constants.RESIDENCEINFO);
										apl.setFieldObjValue(jay.getString(Constants.RESIDENCEINFO));
										applybaseinfomapper.updateByloanNoAndFiledKey(apl);
									}
									//3：更新apply_group_info表报错信息。
									if(ob.containsKey("reasonList")){
										//表示非第一次退回，先把本地数据库中的原因制空
										ApplyGroupInfo groupInfos = new ApplyGroupInfo();
										groupInfos.setLoanNo(loanN);
										groupInfos.setTipMsg("");
										applyGroupInfoMapper.updateMsg(groupInfos);
										JSONArray reason = JSONArray.fromObject(ob.get("reasonList"));
										if(reason!=null){
											for(int j=0;j<reason.size();j++){
												JSONObject objs = JSONObject.fromObject(reason.get(j));
												ApplyGroup record = new ApplyGroup();
												ApplyGroupInfo groupInfo = new ApplyGroupInfo();
												if(objs.containsKey("primaryReasonCode")){
													String reasonCode = objs.getString("primaryReasonCode");//表示第几步的错误
													if("RK001".equals(reasonCode)){//征信
														record.setGroupCode(Constants.CREDITACCOUNTINFO);
														ApplyGroup cord = applyGroupMapper.selectByGroupCode(record);
														groupInfo.setApplyGroupId(cord.getId());
														groupInfo.setLoanNo(loanN);
														ApplyGroupInfo gro = applyGroupInfoMapper.queryById(groupInfo);
														groupInfo.setState("2");
														groupInfo.setIsEdit("1");
														groupInfo.setIsRollback("1");
														if(objs.containsKey("secondReason")){
															String secondreason = objs.getString("secondReason");//错误原因
															//根据groupcode判断是否已经存在原因。否直接插入，有，获取拼接并插入。
															if(StringUtils.isEmpty(gro.getTipMsg())){
																groupInfo.setTipMsg(secondreason);
															}else{
																if(!secondreason.equals(gro.getTipMsg())){
																	groupInfo.setTipMsg(secondreason+","+gro.getTipMsg());
																}
															}
														}
														status = false;
														applyGroupInfoMapper.updateByGroupId(groupInfo);
													}else if("RM0010".equals(reasonCode)){//学历
														record.setGroupCode(Constants.EDUCATIONINFO);
														ApplyGroup cord = applyGroupMapper.selectByGroupCode(record);
														groupInfo.setApplyGroupId(cord.getId());
														groupInfo.setLoanNo(loanN);
														ApplyGroupInfo gro = applyGroupInfoMapper.queryById(groupInfo);
														groupInfo.setState("2");
														groupInfo.setIsEdit("1");
														groupInfo.setIsRollback("1");
														if(objs.containsKey("secondReason")){
															String secondreason = objs.getString("secondReason");//错误原因
															//根据groupcode判断是否已经存在原因。否直接插入，有，获取拼接并插入。
															if(StringUtils.isEmpty(gro.getTipMsg())){
																groupInfo.setTipMsg(secondreason);
															}else{
																if(!secondreason.equals(gro.getTipMsg())){
																	groupInfo.setTipMsg(secondreason+","+gro.getTipMsg());
																}
															}
														}
														applyGroupInfoMapper.updateByGroupId(groupInfo);
													}else if("RM0007".equals(reasonCode)){//保单
														record.setGroupCode(Constants.POLICYINFO);
														ApplyGroup cord = applyGroupMapper.selectByGroupCode(record);
														groupInfo.setApplyGroupId(cord.getId());
														groupInfo.setLoanNo(loanN);
														ApplyGroupInfo gro = applyGroupInfoMapper.queryById(groupInfo);
														groupInfo.setState("2");
														groupInfo.setIsEdit("1");
														groupInfo.setIsRollback("1");
														if(objs.containsKey("secondReason")){
															String secondreason = objs.getString("secondReason");//错误原因
															//根据groupcode判断是否已经存在原因。否直接插入，有，获取拼接并插入。
															if(StringUtils.isEmpty(gro.getTipMsg())){
																groupInfo.setTipMsg(secondreason);
															}else{
																if(!secondreason.equals(gro.getTipMsg())){
																	groupInfo.setTipMsg(secondreason+","+gro.getTipMsg());
																}
															}
														}
														applyGroupInfoMapper.updateByGroupId(groupInfo);
													}else if("RT00037".equals(reasonCode)||"RM0006".equals(reasonCode)){//房产
														record.setGroupCode(Constants.ASSETSINFO);
														ApplyGroup cord = applyGroupMapper.selectByGroupCode(record);
														groupInfo.setApplyGroupId(cord.getId());
														groupInfo.setLoanNo(loanN);
														ApplyGroupInfo gro = applyGroupInfoMapper.queryById(groupInfo);
														groupInfo.setState("2");
														groupInfo.setIsEdit("1");
														groupInfo.setIsRollback("1");
														if(objs.containsKey("secondReason")){
															String secondreason = objs.getString("secondReason");//错误原因
															//根据groupcode判断是否已经存在原因。否直接插入，有，获取拼接并插入。
															if(StringUtils.isEmpty(gro.getTipMsg())){
																groupInfo.setTipMsg(secondreason);
															}else{
																if(!secondreason.equals(gro.getTipMsg())){
																	groupInfo.setTipMsg(secondreason+","+gro.getTipMsg());
																}
															}
														}
														applyGroupInfoMapper.updateByGroupId(groupInfo);
													}else if("RM0008".equals(reasonCode)){//车产
														record.setGroupCode(Constants.CARINFO);
														ApplyGroup cord = applyGroupMapper.selectByGroupCode(record);
														groupInfo.setApplyGroupId(cord.getId());
														groupInfo.setLoanNo(loanN);
														ApplyGroupInfo gro = applyGroupInfoMapper.queryById(groupInfo);
														groupInfo.setState("2");
														groupInfo.setIsEdit("1");
														groupInfo.setIsRollback("1");
														if(objs.containsKey("secondReason")){
															String secondreason = objs.getString("secondReason");//错误原因
															//根据groupcode判断是否已经存在原因。否直接插入，有，获取拼接并插入。
															if(StringUtils.isEmpty(gro.getTipMsg())){
																groupInfo.setTipMsg(secondreason);
															}else{
																if(!secondreason.equals(gro.getTipMsg())){
																	groupInfo.setTipMsg(secondreason+","+gro.getTipMsg());
																}
															}
														}
														applyGroupInfoMapper.updateByGroupId(groupInfo);
													}else if("RM0009".equals(reasonCode)){//网购
														record.setGroupCode(Constants.MERCHANTLOANINFO);
														ApplyGroup cord = applyGroupMapper.selectByGroupCode(record);
														groupInfo.setApplyGroupId(cord.getId());
														groupInfo.setLoanNo(loanN);
														ApplyGroupInfo gro = applyGroupInfoMapper.queryById(groupInfo);
														groupInfo.setState("2");
														groupInfo.setIsEdit("1");
														groupInfo.setIsRollback("1");
														if(objs.containsKey("secondReason")){
															String secondreason = objs.getString("secondReason");//错误原因
															//根据groupcode判断是否已经存在原因。否直接插入，有，获取拼接并插入。
															if(StringUtils.isEmpty(gro.getTipMsg())){
																groupInfo.setTipMsg(secondreason);
															}else{
																if(!secondreason.equals(gro.getTipMsg())){
																	groupInfo.setTipMsg(secondreason+","+gro.getTipMsg());
																}
															}
														}
														applyGroupInfoMapper.updateByGroupId(groupInfo);
													}else if("RM0012".equals(reasonCode)){//公积金/社保信息
														record.setGroupCode(Constants.SOCIALSECURITYINFO);
														ApplyGroup cord = applyGroupMapper.selectByGroupCode(record);
														if(cord != null){
															groupInfo.setApplyGroupId(cord.getId());
															groupInfo.setLoanNo(loanN);
															//ApplyGroupInfo gro = applyGroupInfoMapper.queryById(groupInfo);
															
															//删除之前的信息
															
															List<ApplyGroupInfo> gro1 = applyGroupInfoMapper.queryByLoan(groupInfo);
															if(gro1.size()!=0&&gro1.size()>2){
																ApplyGroupInfo a = new ApplyGroupInfo();
																a.setLoanNo(loanN);
																a.setApplyGroupId(cord.getId());
																a.setState("1");
																applyGroupInfoMapper.delete(a);
															}
															
															ApplyGroupInfo gro = null;
															if(gro1.size()!=0){
																gro = gro1.get(0);
															}
															groupInfo.setState("2");
															groupInfo.setIsEdit("1");
															groupInfo.setIsRollback("1");
															if(objs.containsKey("secondReason")){
																String secondreason = objs.getString("secondReason");//错误原因
																//根据groupcode判断是否已经存在原因。否直接插入，有，获取拼接并插入。
																if(StringUtils.isEmpty(gro.getTipMsg())){
																	groupInfo.setTipMsg(secondreason);
																}else{
																	if(!secondreason.equals(gro.getTipMsg())){
																		groupInfo.setTipMsg(secondreason+","+gro.getTipMsg());
																	}
																}
															}
															applyGroupInfoMapper.updateByGroupId(groupInfo);
														}
													}/*else if("RM0012".equals(reasonCode)){//社保
														record.setGroupCode(Constants.INSURANCEINFO);
														ApplyGroup cord = applyGroupMapper.selectByGroupCode(record);
														groupInfo.setApplyGroupId(cord.getId());
														groupInfo.setLoanNo(loanN);
														ApplyGroupInfo gro = applyGroupInfoMapper.queryById(groupInfo);
														groupInfo.setState("2");
														groupInfo.setIsEdit("1");
														groupInfo.setIsRollback("1");
														if(objs.containsKey("secondReason")){
															String secondreason = objs.getString("secondReason");//错误原因
															//根据groupcode判断是否已经存在原因。否直接插入，有，获取拼接并插入。
															if(StringUtils.isEmpty(gro.getTipMsg())){
																groupInfo.setTipMsg(secondreason);
															}else{
																if(!secondreason.equals(gro.getTipMsg())){
																	groupInfo.setTipMsg(secondreason+","+gro.getTipMsg());
																}
															}
														}
														applyGroupInfoMapper.updateByGroupId(groupInfo);
													}*/else if("RM0002".equals(reasonCode)){//工作证明
														
													}else if("RK002".equals(reasonCode)){//信用卡资料
														record.setGroupCode(Constants.CARDLOANINFO);
														ApplyGroup cord = applyGroupMapper.selectByGroupCode(record);
														groupInfo.setApplyGroupId(cord.getId());
														groupInfo.setLoanNo(loanN);
														ApplyGroupInfo gro = applyGroupInfoMapper.queryById(groupInfo);
														groupInfo.setState("2");
														groupInfo.setIsEdit("1");
														groupInfo.setIsRollback("1");
														if(objs.containsKey("secondReason")){
															String secondreason = objs.getString("secondReason");//错误原因
															//根据groupcode判断是否已经存在原因。否直接插入，有，获取拼接并插入。
															if(StringUtils.isEmpty(gro.getTipMsg())){
																groupInfo.setTipMsg(secondreason);
															}else{
																if(!secondreason.equals(gro.getTipMsg())){
																	groupInfo.setTipMsg(secondreason+","+gro.getTipMsg());
																}
															}
														}
														applyGroupInfoMapper.updateByGroupId(groupInfo);
													}else if("RM0015".equals(reasonCode)){//居住证明
														record.setGroupCode(Constants.RESIDENCEINFO);
														ApplyGroup cord = applyGroupMapper.selectByGroupCode(record);
														groupInfo.setApplyGroupId(cord.getId());
														groupInfo.setLoanNo(loanN);
														ApplyGroupInfo gro = applyGroupInfoMapper.queryById(groupInfo);
														groupInfo.setState("2");
														groupInfo.setIsEdit("1");
														groupInfo.setIsRollback("1");
														if(objs.containsKey("secondReason")){
															String secondreason = objs.getString("secondReason");//错误原因
															//根据groupcode判断是否已经存在原因。否直接插入，有，获取拼接并插入。
															if(StringUtils.isEmpty(gro.getTipMsg())){
																groupInfo.setTipMsg(secondreason);
															}else{
																if(!secondreason.equals(gro.getTipMsg())){
																	groupInfo.setTipMsg(secondreason+","+gro.getTipMsg());
																}
															}
														}
														applyGroupInfoMapper.updateByGroupId(groupInfo);
													}else if("RM99990003".equals(reasonCode)){//客户取消申请
														
													}else{//其他
														record.setGroupCode(Constants.OTHERINFO);
														ApplyGroup cord = applyGroupMapper.selectByGroupCode(record);
														groupInfo.setApplyGroupId(cord.getId());
														groupInfo.setLoanNo(loanN);
														ApplyGroupInfo gro = applyGroupInfoMapper.queryById(groupInfo);
														groupInfo.setState("2");
														groupInfo.setIsEdit("1");
														groupInfo.setIsRollback("1");
														if(objs.containsKey("secondReason")){
															String secondreason = objs.getString("secondReason");//错误原因
															//根据groupcode判断是否已经存在原因。否直接插入，有，获取拼接并插入。
															if(StringUtils.isEmpty(gro.getTipMsg())){
																groupInfo.setTipMsg(secondreason);
															}else{
																if(!secondreason.equals(gro.getTipMsg())){
																	groupInfo.setTipMsg(secondreason+","+gro.getTipMsg());
																}
															}
														}
														applyGroupInfoMapper.updateByGroupId(groupInfo);
													}
												}
											}
										}	
									}
									//调用查询征信报告接口，如果没查到，更新第四步信用账户为灰色，并更新本地信用数据reportid为空 
									Model_004019 models = new Model_004019();
									models.setAppNo(loanN);
									models.setIdCard(idNo);
									models.setName(customer.getCustomerName());
									loanOrderService.queryCreditReport(models,status);
								}
								
							}
						
						}
					}
					page.add(i, loanOrder);
				}
			}
		}
		map.clear();
		map.put("loanOrderList", page);
		map.put("total", page.size());
		return Result.success(map);
	}
	
	/**
	 * 进件回显结果
	 */
	public ApplyLoanInfo updateApplyStatus(String loanNo){
		com.alibaba.fastjson.JSONObject objAll = new com.alibaba.fastjson.JSONObject();
		objAll.put("loanNo", loanNo);
		log.info("借款进度录单入参值："+ JSONObject.fromObject(objAll));
		Result res = queryLoanAndIdCard(objAll);
		ApplyLoanInfo ali = new ApplyLoanInfo();
		ali.setLoanNo(loanNo);
		ali.setStatus(Constants.DATA_VALID);
		ali = applyloaninfomapper.selectLoanOne(ali);
		if(res.getSuccess()){
			JSONObject  js = JSONObject.fromObject(res.getData());
			if(js.containsKey("processList")){
				JSONArray jsarray = JSONArray.fromObject(js.get("processList"));
				if(jsarray != null && jsarray.size() >0){
					JSONObject jsa = jsarray.getJSONObject(0);
					if(jsa.containsKey("rtfNodeState") && StringUtils.isNotEmpty(jsa.getString("rtfNodeState"))){
						String rtfNodeState = jsa.getString("rtfNodeState");
						if(("FKQR-SUBMIT").equals(rtfNodeState)){//放款完成
							ali.setApplyStatus(Constants.IS_COMPLETE_SUBMIT);
						}
						if(rtfNodeState.equals("LRCS-REJECT")||rtfNodeState.equals("CSFP-REJECT")||rtfNodeState.equals("XSCS-REJECT")||rtfNodeState.equals("ZSFP-REJECT")||rtfNodeState.equals("XSZS-REJECT")
								||rtfNodeState.equals("SQJWH-REJECT")||rtfNodeState.equals("HTQY-REJECT")){//拒绝
							ali.setApplyStatus(Constants.IS_REFUSE_SUBMIT);
						}
						if(rtfNodeState.equals("LRCS-CANCEL")||rtfNodeState.equals("QYCS-CANCEL")||rtfNodeState.equals("HTQY-CANCEL")){//取消
							ali.setApplyStatus(Constants.IS_CANCEL_SUBMIT);
						}
						if(rtfNodeState.equals("CSFP-ZDQQRETURN")||rtfNodeState.equals("XSCS-ZDQQRETURN")||rtfNodeState.equals("XSZS-ZDQQRETURN")){
							ali.setApplyStatus(Constants.IS_REBACK_SUBMIT);
						}
						applyloaninfomapper.updateToTable(ali);
					}
				}
			}
		}
		return ali;
	}
	/**
	 * 根据省份证或借款编号获取借款进度
	 * @param objAll
	 * @return
	 */
	public Result queryLoanAndIdCard(com.alibaba.fastjson.JSONObject objAll){
		Response<com.alibaba.fastjson.JSONObject> response = iloanInfoInputExecuter.queyLoanProgress(objAll);
		log.info("借款进度录单出参值："+JSONObject.fromObject(response.getData())+"备注："+ response.getRepMsg());
		if(!response.isSuccess()){
			return Result.fail(response.getRepMsg());
		}
		return Result.success(response.getData());
	}
	/**
	 * 首页借款进度标识
	 * @param modle
	 * @return
	 * */
	public Result selectByFlag(Model_005016 model){
		//调录单实时查询状态是否改变
		String loanNo = model.getLoanNo();
		String idNo = model.getIdNo();
		JSONObject object = new JSONObject();
		if(StringUtils.isEmpty(idNo)){
			object.put("flag", "0");
			return Result.success(object);
		}
		com.alibaba.fastjson.JSONObject objAll = new com.alibaba.fastjson.JSONObject();
		objAll.put("loanNo", loanNo);
		objAll.put("idNo", idNo);
		log.info("借款进度录单入参值："+ JSONObject.fromObject(objAll));
		Response<com.alibaba.fastjson.JSONObject> response = iloanInfoInputExecuter.queyLoanProgress(objAll);
		log.info("借款进度录单出参值："+ JSONObject.fromObject(response.getData()));
		if(!response.isSuccess()){
			return Result.fail(response.getRepMsg());
		}
		JSONObject obj = JSONObject.fromObject(response.getData());
		if(obj.containsKey("processList")){
			JSONArray js = JSONArray.fromObject(obj.get("processList"));
			if(js!=null){
				for(int i=0;i<js.size();i++){
					JSONObject ob = JSONObject.fromObject(js.get(i));
					//String state = (String)ob.get("state");
					String rtfNodeState = (String)ob.get("rtfNodeState");
					//String rtfState = (String)ob.get("rtfState");
					String loanN = (String)ob.get("loanNo");
						if(basicRedisOpts.exists("type"+loanN)){
							String type = basicRedisOpts.getSingleResult("type"+loanN);
							if(type.equals(rtfNodeState)){//首页不提示
								object.put("flag", "0");
							}else{//提示
								object.put("flag", "1");
							}
						}else{
							object.put("flag", "1");
						}
				}
			}
		
		}
		
		return Result.success(object);
		
	}
	
	
	//根据节点获取运营平台初始化信息
	private JSONObject base(String fieldKey){
		log.info("根据节点获取运营平台初始化信息入参为："+fieldKey);
		Req_VO_700001 req = new Req_VO_700001();
		req.setSysCode(Constants.ZDQQ_SYS_CODE);
		req.setNodeCode(fieldKey);
		Response response = iZdqqExecuter.initField(req);
		log.info("根据节点获取运营平台初始化信息出参为："+response.getData());
		return JSONObject.fromObject(response.getData());
	}
	//获取录单数据
	private JSONObject ldBase(String idCard,String loanNo){
		log.info("获取录单数据入参为："+idCard+","+loanNo);
		com.alibaba.fastjson.JSONObject  ApplyInfoVo = new com.alibaba.fastjson.JSONObject();
		ApplyInfoVo.put("idNo", idCard);
		ApplyInfoVo.put("loanNo", loanNo);
		Response<com.alibaba.fastjson.JSONObject> response = iloanInfoInputExecuter.queyBaseInformation(ApplyInfoVo);
		log.info("录单推送回来的值："+ JSONObject.fromObject(response.getData()));
		JSONObject jo = JSONObject.fromObject(response.getData());
		if(jo != null && jo.size() > 0){
			return jo;
		}
		return null;
	}

	//提交前获取规则引擎校验
	/**
	 * @param loanNo
	 */
	private ZDQQApplyRuleExecVo ruleCheck(String loanNo,String code){
		//获取申请贷款
		ApplyLoanInfo loan = new ApplyLoanInfo();
		loan.setLoanNo(loanNo);
		loan.setStatus(Constants.DATA_VALID);
		loan = applyLoanInfoService.selectOne(loan);
		JSONObject loanjson = JSONObject.fromObject(loan.getFieldValue());
		//获取借款人姓名
		Customer cust = customerService.queryByIdCard(loan.getIdCard());
		
		ZDQQApplyRuleExecVo vo = (ZDQQApplyRuleExecVo) JSONObject.toBean(loanjson, ZDQQApplyRuleExecVo.class);
		vo.setIdNo(loan.getIdCard());
		vo.setExecuteType(code);
		vo.setName(cust.getCustomerName());
		//回退不给填借款编号
		if(!Constants.RULE_NODE_SQ.equals(code) || loan.getApplyStatus().equals(Constants.IS_NOT_SUBMIT)){
			vo.setLoanNo(loanNo);
		}
		vo.setSalesCode(loanjson.getString("branchManagerCode"));
		//获取节点信息
		ApplyBaseInfo  base = new ApplyBaseInfo();
		base.setLoanNo(loanNo);
		base.setState(Constants.DATA_VALID);
		List<ApplyBaseInfo> baseList = applybaseinfomapper.selectBaseTable(base);
		if(baseList != null && !baseList.isEmpty()){
			ZDQQPersonVO pv = new ZDQQPersonVO();
			ZDQQWorkVO wo = new ZDQQWorkVO();
			ZDQQPrivateOwnerVO pro = new ZDQQPrivateOwnerVO();
			ZDQQContactVO co = new ZDQQContactVO();
			ZDQQPolicyVO poo = new ZDQQPolicyVO();
			ZDQQCardVO cao = new ZDQQCardVO();
			ZDQQEstateVO eo = new ZDQQEstateVO();
			ZDQQCarVO caro = new ZDQQCarVO();
			ZDQQMasterVO mo = new ZDQQMasterVO();
			ZDQQEducationVO eco = new ZDQQEducationVO();
			List<String> contactList = new ArrayList<>();
			List<String> policyList = new ArrayList<>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			for (ApplyBaseInfo ab : baseList) {
				if(ab.getFieldKey().equals(Constants.PERSIONINFO)){//个人信息
					JSONObject po = JSONObject.fromObject(ab.getFieldObjValue());
					pv = (ZDQQPersonVO) JSONObject.toBean(po, ZDQQPersonVO.class);
					if(po.containsKey("wechatNum") && StringUtils.isNotEmpty(po.getString("wechatNum")))
						pv.setWeChatNum(po.getString("wechatNum"));
					if(po.containsKey("gender") && StringUtils.isNotEmpty(po.getString("gender"))){
						if("M".equals(po.getString("gender")))
							pv.setGender("男");
						else
							pv.setGender("女");
					}
					if(po.containsKey("issuer") && StringUtils.isNotEmpty(po.getString("issuer"))){
						String[] issuer = po.getString("issuer").split(",");
						if(issuer != null &&issuer.length > 0){
							pv.setIssuerStateId(Long.valueOf(issuer[0]));
							pv.setIssuerCityId(Long.valueOf(issuer[1]));
							pv.setIssuerZoneId(Long.valueOf(issuer[2]));
						}
					}
					if(po.containsKey("home") && StringUtils.isNotEmpty(po.getString("home"))){
						String[] home = po.getString("home").split(",");
						if(home != null &&home.length > 0){
							pv.setHomeStateId(Long.valueOf(home[0]));
							pv.setHomeCityId(Long.valueOf(home[1]));
							pv.setHomeZoneId(Long.valueOf(home[2]));
						}
					}
					if(po.containsKey("homeSameRegistered") && StringUtils.isNotEmpty(po.getString("homeSameRegistered"))){
						if("1".equals(po.getString("homeSameRegistered"))){
							pv.setHomeSameRegistered(true);
						}else{
							pv.setHomeSameRegistered(false);
						}
					}
					List<String> list = new ArrayList<>();
					if(po.containsKey("cellphone") && StringUtils.isNotEmpty(po.getString("cellphone")))
						list.add(po.getString("cellphone"));
					if(po.containsKey("cellphoneSec") && StringUtils.isNotEmpty(po.getString("cellphoneSec")))
						list.add(po.getString("cellphoneSec"));
					pv.setCellPhones(list);
				}
				if(ab.getFieldKey().equals(Constants.EMPITEMINFO)){//工作信息
					JSONObject po = JSONObject.fromObject(ab.getFieldObjValue());
					wo = (ZDQQWorkVO) JSONObject.toBean(po,ZDQQWorkVO.class);
					if(po.containsKey("corp") && StringUtils.isNotEmpty(po.getString("corp"))){
						String[] corp = po.getString("corp").split(",");
						wo.setCorpProvinceId(Long.valueOf(corp[0]));
						wo.setCorpCityId(Long.valueOf(corp[1]));
						wo.setCorpZoneId(Long.valueOf(corp[2]));
					}
					List<String> list = new ArrayList<>();
					if(po.containsKey("corpPhone") && StringUtils.isNotEmpty(po.getString("corpPhone")))
						list.add(po.getString("corpPhone"));
					if(po.containsKey("corpPhoneSec") && StringUtils.isNotEmpty(po.getString("corpPhoneSec")))
						list.add(po.getString("corpPhoneSec"));
					if(list != null && !list.isEmpty()){
						wo.setCorpPhones(list);
					}
					if(po.containsKey("corpStandFrom") && StringUtils.isNotEmpty(po.getString("corpStandFrom"))){
						try {
							wo.setCorpStandFrom(sdf.parse(po.getString("corpStandFrom")+"-01"));
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
					pro = (ZDQQPrivateOwnerVO)JSONObject.toBean(po, ZDQQPrivateOwnerVO.class);
					if(po.containsKey("setupDate") && StringUtils.isNotEmpty(po.getString("setupDate"))){
						try {
							pro.setSetupDate(sdf.parse(po.getString("setupDate")+"-01"));
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
					
				}
				//第三步
				if(ab.getFieldKey().equals(Constants.MATEINFO)){//婚配信息
					JSONObject po = JSONObject.fromObject(ab.getFieldObjValue());
					if(po.containsKey("ifForeignPenple") && StringUtils.isNotEmpty(po.getString("ifForeignPenple"))){
						if("1".equals(po.getString("ifForeignPenple"))){
							co.setSpouseIfForeignPenple("Y");
						}else{
							co.setSpouseIfForeignPenple("N");
						}
					}
						
					if(po.containsKey("contactIdNo") && StringUtils.isNotEmpty(po.getString("contactIdNo")))
						co.setSpouseIdNo(po.getString("contactIdNo"));
					List<String> list = new ArrayList<>();
					if(po.containsKey("contactCellphone") && StringUtils.isNotEmpty(po.getString("contactCellphone"))){
						list.add(po.getString("contactCellphone"));
						co.setSpouseCellPhones(list);
					}
					List<String> ll = new ArrayList<>();
					if(po.containsKey("contactCorpPhone") && StringUtils.isNotEmpty(po.getString("contactCorpPhone"))){
						ll.add(po.getString("contactCorpPhone"));
						co.setSpouseCorpPhones(ll);
					}
					if(po.containsKey("contactEmpName") && StringUtils.isNotEmpty(po.getString("contactEmpName")))
						co.setSpouseEmpName(po.getString("contactEmpName"));
					if(po.containsKey("ifKnowLoan")){
						if("1".equals(po.getString("ifKnowLoan")))
							co.setSpouseIfKnowLoan("Y");
						else
							co.setSpouseIfKnowLoan("N");
					}
					
				}
				if(ab.getFieldKey().equals(Constants.CONTACTPERSONINFO)){//联系人信息
					contactList.add(ab.getFieldObjValue());
				}
				if(ab.getFieldKey().equals(Constants.ASSETSINFO)){//房产信息
					JSONObject po = JSONObject.fromObject(ab.getFieldObjValue());
					eo = (ZDQQEstateVO) JSONObject.toBean(po,ZDQQEstateVO.class);
					if(po.containsKey("monthPaymentAmt") && StringUtils.isNotEmpty(po.getString("monthPaymentAmt")))
						eo.setEstateMonthPaymentAmt(new BigDecimal(po.getString("monthPaymentAmt")));
					else
						eo.setEstateMonthPaymentAmt(new BigDecimal("-1"));
					
					if(po.containsKey("eatateSameRegistered") && StringUtils.isNotEmpty(po.getString("eatateSameRegistered"))){
						if("1".equals(po.getString("eatateSameRegistered")))
							eo.setEstateSameRegistered("Y");
						else
							eo.setEstateSameRegistered("N");
					}
					if(po.containsKey("estateLoanIssueDate") && StringUtils.isNotEmpty(po.getString("estateLoanIssueDate"))){
						try {
							eo.setEstateLoanIssueDate(sdf.parse(po.getString("estateLoanIssueDate")+"-01"));
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
					if(po.containsKey("estateBuyDate") && StringUtils.isNotEmpty(po.getString("estateBuyDate"))){
						try {
							eo.setEstateBuyDate(sdf.parse(po.getString("estateBuyDate")+"-01"));
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
					if(po.containsKey("isCommon") && StringUtils.isNotEmpty(po.getString("isCommon"))){
						if("1".equals(po.getString("isCommon")))
							eo.setIsCommon(true);
						else
							eo.setIsCommon(false);
					}
					if(po.containsKey("estate") && StringUtils.isNotEmpty(po.getString("estate"))){
						String[] estate = po.getString("estate").split(",");
						if(estate != null &&estate.length > 0){
							eo.setEstateStateId(Long.valueOf(estate[0]));
							eo.setEstateCityId(Long.valueOf(estate[1]));
							eo.setEstateZoneId(Long.valueOf(estate[2]));
						}
					}
					if(eo != null){
						eo.setEstateCompleted(true);
					}
				}
				if(ab.getFieldKey().equals(Constants.CARINFO)){//	车辆信息
					JSONObject po = JSONObject.fromObject(ab.getFieldObjValue());
					caro = (ZDQQCarVO) JSONObject.toBean(po, ZDQQCarVO.class);
					if(po.containsKey("carBuyDate") && StringUtils.isNotEmpty(po.getString("carBuyDate"))){
						try {
							caro.setCarBuyDate(sdf.parse(po.getString("carBuyDate")+"-01"));
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
					if(po.containsKey("carLoanIssueDate") && StringUtils.isNotEmpty(po.getString("carLoanIssueDate"))){
						try {
							caro.setCarLoanIssueDate(sdf.parse(po.getString("carLoanIssueDate")+"-01"));
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
					if(po.containsKey("monthPaymentAmt") && StringUtils.isNotEmpty(po.getString("monthPaymentAmt"))){
						caro.setCarMonthPaymentAmt(new BigDecimal(po.getString("monthPaymentAmt")));
					}else{
						caro.setCarMonthPaymentAmt(new BigDecimal("-1"));
					}
					
					if(caro != null){
						caro.setCarCompleted(true);
					}
				}
				if(ab.getFieldKey().equals(Constants.MERCHANTLOANINFO)){//	淘宝账户信息表
					JSONObject po = JSONObject.fromObject(ab.getFieldObjValue());
					mo = (ZDQQMasterVO)JSONObject.toBean(po, ZDQQMasterVO.class);
					if(mo != null){
						mo.setMasterCompleted(true);
					}
					if(po.containsKey("consumptionSum") && StringUtils.isNotEmpty(po.getString("consumptionSum"))){
						mo.setLastYearPayAmt(new BigDecimal(po.getString("consumptionSum")));
					}else{
						mo.setLastYearPayAmt(new BigDecimal("0"));
					}
					
				}
				//修改成只要上传图片就是完整的
				if(ab.getFieldKey().equals(Constants.CARDLOANINFO)){//	信用卡信息
					//查询信用卡图片是否上传
					ApplyPicInfo ap = new ApplyPicInfo();
					ap.setLoanNo(loanNo);
					ap.setFieldKey(Constants.CARDLOANINFO);
					ap.setStatus(Constants.DATA_VALID);
					List<ApplyPicInfo> aplist = applypicinfomapper.select(ap);
					if(aplist != null && !aplist.isEmpty()){
						cao.setCardCompleted(true);
					}
				}
				if(ab.getFieldKey().equals(Constants.EDUCATIONINFO)){//学历信息表
					JSONObject po = JSONObject.fromObject(ab.getFieldObjValue());
					eco = (ZDQQEducationVO)JSONObject.toBean(po, ZDQQEducationVO.class);
					if(po.containsKey("area"))
						eco.setEducationArea(po.getString("area").replace(",", "|"));
					if(po.containsKey("qualification") && StringUtils.isNotEmpty(po.getString("qualification")))
						eco.setEducationQualification(po.getString("qualification"));
					if(po.containsKey("graduationDate") && StringUtils.isNotEmpty(po.getString("graduationDate"))){
						try {
							eco.setEducationGraduationDate(sdf.parse(po.getString("graduationDate")+"-01"));
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
						
					if(eco != null){
						eco.setEducationCompleted(true);
					}
				}
				if(ab.getFieldKey().equals(Constants.POLICYINFO)){//寿险投保信息
					policyList.add(ab.getFieldObjValue());
				}
				if(ab.getFieldKey().equals(Constants.SOCIALSECURITYINFO)){//公积金/社保信息
					JSONObject po = JSONObject.fromObject(ab.getFieldObjValue());
					ProvidentInfoDto proDto= (ProvidentInfoDto) JSONObject.toBean(po, ProvidentInfoDto.class);
					//公积金是否填写
					if (proDto != null && (StringUtils.isNotBlank(proDto.getAccumulationFundAccount()) || StringUtils.isNotBlank(proDto.getAccumulationFundPassword()))){
						vo.setAccumulationFundCompleted(true);
					}
					//社保是否填写
					InsuranceInfoDto insDto = (InsuranceInfoDto) JSONObject.toBean(po, InsuranceInfoDto.class);
					if (insDto != null && (StringUtils.isNotBlank(insDto.getSocialInsuranceAccount()) || StringUtils.isNotBlank(insDto.getSocialInsurancePassword()))){
						vo.setSocialSecurityCompleted(true);
					}
				}
//				if(ab.getFieldKey().equals(Constants.INSURANCEINFO)){
//					JSONObject po = JSONObject.fromObject(ab.getFieldObjValue());
//					if (po != null && !po.isNullObject()){
//						vo.setSocialSecurityCompleted(true);
//					}
//				}
			}
			if(contactList != null && !contactList.isEmpty()){
				List<String> contactRelations = new ArrayList<>();
				List<String> contactCellPhones = new ArrayList<>();
				List<String> contactCorpPhones = new ArrayList<>();
				List<String> contactCorpEmpNames = new ArrayList<>();
				List<String> ifKnowLoans = new ArrayList<>();
				List<Integer> contactCellPhoneNum = new ArrayList<>();
				List<Integer> contactCorpPhoneNum = new ArrayList<>();
				for (String string : contactList) {
					JSONObject jo = JSONObject.fromObject(string);
					if(jo.containsKey("contactRelation") && StringUtils.isNotEmpty(jo.getString("contactRelation")))
						contactRelations.add(jo.getString("contactRelation"));
					if(jo.containsKey("contactCellphone") && StringUtils.isNotEmpty(jo.getString("contactCellphone")))
						contactCellPhones.add(jo.getString("contactCellphone"));
					if(jo.containsKey("contactCorpPhone") && StringUtils.isNotEmpty(jo.getString("contactCorpPhone")))
						contactCorpPhones.add(jo.getString("contactCorpPhone"));
					if(jo.containsKey("contactEmpName") && StringUtils.isNotEmpty(jo.getString("contactEmpName")))
						contactCorpEmpNames.add(jo.getString("contactEmpName"));
					if(jo.containsKey("ifKnowLoan")){
						if("1".equals(jo.getString("ifKnowLoan")))
							ifKnowLoans.add("Y");
						else
							ifKnowLoans.add("N");
					}
					contactCellPhoneNum.add(contactCellPhones.size() > 0?1:0);
					contactCorpPhoneNum.add(contactCorpPhones.size() > 0?1:0);
				}
				if(contactRelations != null && !contactRelations.isEmpty())
					co.setContactRelations(contactRelations);
				if(contactCellPhones != null && !contactCellPhones.isEmpty())
					co.setContactCellPhones(contactCellPhones);
				if(contactCorpPhones != null && !contactCorpPhones.isEmpty())
					co.setContactCorpPhones(contactCorpPhones);
				if(contactCorpEmpNames != null && !contactCorpEmpNames.isEmpty())
					co.setContactCorpEmpNames(contactCorpEmpNames);
				if(contactCellPhoneNum != null && !contactCellPhoneNum.isEmpty())
					co.setContactCellPhoneNum(contactCellPhoneNum);
				if(contactCorpPhoneNum != null && !contactCorpPhoneNum.isEmpty())
					co.setContactCorpPhoneNum(contactCorpPhoneNum);
				if(ifKnowLoans != null && !ifKnowLoans.isEmpty())
					co.setIfKnowLoans(ifKnowLoans);
			}
			if(policyList !=null && !policyList.isEmpty()){
				List<String> paymentMethodList = new ArrayList<>();
				List<Date> insuranceDateList = new ArrayList<>();
				List<BigDecimal> paymentMoneyList = new ArrayList<>();
				List<String> insuranceCompanyList = new ArrayList<>();
				for (String string : policyList) {
					JSONObject jo = JSONObject.fromObject(string);
					if(jo.containsKey("paymentMethod") && StringUtils.isNotEmpty(jo.getString("paymentMethod")))
						paymentMethodList.add(jo.getString("paymentMethod"));
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
					try {
						if(jo.containsKey("insuranceDate") && StringUtils.isNotEmpty(jo.getString("insuranceDate"))){
							Date dd = formatter.parse(jo.getString("insuranceDate"));
							insuranceDateList.add(dd);
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
					if(jo.containsKey("paymentMoney") && StringUtils.isNotEmpty(jo.getString("paymentMoney")))
						paymentMoneyList.add(new BigDecimal(jo.getString("paymentMoney")));
					else
						paymentMoneyList.add(new BigDecimal("0"));
					if(jo.containsKey("insuranceCompany") && StringUtils.isNotEmpty(jo.getString("insuranceCompany")))
						insuranceCompanyList.add(jo.getString("insuranceCompany"));
				}
				if(paymentMethodList != null && !paymentMethodList.isEmpty())
					poo.setPaymentMethodList(paymentMethodList);
				if(insuranceDateList != null && !insuranceDateList.isEmpty())
					poo.setInsuranceDateList(insuranceDateList);
				if(paymentMoneyList != null && !paymentMoneyList.isEmpty())
					poo.setPaymentMoneyList(paymentMoneyList);
				if(insuranceCompanyList != null && !insuranceCompanyList.isEmpty())
					poo.setInsuranceCompanyList(insuranceCompanyList);
				poo.setPolicyCompleted(true);
			}
			vo.setPersonVO(pv);
			vo.setWorkVO(wo);
			vo.setPrivateOwnerVO(pro);
			vo.setContactVO(co);
			vo.setPolicyVO(poo);
			vo.setCardVO(cao);
			vo.setEstateVO(eo);
			vo.setCarVO(caro);
			vo.setMasterVO(mo);
			vo.setEducationVO(eco);
		}
		return vo;
	}

	/**
	 * 初始化第四步每个步骤状态
	 * @param fieldKey
	 */
	private  void insertNodeInfo(/*String fieldKey,*/String loanNo){
		log.info("初始化第四步每步的状态");
		ApplyGroup ag = new ApplyGroup();
		ag.setState(Constants.DATA_VALID);
		//ag.setGroupParentCode(fieldKey);
		List<ApplyGroup> list = applyGroupMapper.select(ag);
		if(list != null && !list.isEmpty()){
			for (ApplyGroup applyGroup : list) {
				ApplyGroupInfo agi = new ApplyGroupInfo();
				agi.setLoanNo(loanNo);
				agi.setApplyGroupId(applyGroup.getId());
				if(applyGroup.getGroupCode().equals(Constants.CREDITACCOUNTINFO)){
					agi.setIsRequested("1");//是否1是必填0不需必填
				}else{
					agi.setIsRequested("0");//是否是必填
				}
				agi.setState("0");//0未填写，1填写，2.有误
				agi.setIsEdit("1");//0不可编辑1可编辑
				agi.setIsRollback("0");//回滚回来的0没有提示，1有提示
				applyGroupInfoMapper.insert(agi);
			}
		}
	}
	/**
	 * 更新第四步状态
	 * @param loanNo
	 * @param fieldKey
	 */
	private void updateNodeInfo(String loanNo,String fieldKey){
		
		ApplyGroup ag = new ApplyGroup();
		ag.setGroupCode(fieldKey);
		ag.setState(Constants.DATA_VALID);
		ag = applyGroupMapper.selectOne(ag);
		if(ag != null){
			ApplyGroupInfo agi = new ApplyGroupInfo();
			agi.setApplyGroupId(ag.getId());
			agi.setLoanNo(loanNo);
			List<ApplyGroupInfo> ag2 = applyGroupInfoMapper.queryByLoan(agi);
			if(ag2.size()!=0&&ag2.size()>2){//有重复记录，删除在新增。
				applyGroupInfoMapper.delete(agi);
				//新增
				ApplyGroupInfo app = new ApplyGroupInfo();
				app.setLoanNo(loanNo);
				app.setState("1");
				app.setIsEdit("1");
				app.setIsRollback("0");
				app.setIsRequested("0");
				app.setApplyGroupId(Long.valueOf("17"));
				applyGroupInfoMapper.insert(app);
				
			}
			if(ag2.size()!=0){
				agi=ag2.get(0);
			}
			ApplyGroupInfo apg = new ApplyGroupInfo();
			
			if(agi != null ){
				//取文本填写情况
				ApplyBaseInfo ali = new ApplyBaseInfo();
				ali.setLoanNo(loanNo);
				ali.setFieldKey(fieldKey);
				List<ApplyBaseInfo> alilist = applybaseinfomapper.select(ali);
				//取图片先写情况
				ApplyPicInfo pic = new ApplyPicInfo();
				pic.setLoanNo(loanNo);
				pic.setFieldKey(fieldKey);
				pic.setStatus(Constants.DATA_VALID);
				List<ApplyPicInfo> list = applypicinfomapper.select(pic);
				
				if((alilist != null && !alilist.isEmpty())||(list != null && !list.isEmpty())){
					apg.setState("1");
				}else
					apg.setState("0");
				apg.setId(agi.getId());
				applyGroupInfoMapper.updateByPrimaryKeySelective(apg);
			}
		}
	}
	
	/**
	 * 征信
	 * @param modle
	 * @return
	 * */
	public Result requestDirect(Model_005017 model){

		String password = model.getPassword();
		String randNum = model.getRandNum();
		String pgeRZRandNum = model.getPgeRZRandNum();
		String pgeRZDataB = model.getPgeRZDataB();
		JSONObject param = new JSONObject();
		param.put("password", password);
		param.put("randNum", randNum);
		param.put("pgeRZRandNum", pgeRZRandNum);
		param.put("pgeRZDataB", pgeRZDataB);
		log.info("调用征信转换接口开始，入参:"+"密码"+password+"随机数"+randNum+pgeRZRandNum+pgeRZDataB);
		com.alibaba.fastjson.JSONObject obj = HttpClientUtil.sendHttpGetReport(password,randNum,pgeRZRandNum,pgeRZDataB);
		log.info("调用征信转换接口结束，请求响应:"+obj.toString());
		if(obj.containsKey("reponseCode")){
			if(obj.get("reponseCode").equals("0000")){
				return Result.success(obj);
			}else{
				return Result.fail("央行服务器繁忙，请稍后再试！");
			}
		}
		return Result.success(obj);
	}
	
}
