package com.ymkj.credit.mapper;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.Page;
import com.ymkj.credit.common.entity.BannerList;
import com.ymkj.credit.common.entity.Dictionary;
import com.ymkj.credit.web.api.dto.UploadBannerRecord;
import com.ymkj.springside.modules.orm.mybatis.JdMapper;

public interface BannerListMapper extends JdMapper<BannerList, Long>{
	/**分页查询图片信息*/
	List<BannerList> findBymap(BannerList uploadbannerrecord);
	/**新增图片*/
	void insetPicture(BannerList uploadbannerrecord);
	/**修改图片*/
	void updateByParam(BannerList uploadbannerrecord);
	/**查询序号最大记录*/
	List<UploadBannerRecord> selectNumber(BannerList uploadbannerrecord);
	/**查询序号大于等于当前序号的记录*/
	void selectByNum(BannerList banner);
	/**查询所有记录*/
	List<BannerList> selectRecode(BannerList uploadbannerrecord);
	/**查询图片序号是否已经存在*/
	List<BannerList> selectInNum(BannerList uploadbannerrecord);
	/**根据id查询图片信息*/
	List<BannerList> selectByID(BannerList uploadbannerrecord);
	/**序号互换*/
	void updateInNum(BannerList banner);
}
