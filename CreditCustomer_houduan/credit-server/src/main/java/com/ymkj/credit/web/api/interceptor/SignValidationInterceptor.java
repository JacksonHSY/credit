package com.ymkj.credit.web.api.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.ymkj.credit.common.ex.BussErrorCode;
import com.ymkj.credit.common.ex.JsonException;
import com.ymkj.credit.common.untils.CalendarUtils;
import com.ymkj.credit.common.untils.MD5;
import com.ymkj.credit.common.untils.RSAUtil;
import com.ymkj.credit.common.untils.XXTeaUtil;
import com.ymkj.credit.config.KeyParamBean;
import com.ymkj.credit.config.SwitchParamBean;

/**
 * 签名验证拦截器
 *
 * @author 00237071
 *
 */
@Slf4j
@Component
public class SignValidationInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private SwitchParamBean switchParamBean;

    @Autowired
    private KeyParamBean keyParamBean;

    private static final String SIGN_SEPARATOR = "|";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object obj) {
        String keySource = null;
        List<String> errors = (List<String>)obj; // 为了创建filter list
        try {
            if (!SwitchParamBean.SWITCH_OFF.equals(switchParamBean.getCeValidateSwitch())) {
                String functionId = request.getParameter("arg0");
                String params = request.getParameter("arg1");
                String sign = request.getParameter("arg2");

                if (StringUtils.isBlank(functionId) || StringUtils.isBlank(params) || StringUtils.isBlank(sign)) {
                    log.error("验签失败，请求参数为空");
                    validateFailed(JsonException.toJson(BussErrorCode.ERROR_CODE_0110), errors);
                    return false;
                }

                // 加密key
                String md5SignKey = keyParamBean.getMd5SignKey();
                StringBuffer source = new StringBuffer();
                source.append(functionId).append(SIGN_SEPARATOR).append(params).append(SIGN_SEPARATOR).append(md5SignKey);

                String signCal = MD5.MD5Encode(source.toString());

                log.debug("*************************验签*************************");
                log.debug("验签参数"+source.toString());
                log.debug("服务验签值："+signCal);
                log.debug("报文验签值："+sign);

                if (sign.equals(signCal)) {
                    return true;
                } else {
                    validateFailed(JsonException.toJson(BussErrorCode.ERROR_CODE_0110), errors);
                    return false;
                }
            }
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
        return true;
    }

    private void validateFailed(String errorMsg, List<String> errorObj){
        errorObj.add(errorMsg);
    }

    // version 1, 包含加密错误信息，通过response 返回
    @SuppressWarnings("unused")
    private void validateFailed(HttpServletResponse response, String errorMsg, String keySource) throws IOException{
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        PrintWriter printWriter = response.getWriter();
        printWriter.print(encriptResponse(errorMsg, keySource));
        printWriter.flush();
    }

    /**
     * 检查请求的URL 是否过期
     * @param timeStamp
     * @return
     * @throws ParseException
     */
    private boolean isURLTimeout(String timeStamp) throws ParseException {
        if (!SwitchParamBean.SWITCH_OFF.equals(switchParamBean.getXxteaEncriptSwitch())) {
            Calendar cal = Calendar.getInstance();
            long currentServerTime = cal.getTimeInMillis();
            long requestTime = DateUtils.parseDate(timeStamp, CalendarUtils.LONG_FORMAT).getTime();
            return currentServerTime - requestTime > keyParamBean.getReqUrlValidTime();
        }
        return false;
    }

    /**
     * 对响应 APP 的结果进行加密 
     * @param key 请求参数 key 解密之后的结果
     * @return
     * @throws Exception
     */
    private String encriptResponse(String errorMsg, String key) {
        if (!SwitchParamBean.SWITCH_OFF.equals(switchParamBean.getXxteaEncriptSwitch())) {
            errorMsg = XXTeaUtil.xxteaEncrypt(errorMsg, key);
        }
        return errorMsg;
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

    public static void main(String[] args) {
        String encoder = URLEncoder.encode("KOoobMNvFsV8xZg8Gv/OhbCgZui8P5vOjNuKKOtd6SxMJoz9k0idl+uIO8HqIKlfq1d5Hvg/z3OvbYWhuJxWeXTg+/sXVRBK2YLf5pRGveXHFSctPH/Ul3Yh3qkbITw1uO4R7z83C5QBOOf7ZiFYuCxbQPOCYtF8RNUwQ9lKGuw=");
        System.out.println(encoder);
        String source = "400001|c4098529c877a74ea7ae95e5abd6145df9d4fa3a31100bd3a328b9d31bcce6403122881b341ac5e2f6cc18c144949c680db37cc156897fb38547b1ad03c5626d5b0bff3afe8a9e066fd1fabc9cc04e438140c78eafcfe8253ad112647d4fc6a51a84fc9caa3767642f2fd26a0274f4eef38a161857645437205ef25a3c87f0c4f562fab6eb35328991cf0b3da665c8153a47dc019e8ed6e07236ae95e8d4292de0cae437c720b9a0cee125272ba1358ab7d6bc3cf6d9f5d6cb43d53f81d1232b894a4a19e88bb06f8b3f657cdac43aaeb27ecb11f337546681b5330053f635e71470ea49cd7b4faa0d72120a2f1660f21e2f6d1205f4d418f7fca60c65c32a899484d0f799bcfc7406f70ddfb11d16cd1dfa7e8712e26be0958aea76a456d81b46892062c7a0744c273d8b35b361f0ed|KOoobMNvFsV8xZg8Gv/OhbCgZui8P5vOjNuKKOtd6SxMJoz9k0idl+uIO8HqIKlfq1d5Hvg/z3OvbYWhuJxWeXTg+/sXVRBK2YLf5pRGveXHFSctPH/Ul3Yh3qkbITw1uO4R7z83C5QBOOf7ZiFYuCxbQPOCYtF8RNUwQ9lKGuw=|damuzhiapp951753";
        String signCal = MD5.MD5Encode(source);
        System.out.println(signCal);
    }
}
