package com.ymkj.credit.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//import com.ymkj.credit.common.constants.Constants;
//import com.ymkj.credit.api.IDealProcessorService;
//import com.ymkj.credit.api.req.Request;
//import com.ymkj.credit.api.resp.Response;

@Service
@Slf4j
public class ServerAdaptor {

//	@Autowired
//	private IDealProcessorService dealProcessorService;
//
//	public Response send(Request request){
//		Response response = null;
//		try {
//			request.setSystem(Constants.SYS_SERVER);
//			response = dealProcessorService.dispatchCommand(request);
//	} catch (Exception e) {
//			log.error("服务调用异常", e);
//		response = new Response("-1", "服务异常");
//		}
//		return response;
//	}
}
