package com.ymkj.credit.common.untils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
public class AesUtil {
	// ("数据加密 plainTextData要加密的字符串")
		public static String encryptAES(String plainTextData, String key) throws Exception {
			// 加密数据
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(),"AES"));
			byte[] encryptedData = cipher.doFinal(plainTextData.getBytes("UTF-8"));
			return Base64.encodeBase64String(encryptedData);
		}

		// ("数据解密 encryptedData要解密的字符串")
		public static String decryptAES(String encryptedData, String key) throws Exception {
			// 解密数据
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(), "AES"));
			byte[] decryptedData = cipher.doFinal(Base64.decodeBase64(encryptedData));
			return new String(decryptedData, "UTF-8");
		}

		public static void main(String[] args) throws Exception {
			//http://172.16.230.50:8080/pic-app/file/aps/20170605190005204098/A/5ec2ff590991442482103d7aae4ecd2f.jpg
			//String  test="http://172.16.230.50:8080/pic-app/file/aps/20170605190005204098/A/5ec2ff590991442482103d7aae4ecd2f.jpg";
			//String ss ="RwSo3UJYQVnX40Fe2MvaF1SSeIVbzMWk052 ZA4CDAvP3LOyGejIJo0Z19NykpDTRD3yH6IvXbiJz5hxy7ELg7JjB fSms3qkhi1Gz1XUGlLjDFuyMzmDvnY9sJt01isLSq7qLkX2EpQFkN6AFPxnQ==";
			//String ss = encryptAES(test,"5684785956124875");///xYAmETTB3laqOE7Elu4MEqqwMmncmpK5t9kj92nSv5bvJD1h0Ab8f6nEPkEvPvHmRQi8VBNHqGgNHoWvfERARxeiD3PNKz6Bvibz8DrEJgqTUp+FQe4LiA7PC2kIf+6SoBJ2A7WYps2ycXyJy5ur5mZY9J1Qa/4o6G3iT8jV2Dax9kr80EIHnhkjbO66NXR9qaMpzWxRE5vtuDauqrUCA==
			//System.out.println(ss);                        //8cxyLWxJkDSdT9bE63bhSoVVUrwnTRu819bLr0WhBz97NlPRELLvkaPB2k33KBd8CIAzPX49Jbt5Rh/24kr/mVnEy8W1psPQeWJ0MhonQSDZqlc/yaL+GWSwHg7UtwR9OYEcVJoE+46QvCjNSJHB+Q==
			
			//8cxyLWxJkDSdT9bE63bhSoVVUrwnTRu819bLr0WhBz97NlPRELLvkaPB2k33KBd8CIAzPX49Jbt5Rh/24kr/mVnEy8W1psPQeWJ0MhonQSDZqlc/yaL+GWSwHg7UtwR9OYEcVJoE+46QvCjNSJHB+Q==
			String ss = "8cxyLWxJkDSdT9bE63bhSoVVUrwnTRu819bLr0WhBz97NlPRELLvkaPB2k33KBd8CIAzPX49Jbt5Rh/24kr/mVnEy8W1psPQeWJ0MhonQSDZqlc/yaL+GWSwHg7UtwR9OYEcVJoE+46QvCjNSJHB+Q==";
		    String hh=	ss.replaceAll("+", "2B%");
		    System.out.println(hh);
			String sss = java.net.URLEncoder.encode(ss, "utf-8");
			String UUU = decryptAES(sss,"5684785956124875");
			System.out.println();
			System.out.println(UUU);
		}
}
