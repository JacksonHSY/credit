package com.ymkj.credit.web.api.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.groups.Default;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSON;
import com.ymkj.credit.common.beanvalidator.Validate;
import com.ymkj.credit.common.constants.AppConstants;
import com.ymkj.credit.common.constants.Constants;
import com.ymkj.credit.common.ex.BussErrorCode;
import com.ymkj.credit.common.ex.JsonException;
import com.ymkj.credit.common.untils.RSAUtil;
import com.ymkj.credit.common.untils.XXTeaUtil;
import com.ymkj.credit.config.KeyParamBean;
import com.ymkj.credit.config.SwitchParamBean;
import com.ymkj.credit.web.api.model.req.ReqMain;
import com.ymkj.credit.web.api.model.req.ReqParam;

/**
 * 值域验证拦截器
 * @author 00237071
 *
 */
@Slf4j
@Component
public class FieldsValidationInterceptor extends HandlerInterceptorAdapter {

    private static final String PKG_NAME_MODEL = "com.ymkj.credit.web.api.model.base";

    @Autowired
    private SwitchParamBean switchParamBean;

    @Autowired
    private KeyParamBean keyParamBean;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object obj) {
        String keySource = null;
        List<String> errors = (List<String>)obj; // 为了创建filter list
        try {
            String functionId = request.getParameter("arg0");
            String param = request.getParameter("arg1");
            String key = request.getParameter("arg2");

            keySource = decriptKey(key);
            String paramSource = decriptParam(keySource, param);

            log.info("*************************请求*************************");
            log.info("请求报文："+paramSource);

            Class<?> paramClass = Class.forName(PKG_NAME_MODEL + ".Model_" + functionId);

            ReqMain reqMain = JSON.parseObject(paramSource, ReqMain.class);
            String reqParamJson = JSON.parseObject(paramSource).getString("reqParam");
            reqMain.setReqParam((ReqParam) JSON.parseObject(reqParamJson, paramClass));
            
            if(Constants.ProjectNo_TYPE_WebService.equals(reqMain.getProjectNo())){//h5请求免校验
            	return true;
            }
            //校验参数
            Set<ConstraintViolation<ReqMain>> constraintViolations = checkParam(reqMain);
            if (constraintViolations.size() > 0) {
                String errrMsg = "";
                for (ConstraintViolation<ReqMain> constraintViolation : constraintViolations) {
                    errrMsg += constraintViolation.getMessage() + ",";
                }
                log.error(MessageFormat.format("功能号: {0} 错误: {1}", functionId, errrMsg.substring(0, errrMsg.length() - 1)));

                String message = JsonException.toJsonStr(BussErrorCode.ERROR_CODE_0103.getErrorcode(), AppConstants.Status.EXCEPTION, errrMsg.substring(0, errrMsg.length() - 1), "", null);

                log.info("*************************响应*************************");
                log.info(MessageFormat.format("功能号: {0} 响应: {1}", functionId, message));
                validateFailed(message, errors);
                return false;
            }
            return true;
        } catch (Exception e) {
            String errorMsg = JsonException.toJson(BussErrorCode.ERROR_CODE_0102);
            log.error(errorMsg, e);
            try {
                validateFailed(errorMsg, errors);
            } catch (Exception e1) {
                log.error(errorMsg, e1);
            }
            return false;
        }
    }

    /**
     * 解密 业务参数，在解密业务参数之前，首先要解密key
     * @param keySource
     * @param param
     * @return
     * @throws Exception
     */
    private String decriptParam(String keySource, String param) throws Exception {
        if (!SwitchParamBean.SWITCH_OFF.equals(switchParamBean.getXxteaEncriptSwitch())) {
            return XXTeaUtil.xxteaDecrypt(param, keySource);
        }
        return param;
    }

    /**
     * 对参数的 key 进行解密操作，该 key 将被用于XXTEA 算法的解密工作
     * @param key
     * @return
     * @throws Exception
     */
    private String decriptKey(String key) throws Exception {
        if (!SwitchParamBean.SWITCH_OFF.equals(switchParamBean.getXxteaEncriptSwitch())) {
            String keyDecripted = RSAUtil.rsaDecrypt(key);
            return keyDecripted;
        }
        return key;
    }

    @SuppressWarnings("unchecked")
    private Set<ConstraintViolation<ReqMain>> checkParam(ReqMain reqMain) {
        return Validate.getInstance().validate(reqMain, Default.class);
    }

    private void validateFailed(String errorMsg, List<String> errorObj){
        errorObj.add(errorMsg);
    }

    public SwitchParamBean getSwitchParamBean() {
        return switchParamBean;
    }

    public void setSwitchParamBean(SwitchParamBean switchParamBean) {
        this.switchParamBean = switchParamBean;
    }

    public KeyParamBean getKeyParamBean() {
        return keyParamBean;
    }

    public void setKeyParamBean(KeyParamBean keyParamBean) {
        this.keyParamBean = keyParamBean;
    }

    @SuppressWarnings("unused")
    private void validateFailed(HttpServletResponse response, String errrMsg, String keySource) throws IOException{
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        PrintWriter printWriter = response.getWriter();
        printWriter.print(encriptResponse(errrMsg, keySource));
        printWriter.flush();
    }

    /**
     * 对响应 APP 的结果进行加密
     * @return
     * @throws Exception
     */
    private String encriptResponse(String errrMsg, String key) {
        if (!SwitchParamBean.SWITCH_OFF.equals(switchParamBean.getXxteaEncriptSwitch())) {
            errrMsg = XXTeaUtil.xxteaEncrypt(errrMsg, key);
        }
        return errrMsg;
    }

}
