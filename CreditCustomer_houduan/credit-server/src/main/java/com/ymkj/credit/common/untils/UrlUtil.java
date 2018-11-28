package com.ymkj.credit.common.untils;


/**
 * Copyright : www.easipay.net , 2011-2012
 * Project : PEPP
 * $Id$
 * $Revision$
 * Last Changed by $Author$ at $Date$
 * $URL$
 * 
 * Change Log
 * Author      Change Date    Comments
 *-------------------------------------------------------------
 * zhangyl     2011-11-18        Initailized
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UrlPathHelper;

/**
 * url的工具类
 * 
 * @author zhangyl
 */
public abstract class UrlUtil {

	private static final Logger logger = Logger.getLogger(UrlUtil.class);

	/**
	 * post xml给指定的url
	 * 
	 * @param urlStr
	 *            url
	 * @param xmlString
	 *            xml
	 * @return 返回的xml信息
	 */
	public static void postXmlToUrl(String urlStr, String xmlString) {
		byte[] xmlData = xmlString.getBytes();
		DataOutputStream printout = null;
		try {
			// 获得到位置服务的链接
			URL url = new URL(urlStr);
			URLConnection urlCon = url.openConnection();
			urlCon.setDoOutput(true);
			urlCon.setDoInput(true);
			urlCon.setUseCaches(false);

			// 将xml数据发送到位置服务
			urlCon.setRequestProperty("Content-Type", "text/xml");
			urlCon.setRequestProperty("Content-length", String.valueOf(xmlData.length));
			printout = new DataOutputStream(urlCon.getOutputStream());
			printout.write(xmlData);
			printout.flush();
		} catch (Exception e) {
			logger.error("post url error！ \n\r " + "url=" + urlStr + " param=" + xmlString, e);
		} finally {
			try {
				printout.close();
			} catch (Exception ex) {
				logger.error("close Stream error", ex);
			}
		}
	}

	public static String postToUrl(String urlStr, String data) {
		try {
			URL url = new URL(urlStr);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoInput(true);
			con.setDoOutput(true);
			OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
			osw.write(data);
			osw.flush();
			osw.close();
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
			StringBuffer response = new StringBuffer();
			String temp;
			while ((temp = br.readLine()) != null) {
				response.append(temp);
				response.append("\n");
			}
			return response.toString();
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
			return null;
		}
	}

	public static void main(String[] args) throws Exception {
		test2();

	}

	public static void test1() {
		String s = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "  <bocb2e version=\"110\" locale=\"zh_CN\">"
				+ "  <head>" + "   <termid>E124074246244</termid>" + "   <trnid>04051003129</trnid>"
				+ "   <custid>25378061</custid>" + "   <cusopr>25959684</cusopr>" + "   <trncod>b2e0001</trncod>"
				+ " </head>" + "  <trans>" + "  <trn-b2e0001-rq>" + " <b2e0001-rq>"
				+ " <custdt>20130918013013</custdt>" + "   <oprpwd>mYmt7dsM</oprpwd>" + "</b2e0001-rq>"
				+ "  </trn-b2e0001-rq>" + "</trans>" + "</bocb2e>";

		System.out.println(s);
		String urlStr = "http://127.0.0.1:8280/B2EC/E2BServlet";
		String returnStr = postToUrl(urlStr, s);
		System.out.println(returnStr);

	}

	public static void test2() {
		String s = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "  <bocb2e version=\"110\" locale=\"zh_CN\">"
				+ "  <head>" + "   <termid>E124074246244</termid>" + "   <trnid>04051003129</trnid>"
				+ "   <custid>25378061</custid>" + "   <cusopr>25917199</cusopr>" + "   <trncod>b2e0001</trncod>"
				+ " </head>" + "  <trans>" + "  <trn-b2e0001-rq>" + " <b2e0001-rq>"
				+ " <custdt>20130918013013</custdt>" + "   <oprpwd>D2BEYYmy</oprpwd>" + "</b2e0001-rq>"
				+ "  </trn-b2e0001-rq>" + "</trans>" + "</bocb2e>";

		System.out.println(s);
		String urlStr = "http://127.0.0.1:8280/B2EC/E2BServlet";
		String returnStr = postToUrl(urlStr, s);
		System.out.println(returnStr);

	}

	/**
	 * post消息给指定的url
	 * 
	 * @param url
	 *            url
	 * @param param
	 *            参数
	 */
	public static void postUrl(String url, String param) {
		PrintWriter out = null;
		try {
			URL httpurl = new URL(url);
			HttpURLConnection httpConn = (HttpURLConnection) httpurl.openConnection();
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			out = new PrintWriter(httpConn.getOutputStream());
			out.print(param);
			out.flush();
		} catch (Exception e) {
			System.out.println("post url error！ \n\r " + "url=" + url + " param=" + param);
			System.out.println(e);
			logger.error("post url error！ \n\r " + "url=" + url + " param=" + param, e);
		} finally {
			try {
				out.close();
			} catch (Exception e) {
				logger.error("close Stream error", e);
			}
		}
	}



