package com.ymkj.credit.service.Zs;

import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.ymkj.credit.common.constants.Constants;
import com.ymkj.credit.common.ex.BussErrorCode;
import com.ymkj.credit.network.HttpClientUtil;
import com.ymkj.springside.modules.exception.BusinessException;

import lombok.extern.slf4j.Slf4j;

/**
 * 征审接口
 * 
 * @author changj@yuminsoft.com
 * @date2018年3月13日
 * @version 1.0
 */
@Slf4j
@Service
public class ZsService {

	/**
	 * 征审获取合同签名结果
	 * 
	 * @TODO
	 * @param map
	 * @return JSONObject
	 * @author changj@yuminsoft.com
	 * @date2018年3月6日
	 */
	public static String sendHttpPost4GetSignContractStatus(String params) {
		log.info("征审获取合同签名结果接口请求入参：" + params);
		String resultStr = HttpClientUtil.sendHttpPost4Zs(
				Constants.ZS_URL_GetSignContractStatus, params);
		log.info("征审获取合同签名结果接口响应：" + resultStr);
		if (StringUtils.isBlank(resultStr)) {
			log.error("=============>>>>>征审获取合同签名结果接口请求异常：接口响应为空");
			throw new BusinessException(
					BussErrorCode.ERROR_CODE_0102.getErrorcode(),
					BussErrorCode.ERROR_CODE_0102.getErrordesc());
		}
		JSONObject respModel = JSONObject.fromObject(resultStr);
		if (!Constants.RESP_CODE_SUCCESS.equals(respModel.get("resCode"))) {
			log.info("=============>>>>>征审获取合同签名结果请求异常,异常状态码:"
					+ respModel.get("resCode") + ",异常内容:"
					+ respModel.get("resMsg").toString());
			throw new BusinessException(respModel.get("resMsg").toString());
		}
		return respModel.getString("attachment");
	}

	/**
	 * 登录注册接口
	 * 
	 * @TODO
	 * @param params
	 * @return JSONObject
	 * @author changj@yuminsoft.com
	 * @date2018年3月14日
	 */
	public static JSONObject sendHttpPost4UserLoginToRegisterSignature(
			String params) {
		log.info("征审登录注册接口请求入参：" + params);
		String resultStr = HttpClientUtil.sendHttpPost4Zs(
				Constants.ZS_URL_UserLoginToRegisterSignature, params);
		log.info("征审登录注册接口响应：" + resultStr);
		if (StringUtils.isBlank(resultStr)) {
			log.error("=============>>>>>征审登录注册接口请求异常：接口响应为空");
			throw new BusinessException(
					BussErrorCode.ERROR_CODE_0102.getErrorcode(),
					BussErrorCode.ERROR_CODE_0102.getErrordesc());
		}
		JSONObject respModel = JSONObject.fromObject(resultStr);
		if (!Constants.RESP_CODE_SUCCESS.equals(respModel.get("resCode"))) {
			log.info("=============>>>>>征审登录注册接口请求异常,异常状态码:"
					+ respModel.get("resCode") + ",异常内容:"
					+ respModel.get("resMsg").toString());
			throw new BusinessException(respModel.get("resMsg").toString());
		}
		return respModel;
	}

	/**
	 * 查询用户有几份需要展示的合同list。
	 * 
	 * @TODO
	 * @param params
	 * @return String
	 * @author changj@yuminsoft.com
	 * @date2018年3月14日
	 */
	public static String sendHttpPost4FindSignContractListInfos(String params) {
		log.info("征审查询合同列表接口请求入参：" + params);
		String resultStr = HttpClientUtil.sendHttpPost4Zs(
				Constants.ZS_URL_FindSignContractListInfos, params);
		log.info("征审查询合同列表接口接口响应：" + resultStr);
		if (StringUtils.isBlank(resultStr)) {
			log.error("=============>>>>>征审查询合同列表接口请求异常：接口响应为空");
			throw new BusinessException(
					BussErrorCode.ERROR_CODE_0102.getErrorcode(),
					BussErrorCode.ERROR_CODE_0102.getErrordesc());
		}
		JSONObject respModel = JSONObject.fromObject(resultStr);
		if (!Constants.RESP_CODE_SUCCESS.equals(respModel.get("resCode"))) {
			log.info("=============>>>>>征审查询合同列表接口请求异常,异常状态码:"
					+ respModel.get("resCode") + ",异常内容:"
					+ respModel.get("resMsg").toString());
			throw new BusinessException(respModel.get("resMsg").toString());
		}
		return respModel.getString("attachment");
	}

