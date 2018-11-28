/*
* Copyright (c) 2015 zendaimoney.com. All Rights Reserved.
*/
package com.ymkj.credit.server.handler.impl.loanOrder;

import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ymkj.credit.api.listener.anno.FunctionId;
import com.ymkj.credit.common.dto.LoanOrderDto;
import com.ymkj.credit.server.handler.loanOrder.ILoanOrderHandler;
import com.ymkj.credit.server.service.LoanOrderService;
import com.ymkj.credit.api.req.Request;
import com.ymkj.credit.api.resp.Response;
import com.ymkj.springside.modules.orm.PageInfo;

/**
 * DictionaryHandlerImpl.java
 *
 * Author: Yangying
 * Date: 2016年11月7日 上午11:29:25
 * Mail: yangy06@zendaimoney.com
 */
@Service
public class LoanOrderHandlerImpl implements ILoanOrderHandler{

	@Autowired
	private LoanOrderService loanOrderService;
	
	@SuppressWarnings("unchecked")
	@Override
	@FunctionId(value = "100001", desc = "借款申请单分页查询")
	public Response getLoanOrderPage(Request request) {
		Map<String,String> map = (Map<String, String>) request.getBody();
		PageInfo<LoanOrderDto> pageInfo = new PageInfo<LoanOrderDto>();
		pageInfo = loanOrderService.getPage(map);
		return Response.success(pageInfo);
	}

	@SuppressWarnings("unchecked")
	@Override
	@FunctionId(value = "100002", desc = "更新借款申请单")
	public Response updateLoanOrder(Request request) {
		Map<String,String> map = (Map<String, String>) request.getBody();
		return loanOrderService.updateLoanOrder(map);
	}
}
