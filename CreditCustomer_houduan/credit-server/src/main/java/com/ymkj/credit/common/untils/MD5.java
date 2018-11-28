package com.ymkj.credit.common.untils;

import java.security.MessageDigest;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.bridge.Message;
public class MD5 {
    public static final String KEY_MD5 = "MD5";
    
	private final static String[] hexDigits = {
      "0", "1", "2", "3", "4", "5", "6", "7", 
      "8", "9", "a", "b", "c", "d", "e", "f"}; 

	public static String byteArrayToHexString(byte[] b){
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++){
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	private static String byteToHexString(byte b){
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	public static String MD5Encode(String origin){
		String resultString = null;
			try {
				resultString=new String(origin);
				MessageDigest md = MessageDigest.getInstance("MD5");
				resultString=byteArrayToHexString(md.digest(resultString.getBytes("utf-8")));
			}catch (Exception ex){}
			return resultString;
		}

	public static void main(String[] args) 
	{
		String key = "TyPay$YY#655$club#miy2(t8(";
/*		<?xml version="1.0" encoding="UTF-8"?>
		<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
		<soap:Body>
		<JFRollBack xmlns="http://tempuri.org/">
		<strRequestMsgType>JFRollBackRequest</strRequestMsgType>
		<strPartner>16749</strPartner>
		<strOutOrderId>2011050900003975</strOutOrderId>
		<strPhoneNo>18910229378</strPhoneNo>
		<JFNo>1</JFNo>
		<ProductName>card</ProductName>
		<strRequestIp>132.97.117.122</strRequestIp>
		<strRemark>�쳣����</strRemark>
		<strUserName>tycpkj</strUserName>
		<strPassWord>123456</strPassWord>
		<strSign>3C7A86F43E060E250886AA4C72AE4EA5</strSign>
		</JFRollBack>
		</soap:Body>
		</soap:Envelope>*/

//		87AF1010B0EDEA28159812C3E96F1121
		//7385E1B2448709F21B1448F18119E3D7
		String newSign = MD5.MD5Encode(
				"JFRollBackRequest" + "16749" + "2011050900003975" + "18910229378"
						+ "1" + "card" + "132.97.117.122"
						+ "�쳣����" + "tycpkj" + "123456" + key)
		.toUpperCase();
		
		String newSign1 = MD5.MD5Encode(
				"123456");
		
		System.out.println(newSign);
		System.out.println(newSign1);
	}
	
	public static String MD5Encode(String origin,String coding){
		String resultString = null;
			try {
				resultString=new String(origin);
				MessageDigest md = MessageDigest.getInstance("MD5");
				resultString=byteArrayToHexString(md.digest(resultString.getBytes(coding)));
			}catch (Exception ex){}
			return resultString;
		}
    /**
     * md5加密  
     * @param str
     * @return
     * @throws NoSuchAlgorithmException 
     */
    public static String md5(String str, String charsetname) throws Exception {  
        return encrypt(str, KEY_MD5, charsetname);  
    }
    /** 
     * md5或者sha-1加密 
     *  
     * @param str 
     *            要加密的内容 
     * @param algorithmName 
     *            加密算法名称：md5或者sha-1，不区分大小写 
     * @return 
     * @throws NoSuchAlgorithmException 
     */  
    private static String encrypt(String str, String algorithmName, String charsetname) throws Exception {  
        MessageDigest m = MessageDigest.getInstance(algorithmName);  
        m.update(str.getBytes(charsetname));  
        byte s[] = m.digest();  
        return Hex.encodeHexString(s);  
    }
    /**
     * 生成签名
     * <p>
     * a.对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）后，
     * 使用 URL 键值对的格式（即 key1=value1&key2=value2…）拼接成字符串 string1,
     * 注意：值为空的参数不参不签名
     * b.在 string1 最后拼接上 key=KEY(密钥)得到 stringSignTemp
     * 字符串，并对stringSignTemp 进行 md5 运算，再将得到的字符串所有字符转换为大写，得到 sign值
     * </p>
     *
     * @param reqParams
     * @return
     */
    public static String getSign(TreeMap<String, String> reqParams,String creditReqKey) {
        StringBuilder sbl = new StringBuilder();
        Iterator<String> itr = reqParams.keySet().iterator();
        String key;
        String value;
        while (itr.hasNext()) {
            key = itr.next();
            value = reqParams.get(key);
            if (StringUtils.isNotBlank(value)
                    && !StringUtils.equals(key, "sign")
                    && !StringUtils.equals(key, "key")) {
                sbl.append(key).append("=").append(value).append("&");
            }
        }
        String stringSignTemp = sbl.append("key=").append(creditReqKey).toString();
        return MD5.MD5Encode(stringSignTemp, "utf-8").toUpperCase();
    }
}
