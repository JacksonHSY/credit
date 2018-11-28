package com.ymkj.credit.common.entity;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.ymkj.springside.modules.orm.AbstractEntity;

import lombok.Getter;
import lombok.Setter;

@Table(name = "idCardInfo")
@Setter
@Getter
public class IdCardInfo extends AbstractEntity<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5332922904993660643L;

	@Id
	private String id;

	/**
    * 
    */
	private Long customerId;

	/**
    * 
    */
	private String score;

	/**
	 * 0:正面 1:反面
	 */
	private String type;

	/**
    * 
    */
	private String info;

	/**
    * 
    */
	private String url;

	/**
	 * 借款申请编号
	 */
	private String batchNum;

	/**
     * 
     */
	private String memo;

	/**
     * 
     */
	private String status;

	/**
      * 
      */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	/**
      * 
      */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	
	private String isLivenessOpen;//是否开启人脸识别
//	/**
//	 * 身份证号
//	 */
//	private String idCardNo;
//	/**
//	 * 姓名
//	 */
//	private String name;
}
