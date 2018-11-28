package com.ymkj.credit.ftp;

import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

public class FTPPoolConfig extends GenericKeyedObjectPoolConfig{
	public FTPPoolConfig() {
		setTestWhileIdle(true);// 发呆过长移除的时候是否test一下
		setTimeBetweenEvictionRunsMillis(1 * 60000L);// -1不启动。默认1min一次
		setMinEvictableIdleTimeMillis(10 * 60000L); // 可发呆的时间,30mins
		setTestOnBorrow(true);
	}
}
