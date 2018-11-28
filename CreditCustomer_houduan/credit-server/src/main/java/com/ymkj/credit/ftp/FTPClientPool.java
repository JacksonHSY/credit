package com.ymkj.credit.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

public class FTPClientPool extends GenericKeyedObjectPool<FTPClientConfigure, FTPClient>{

	public FTPClientPool(KeyedPooledObjectFactory<FTPClientConfigure, FTPClient> factory, GenericKeyedObjectPoolConfig config) {
		super(factory, config);
	}

}
