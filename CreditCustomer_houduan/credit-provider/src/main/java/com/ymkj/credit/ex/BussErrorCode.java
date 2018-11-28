package com.ymkj.credit.ex;

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

    /**
     * 请求参数不正确
     */
    ERROR_CODE_0103("0103","请求参数不正确"),

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
    
    ERROR_CODE_4001("4001","初始化数据更新");
    
    private String errorcode;

    public String getErrorcode() {
        return errorcode;
    }

    public String getErrordesc() {
        return errordesc;
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
