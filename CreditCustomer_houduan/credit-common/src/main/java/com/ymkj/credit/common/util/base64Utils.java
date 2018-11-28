package com.ymkj.credit.common.util;

import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class base64Utils {

	protected static Log logger = LogFactory.getLog(base64Utils.class);
	
public static boolean GenerateImage(String imgStr,String fileName)  
    {//对字节数组字符串进行Base64解码并生成图片  
        if (imgStr == null) //图像数据为空  
            return false;  
        try   
        {  
            //Base64解码   
            byte[] b = Base64.decodeBase64(new String(imgStr).getBytes());  
              
            for(int i=0;i<b.length;++i)  
            {  
                if(b[i]<0)  
                {//调整异常数据  
                    b[i]+=256;  
                }  
            }  
            //生成jpeg图片  
            String imgFilePath = fileName;//新生成的图片  
            OutputStream out = new FileOutputStream(imgFilePath);      
            out.write(b);  
            out.flush();  
            out.close();  
            return true;  
        }   
        catch (Exception e)   
        {  
        	logger.debug("生成图片失败："+e);
            return false;  
        }  
    }  
}  

