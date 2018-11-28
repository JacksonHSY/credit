package com.ymkj.credit.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import ch.qos.logback.classic.Logger;

import com.alibaba.dubbo.container.page.Menu;
import com.ymkj.credit.common.constants.Constants;
import com.ymkj.credit.common.entity.BankCards;
import com.ymkj.credit.common.entity.BannerList;
import com.ymkj.credit.common.entity.BannerPicture;
import com.ymkj.credit.common.entity.Customer;
import com.ymkj.credit.common.entity.SmsMessageRecord;
import com.ymkj.credit.common.vo.ResultVo;
import com.ymkj.credit.service.CustomerService;
import com.ymkj.credit.service.SmsMessageRecordService;
import com.ymkj.credit.service.SmsService;
import com.ymkj.credit.service.UploadPicture;
import com.ymkj.credit.web.api.anno.PageSolver;
import com.ymkj.credit.web.api.dto.UploadBannerRecord;
import com.ymkj.credit.web.api.model.Result;
import com.ymkj.springside.modules.orm.PageInfo;
import com.ymkj.sso.client.ShiroUtils;

@RequestMapping(value = "/manage")
@Controller
@Slf4j
public class BannerListController {
	
	@Autowired
	private UploadPicture uploadpicture;
	
	@RequestMapping(value = "/bannerList")
	public String index(Model model,HttpSession httpSession){
		return "banner/bannerList";
		
	}
	
	
	/**
	 * 查询图片信息
	 */
	@RequestMapping("/banner/bannerPage")
	@ResponseBody 
	public ResultVo selectBanner(
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "rows", defaultValue = "10") int rows,
			@RequestParam(value = "sort", defaultValue = "id") String sort,
			@RequestParam(value = "order", defaultValue = "desc") String order,
			BannerList uploadbannerrecord,HttpServletRequest request){
		    PageInfo<BannerList> requestbody = new PageInfo<BannerList>();
		    requestbody.setPageNo(page);
			requestbody.setPageSize(rows);
			requestbody.setQueryParam(uploadbannerrecord);
			requestbody = uploadpicture.findBymap(requestbody);
			return ResultVo.returnPage(requestbody);
	}
	/**
	 * 查询最大序号图片
	 * */
	@RequestMapping("/banner/selectNumber")
	@ResponseBody 
	public ResultVo selectNumber(@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "rows", defaultValue = "10") int rows,
			@RequestParam(value = "sort", defaultValue = "id") String sort,
			@RequestParam(value = "order", defaultValue = "desc") String order,
			BannerList uploadbannerrecord,HttpServletRequest request){
		 	PageInfo<BannerList> requestbody = new PageInfo<BannerList>();
		    requestbody.setPageNo(page);
			requestbody.setPageSize(rows);
			requestbody.setQueryParam(uploadbannerrecord);
			requestbody = uploadpicture.selectNumber(requestbody);
			return ResultVo.returnPage(requestbody);
	}
	/**
	 * 新增or修改图片
	 * */
	@RequestMapping(value = "banner/creatPicture")
	@ResponseBody 
	public ResultVo creatPicture(@RequestParam("imgfile") MultipartFile imgfile,BannerPicture picture,HttpServletRequest request){
		try {
			if(picture!=null){
				if(picture.getId()!=null){//修改
					BannerList banner = new BannerList();
					if(!imgfile.isEmpty()){
						
						String path = uploadpicture.uploadPic(imgfile.getInputStream(),imgfile.getOriginalFilename());
						if(path==(null)||path.equals("")){
							Result result = new Result();
							result.setMessage("图片上传失败");
							return ResultVo.returnMsg(false, result.getMessage());
						}
						banner.setPicture(path);
					}
					
					banner.setId(picture.getId());
					banner.setCodekey(picture.getCodekey());
					banner.setUrl(picture.getUrl());
					banner.setNum(picture.getNum());
					banner.setStatus(picture.getStatus());
					//根据id查询判断图片信息是否已存在
					List<BannerList> listB = uploadpicture.selectByID(banner);
					BannerList b = listB.get(0);
					if(picture.getNum().equals(b.getNum())){//没修改序号，不往后顺延
						Result result = uploadpicture.updatePicture(banner);
						if(result.getSuccess()){
							return ResultVo.returnMsg(result.getSuccess(), result.getMessage());
						}
						return ResultVo.returnMsg(false, result.getMessage());
					}else{//判断是否已存在
						List<BannerList> listBaner = uploadpicture.selectInNum(banner);
						if(listBaner.isEmpty()){//此次修改的序号不存在，直接修改，不往后顺延
							Result result = uploadpicture.updatePicture(banner);
							if(result.getSuccess()){
								return ResultVo.returnMsg(result.getSuccess(), result.getMessage());
							}
							return ResultVo.returnMsg(false, result.getMessage());
						}else{//修改之前按序号更新其他记录
							//修改之前按序号更新其他记录
							//判断大小如果修改的序号比之前的小走以下逻辑
							if(picture.getNum()<b.getNum()){
								
								banner.setNumById(b.getNum());
								Result result1 = uploadpicture.selectByNum(banner);
								if(result1.getSuccess()){
									
								}else{
									Result result = new Result();
									result.setMessage("更新序号失败");
									return ResultVo.returnMsg(false, result.getMessage());
								}
								
								Result result = uploadpicture.updatePicture(banner);
								if(result.getSuccess()){
									return ResultVo.returnMsg(result.getSuccess(), result.getMessage());
								}
								return ResultVo.returnMsg(false, result.getMessage());
							}else{
								//序号互换
								banner.setNumById(b.getNum());
								Result re = uploadpicture.updateInNum(banner);
								if(re.getSuccess()){
									
								}else{
									Result result = new Result();
									result.setMessage("更新序号失败");
									return ResultVo.returnMsg(false, result.getMessage());
								}
								Result result = uploadpicture.updatePicture(banner);
								if(result.getSuccess()){
									return ResultVo.returnMsg(result.getSuccess(), result.getMessage());
								}
								return ResultVo.returnMsg(false, result.getMessage());
							}
						}
					}
					
					
				}else{//新增
					//判断图片序号是否已经存在。
					BannerList banner = new BannerList();
					banner.setCodekey(picture.getCodekey());
					banner.setUrl(picture.getUrl());
					banner.setNum(picture.getNum());
					List<BannerList> listB = uploadpicture.selectInNum(banner);
					if(listB.isEmpty()){//不存在
						//图片上传
						String path = uploadpicture.uploadPic(imgfile.getInputStream(),imgfile.getOriginalFilename());
						if(path==(null)||path.equals("")){
							Result result = new Result();
							result.setMessage("图片上传失败");
							return ResultVo.returnMsg(false, result.getMessage());
						}
						
						banner.setPicture(path);
						banner.setStatus(picture.getStatus());
						Result result = uploadpicture.creatPicture(banner);
						if(result.getSuccess()){
							return ResultVo.returnMsg(result.getSuccess(), result.getMessage());
						}
						return ResultVo.returnMsg(false, result.getMessage());
					}else{//存在
						//图片上传
						String path = uploadpicture.uploadPic(imgfile.getInputStream(),imgfile.getOriginalFilename());
						if(path==(null)||path.equals("")){
							Result result = new Result();
							result.setMessage("图片上传失败");
							return ResultVo.returnMsg(false, result.getMessage());
						}
						
						banner.setPicture(path);
						banner.setStatus(picture.getStatus());
						//增加校验先查询并更新大于等于当前传入序号的记录，并往后更新，序号加1
						Result result1 = uploadpicture.selectByNum(banner);
						if(result1.getSuccess()){
							
						}else{
							Result result = new Result();
							result.setMessage("更新序号失败");
							return ResultVo.returnMsg(false, result.getMessage());
						}
						Result result = uploadpicture.creatPicture(banner);
						if(result.getSuccess()){
							return ResultVo.returnMsg(result.getSuccess(), result.getMessage());
						}
						return ResultVo.returnMsg(false, result.getMessage());
					}
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResultVo.returnMsg(false, "图片保存失败");
	}

}
