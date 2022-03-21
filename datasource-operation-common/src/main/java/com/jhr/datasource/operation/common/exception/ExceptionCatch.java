package com.jhr.datasource.operation.common.exception;


import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.ImmutableMap;
import com.jhr.datasource.operation.api.domain.response.ResultCode;
import com.jhr.datasource.operation.common.domain.response.CommonCode;
import com.jhr.datasource.operation.api.domain.response.DsResponseResult;
import com.jhr.datasource.operation.common.domain.response.ResponseResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 描述:统一异常捕获类
 *
 * @author xukun
 * @version 1.00
 */
@RestControllerAdvice
public class ExceptionCatch {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionCatch.class);

    /**
     * 定义map，配置异常类型所对应的错误代码
     */
    private static ImmutableMap<Class<? extends Throwable>, ResultCode> EXCEPTIONS;
    /**
     * 定义map的builder对象，去构建ImmutableMap
     */
    protected static ImmutableMap.Builder<Class<? extends Throwable>, ResultCode> builder = ImmutableMap.builder();

    /**
     * 捕获CustomException此类异常
     *
     * @param customException 自定义异常
     * @return 响应前台数据
     */
    @ExceptionHandler(CustomException.class)
    public DsResponseResult<Void> customException(CustomException customException) {
        ResultCode resultCode = customException.getResultCode();
        // 记录日志
        LOGGER.error(MessageFormat.format("catch custom exception : {0}", resultCode.message()), customException);
        return ResponseResultBuilder.builder().withCode(resultCode).build();
    }

    /**
     * 捕获Exception此类异常
     *
     * @param exception 异常
     * @return 响应前台数据
     */
    @ExceptionHandler(Exception.class)
    public DsResponseResult<Void> exception(Exception exception) {
        // 记录日志
        LOGGER.error("catch exception:", exception);
        if (EXCEPTIONS == null) {
            // EXCEPTIONS构建成功
            EXCEPTIONS = builder.build();
        }
        // 从EXCEPTIONS中找异常类型所对应的错误代码，如果找到了将错误代码响应给用户，如果找不到给用户响应99999异常
        ResultCode resultCode = EXCEPTIONS.get(exception.getClass());
        resultCode = resultCode == null ? CommonCode.SERVER_ERROR : resultCode;
        return ResponseResultBuilder.builder().withCode(resultCode).build();
    }

    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public DsResponseResult<Void> handleValidException(Exception exception) {
        List<ObjectError> errors;
        StringBuilder message = new StringBuilder();
        if (exception instanceof BindException) {
            BindException bindException = (BindException) exception;
            errors = bindException.getBindingResult().getAllErrors();
            message = new StringBuilder(errors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(";")));
        } else if (exception instanceof ConstraintViolationException) {
            ConstraintViolationException constraintViolationException = (ConstraintViolationException) exception;
            Set<ConstraintViolation<?>> constraintViolations = constraintViolationException.getConstraintViolations();
            if (CollectionUtil.isNotEmpty(constraintViolations)) {
                for (ConstraintViolation<?> constraintViolation : constraintViolations) {
                    message.append(constraintViolation.getMessage()).append(";");
                }
                message.deleteCharAt(message.length() - 1);
            }
        } else {
            MethodArgumentNotValidException validException = (MethodArgumentNotValidException) exception;
            errors = validException.getBindingResult().getAllErrors();
            message = new StringBuilder(errors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(";")));
        }
        // 记录日志
        LOGGER.error(MessageFormat.format("catch bind exception : {0}", message.toString()), exception);
        return ResponseResultBuilder.builder().withCode(CommonCode.PARAMETER_ERROR.code()).withSuccess(CommonCode.PARAMETER_ERROR.success()).withMessage(message.toString()).build();
    }

    static {
        // 定义异常类型所对应的错误代码
        builder.put(HttpRequestMethodNotSupportedException.class, CommonCode.METHOD_NOT_SUPPORTED);
        builder.put(HttpMessageNotReadableException.class, CommonCode.PARAMETER_ERROR);
        builder.put(java.time.format.DateTimeParseException.class, CommonCode.DATE_FORMAT_ILLEGAL);
    }
}
