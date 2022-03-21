package com.jhr.datasource.operation.api.domain.response;

/**
 * 响应实体
 *
 * @author xukun
 * @version 1.00
 */
public interface ResultCode {
    /**
     * 操作是否成功,true为成功，false操作失败
     *
     * @return true或false
     */
    boolean success();

    /**
     * 操作代码
     *
     * @return 响应码
     */
    String code();

    /**
     * 提示信息
     *
     * @return 信息
     */
    String message();
}