	/**
	 * 加载合同文件
	 * 
	 * @TODO
	 * @param params
	 * @return String
	 * @author changj@yuminsoft.com
	 * @date2018年3月14日
	 */
	public static String sendHttpPost4LoadContractFile(String params) {
		log.info("征审加载合同文件接口请求入参：" + params);
		String resultStr = HttpClientUtil.sendHttpPost4Zs(
				Constants.ZS_URL_LoadContractFile, params);
		log.info("征审加载合同文件口接口响应：" + resultStr);
		if (StringUtils.isBlank(resultStr)) {
			log.error("=============>>>>>征审加载合同文件接口请求异常：接口响应为空");
			throw new BusinessException(
					BussErrorCode.ERROR_CODE_0102.getErrorcode(),
					BussErrorCode.ERROR_CODE_0102.getErrordesc());
		}
		JSONObject respModel = JSONObject.fromObject(resultStr);
		if (!Constants.RESP_CODE_SUCCESS.equals(respModel.get("resCode"))) {
			log.info("=============>>>>>征审加载合同文件接口请求异常,异常状态码:"
					+ respModel.get("resCode") + ",异常内容:"
					+ respModel.get("resMsg").toString());
			throw new BusinessException(respModel.get("resMsg").toString());
		}
		return respModel.getString("attachment");
	}

	/**
	 * 客户签章
	 * 
	 * @TODO
	 * @param params
	 * @return JSONObject
	 * @author changj@yuminsoft.com
	 * @date2018年3月14日
	 */
	public static JSONObject sendHttpPost4ClientSinature(String params) {
		log.info("征审客户签章接口请求入参：" + params);
		String resultStr = HttpClientUtil.sendHttpPost4Zs(
				Constants.ZS_URL_ClientSinature, params);
		log.info("征审客户签章接口响应：" + resultStr);
		if (StringUtils.isBlank(resultStr)) {
			log.error("=============>>>>>征审客户签章接口请求异常：接口响应为空");
			throw new BusinessException(
					BussErrorCode.ERROR_CODE_0102.getErrorcode(),
					BussErrorCode.ERROR_CODE_0102.getErrordesc());
		}
		JSONObject respModel = JSONObject.fromObject(resultStr);
		if (!Constants.RESP_CODE_SUCCESS.equals(respModel.get("resCode"))) {
			log.info("=============>>>>>征审客户签章接口请求异常,异常状态码:"
					+ respModel.get("resCode") + ",异常内容:"
					+ respModel.get("resMsg").toString());
			throw new BusinessException(respModel.get("resMsg").toString());
		}
		return respModel;
	}

	/**
	 * 发送短信验证码
	 * 
	 * @TODO
	 * @param params
	 * @return JSONObject
	 * @author changj@yuminsoft.com
	 * @date2018年3月14日
	 */
	public static JSONObject sendHttpPost4SendValidCode(String params) {
		log.info("征审发送短信验证码接口请求入参：" + params);
		String resultStr = HttpClientUtil.sendHttpPost4Zs(
				Constants.ZS_URL_SendValidCode, params);
		log.info("征审发送短信验证码接口响应：" + resultStr);
		if (StringUtils.isBlank(resultStr)) {
			log.error("=============>>>>>征审发送短信验证码接口请求异常：接口响应为空");
			throw new BusinessException(
					BussErrorCode.ERROR_CODE_0102.getErrorcode(),
					BussErrorCode.ERROR_CODE_0102.getErrordesc());
		}
		JSONObject respModel = JSONObject.fromObject(resultStr);
		if (!Constants.RESP_CODE_SUCCESS.equals(respModel.get("resCode"))) {
			log.info("=============>>>>>征审发送短信验证码接口请求异常,异常状态码:"
					+ respModel.get("resCode") + ",异常内容:"
					+ respModel.get("resMsg").toString());
			throw new BusinessException(respModel.get("resMsg").toString());
		}
		return respModel;
	}

	/**
	 * 校验短信验证码
	 * 
	 * @TODO
	 * @param params
	 * @return JSONObject
	 * @author changj@yuminsoft.com
	 * @date2018年3月14日
	 */
	public static JSONObject sendHttpPost4CheckValidCode(String params) {
		log.info("征审校验短信验证码接口请求入参：" + params);
		String resultStr = HttpClientUtil.sendHttpPost4Zs(
				Constants.ZS_URL_CheckValidCode, params);
		log.info("征审发送短信验证码接口响应：" + resultStr);
		if (StringUtils.isBlank(resultStr)) {
			log.error("=============>>>>>征审校验短信验证码接口请求异常：接口响应为空");
			throw new BusinessException(
					BussErrorCode.ERROR_CODE_0102.getErrorcode(),
					BussErrorCode.ERROR_CODE_0102.getErrordesc());
		}
		JSONObject respModel = JSONObject.fromObject(resultStr);
		if (!Constants.RESP_CODE_SUCCESS.equals(respModel.get("resCode"))) {
			log.info("=============>>>>>征审校验短信验证码接口请求异常,异常状态码:"
					+ respModel.get("resCode") + ",异常内容:"
					+ respModel.get("resMsg").toString());
			throw new BusinessException(respModel.get("resMsg").toString());
		}
		return respModel;
	}
}