package com.ymkj.credit.common.ex;

import java.util.Date;

import com.google.common.base.Objects;

/**
 * 错误码
 * @author 黄基霖
 *
 */
public enum BussErrorCode {
	/**
     * 前两位:系统or模块                  例:推送任务模块11
     * 后两位:具体详细错误 例:校验码验证失败01
     * */

    /**
     * 成功
     */
    ERROR_CODE_0000("0000","成功"),

    /**
     * 验签失败
     */
    ERROR_CODE_0110("0110","验签失败"),

    /**
     * 未登录
     */
    ERROR_CODE_0111("0111","您的会话已超时，请重新登录"),
    //ERROR_CODE_0112("0111","您的账户于"+(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date())+"在其他设备登录，本设备被强制退出。若非本人操作，请及时修改密码。"),
    /**
     * 请求参数不正确
     */
    ERROR_CODE_0103("0103","请求参数不正确"),
    ERROR_CODE_0113("0113","推送客户未注册"),
    ERROR_CODE_0123("0123","客户推送设备号为空"),
    ERROR_CODE_0133("0133","客户推送平台为空"),
    /**
     * 系统异常,请联系管理员
     */
    ERROR_CODE_0102("0102","系统繁忙,请稍后再试"),

    ERROR_CODE_1111("1111","系统正在维护中，请稍后再试"),

    /**
     * 匹配不到请求APP的类型
     */
    ERROR_CODE_2101("2101","匹配不到请求APP的类型"),

    /**
     * 请求APP的版本过低
     */
    ERROR_CODE_2100("2100","请求APP的版本过低"),

    /**
     * 统一通信发送客户接受标识为空
     */
    ERROR_CODE_3101("3101","客户接受标识为空"),

    /**
     * 统一通信发送内容为空
     */
    ERROR_CODE_3102("3102","发送内容为空"),

    ERROR_CODE_3001("3001","账户未通过审核"),
    
    ERROR_CODE_3002("3002","账户未认证"),
    ERROR_CODE_3003("3003","暂不支持该银行,请更换其他银行卡"),
    
    ERROR_CODE_4001("4001","初始化数据更新"),
    
    ERROR_CODE_5001("5001","调用益博睿录单接口失败！返回null"),
    ERROR_CODE_5002("5002","调用益博睿录单接口失败！返回非000000"),
    
    ERROR_CODE_6001("6001","调用录单查询签约信息接口失败！返回null"),
    ERROR_CODE_6002("6002","调用录单查询签约信息接口失败！返回非000000"),
    
    ERROR_CODE_6003("6003","调用录单保存银行卡接口失败！返回null"),
    ERROR_CODE_6004("6004","调用录单保存银行卡接口失败！返回非000000"),
    
    ERROR_CODE_6005("6005","调用录单签约接口失败！返回null"),
    ERROR_CODE_6006("6006","调用录单签约接口失败！返回非000000"),
    
    ERROR_CODE_6007("6007","调用录单发送短信验证码接口失败！返回null"),
    ERROR_CODE_6008("6008","调用录单发送短信验证码接口失败！返回非000000"),
    
    ERROR_CODE_6009("6009","调用录单电子签章通知接口失败！返回null"),
    ERROR_CODE_6010("6010","调用录单电子签章通知接口失败！返回非000000"),
    
    ERROR_CODE_6011("6011","调用录单获取银行列表接口失败！返回null"),
    ERROR_CODE_6012("6012","调用录单获取银行列表接口失败！返回非000000"),
    
    ERROR_CODE_6013("6013","调用录单获捞财宝签约状态接口失败！返回null"),
    ERROR_CODE_6014("6014","调用录单获捞财宝签约状态接口失败！返回非000000"),
    
    ERROR_CODE_6015("6015","调用录单获取用户信息接口失败！返回null"),
    ERROR_CODE_6016("6016","调用录单获取用户信息接口失败！返回非000000"),
    
    ERROR_CODE_6017("6017","调用录单借新还旧签订接口失败！返回null"),
    ERROR_CODE_6018("6018","调用录单借新还旧签订接口失败！返回非000000"),
    
    ERROR_CODE_7001("7001","该笔借款单不存在"),
    ERROR_CODE_7002("7002","银行卡信息不存在"),
    
    ERROR_CODE_8001("8001","征审获取合同签名结果接口失败！返回null"),
    
    ERROR_CODE_9001("9001","调用tpp协议支付签约短信接口失败！返回null"),
    ERROR_CODE_9002("9002","调用tpp协议支付签约短信接口失败！返回非000000"),
    
    ERROR_CODE_9003("9003","调用tpp协议支付签约接口失败！返回null"),
    ERROR_CODE_9004("9004","调用tpp协议支付签约接口失败！返回非000000"),
    
    ERROR_CODE_9005("9005","调用tpp获取支持银行卡列表接口失败！返回null"),
    ERROR_CODE_9006("9006","调用tpp获取支持银行卡列表接口失败！返回非000000"),
  
    ERROR_CODE_10000("10000","调用规则引擎接口失败！返回null"),
  ERROR_CODE_10001("10001","调用规则引擎接口失败！返回非000000"),
  
  
  ;
    private String errorcode;

    public String getErrorcode() {
        return errorcode;
    }

    public String getErrordesc() {
        return errordesc;
    }

    public void setErrorcode(String errorcode) {
		this.errorcode = errorcode;
	}

	public void setErrordesc(String errordesc) {
		this.errordesc = errordesc;
	}
	
	private String errordesc;

    BussErrorCode(String errorcode, String errordesc) {
        this.errorcode = errorcode;
        this.errordesc = errordesc;
    }

    public static String explain(String errorCode) {
        for (BussErrorCode bussErrorCode : BussErrorCode.values()) {
            if (Objects.equal(errorCode, bussErrorCode.errorcode)) {
                return bussErrorCode.errordesc;
            }
        }
        return errorCode;
    }

}
