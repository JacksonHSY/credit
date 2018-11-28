package com.ymkj.credit.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FTPServiceImpl implements FTPService{
	
	private static final Log log = LogFactory.getLog(FTPServiceImpl.class);

	// 本地字符编码
	private static String LOCAL_CHARSET = "UTF-8";

	// FTP协议里面，规定文件名编码为iso-8859-1
	private static String SERVER_CHARSET = "ISO-8859-1";

	@Value("${ftp.port}")
	private int port;
	@Value("${ftp.url}")
	private String host;
	@Value("${ftp.username}")
	private String username;
	@Value("${ftp.password}")
	private String password;

	private static FTPClientConfigure config;

	// 连接池
	final GenericKeyedObjectPool<FTPClientConfigure, FTPClient> pool = new FTPClientPool(new FTPClientFactory(), new FTPPoolConfig());

	/**
	 * 获取. <br/>
	 *
	 * @author gaojf@yuminsoft.com
	 * @date: 2017年1月23日 上午10:22:42
	 * @return
	 */
	private FTPClient get() {
		try {
			if (null == config)
				config = new FTPClientConfigure(host, port, username, password);
			return pool.borrowObject(config);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 归还. <br/>
	 *
	 * @author gaojf@yuminsoft.com
	 * @date: 2017年1月23日 上午10:22:52
	 * @param obj
	 */
	private void returnObject(FTPClient obj) {
		pool.returnObject(config, obj);
	}

	@Override
	public boolean upload(String srcfile, String fileName) {
		return upload(new File(srcfile), fileName);
	}

	@Override
	public boolean upload(String srcfile, String dir, String fileName) {
		return upload(new File(srcfile), dir, fileName);
	}

	@Override
	public boolean upload(File file, String fileName) {
		return upload(file, null, fileName);
	}

	@Override
	public boolean upload(File file, String dir, String fileName) {
		try {
			return upload(new FileInputStream(file), dir, fileName);
		} catch (FileNotFoundException e) {
			log.error(e);
			throw new RuntimeException("FTP客户端出错！", e);
		}
	}

	@Override
	public boolean upload(InputStream fis, String fileName) {
		return upload(fis, null, fileName);
	}

	@Override
	public boolean upload(InputStream fis, String dir, String fileName) {
		boolean result = false;
		FTPClient client = get();
		if (null == client)
			return result;
		try {
			// 切换至跟目录
			client.changeWorkingDirectory("/");
			// 设置上传目录
			if (StringUtils.isNotBlank(dir)) {
				// 防止中文目录乱码
				dir = new String(dir.getBytes(LOCAL_CHARSET), SERVER_CHARSET);
				// 切换目录
				if (!client.changeWorkingDirectory(dir)) {
					// 切换失败，创建目录
					String[] dirs = dir.replaceAll("\\\\", "/").replaceAll("//", "/").split("/");
					for (String d : dirs) {
						if (StringUtils.isBlank(d))
							continue;
						if (!client.changeWorkingDirectory(d)) {
							if (client.makeDirectory(d)) {
								if (!client.changeWorkingDirectory(d)) {
									log.error("切换目录失败！");
									return result;
								}
							} else {
								log.error("创建目录失败！");
								return result;
							}
						}
					}
				}
			}
			fileName = new String(fileName.getBytes(LOCAL_CHARSET), SERVER_CHARSET);
			result = client.storeFile(fileName, fis);
		} catch (IOException e) {
			log.error(e);
			throw new RuntimeException("FTP客户端出错！", e);
		} finally {
			IOUtils.closeQuietly(fis);
			returnObject(client);
		}
		return result;
	}

	@Override
	public InputStream download(String url) {
		InputStream inStream = null ;
		FTPClient client = get();
		try {
			inStream = client.retrieveFileStream(url);
		} catch (IOException e) {
			log.error(e);
			throw new RuntimeException("FTP客户端出错！", e);
		}finally {
			returnObject(client);
		}
		return inStream;
	}

	@Override
	public boolean delete(String url) {
		boolean flag = false;
		FTPClient client = get();
		try {
			 flag = client.deleteFile(url);
		} catch (IOException e) {
			log.error(e);
			throw new RuntimeException("FTP客户端出错！", e);
		}finally {
			returnObject(client);
		}
		return flag;
	}

	@Override
	public boolean transfer(String fromPath, String toPath) {
		InputStream inStream = download(fromPath);  
		if(inStream != null){
			int lastIndex = toPath.replaceAll("\\\\", "/").replaceAll("//", "/").lastIndexOf("/")+1;
			return upload(inStream, toPath.substring(0, lastIndex), toPath.substring(lastIndex, toPath.length()));
		}
		return false;
	}

	@Override
	public boolean transferDelete(String fromPath, String toPath) {
		InputStream inStream = download(fromPath);
		if(inStream != null){
			int lastIndex = toPath.replaceAll("\\\\", "/").replaceAll("//", "/").lastIndexOf("/")+1;
			boolean flag = upload(inStream, toPath.substring(0, lastIndex), toPath.substring(lastIndex, toPath.length()));
			if(flag){
				delete(fromPath);
				return flag;
			}
		}
		return false;
	}

}
