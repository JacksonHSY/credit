/*
* Copyright (c) 2016 zendaimoney.com. All Rights Reserved.
*/
package com.ymkj.credit.api.listener;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.google.common.collect.Maps;
import com.ymkj.credit.api.listener.anno.FunctionId;
import com.ymkj.credit.server.handler.FunctionService;
import com.ymkj.credit.server.utils.ClassUtil;
import com.ymkj.springside.modules.exception.BusinessException;
import com.ymkj.credit.api.req.Request;
import com.ymkj.credit.api.resp.Response;

/**
 * 分发控制类，加载映射关系，实现请求分发
 * 
 * longjw
 */

public class InstantiationTracingBeanPostListener implements ApplicationContextAware {

    private final static Map<String, Object[]> methodCacheMap = Maps.newHashMap();

    @PostConstruct
    public void onApplicationEvent() throws Exception {
    	methodCacheMap.clear();
    	List<String> list = ClassUtil.getClassPathClassNames(ClassUtil.ACTION_ITSM_PACKAGE, true);
    	for (String className : list) {
    		Class<?> clazz = Class.forName(className);
            Method[] declaredMethods = clazz.getDeclaredMethods();
            for (Method method : declaredMethods) {
                FunctionId functionId = method.getAnnotation(FunctionId.class);
                if (functionId != null) {
                    if (methodCacheMap.containsKey(functionId.value())) {
                        throw new BusinessException("重复的功能号: " + functionId.value());
                    }
                    methodCacheMap.put(functionId.value(), new Object[]{applicationContext.getBean(clazz), method});
                }
            }
        }
    }
    
    /**
     * 分发主入口
     * @param functionId
     * @param reqMain
     * @return
     * @throws Exception
     */
    
    public static Response dispatch(Request request) throws Exception {
    	String functionId = request.getFunctionId();
    	 Object[] objs = methodCacheMap.get(functionId);
    	 // 验证 功能号 是否合法
    	 if (null == objs || objs.length != 2) {
    		 throw new BusinessException("功能号：" + functionId + " 不存在");
    	 }
    	 FunctionService functionService = (FunctionService)objs[0];
    	 Method method = (Method)objs[1];
    	 return (Response)method.invoke(functionService, request);
    }

    private ApplicationContext applicationContext;
    
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}
}