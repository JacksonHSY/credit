package com.ymkj.credit;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
	
	private static final Logger logger = Logger.getLogger(Main.class);

	public static void main(String[] args) throws Exception {
		new ClassPathXmlApplicationContext("spring/spring-context.xml");
		logger.info("启动完成...");
		
		System.in.read();//按任意键退出
	}
}
