package com.ymkj.credit.server.handler.loanOrder;

import com.ymkj.credit.server.handler.FunctionService;
import com.ymkj.credit.api.req.Request;
import com.ymkj.credit.api.resp.Response;

/**
 * 借款单处理接口
 * 
 * @author liangj
 *
 */
public interface ILoanOrderHandler extends FunctionService{
	
	//分页查询
	public Response getLoanOrderPage(Request request);
	
	//更新借款申请单
	public Response updateLoanOrder(Request request);
}
