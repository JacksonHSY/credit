/*
* Copyright (c) 2015 zendaimoney.com. All Rights Reserved.
*/
package com.ymkj.credit.server.handler.impl.dictionary;

import java.util.List;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ymkj.credit.api.listener.anno.FunctionId;
import com.ymkj.credit.common.entity.Dictionary;
import com.ymkj.credit.server.handler.dictionary.IDictionaryHandler;
import com.ymkj.credit.server.service.DictionaryService;
import com.ymkj.credit.api.req.Request;
import com.ymkj.credit.api.resp.Response;

/**
 * DictionaryHandlerImpl.java
 *
 * Author: Yangying
 * Date: 2016年11月7日 上午11:29:25
 * Mail: yangy06@zendaimoney.com
 */
@Service
public class DictionaryHandlerImpl implements IDictionaryHandler{

	@Autowired
	private DictionaryService dictionaryService;
	
	@Override
	@FunctionId(value = "200001", desc = "查询字典数据")
	public Response getListByType(Request request) {
		String type = (String) request.getBody();
		Map<String,String>  dictionarylist = dictionaryService.queryByType(type);
		return Response.success(dictionarylist);
	}
}
