package com.ymkj.credit.common.untils;

import javax.annotation.PostConstruct;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.zdmoney.credit.util.ConfigUtil;

@Component
public class SpringContextHelper implements ApplicationContextAware {
	
	public static ApplicationContext context;
	
	@Value("${encrypt.private.key}")
	private String privateKey;
	  
	@PostConstruct
	public void setPrivateKey(){
	  ConfigUtil.privateKey = privateKey;
	}
	
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		// 在加载Spring时自动获得context
		SpringContextHelper.context = context;
	}

	public static Object getBean(String beanName) {
		return context.getBean(beanName);
	}

	public static <T> T getBean(Class<T> clz) throws BeansException {
        T result = (T) context.getBean(clz);
        return result;
    }

}
