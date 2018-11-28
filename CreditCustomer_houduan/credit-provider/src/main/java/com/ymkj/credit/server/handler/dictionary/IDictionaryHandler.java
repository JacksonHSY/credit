package com.ymkj.credit.server.handler.dictionary;

import com.ymkj.credit.server.handler.FunctionService;
import com.ymkj.credit.api.req.Request;
import com.ymkj.credit.api.resp.Response;

/**
 * 考核相关处理接口
 * 
 * @author liangj
 *
 */
public interface IDictionaryHandler extends FunctionService{
	
	//获取订单状态枚举
	public Response getListByType(Request request);
	
}
