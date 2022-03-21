package com.jhr.datasource.operation.common.domain.response;

import com.jhr.datasource.operation.api.domain.response.DsResponseResult;
import com.jhr.datasource.operation.api.domain.response.ResultCode;

/**
 * 响应值Builder
 *
 * @author xukun
 * @version 1.00
 */
public final class ResponseResultBuilder {
    private boolean success;
    private String code;
    private String message;

    private ResponseResultBuilder() {
    }

    public static ResponseResultBuilder builder() {
        return new ResponseResultBuilder();
    }

    public ResponseResultBuilder withSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public ResponseResultBuilder withCode(String code) {
        this.code = code;
        return this;
    }

    public ResponseResultBuilder withMessage(String message) {
        this.message = message;
        return this;
    }

    public ResponseResultBuilder withCode(ResultCode resultCode) {
        this.code = resultCode.code();
        this.success = resultCode.success();
        this.message = resultCode.message();
        return this;
    }

    public <T> DsResponseResult<T> success() {
        return withCode(CommonCode.SUCCESS).build();
    }

    public <T> DsResponseResult<T> success(T data) {
        return withCode(CommonCode.SUCCESS).build(data);
    }

    public <T> DsResponseResult<T> build(T data) {
        DsResponseResult<T> responseResult = new DsResponseResult<>();
        responseResult.setSuccess(success);
        responseResult.setCode(code);
        responseResult.setMessage(message);
        responseResult.setData(data);
        return responseResult;
    }

    public <T> DsResponseResult<T> build() {
        DsResponseResult<T> responseResult = new DsResponseResult<>();
        responseResult.setSuccess(success);
        responseResult.setCode(code);
        responseResult.setMessage(message);
        return responseResult;
    }
}
