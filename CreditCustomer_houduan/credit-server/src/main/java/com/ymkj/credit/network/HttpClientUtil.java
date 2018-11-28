package com.ymkj.credit.network;

import static org.apache.http.entity.ContentType.*;

import com.ymkj.credit.common.constants.Constants;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ymkj.credit.common.ex.BussErrorCode;
import com.ymkj.springside.modules.exception.BusinessException;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import com.ymkj.credit.common.untils.SpringContextHelper;
import com.ymkj.credit.config.ApplicationBean;

import lombok.extern.log4j.Log4j;

/**
 * 向服务发送请求
 */
@Log4j
public class HttpClientUtil {
	
	 // utf-8字符编码
    public static final String CHARSET_UTF_8 = "utf-8";

    // HTTP内容类型。
    public static final String CONTENT_TYPE_TEXT_HTML = "text/xml";

    // HTTP内容类型。相当于form表单的形式，提交数据
    public static final String CONTENT_TYPE_FORM_URL = "application/x-www-form-urlencoded";

    // HTTP内容类型。相当于form表单的形式，提交数据
    public static final String CONTENT_TYPE_JSON_URL = "application/json;charset=utf-8";

    // 连接管理器
    private static PoolingHttpClientConnectionManager pool;

    // 请求配置
    private static RequestConfig requestConfig;
    
    private static ApplicationBean applicationBean;

