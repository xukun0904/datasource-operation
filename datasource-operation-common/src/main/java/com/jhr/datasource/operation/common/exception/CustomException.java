package com.jhr.datasource.operation.common.exception;


import com.jhr.datasource.operation.api.domain.response.ResultCode;

/**
 * 描述:自定义异常类型
 *
 * @author xukun
 * @version 1.00
 */
public class CustomException extends RuntimeException {
    /**
     * 错误代码
     */
    private final ResultCode resultCode;

    @Override
    public String getMessage() {
        return resultCode.message();
    }

    public CustomException(ResultCode resultCode) {
        this.resultCode = resultCode;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }
}
