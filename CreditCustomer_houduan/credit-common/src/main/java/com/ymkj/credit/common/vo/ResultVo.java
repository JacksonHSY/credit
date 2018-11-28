package com.ymkj.credit.common.vo;

import com.ymkj.credit.common.constants.Constants;
import com.ymkj.springside.modules.orm.PageInfo;
import com.ymkj.springside.modules.utils.Response;

import lombok.Getter;
import lombok.Setter;


/**
 * @Description：返回视图对象
 * @ClassName: ResultVo.java
 * @Author：tianx
 * @Date：2017年7月27日
 * -----------------变更历史-----------------
 * 如：who  2017年7月27日  修改xx功能
 */
@Getter
@Setter
public class ResultVo {
	
	private Object rows;
	private int total;
	private boolean status;
	private Object data;
	private Object footer ;
	
	private static final String SUCCESS = "操作成功！";
	private static final String FAILURE = "操作失败！";
	
	
	//返回分页数据
	public static ResultVo returnPage(PageInfo pageInfo){
		return new ResultVo(pageInfo.getResults(), pageInfo.getTotalRecord(),Boolean.TRUE,null); 
	}
	//返回带合计行footer的分页数据
	public static ResultVo returnPageWithFooter(PageInfo pageInfo,Object footer){
		return new ResultVo(pageInfo.getResults(), pageInfo.getTotalRecord(),Boolean.TRUE,null,footer); 
	}
	
	//返回操作结果
		public static ResultVo returnMsg(Response response){
			Boolean flag = Constants.CODE_SUCCESS.equals(response.getCode());
			return new ResultVo(null,1,flag,flag?SUCCESS:FAILURE);
		}
	
	public ResultVo(Object rows, int total, boolean status, Object data) {
		super();
		this.rows = rows;
		this.total = total;
		this.status = status;
		this.data = data;
	}
	public ResultVo(Object rows, int total, boolean status, Object data,Object footer) {
		super();
		this.rows = rows;
		this.total = total;
		this.status = status;
		this.data = data;
		this.footer=footer;
	}
	
	public ResultVo(){
		super();
	}
	
	public static ResultVo returnMsg(boolean flag, String msg){
		return new ResultVo(null, 0, flag, msg);
	}
	
}
