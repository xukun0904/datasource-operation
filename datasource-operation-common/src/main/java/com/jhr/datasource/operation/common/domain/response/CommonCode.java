package com.jhr.datasource.operation.common.domain.response;

import com.jhr.datasource.operation.api.domain.response.ResultCode;

/**
 * 通用响应码
 * 错误码为字符串类型，共5位，分成两个部分：错误产生来源+四位数字编号。说明：错误产生来源分为A/B/C，A表示错误来源于用户，比如参数错误，用户安装版本过低，
 * 用户支付超时等问题；B表示错误来源于当前系统，往往是业务逻辑出错，或程序健壮性差等问题；C表示错误来源于第三方服务，比如CDN服务出错，消息投递超时等问题；
 * 四位数字编号从0001到9999，大类之间的步长间距预留100
 *
 * @author xukun
 * @version 1.00
 */
public enum CommonCode implements ResultCode {
    SUCCESS(true, "00000", "操作成功！"),
    PARAMETER_ERROR(false, "A0001", "参数错误！"),
    UNAUTHENTICATED(false, "A0002", "此操作需要登陆系统！"),
    UNAUTHORIZED(false, "A0003", "权限不足，无权操作！"),
    NOT_FOUND(false, "A0004", "没有找到数据！"),
    METHOD_NOT_SUPPORTED(false, "A0005", "请求方式不支持！"),
    DATE_FORMAT_ILLEGAL(false, "A0006", "时间格式不正确！"),
    SERVER_ERROR(false, "B0001", "抱歉，系统繁忙，请稍后重试！"),
    EXPORT_ERROR(false, "B0002", "导出失败！"),
    SERVICE_CONNECTION_ERROR(false, "B0003", "服务连接异常！"),
    ;

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

    CommonCode(boolean success, String code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    @Override
    public boolean success() {
        return success;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
