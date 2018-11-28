package com.ymkj.credit.web.api.model;

/**
 * Result
 * <p/>
 * Author: Hao Chen
 * Date: 2015/10/16 14:23
 * Mail: haoc@zendaimoney.com
 * $Id$
 */
public class Result {
    private Boolean success;
    private String message;
    private Object data;

    public Result() {
        this(Boolean.TRUE, "操作成功");
    }

    public Result(Boolean success) {
        this(success, null);
    }

    public Result(Object data) {
        this(Boolean.TRUE, "操作成功");
        this.data = data;
    }

    public Result(Boolean success, String message) {
        this(success, message, null);
    }


    public Result(Boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
        if (this.message == null) {
            if (Boolean.FALSE.equals(success)) {
                this.message = "系统繁忙,请稍后再试";
            }
            if (Boolean.TRUE.equals(success)) {
                this.message = "操作成功";
            }

        }
    }


    public static Result fail() {
        return fail(null);
    }

    public static Result fail(String message) {
        return new Result(Boolean.FALSE, message);
    }

    public static Result fail(String message, Object data) {
        return new Result(Boolean.FALSE, message, data);
    }

    public static Result success() {
        return success(null);
    }

    public static Result success(Object data) {
        return new Result(data);
    }

    public static Result success(String message) {
        return new Result(Boolean.TRUE, message);
    }


    public static Result success(String message, Object data) {
        return new Result(Boolean.TRUE, message, data);
    }


    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}