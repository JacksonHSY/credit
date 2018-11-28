package com.ymkj.credit.api.impl;

import java.lang.reflect.InvocationTargetException;

import lombok.extern.slf4j.Slf4j;

import com.ymkj.credit.api.listener.InstantiationTracingBeanPostListener;
import com.ymkj.springside.modules.exception.BusinessException;
import com.ymkj.credit.api.IDealProcessorService;
import com.ymkj.credit.api.req.Request;
import com.ymkj.credit.api.resp.Response;
import com.ymkj.credit.common.util.PropertiesUtil;

/**
 * 统一服务处理器
 * 
 * @author longjw
 *
 */
@Slf4j
public class DealProcessorServiceImpl implements IDealProcessorService {
	
	/**
	 * 统一接口
	 * 
	 * @param request
	 * @return
	 */
	@Override
	public Response dispatchCommand(Request request) {
		Response response = null;
		try {
			log.info(request.getSequence() + "\t请求报文：" + request.toString());
			try {
				response = InstantiationTracingBeanPostListener.dispatch(request);
			} catch (InvocationTargetException e) {
				Throwable te = e.getTargetException();
				if (te instanceof BusinessException) {
					log.error("调用功能时业务异常: {}", te.getMessage());
					response = new Response(((BusinessException) te).getCode(), te.getMessage());
				} else {
					response = Response.fail("调用功能时系统异常");
					log.error("调用功能时系统异常", te);
				}
			}
		} catch (Exception e) {
			response = Response.fail("系统异常");
			log.error("系统异常", e);
		}
		log.info(request.getSequence() + "\t返回报文：" + printLog(response.toString()));
		return response;
	}
	
	 /**
	   * 日志打印处理，打印前200行，防止返回报文过长情况
	   * 
	   * @param log
	   * @return 
	   */
	  private String printLog(String log){
	    String showLog = PropertiesUtil.getValue("show.all.log");
	    if("false".equals(showLog)){
	      return log.length() > 1000 ? log.substring(0, 1000) + "..." : log;
	    }
	    return log;
	  }
}
