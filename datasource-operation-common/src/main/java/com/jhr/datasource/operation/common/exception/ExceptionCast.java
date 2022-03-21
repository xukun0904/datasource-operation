package com.jhr.datasource.operation.common.exception;


import com.jhr.datasource.operation.api.domain.response.ResultCode;

/**
 * 描述:统一异常抛出类
 *
 * @author xukun
 * @version 1.00
 */
public class ExceptionCast {

    public static void cast(ResultCode resultCode) {
        throw new CustomException(resultCode);
    }
}