	/**
	 * 通过response返回通知，用于后台响应
	 * 
	 * @param response
	 * @param param
	 * @return
	 */
	public static void postResponse(HttpServletResponse response, String param) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.print(param);
			out.flush();
		} catch (Exception e) {
			logger.error("postResponse error！ \n\r ", e);
		} finally {
			try {
				out.close();
			} catch (Exception e) {
				logger.error("close postResponse Stream error", e);
			}
		}
	}

	/**
	 * 向HTTPS地址发送POST请求
	 * 
	 * @param reqURL
	 *            请求地址
	 * @param params
	 *            请求参数
	 * @return 响应内容
	 */
	@SuppressWarnings("finally")
	public static String sendSSLPostRequest(String reqURL, Map<String, String> params) {
		long responseLength = 0; // 响应长度
		String responseContent = null; // 响应内容
		org.apache.http.client.HttpClient httpClient = new DefaultHttpClient(); // 创建默认的httpClient实例
		X509TrustManager xtm = new X509TrustManager() { // 创建TrustManager
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		// 这个好像是HOST验证
		X509HostnameVerifier hostnameVerifier = new X509HostnameVerifier() {
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}

			public void verify(String arg0, SSLSocket arg1) throws IOException {
			}

			public void verify(String arg0, String[] arg1, String[] arg2) throws SSLException {
			}

			public void verify(String arg0, X509Certificate arg1) throws SSLException {
			}
		};
		try {
			// TLS1.0与SSL3.0基本上没有太大的差别，可粗略理解为TLS是SSL的继承者，但它们使用的是相同的SSLContext
			SSLContext ctx = SSLContext.getInstance("TLS");

			// 使用TrustManager来初始化该上下文，TrustManager只是被SSL的Socket所使用
			ctx.init(null, new TrustManager[] { xtm }, null);

			// 创建SSLSocketFactory
			SSLSocketFactory socketFactory = new SSLSocketFactory(ctx);
			socketFactory.setHostnameVerifier(hostnameVerifier);

			// 通过SchemeRegistry将SSLSocketFactory注册到我们的HttpClient上
			httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));

			HttpPost httpPost = new HttpPost(reqURL); // 创建HttpPost
			List<org.apache.http.NameValuePair> formParams = new ArrayList<org.apache.http.NameValuePair>(); // 构建POST请求的表单参数
			for (Map.Entry<String, String> entry : params.entrySet()) {
				formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			httpPost.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8"));

			HttpResponse response = httpClient.execute(httpPost); // 执行POST请求
			HttpEntity entity = response.getEntity(); // 获取响应实体

			if (null != entity) {
				responseLength = entity.getContentLength();
				responseContent = EntityUtils.toString(entity, "UTF-8");
				EntityUtils.consume(entity); // Consume response content
			}
			System.out.println("请求地址: " + httpPost.getURI());
			System.out.println("响应状态: " + response.getStatusLine());
			System.out.println("响应长度: " + responseLength);
			System.out.println("响应内容: " + responseContent);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown(); // 关闭连接,释放资源
			return responseContent;
		}
	}

	/**
	 * 提交没有参数的post
	 * 
	 * @param reqURL
	 * @param params
	 *            直接提交这个字符串
	 * @return
	 */
	@SuppressWarnings("finally")
	public static String httpsRequestNoParams(String reqURL, String params) {
		long responseLength = 0; // 响应长度
		String responseContent = null; // 响应内容
		org.apache.http.client.HttpClient httpClient = new DefaultHttpClient(); // 创建默认的httpClient实例
		X509TrustManager xtm = new X509TrustManager() { // 创建TrustManager
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		// 这个好像是HOST验证
		X509HostnameVerifier hostnameVerifier = new X509HostnameVerifier() {
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}

			public void verify(String arg0, SSLSocket arg1) throws IOException {
			}

			public void verify(String arg0, String[] arg1, String[] arg2) throws SSLException {
			}

			public void verify(String arg0, X509Certificate arg1) throws SSLException {
			}
		};

		try {
			// TLS1.0与SSL3.0基本上没有太大的差别，可粗略理解为TLS是SSL的继承者，但它们使用的是相同的SSLContext
			SSLContext ctx = SSLContext.getInstance("TLS");

			// 使用TrustManager来初始化该上下文，TrustManager只是被SSL的Socket所使用
			ctx.init(null, new TrustManager[] { xtm }, null);

			// 创建SSLSocketFactory
			SSLSocketFactory socketFactory = new SSLSocketFactory(ctx);
			socketFactory.setHostnameVerifier(hostnameVerifier);

			// 通过SchemeRegistry将SSLSocketFactory注册到我们的HttpClient上
			httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));

			HttpPost httpPost = new HttpPost(reqURL); // 创建HttpPost
			httpPost.setEntity(new StringEntity(params, null, "utf-8"));
			HttpResponse response = httpClient.execute(httpPost); // 执行POST请求
			HttpEntity entity = response.getEntity(); // 获取响应实体

			if (null != entity) {
				responseLength = entity.getContentLength();
				responseContent = EntityUtils.toString(entity, "UTF-8");
				EntityUtils.consume(entity); // Consume response content
			}
			System.out.println("请求地址: " + httpPost.getURI());
			System.out.println("响应状态: " + response.getStatusLine());
			System.out.println("响应长度: " + responseLength);
			System.out.println("响应内容: " + responseContent);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown(); // 关闭连接,释放资源
			return responseContent;
		}

	}

	/**
	 * @author 00236633 获取uri最后一部分
	 * @return
	 */
	public static String getUriLastComponent() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		UrlPathHelper urlPathHelper = new UrlPathHelper();
		String[] path = urlPathHelper.getRequestUri(request).split("/");
		if (path.length > 0) {
			return path[path.length - 1];
		}
		return null;
	}

	/**
	 * 获取请求的uri
	 * 
	 * @return
	 */
	public static String getUri() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		UrlPathHelper urlPathHelper = new UrlPathHelper();
		return urlPathHelper.getRequestUri(request);
	}

	public static String paramDecoder(String param) {
		try {
			return URLDecoder.decode(param, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			logger.error(ex, ex);
		}
		return "";
	}

	public static String paramEncoder(String param) {
		try {
			return URLEncoder.encode(param, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error(e, e);
		}
		return "";
	}

}
