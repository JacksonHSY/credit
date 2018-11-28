package com.ymkj.credit.web.api;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ymkj.springside.modules.exception.BusinessException;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSON;
import com.ymkj.credit.common.ex.BussErrorCode;
import com.ymkj.credit.common.ex.JsonException;
import com.ymkj.credit.common.ex.PlatformException;
import com.ymkj.credit.common.untils.AesUtil;
import com.ymkj.credit.common.untils.XXTeaUtil;
import com.ymkj.credit.config.KeyParamBean;
import com.ymkj.credit.config.SwitchParamBean;
import com.ymkj.credit.service.SignService;
import com.ymkj.credit.service.dms.DmsService;
import com.ymkj.credit.web.api.listener.InstantiationTracingBeanPostListener;
import com.ymkj.credit.web.api.model.Result;
import com.ymkj.credit.web.api.model.base.Model_002022;
import com.ymkj.credit.web.api.model.req.ReqMain;
import com.ymkj.credit.web.api.model.req.ReqParam;

/**
 * APP统一接口
 * 
 * @author longjw@yuminsoft.com
 */
@RequestMapping(value = "/Api")
@Slf4j
public class DispatcherController {


    private static final String PKG_NAME_MODEL = "com.ymkj.credit.web.api.model.base";

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private KeyParamBean keyParamBean;

    @Autowired
    private SwitchParamBean switchParamBean;

    private List<HandlerInterceptorAdapter> handlerList;

    @Autowired
    private SignService signService;
    
    
    /**
     * @param functionId 功能号
     * @param param 请求字符串
     * @param key 签名串
     * @return
     */
    @RequestMapping(value = "/requestDeal")
    @ResponseBody
	public String requestDeal(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse,
			@RequestParam("arg0") String functionId,
			@RequestParam("arg1") String param, 
			@RequestParam("arg2") String sign) {
        String response = null;
        String keySource = null;
        try {

            String paramSource = param;

            Class<?> paramClass = Class.forName(PKG_NAME_MODEL + ".Model_" + functionId);
            ReqMain reqMain = JSON.parseObject(paramSource, ReqMain.class);
            String reqParamJson = JSON.parseObject(paramSource).getString("reqParam");
            reqMain.setReqParam((ReqParam) JSON.parseObject(reqParamJson, paramClass));

            Result result;
            try {
            	if("002022".equals(functionId)){
            		Model_002022 model = (Model_002022) reqMain.getReqParam();
            		return signService.loadContractFile(model);
            	}
                List<String> errorList = new ArrayList<String>();
                for (HandlerInterceptorAdapter hia : handlerList) {
                    if (!hia.preHandle(httpRequest, httpResponse, errorList)) {
                    	break;
                    }
                }
                if (errorList.size() > 0) {
                    return encriptResponse(errorList.get(0), keySource);
                }
                result = InstantiationTracingBeanPostListener.dispatch(functionId, reqMain);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                Throwable te = e.getTargetException();
                if (te instanceof BusinessException) {
                    String message = te.getMessage();
                    result = Result.fail(message);
                    log.error(MessageFormat.format("调用功能时业务异常: {0}", message));
                } else {
                    result = Result.fail();
                    log.error("调用功能时系统异常: ", te);
                }
            }
            if (result.getMessage().matches("[\\w\\.]+")) {
                result.setMessage(getMessage(result.getMessage(), null));
            }
             response = JsonException.toJsonStr(result);
            log.info("*************************响应*************************");
            log.info(MessageFormat.format("功能号: {0} 响应: {1}", functionId, response));
        } catch (Exception e) {
            log.error("系统出现异常", e);
            response = JsonException.toJson(BussErrorCode.ERROR_CODE_0102);
        }
        return encriptResponse(response, keySource);
    }

    private String getMessage(String code, Object[] args) {
        if (StringUtils.isEmpty(code)) {
            return "未知";
        }
        return messageSource.getMessage(code, args, null);
    }

    /**
     * 对响应 APP 的结果进行加密 
     * @param key 请求参数 key 解密之后的结果
     * @param response
     * @return
     * @throws Exception
     */
    private String encriptResponse(String response, String key) {
        if (!SwitchParamBean.SWITCH_OFF.equals(switchParamBean.getXxteaEncriptSwitch())) {
            response = XXTeaUtil.xxteaEncrypt(response, key);
        }
        return response;
    }

    public void setHandlerList(List<HandlerInterceptorAdapter> handlerList) {
        this.handlerList = handlerList;
    }
    
    @RequestMapping(value = "/getImg", method = RequestMethod.GET)
    public void getImg(HttpServletRequest request, HttpServletResponse response) {
    	try {
    		String picUrl = request.getParameter("picUrl");
    		picUrl=	picUrl.replace(" ","+");
    		picUrl = AesUtil.decryptAES(picUrl, keyParamBean.getAppKey());//解密后的
    		log.info("☆☆☆☆☆☆☆☆☆☆☆☆解密后的图片地址："+picUrl);
    		String[] subUrl = picUrl.split("\\.");
    		String picType=subUrl[subUrl.length-1];//图片后缀类型
    		/** 拼装图片地址 **/
//    		String newPicUrl = systemProperties.getPicServerUrl()+deUrl;
//    		logger.info("☆☆☆☆☆☆☆☆☆☆☆☆组装后的图片地址："+newPicUrl);   		
    		if("pdf".equalsIgnoreCase(picType)){
    			response.setHeader("Content-Type", "application/pdf");
    		}else if("png".equalsIgnoreCase(picType)){
    			response.setHeader("Content-Type", "image/png");
    		}else if("jpg".equalsIgnoreCase(picType)){
    			response.setHeader("Content-Type", "image/jpeg");
    		}else if("bmp".equalsIgnoreCase(picType)){
    			response.setHeader("Content-Type", "application/x-bmp");
    		}else if("jpeg".equalsIgnoreCase(picType)){
    			response.setHeader("Content-Type", "image/jpeg");
    		}else if("gif".equalsIgnoreCase(picType)){
    			response.setHeader("Content-Type", "image/gif");
    		}else{
    			log.info("☆☆☆☆☆☆☆☆☆☆☆☆图片后缀不支持");
    			throw new PlatformException("APP调用网关系统出现异常");
    		}
    		InputStream is = new URL(picUrl).openStream();
    		byte[] buff = IOUtils.toByteArray(is);
    		response.getOutputStream().write(buff);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new PlatformException("APP调用网关系统出现异常");
		}
    	
    }

}
