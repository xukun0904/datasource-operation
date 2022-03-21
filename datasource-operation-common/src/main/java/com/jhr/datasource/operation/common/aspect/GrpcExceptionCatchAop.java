package com.jhr.datasource.operation.common.aspect;

import cn.hutool.core.util.ArrayUtil;
import com.jhr.datasource.operation.api.grpc.response.ResponseResult;
import com.jhr.datasource.operation.common.exception.CustomException;
import com.jhr.datasource.operation.common.util.DatasourceOperationUtils;
import io.grpc.stub.StreamObserver;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author xukun
 * @since 1.0
 */
@Aspect
@Component
public class GrpcExceptionCatchAop {

    @AfterThrowing(pointcut = "execution(* com.jhr.datasource.operation.common.service.grpc..*.*(..))", throwing = "ex")
    public void afterThrowing(JoinPoint point, Throwable ex) {
        Object[] args = point.getArgs();
        if (ArrayUtil.isEmpty(args)) {
            return;
        }
        StreamObserver<ResponseResult> responseObserver = null;
        for (Object arg : args) {
            if (arg instanceof StreamObserver) {
                responseObserver = (StreamObserver<ResponseResult>) arg;
            }
        }
        if (responseObserver != null) {
            ResponseResult responseResult;
            if (ex instanceof CustomException) {
                responseResult = DatasourceOperationUtils.withCode(((CustomException) ex).getResultCode());
            } else {
                responseResult = DatasourceOperationUtils.error(ex.getMessage());
            }
            responseObserver.onNext(responseResult);
            responseObserver.onCompleted();
        }
    }
}
