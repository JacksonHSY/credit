package com.ymkj.credit.api;

import com.ymkj.credit.api.req.Request;
import com.ymkj.credit.api.resp.Response;

/**
 * 统一服务处理器
 * 
 * @author longjw
 *
 */
public interface IDealProcessorService {
	
	/**
	 * 统一接口
	 * 
	 * @param request
	 * @return
	 */
	public Response dispatchCommand(Request request);
}
