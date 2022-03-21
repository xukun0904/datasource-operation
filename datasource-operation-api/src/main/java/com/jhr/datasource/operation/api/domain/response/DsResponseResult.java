package com.jhr.datasource.operation.api.domain.response;

import java.io.Serializable;

/**
 * 响应值
 *
 * @author xukun
 * @version 1.00
 */
public class DsResponseResult<T> implements Serializable {
    /**
     * 操作是否成功
     */
    private boolean success;

    /**
     * 操作代码
     */
    private String code;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    public DsResponseResult(ResultCode resultCode) {
        this.success = resultCode.success();
        this.message = resultCode.message();
        this.code = resultCode.code();
    }

    public DsResponseResult(ResultCode resultCode, T data) {
        this.success = resultCode.success();
        this.message = resultCode.message();
        this.code = resultCode.code();
        this.data = data;
    }

    public DsResponseResult() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
