package com.ymkj.credit.ftp;

import java.io.IOException;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class FTPClientFactory implements KeyedPooledObjectFactory<FTPClientConfigure, FTPClient>{

	private static final Log log = LogFactory.getLog(FTPClientFactory.class);
	// 字符集
	private static String ENCODING = "GBK";
	// 端口号
	private static final int PORT = 21;

	// 创建一个新对象;当对象池中的对象个数不足时,将会使用此方法来"输出"一个新的"对象",并交付给对象池管理
	@Override
	public PooledObject<FTPClient> makeObject(FTPClientConfigure key) throws Exception {
		FTPClient c = new FTPClient();
		c.setBufferSize(1024 * 2);
		// 3 秒超时
		c.setConnectTimeout(3000);
		c.setDataTimeout(30 * 1000);
		FTPClientConfig ftpClientConfig = new FTPClientConfig();
		ftpClientConfig.setServerTimeZoneId(TimeZone.getDefault().getID());
		c.configure(ftpClientConfig);
		long connectStart = System.currentTimeMillis();
		// 连接
		c.connect(key.getHost(), null != key.getPort() ? key.getPort() : PORT);
		long connectUsed = System.currentTimeMillis() - connectStart;
		log.info("connecting " + key.toString() + " used time :" + connectUsed + " ms.");
		// 测试连接是否可用
		int reply = c.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			c.disconnect();
			log.error("ftp connect fail," + key.toString());
			return null;
		}
		// 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）.
		if (FTPReply.isPositiveCompletion(c.sendCommand("OPTS UTF8", "ON"))) {
			ENCODING = "UTF-8";
		}
		// 字符集
		c.setControlEncoding(ENCODING);
		// 设置被动模式
		c.enterLocalPassiveMode();
		long loginStart = System.currentTimeMillis();
		boolean loginSuccess = c.login(key.getUsername(), key.getPassword());
		long loginUsed = System.currentTimeMillis() - loginStart;
		log.info("login " + key.toString() + " used time :" + loginUsed + " ms.");
		if (loginSuccess) {
			log.debug("FTP login success : " + key.toString());
		} else {
			log.error("FTP login fail : " + key.toString());
			return null;
		}
		reply = c.getReplyCode();
		// 文件类型,默认是ASCII
		c.setFileType(FTPClient.BINARY_FILE_TYPE);
		reply = c.getReplyCode();

		return new DefaultPooledObject<FTPClient>(c);
	}

	// 销毁对象,如果对象池中检测到某个"对象"idle的时间超时,或者操作者向对象池"归还对象"时检测到"对象"已经无效,那么此时将会导致"对象销毁";
	// "销毁对象"的操作设计相差甚远,但是必须明确:当调用此方法时,"对象"的生命周期必须结束.如果object是线程,那么此时线程必须退出;
	// 如果object是socket操作,那么此时socket必须关闭;如果object是文件流操作,那么此时"数据flush"且正常关闭.
	@Override
	public void destroyObject(FTPClientConfigure key, PooledObject<FTPClient> p) throws Exception {
		FTPClient c = p.getObject();
		if (c == null)
			return;
		if (c.isConnected()) {
			c.logout();
			c.disconnect();
		}
	}

	// 检测对象是否"有效";Pool中不能保存无效的"对象",因此"后台检测线程"会周期性的检测Pool中"对象"的有效性,如果对象无效则会导致此对象从Pool中移除,并destroy;
	// 此外在调用者从Pool获取一个"对象"时,也会检测"对象"的有效性,确保不能讲"无效"的对象输出给调用者;
	// 当调用者使用完毕将"对象归还"到Pool时,仍然会检测对象的有效性.所谓有效性,就是此"对象"的状态是否符合预期,是否可以对调用者直接使用;
	// 如果对象是Socket,那么它的有效性就是socket的通道是否畅通/阻塞是否超时等.
	@Override
	public boolean validateObject(FTPClientConfigure key, PooledObject<FTPClient> p) {
		FTPClient c = p.getObject();

		boolean isConnected = c.isConnected();
		boolean isAvailable = c.isAvailable();

		if (!isConnected || !isAvailable) {
			log.info("连接失效 : " + key + ", 连接状态: " + isConnected + ", 可用状态 : " + isAvailable);
			return false;
		}

		try {
			// 心跳,发送一个NOOP命令到FTP服务器。防止服务器超时。
			return c.sendNoOp();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	// "激活"对象,当Pool中决定移除一个对象交付给调用者时额外的"激活"操作,比如可以在activateObject方法中"重置"参数列表让调用者使用时感觉像一个"新创建"的对象一样;如果object是一个线程,可以在"激活"操作中重置"线程中断标记",或者让线程从阻塞中唤醒等;
	// 如果object是一个socket,那么可以在"激活操作"中刷新通道,或者对socket进行链接重建(假如socket意外关闭)等.
	@Override
	public void activateObject(FTPClientConfigure arg0, PooledObject<FTPClient> arg1) throws Exception {
	}

	// "钝化"对象,当调用者"归还对象"时,Pool将会"钝化对象"；钝化的言外之意,就是此"对象"暂且需要"休息"一下.
	// 如果object是一个socket,那么可以passivateObject中清除buffer,将socket阻塞;如果object是一个线程,可以在"钝化"操作中将线程sleep或者将线程中的某个对象wait.需要注意的时,activateObject和passivateObject两个方法需要对应,避免死锁或者"对象"状态的混乱.
	@Override
	public void passivateObject(FTPClientConfigure arg0, PooledObject<FTPClient> arg1) throws Exception {
	}

}