    static {
    	
    	applicationBean = (ApplicationBean) SpringContextHelper.getBean("applicationBean");
        try {
            //System.out.println("初始化HttpClientTest~~~开始");
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    builder.build());
            // 配置同时支持 HTTP 和 HTPPS
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create().register(
                    "http", PlainConnectionSocketFactory.getSocketFactory()).register(
                    "https", sslsf).build();
            // 初始化连接管理器
            pool = new PoolingHttpClientConnectionManager(
                    socketFactoryRegistry);
            // 将最大连接数增加到200，实际项目最好从配置文件中读取这个值
            pool.setMaxTotal(200);
            // 设置最大路由
            pool.setDefaultMaxPerRoute(2);
            // 根据默认超时限制初始化requestConfig
            int socketTimeout = 50000;
            int connectTimeout = 50000;
            int connectionRequestTimeout = 50000;
            // 设置请求超时时间
            requestConfig = RequestConfig.custom().setConnectionRequestTimeout(
                    connectionRequestTimeout).setSocketTimeout(socketTimeout).setConnectTimeout(
                    connectTimeout).build();
            //System.out.println("初始化HttpClientTest~~~结束");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    public static CloseableHttpClient getHttpClient() {
        
        CloseableHttpClient httpClient = HttpClients.custom()
                // 设置连接池管理
                .setConnectionManager(pool)
                // 设置请求配置
                .setDefaultRequestConfig(requestConfig)
                // 设置重试次数
                .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false))
                .build();
        
        return httpClient;
    }

    /**
     * 发送Post请求
     * 
     * @param httpPost
     * @return
     */
    private static String sendHttpPost(HttpPost httpPost) {

        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        // 响应内容
        String responseContent = null;
        try {
            // 创建默认的httpClient实例.
            httpClient = getHttpClient();
            // 配置请求信息
            httpPost.setConfig(requestConfig);
            // 执行请求
            response = httpClient.execute(httpPost);
            // 得到响应实例
            HttpEntity entity = response.getEntity();

            // 可以获得响应头
            // Header[] headers = response.getHeaders(HttpHeaders.CONTENT_TYPE);
            // for (Header header : headers) {
            // System.out.println(header.getName());
            // }

            // 得到响应类型
            // System.out.println(ContentType.getOrDefault(response.getEntity()).getMimeType());

            // 判断响应状态
            if (response.getStatusLine().getStatusCode() >= 300) {
                throw new Exception(
                        "HTTP Request is not success, Response code is " + response.getStatusLine().getStatusCode());
            }

            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                responseContent = EntityUtils.toString(entity, CHARSET_UTF_8);
                EntityUtils.consume(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseContent;
    }

    /**
     * 发送Get请求
     * 
     * @param httpGet
     * @return
     */
    public static String sendHttpGet(HttpGet httpGet) {

        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        // 响应内容
        String responseContent = null;
        try {
            // 创建默认的httpClient实例.
            httpClient = getHttpClient();
            // 配置请求信息
            httpGet.setConfig(requestConfig);
            // 执行请求
            response = httpClient.execute(httpGet);
            // 得到响应实例
            HttpEntity entity = response.getEntity();

            // 可以获得响应头
            // Header[] headers = response.getHeaders(HttpHeaders.CONTENT_TYPE);
            // for (Header header : headers) {
            // System.out.println(header.getName());
            // }

            // 得到响应类型
            // System.out.println(ContentType.getOrDefault(response.getEntity()).getMimeType());

            // 判断响应状态
            if (response.getStatusLine().getStatusCode() >= 300) {
                throw new Exception(
                        "HTTP Request is not success, Response code is " + response.getStatusLine().getStatusCode());
            }

            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                responseContent = EntityUtils.toString(entity, CHARSET_UTF_8);
                EntityUtils.consume(entity);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseContent;
    }

    /**
     * 发送 post请求（带文件）
     * 
     * @param httpUrl
     *            地址
     * @param maps
     *            参数
     * @param fileLists
     *            附件
     */
    public static String sendHttpPost(String httpUrl, Map<String, String> maps, List<File> fileLists) {
        HttpPost httpPost = new HttpPost(httpUrl);// 创建httpPost
        MultipartEntityBuilder meBuilder = MultipartEntityBuilder.create();
        if (maps != null) {
            for (String key : maps.keySet()) {
                meBuilder.addPart(key, new StringBody(maps.get(key), TEXT_PLAIN));
            }
        }
        if (fileLists != null) {
            for (File file : fileLists) {
                FileBody fileBody = new FileBody(file);
                meBuilder.addPart("files", fileBody);
            }
        }
        HttpEntity reqEntity = meBuilder.build();
        httpPost.setEntity(reqEntity);
        return sendHttpPost(httpPost);
    }

    /**
     * 发送 post请求
     * 
     *            地址
     * @param params
     *            参数(格式:key1=value1&key2=value2)
     * 
     */
    public static String sendHttpPost(String params) {
        HttpPost httpPost = new HttpPost(applicationBean.getUrl());// 创建httpPost
        try {
            // 设置参数
            if (params != null && params.trim().length() > 0) {
                StringEntity stringEntity = new StringEntity(params, "UTF-8");
                stringEntity.setContentType(CONTENT_TYPE_FORM_URL);
                httpPost.setEntity(stringEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sendHttpPost(httpPost);
    }
    public static String sendHttpPost1(String params) {
        HttpPost httpPost = new HttpPost(applicationBean.getUrlUPIC()+Constants.PIC_URL_DELETE);// 创建httpPost
        try {
            // 设置参数
            if (params != null && params.trim().length() > 0) {
                StringEntity stringEntity = new StringEntity(params, "UTF-8");
                stringEntity.setContentType(CONTENT_TYPE_FORM_URL);
                httpPost.setEntity(stringEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sendHttpPost(httpPost);
    }
    /**
     * 展业
     * @TODO
     * @param params
     * @return
     * String
     * @author changj@yuminsoft.com
     * @date2018年1月8日
     */
    public static String sendHttpPostFZY(String params) {
        HttpPost httpPost = new HttpPost(applicationBean.getUrl4ZY());// 创建httpPost
        try {
            // 设置参数
            if (params != null && params.trim().length() > 0) {
                StringEntity stringEntity = new StringEntity(params, "UTF-8");
                stringEntity.setContentType(CONTENT_TYPE_FORM_URL);
                httpPost.setEntity(stringEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sendHttpPost(httpPost);
    }
    /**
     * 功能描述：
     * 输入参数：
     * @param httpUrl
     * @param params
     * @return
     * 返回类型：String
     * 创建人：tianx
     * 日期：2017年8月24日
     */
    public static String sendHttpPost(String httpUrl,String params) {
        HttpPost httpPost = new HttpPost(applicationBean.getUrl()+httpUrl);// 创建httpPost
        try {
            // 设置参数
            if (params != null && params.trim().length() > 0) {
                StringEntity stringEntity = new StringEntity(params, ContentType.APPLICATION_JSON);
//                stringEntity.setContentType(CONTENT_TYPE_FORM_URL);
                httpPost.setEntity(stringEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info(MessageFormat.format("请求url: {0}；入参: {1}", applicationBean.getUrl()+httpUrl, params));
        return sendHttpPost(httpPost);
    }
    
    public static String sendHttpPostPicture(String httpUrl,String params) {
        HttpPost httpPost = new HttpPost(httpUrl);// 创建httpPost
        try {
            // 设置参数
            if (params != null && params.trim().length() > 0) {
                StringEntity stringEntity = new StringEntity(params, ContentType.APPLICATION_JSON);
//                stringEntity.setContentType(CONTENT_TYPE_FORM_URL);
                httpPost.setEntity(stringEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info(MessageFormat.format("请求url: {0}；入参: {1}", applicationBean.getUrl()+httpUrl, params));
        return sendHttpPost(httpPost);
    }
    
    /**
     * 发送 post请求
     * 
     * @param maps 参数
     */
    public static String sendHttpPost(Map<String, String> maps) {
        String parem = convertStringParamter(maps);
        return sendHttpPost(parem);
    }

    
    
    
    /**
     * 发送 post请求 发送json数据
     * 
     * @param httpUrl
     *            地址
     * @param paramsJson
     *            参数(格式 json)
     * 
     */
    public static String sendHttpPostJson(String paramsJson) {
        HttpPost httpPost = new HttpPost(applicationBean.getUrl());// 创建httpPost
        try {
            // 设置参数
            if (paramsJson != null && paramsJson.trim().length() > 0) {
                StringEntity stringEntity = new StringEntity(paramsJson, "UTF-8");
                stringEntity.setContentType(CONTENT_TYPE_JSON_URL);
                httpPost.setEntity(stringEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sendHttpPost(httpPost);
    }
    
    /**
     * 发送 post请求 发送xml数据
     * 
     * @param httpUrl   地址
     * @param paramsXml  参数(格式 Xml)
     * 
     */
    public static String sendHttpPostXml(String paramsXml) {
        HttpPost httpPost = new HttpPost(applicationBean.getUrl());// 创建httpPost
        try {
            // 设置参数
            if (paramsXml != null && paramsXml.trim().length() > 0) {
                StringEntity stringEntity = new StringEntity(paramsXml, "UTF-8");
                stringEntity.setContentType(CONTENT_TYPE_TEXT_HTML);
                httpPost.setEntity(stringEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sendHttpPost(httpPost);
    }
    

    /**
     * 将map集合的键值对转化成：key1=value1&key2=value2 的形式
     * 
     * @param parameterMap
     *            需要转化的键值对集合
     * @return 字符串
     */
    public static String convertStringParamter(Map parameterMap) {
        StringBuffer parameterBuffer = new StringBuffer();
        if (parameterMap != null) {
            Iterator iterator = parameterMap.keySet().iterator();
            String key = null;
            String value = null;
            while (iterator.hasNext()) {
                key = (String) iterator.next();
                if (parameterMap.get(key) != null) {
                    value = (String) parameterMap.get(key);
                } else {
                    value = "";
                }
                parameterBuffer.append(key).append("=").append(value);
                if (iterator.hasNext()) {
                    parameterBuffer.append("&");
                }
            }
        }
        return parameterBuffer.toString();
    }
    public static JSONObject sendHttpPostForCreditCustomerInfo(String url,String params) {
        log.info("信贷端接口请求入参："+params);
        String resultStr = HttpClientUtil.sendHttpPost(url,params);
        log.info("信贷端接口请求响应："+resultStr);
        if(StringUtils.isBlank(resultStr)){
            log.error("=============>>>>>信贷端接口请求异常：接口响应为空");
            throw new BusinessException(BussErrorCode.ERROR_CODE_0102.getErrorcode(),BussErrorCode.ERROR_CODE_0102.getErrordesc());
        }
        JSONObject respModel = JSONObject.fromObject(resultStr);
        if(!Constants.RESP_CODE_SUCCESS.equals(respModel.get("resCode"))){
            log.info("=============>>>>>信贷端接口请求异常,异常状态码:"+respModel.get("resCode")+",异常内容:"+respModel.get("resMsg").toString());
            return null;
        }
        return respModel.getJSONObject("attachment");
    }
    /**
     * 返回完整报文
     * @TODO
     * @param url
     * @param params
     * @return
     * @author changj@yuminsoft.com
     * @date2018年6月14日
     */
    public static JSONObject sendHttpPostForCreditCustomerInfo4All(String url,String params) {
        log.info("信贷端接口请求入参："+params);
        String resultStr = HttpClientUtil.sendHttpPost(url,params);
        log.info("信贷端接口请求响应："+resultStr);
        if(StringUtils.isBlank(resultStr)){
            log.error("=============>>>>>信贷端接口请求异常：接口响应为空");
            throw new BusinessException(BussErrorCode.ERROR_CODE_0102.getErrorcode(),BussErrorCode.ERROR_CODE_0102.getErrordesc());
        }
        JSONObject respModel = JSONObject.fromObject(resultStr);
        if(!Constants.RESP_CODE_SUCCESS.equals(respModel.get("resCode"))){
            log.info("=============>>>>>信贷端接口请求异常,异常状态码:"+respModel.get("resCode")+",异常内容:"+respModel.get("resMsg").toString());
        }
        return respModel;
    }
    /**
     * 借新还旧
     * @TODO
     * @param url
     * @param params
     * @return
     * JSONObject
     * @author changj@yuminsoft.com
     * @date2018年4月10日
     */
    public static JSONObject sendHttpPostForCredit4Jxhj(String url,String params) {
        log.info("信贷端接口请求入参："+params);
        String resultStr = HttpClientUtil.sendHttpPost(url,params);
        log.info("信贷端接口请求响应："+resultStr);
        if(StringUtils.isBlank(resultStr)){
            log.error("=============>>>>>信贷端接口请求异常：接口响应为空");
            throw new BusinessException(BussErrorCode.ERROR_CODE_0102.getErrorcode(),BussErrorCode.ERROR_CODE_0102.getErrordesc());
        }
        JSONObject respModel = JSONObject.fromObject(resultStr);
        if(!Constants.RESP_CODE_SUCCESS.equals(respModel.get("resCode"))){
            log.info("=============>>>>>信贷端接口请求异常,异常状态码:"+respModel.get("resCode")+",异常内容:"+respModel.get("resMsg").toString());
            if("800001".equals(respModel.get("resCode"))){
            	return respModel;
            }else{
            	throw new BusinessException(respModel.get("resCode").toString(),respModel.get("resMsg").toString());
            }
        }
        return respModel.getJSONObject("attachment");
    }
    public static JSONObject sendHttpPostForCredit(String url,String params) {
        log.info("信贷端接口请求入参："+params);
        String resultStr = HttpClientUtil.sendHttpPost(url,params);
        log.info("信贷端接口请求响应："+resultStr);
        if(StringUtils.isBlank(resultStr)){
            log.error("=============>>>>>信贷端接口请求异常：接口响应为空");
            throw new BusinessException(BussErrorCode.ERROR_CODE_0102.getErrorcode(),BussErrorCode.ERROR_CODE_0102.getErrordesc());
        }
        JSONObject respModel = JSONObject.fromObject(resultStr);
        if(!Constants.RESP_CODE_SUCCESS.equals(respModel.get("resCode")) && !Constants.RESP_CODE_EXCEPTION.equals(respModel.get("resCode"))){
            log.error("=============>>>>>信贷端接口请求异常："+respModel.get("resMsg").toString());
            throw new BusinessException(BussErrorCode.ERROR_CODE_0102.getErrorcode(),BussErrorCode.ERROR_CODE_0102.getErrordesc());
        }
        if(Constants.RESP_CODE_EXCEPTION.equals(respModel.get("resCode"))){
            if(Constants.CREDIT_URL_CUSTOMERINFO.equals(url)){//债权取消 或者 拒绝不提示异常
            	return null;
            }else{
                log.info("=============>>>>>信贷端接口请求异常："+respModel.get("resMsg").toString());
                throw new BusinessException(respModel.get("resMsg").toString());
            }
        }
        return respModel.getJSONObject("attachment");
    }

    public static JSONObject sendHttpPostForPIC(String url,String params) {
        log.info("PIC接口请求入参："+params);
        String resultStr = HttpClientUtil.sendHttpPost(url,params);
        log.info("PIC接口请求响应："+resultStr);
        if(StringUtils.isBlank(resultStr)){
            log.error("=============>>>>>PIC接口请求异常：接口响应为空");
            throw new BusinessException(BussErrorCode.ERROR_CODE_0102.getErrorcode(),BussErrorCode.ERROR_CODE_0102.getErrordesc());
        }
        JSONObject respModel = JSONObject.fromObject(resultStr);
        if(!Constants.RESP_CODE_SUCCESS.equals(respModel.get("resCode")) && !Constants.RESP_CODE_EXCEPTION.equals(respModel.get("resCode"))){
            log.error("=============>>>>>PIC接口请求异常："+respModel.get("resMsg").toString());
            throw new BusinessException(BussErrorCode.ERROR_CODE_0102.getErrorcode(),BussErrorCode.ERROR_CODE_0102.getErrordesc());
        }
        if(Constants.RESP_CODE_EXCEPTION.equals(respModel.get("resCode"))){
            log.info("=============>>>>>PIC接口请求异常："+respModel.get("resMsg").toString());
            throw new BusinessException(respModel.get("resMsg").toString());
        }
        return respModel.getJSONObject("attachment");
    }
    
    public static JSONObject sendHttpPostForPICTURE(String url,String params) {
        log.info("PIC接口请求入参："+params);
        String resultStr = HttpClientUtil.sendHttpPostPicture(url,params);
        log.info("PIC接口请求响应："+resultStr);
        if(StringUtils.isBlank(resultStr)){
            log.error("=============>>>>>PIC接口请求异常：接口响应为空");
            throw new BusinessException(BussErrorCode.ERROR_CODE_0102.getErrorcode(),BussErrorCode.ERROR_CODE_0102.getErrordesc());
        }
        JSONObject respModel = JSONObject.fromObject(resultStr);
        if(!Constants.RESP_CODE_SUCCESS.equals(respModel.get("resCode")) && !Constants.RESP_CODE_EXCEPTION.equals(respModel.get("resCode"))){
            log.error("=============>>>>>PIC接口请求异常："+respModel.get("resMsg").toString());
            throw new BusinessException(BussErrorCode.ERROR_CODE_0102.getErrorcode(),BussErrorCode.ERROR_CODE_0102.getErrordesc());
        }
        if(Constants.RESP_CODE_EXCEPTION.equals(respModel.get("resCode"))){
            log.info("=============>>>>>PIC接口请求异常："+respModel.get("resMsg").toString());
            throw new BusinessException(respModel.get("resMsg").toString());
        }
        return respModel.getJSONObject("attachment");
    }
    /**
     * 征审获取合同签名结果
     * @TODO
     * @param map
     * @return
     * JSONObject
     * @author changj@yuminsoft.com
     * @date2018年3月6日
     */
    public static String sendHttpPost4GetSignContractStatus( String params) {
	   log.info("征审获取合同签名结果接口请求入参："+params);
       String resultStr = HttpClientUtil.sendHttpPost4Zs(Constants.ZS_URL_GetSignContractStatus,params);
       log.info("征审获取合同签名结果接口响应："+resultStr);
       if(StringUtils.isBlank(resultStr)){
           log.error("=============>>>>>征审获取合同签名结果接口请求异常：接口响应为空");
           throw new BusinessException(BussErrorCode.ERROR_CODE_0102.getErrorcode(),BussErrorCode.ERROR_CODE_0102.getErrordesc());
       }
       JSONObject respModel = JSONObject.fromObject(resultStr);
       if(!Constants.RESP_CODE_SUCCESS.equals(respModel.get("resCode"))){
           log.info("=============>>>>>征审获取合同签名结果请求异常,异常状态码:"+respModel.get("resCode")+",异常内容:"+respModel.get("resMsg").toString());
           return null;
       }
       return respModel.getString("attachment");
    }
    /**
     * 征审post请求
     * @TODO
     * @param httpUrl
     * @param params
     * @return
     * String
     * @author changj@yuminsoft.com
     * @date2018年3月6日
     */
    public static String sendHttpPost4Zs(String httpUrl,String params) {
        HttpPost httpPost = new HttpPost(applicationBean.getUrlZs()+httpUrl);// 创建httpPost
        try {
            // 设置参数
            if (params != null && params.trim().length() > 0) {
                StringEntity stringEntity = new StringEntity(params, ContentType.APPLICATION_JSON);
//                stringEntity.setContentType(CONTENT_TYPE_FORM_URL);
                httpPost.setEntity(stringEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info(MessageFormat.format("请求url: {0}；入参: {1}", applicationBean.getUrlZs()+httpUrl, params));
        return sendHttpPost(httpPost);
    }

	public static String getLcbLogin(){
		log.info("捞财宝登录页地址(手机端):"+applicationBean.getUrlLcbLogin());
		return applicationBean.getUrlLcbLogin();
	}
	public static String getLcbLogin4Pc(){
		log.info("捞财宝登录页地址(PC端):"+applicationBean.getUrlLcbLoginPc());
		return applicationBean.getUrlLcbLoginPc();
	}
	/**
	 * 借新还旧贷款信息
	 * @TODO
	 * @param url
	 * @param params
	 * @return
	 * JSONObject
	 * @author changj@yuminsoft.com
	 * @date2018年4月9日
	 */
    public static JSONObject sendHttpPostForCreditJxhjLoanInfo(String url,String params) {
        log.info("信贷端接口请求入参："+params);
        String resultStr = HttpClientUtil.sendHttpPost(url,params);
        log.info("信贷端接口请求响应："+resultStr);
        if(StringUtils.isBlank(resultStr)){
            log.error("=============>>>>>信贷端接口请求异常：接口响应为空");
            throw new BusinessException(BussErrorCode.ERROR_CODE_0102.getErrorcode(),BussErrorCode.ERROR_CODE_0102.getErrordesc());
        }
        JSONObject respModel = JSONObject.fromObject(resultStr);
        if(!Constants.RESP_CODE_SUCCESS.equals(respModel.get("resCode"))){
            log.info("=============>>>>>信贷端接口请求异常,异常状态码:"+respModel.get("resCode")+",异常内容:"+respModel.get("resMsg").toString());
            return null;
        }
        return respModel.getJSONObject("attachment");
    }
    
    /**
     * 绑卡
     * @TODO
     * @param url
     * @param params
     * @return
     * JSONObject
     * @author changj@yuminsoft.com
     * @date2018年4月13日
     */
    public static JSONObject sendHttpPostForCredit4BindBankCard(String url,String params) {
        log.info("信贷端接口请求入参："+params);
        String resultStr = HttpClientUtil.sendHttpPost(url,params);
        log.info("信贷端接口请求响应："+resultStr);
        if(StringUtils.isBlank(resultStr)){
            log.error("=============>>>>>信贷端接口请求异常：接口响应为空");
            throw new BusinessException(BussErrorCode.ERROR_CODE_0102.getErrorcode(),BussErrorCode.ERROR_CODE_0102.getErrordesc());
        }
        JSONObject respModel = JSONObject.fromObject(resultStr);
        if(!Constants.RESP_CODE_SUCCESS.equals(respModel.get("resCode"))){
            log.info("=============>>>>>信贷端接口请求异常,异常状态码:"+respModel.get("resCode")+",异常内容:"+respModel.get("resMsg").toString());
            throw new BusinessException(respModel.get("resCode").toString(),respModel.get("resMsg").toString());
        }
        return respModel.getJSONObject("attachment");
    }
    
    /**
<<<<<<< HEAD
     * 征信报告获取
     * 
     * */
    public static com.alibaba.fastjson.JSONObject sendHttpGetReport(String password,String randNum,String pgeRZRandNum,String pgeRZDataB) {
        log.info("征信接口请求入参："+"密码"+password+"随机数"+randNum+pgeRZRandNum+pgeRZDataB);
        String resultStr = HttpClientUtil.sendHttpGetport(password,randNum,pgeRZRandNum,pgeRZDataB);
        log.info("征信接口请求响应："+resultStr);
        if(StringUtils.isBlank(resultStr)){
            log.error("=============>>>>>征信接口请求异常：接口响应为空");
            throw new BusinessException(BussErrorCode.ERROR_CODE_0102.getErrorcode(),BussErrorCode.ERROR_CODE_0102.getErrordesc());
        }
        com.alibaba.fastjson.JSONObject respModel =  com.alibaba.fastjson.JSONObject.parseObject(resultStr);
        if(!"0000".equals(respModel.get("reponseCode"))){
            log.info("=============>>>>>征信接口请求异常,异常状态码:"+respModel.get("reponseCode")+",异常内容:央行服务器繁忙，请稍后再试！");
            throw new BusinessException(respModel.get("reponseCode").toString(),"央行服务器繁忙，请稍后再试！");
        }
        return respModel;
    }
    public static String sendHttpGetport(String password,String randNum,String pgeRZRandNum,String pgeRZDataB) {
    	HttpGet httpGet = new HttpGet(applicationBean.getUrlReport()+"?password="+password+"&randNum="+randNum+"&pgeRZRandNum="+pgeRZRandNum+"&pgeRZDataB="+pgeRZDataB);// 创建httpget
        
        log.info(MessageFormat.format("请求url: {0}；入参: {1}", applicationBean.getUrlReport()+"?password="+password+"&randNum="+randNum+"&pgeRZRandNum="+pgeRZRandNum+"&pgeRZDataB="+pgeRZDataB));
        return sendHttpGet(httpGet);
    }
    
    /**
     * 上传征信报告
     * 
     * */
    public static com.alibaba.fastjson.JSONObject sendHttpPostSaveReport(String params) {
        log.info("征信接口请求入参："+params);
        String resultStr = HttpClientUtil.sendHttpPostReport(params);
        log.info("征信接口请求响应："+resultStr);
        if(StringUtils.isBlank(resultStr)){
            log.error("=============>>>>>征信接口请求异常：接口响应为空");
            throw new BusinessException(BussErrorCode.ERROR_CODE_0102.getErrorcode(),BussErrorCode.ERROR_CODE_0102.getErrordesc());
        }
        com.alibaba.fastjson.JSONObject respModel =  com.alibaba.fastjson.JSONObject.parseObject(resultStr);
        if(!Constants.RESP_CODE_SUCCESS.equals(respModel.get("resCode"))){
            log.info("=============>>>>>征信接口请求异常,异常状态码:"+respModel.get("resCode")+",异常内容:"+respModel.get("resMsg").toString());
            throw new BusinessException(respModel.get("resCode").toString(),respModel.get("resMsg").toString());
        }
        return respModel;
    }
    public static String sendHttpPostReport(String params) {
    	HttpPost httpPost = new HttpPost(applicationBean.getUrlUploadReport()+Constants.UPLOAD_CREDIT_REPORT);// 创建httpPost
        try {
            // 设置参数
            if (params != null && params.trim().length() > 0) {
                StringEntity stringEntity = new StringEntity(params, ContentType.APPLICATION_JSON);
//                stringEntity.setContentType(CONTENT_TYPE_FORM_URL);
                httpPost.setEntity(stringEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info(MessageFormat.format("请求url: {0}；入参: {1}", applicationBean.getUrlUploadReport()+Constants.UPLOAD_CREDIT_REPORT, params));
        return sendHttpPost(httpPost);
    }
    
    /**
     * 查询征信报告
     * 
     * */
    public static com.alibaba.fastjson.JSONObject sendHttpPostSelectReport(String params) {
        log.info("征信接口请求入参："+params);
        String resultStr = HttpClientUtil.sendHttpPostSeltReport(params);
        log.info("征信接口请求响应："+resultStr);
        if(StringUtils.isBlank(resultStr)){
            log.error("=============>>>>>征信接口请求异常：接口响应为空");
            throw new BusinessException(BussErrorCode.ERROR_CODE_0102.getErrorcode(),BussErrorCode.ERROR_CODE_0102.getErrordesc());
        }
        com.alibaba.fastjson.JSONObject respModel =  com.alibaba.fastjson.JSONObject.parseObject(resultStr);
        if(!Constants.RESP_CODE_SUCCESS.equals(respModel.get("resCode"))){
            log.info("=============>>>>>征信接口请求异常,异常状态码:"+respModel.get("resCode")+",异常内容:"+respModel.get("resMsg").toString());
            throw new BusinessException(respModel.get("resCode").toString(),respModel.get("resMsg").toString());
        }
        return respModel;
    }
    public static String sendHttpPostSeltReport(String params) {
    	HttpPost httpPost = new HttpPost(applicationBean.getUrlUploadReport()+Constants.SELECT_CREDIT_REPORT);// 创建httpPost
        try {
            // 设置参数
            if (params != null && params.trim().length() > 0) {
                StringEntity stringEntity = new StringEntity(params, ContentType.APPLICATION_JSON);
//                stringEntity.setContentType(CONTENT_TYPE_FORM_URL);
                httpPost.setEntity(stringEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info(MessageFormat.format("请求url: {0}；入参: {1}", applicationBean.getUrlUploadReport()+Constants.SELECT_CREDIT_REPORT, params));
        return sendHttpPost(httpPost);
    }
    /**
     * 费率试算请求核心
     * 
     * */
    public static com.alibaba.fastjson.JSONObject sendHttpPostForCore(String params) {
        log.info("核心接口请求入参："+params);
        String resultStr = HttpClientUtil.sendHttpPostCore(params);
        log.info("核心接口请求响应："+resultStr);
        if(StringUtils.isBlank(resultStr)){
            log.error("=============>>>>>核心接口请求异常：接口响应为空");
            throw new BusinessException(BussErrorCode.ERROR_CODE_0102.getErrorcode(),BussErrorCode.ERROR_CODE_0102.getErrordesc());
        }
        com.alibaba.fastjson.JSONObject respModel =  com.alibaba.fastjson.JSONObject.parseObject(resultStr);
        if(!Constants.RESP_CODE_SUCCESS.equals(respModel.get("resCode"))){
            log.info("=============>>>>>核心接口请求异常,异常状态码:"+respModel.get("resCode")+",异常内容:"+respModel.get("resMsg").toString());
            throw new BusinessException(respModel.get("resCode").toString(),respModel.get("resMsg").toString());
        }
        return respModel.getJSONObject("attachment");
    }
    public static String sendHttpPostCore(String params) {
    	HttpPost httpPost = new HttpPost(applicationBean.getUrlAppCore()+Constants.APPCORE);// 创建httpPost
        try {
            // 设置参数
            if (params != null && params.trim().length() > 0) {
                StringEntity stringEntity = new StringEntity(params, ContentType.APPLICATION_JSON);
//                stringEntity.setContentType(CONTENT_TYPE_FORM_URL);
                httpPost.setEntity(stringEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info(MessageFormat.format("请求url: {0}；入参: {1}", applicationBean.getUrlAppCore()+Constants.APPCORE, params));
        return sendHttpPost(httpPost);
    }
    
    /**
     * 前前网关手机推送消息
     * */
    public static com.alibaba.fastjson.JSONObject sendHttpPostPushMes(String params) {
        log.info("网关接口请求入参："+params);
        String resultStr = HttpClientUtil.sendHttpPostMes(params);
        log.info("网关接口请求响应："+resultStr);
        if(StringUtils.isBlank(resultStr)){
            log.error("=============>>>>>网关接口请求异常：接口响应为空");
            throw new BusinessException(BussErrorCode.ERROR_CODE_0102.getErrorcode(),BussErrorCode.ERROR_CODE_0102.getErrordesc());
        }
        com.alibaba.fastjson.JSONObject respModel =  com.alibaba.fastjson.JSONObject.parseObject(resultStr);
        /*if(!Constants.RESP_CODE_SUCCESS.equals(respModel.get("resCode"))){
            log.info("=============>>>>>网关接口请求异常,异常状态码:"+respModel.get("resCode")+",异常内容:"+respModel.get("resMsg").toString());
            throw new BusinessException(respModel.get("resCode").toString(),respModel.get("resMsg").toString());
        }*/
        return respModel;
    }
    public static String sendHttpPostMes(String params) {
    	HttpPost httpPost = new HttpPost(applicationBean.getMessagePushUrl());// 创建httpPost
        try {
            // 设置参数
            if (params != null && params.trim().length() > 0) {
                StringEntity stringEntity = new StringEntity(params, ContentType.APPLICATION_JSON);
//                stringEntity.setContentType(CONTENT_TYPE_FORM_URL);
                httpPost.setEntity(stringEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info(MessageFormat.format("请求url: {0}；入参: {1}", applicationBean.getMessagePushUrl(), params));
        return sendHttpPost(httpPost);
    }
    
}
