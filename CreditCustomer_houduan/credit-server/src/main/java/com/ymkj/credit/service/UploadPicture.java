package com.ymkj.credit.service;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ymkj.credit.common.entity.BannerList;
import com.ymkj.credit.common.util.PageUtils;
import com.ymkj.credit.ftp.FTPService;
import com.ymkj.credit.mapper.BannerListMapper;
import com.ymkj.credit.web.api.dto.UploadBannerRecord;
import com.ymkj.credit.web.api.model.Result;
import com.ymkj.springside.modules.orm.PageInfo;
/**
 * 
 * @Description：图片上传
 * @ClassName: UploadPicture.java
 * @Author：huangsy
 * @Date：2018/6/4
 */
@Slf4j
@Service
public class UploadPicture {
	
	@Value("${ftp.dir}")
	private String ftpDir;
	@Value("${ftp.view.url}")
	private String ftpViewUrl;
	
	@Autowired
	private BannerListMapper bannerListMapper;
	
	@Autowired
	private FTPService ftp;
	/**
	 * 查询图片信息
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PageInfo<BannerList> findBymap(PageInfo<BannerList> pageInfo){
		
		PageHelper.startPage(pageInfo.getPageNo(), pageInfo.getPageSize());
		BannerList uploadbannerrecord = (BannerList) pageInfo.getQueryParam();
		Page<BannerList> page = (Page)bannerListMapper.findBymap(uploadbannerrecord);
        return PageUtils.convertPage(page);
	}
	
	/**
	 * 新增图片
	 * */
	public Result creatPicture(BannerList banner){
		
		bannerListMapper.insetPicture(banner);
		return Result.success("图片信息保存成功");
	}
	/**
	 * 
	 * 查询序号最大数据
	 * */
	public PageInfo<BannerList> selectNumber(PageInfo<BannerList> pageInfo){
		PageHelper.startPage(pageInfo.getPageNo(), pageInfo.getPageSize());
		BannerList uploadbannerrecord = (BannerList) pageInfo.getQueryParam();
		Page<BannerList> page = (Page)bannerListMapper.selectNumber(uploadbannerrecord);
		 return PageUtils.convertPage(page);
	}
	/**
	 * 更新大于等于当前序号的记录
	 * 
	 * */
	public Result selectByNum(BannerList banner){
		 bannerListMapper.selectByNum(banner);
		 return Result.success("更新成功");
	}
	/**
	 * 查询图片列表
	 * */
	public List<BannerList> selectRecode(BannerList banner){
		
		return bannerListMapper.selectRecode(banner);
	}
	
	/**
	 * 查询图片序号是否已经存在
	 * 
	 * */
	public List<BannerList> selectInNum(BannerList banner){
		return bannerListMapper.selectInNum(banner);
		 
	}
	
	/**
	 * 序号互换
	 * 
	 * */
	public Result updateInNum(BannerList banner){
		bannerListMapper.updateInNum(banner);
		return Result.success("更新成功");
	}
	
	/**
	 * 根据id查询图片信息
	 * */
	public List<BannerList> selectByID(BannerList banner){
		return bannerListMapper.selectByID(banner);
	}
	
	/**
	 * 修改图片
	 * */
	public Result updatePicture(BannerList banner){
		
		bannerListMapper.updateByParam(banner);
		return Result.success("图片信息修改成功");
	}
	
	public String uploadPic(InputStream fis,String name){
		String dir = ftpDir + "/banner/img/";
		String uuidName = UUID.randomUUID().toString();
		uuidName = uuidName + name.substring(name.indexOf("."));
		String viewUrl = ftpViewUrl + dir + uuidName;
		boolean bln = ftp.upload(fis, dir, uuidName);
		if(bln)
			return viewUrl;
		else
			return "";
	}

}
