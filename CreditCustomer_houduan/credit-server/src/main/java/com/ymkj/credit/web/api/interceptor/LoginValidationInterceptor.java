package com.ymkj.credit.web.api.interceptor;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSON;
import com.ymkj.credit.common.constants.Constants;
import com.ymkj.credit.common.entity.Customer;
import com.ymkj.credit.common.entity.LoginLog;
import com.ymkj.credit.common.ex.BussErrorCode;
import com.ymkj.credit.common.ex.JsonException;
import com.ymkj.credit.service.CustomerService;
import com.ymkj.credit.service.LoginLogService;
import com.ymkj.credit.service.redis.BasicRedisOpts;
import com.ymkj.credit.web.api.model.req.ReqHeadParam;
import com.ymkj.credit.web.api.model.req.ReqMain;

/**
 * 登录拦截器
 * 
 * @author longjw@yuminsoft.com
 */
@Slf4j
@Component
public class LoginValidationInterceptor extends HandlerInterceptorAdapter {
    
	public static Set<String> loginFunctionId = new HashSet<String>();//需登录拦截的功能号
	
	@Autowired
	private BasicRedisOpts basicRedisOpts;
	
	@Autowired
	CustomerService customerService;
	@Autowired
	LoginLogService loginLogService;
	
	static{
		loginFunctionId.add("002002");//登录密码校验
		loginFunctionId.add("002029");//捞财宝签约
		loginFunctionId.add("003001");//贷款信息查询
		loginFunctionId.add("004001");//个人信息查询
		loginFunctionId.add("004002");//修改密码
		loginFunctionId.add("004003");//设置手势密码
		loginFunctionId.add("004007");//设置手势密码开关
		loginFunctionId.add("004005");//登出
		loginFunctionId.add("004006");//消息管理
		loginFunctionId.add("004008");//首页轮播图
		loginFunctionId.add("004009");//借款进度查询
		loginFunctionId.add("004010");//提交借款申请
		loginFunctionId.add("004011");//业务员信息查询
		loginFunctionId.add("004012");//验证是否为同一人
		loginFunctionId.add("004013");//上传身份证识别信息
		loginFunctionId.add("004014");//查询是否已通过身份认证
		loginFunctionId.add("004016");//费率计算
		loginFunctionId.add("004017");//获取申请渠道
		loginFunctionId.add("004018");//征信报告上传
		loginFunctionId.add("004019");//征信报告查询
		loginFunctionId.add("005001");//查询是否能借款
		loginFunctionId.add("005002");//贷款申请，以及借款编号生成
		loginFunctionId.add("005003");//查询借款申请信息
		loginFunctionId.add("005004");//申请信息保存or修改
		loginFunctionId.add("005005");//根据节点获取借款信息
		loginFunctionId.add("005006");//规则引擎校验是否可以提交
		loginFunctionId.add("005012");//图片上传
		loginFunctionId.add("005013");//
		loginFunctionId.add("005014");//推送录单
		loginFunctionId.add("005015");//
		loginFunctionId.add("005016");//首页借款进度的提示
		loginFunctionId.add("005017");//获取征信报告
		loginFunctionId.add("006001");//签约h5获取验证码
		loginFunctionId.add("006002");//签约H5与捞财宝注册登录
		loginFunctionId.add("006003");//签约H5与捞财宝绑卡
		loginFunctionId.add("006004");//APP端合同签章
		loginFunctionId.add("006005");//H5要签章的图片
		loginFunctionId.add("006006");//H5从核心获取图片
		loginFunctionId.add("006007");//获取银行卡信息
		loginFunctionId.add("006008");//捞财宝实名认证
		loginFunctionId.add("006009");//前前费率试算对接后台配置参数获取
		loginFunctionId.add("006010");//查询是否可以到签约
		loginFunctionId.add("006107");//合同签约
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object obj) {
        String functionId = request.getParameter("arg0");
        String param = request.getParameter("arg1");
        
        List<String> errors = (List<String>)obj; // 为了创建filter list
		if(loginFunctionId.contains(functionId)){
			//根据设备号从redis获取登录信息
            ReqMain reqMain = JSON.parseObject(param, ReqMain.class);
            
            if(Constants.ProjectNo_TYPE_WebService.equals(reqMain.getProjectNo())){//h5请求免校验
            	return true;
            }
            
            ReqHeadParam headParam = reqMain.getReqHeadParam();
            //判断客户是否已注销
            Customer customer =  customerService.findCustomerMes(Long.valueOf(headParam.getCustomerId()));
            //设备号
            String deviceNum = headParam.getDeviceNum();
            
            log.info("当前用户过期时间为：" + basicRedisOpts.getTimeOut(deviceNum));
            
			//查询登录信息
			if(null != customer && null != basicRedisOpts.getSingleResult(deviceNum)){
				return true;
			}
			if(null == basicRedisOpts.getSingleResult(deviceNum)&&!deviceNum.equals(customer.getDeviceNum())){
				//获取登录时间
				LoginLog loginlog = new LoginLog();
				loginlog.setPhone(customer.getPhone());
				loginlog = loginLogService.selectCustomer(loginlog);
				
				//修改提示信息
				BussErrorCode ecode = BussErrorCode.ERROR_CODE_0111;
				ecode.setErrordesc("您的账户于"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(loginlog.getLoginDate())+"在其他设备登录，本设备被强制退出。若非本人操作，请及时修改密码。");
				validateFailed(JsonException.toJson(ecode), errors);
				return false;
			}
			log.debug(MessageFormat.format("设备号【{0}】，未获取到登录信息", deviceNum));
			validateFailed(JsonException.toJson(BussErrorCode.ERROR_CODE_0111), errors);
			return false;
		}
		return true;
	}

    private void validateFailed(String errorMsg, List<String> errorObj){
        errorObj.add(errorMsg);
    }
}
