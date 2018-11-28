package com.ymkj.credit.common.untils;

import java.net.URLEncoder;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Workbook;
import lombok.extern.log4j.Log4j;

/**
 * @Description：文件工具类
 * @ClassName: FileUtil.java
 * @Author：tianx
 * @Date：2017年8月7日
 * -----------------变更历史-----------------
 * 如：who  2017年8月7日  修改xx功能
 */
@Log4j
public class FileUtil {
	
	/**
	 * 功能描述：excel文件下载
	 * 输入参数：
	 * @param fileName
	 * @param work
	 * @param resp
	 * @throws Exception
	 * 返回类型：void
	 * 创建人：tianx
	 * 日期：2017年8月7日
	 */
	public static void downloadXls(String fileName,Workbook work,HttpServletResponse resp) throws Exception{
		ServletOutputStream outputStream = null;
		try {
			outputStream = resp.getOutputStream();
			resp.setContentType("application/ms-excel;charset=UTF-8");  
            resp.setHeader("Content-Disposition", "attachment;filename="  
                    .concat(String.valueOf(URLEncoder.encode(fileName + ".xls", "UTF-8"))));
            work.write(outputStream); 
		} catch (Exception e) {
			log.error("excel下载失败,文件流错误",e);
			throw e;
		}finally{
			 outputStream.close();
		}
	}
	
	
}
