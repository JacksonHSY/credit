package com.ymkj.credit.ftp;

import java.io.File;
import java.io.InputStream;

/**
 * 图片链接池
 * @author YM10156
 *
 */
public interface FTPService {

	/**
	 * 上传. <br/>
	 * @param srcfile
	 *            文件路径
	 * @param fileName
	 *            文件名
	 * @return
	 */
	public boolean upload(String srcfile, String fileName);

	/**
	 * 上传. <br/>
	 * @param srcfile
	 *            文件路径
	 * @param dir
	 *            目录
	 * @param fileName
	 *            文件名
	 * @return
	 */
	public boolean upload(String srcfile, String dir, String fileName);

	/**
	 * 上传. <br/>
	 * @param file
	 *            文件
	 * @param fileName
	 *            文件名
	 * @return
	 */
	public boolean upload(File file, String fileName);

	/**
	 * 上传. <br/>
	 * @param file
	 *            文件
	 * @param dir
	 *            目录
	 * @param fileName
	 *            文件名
	 * @return
	 */
	public boolean upload(File file, String dir, String fileName);

	/**
	 * 上传. <br/>
	 * @param fis
	 *            输入流
	 * @param fileName
	 *            文件名
	 * @return
	 */
	public boolean upload(InputStream fis, String fileName);

	/**
	 * 上传. <br/>
	 *
	 * @param fis
	 *            输入流
	 * @param dir
	 *            目录
	 * @param fileName
	 *            文件名
	 * @return
	 */
	public boolean upload(InputStream fis, String dir, String fileName);
	
	/**
	 * 下载
	 * @Title: download 
	 * @Description: TODO
	 * @return
	 * @return: InputStream
	 */
	public InputStream download(String url);
	
	/**
	 * 删除
	 * @Title: delete 
	 * @Description: TODO
	 * @param url
	 * @return
	 * @return: boolean
	 */
	public boolean delete(String url);
	
	/**
	 * 文件转移
	 * @Title: transfer 
	 * @Description: TODO
	 * @param fromPath
	 * @param targetPath
	 * @return
	 * @return: boolean
	 */
	public boolean transfer(String fromPath,String toPath);
	
	/**
	 * 文件转移并删除源文件
	 * @Title: transferDelete 
	 * @Description: TODO
	 * @param fromPath
	 * @param targetPath
	 * @return
	 * @return: boolean
	 */
	public boolean transferDelete(String fromPath,String toPath);
}
