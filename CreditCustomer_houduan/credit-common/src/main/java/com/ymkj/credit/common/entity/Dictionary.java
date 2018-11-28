package com.ymkj.credit.common.entity;
import java.util.Date;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import com.ymkj.springside.modules.orm.AbstractEntity;

/**
* Dictionary
* <p/>
* Author: 
* Date: 2017-08-24 17:16:59
* Mail: 
*/
@Table(name = "dictionary")
@Getter
@Setter
public class Dictionary extends AbstractEntity<Long> {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 5332922904993660643L;

	  @Id
    private Long id;

    /**
    * 
    */
    private String dataType;

    /**
    * 
    */
    private String dataCode;

    /**
    * 
    */
    private String dataName;

    /**
    * 
    */
    private String dataValue;

    /**
    * 
    */
    private String status;

    /**
    * 
    */
    private String memo;

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

}