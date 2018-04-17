package com.zyg.guns.core.excel.exception;

/**
 * 业务运行时异常
 * <p>
 * Created by hanxiaoqiang on 2017/2/27.
 */
public class ExcelExportRuntimeException extends RuntimeException {

    /**
     * 错误编码
     */
    private String errorCode;
    /**
     * 错误描述
     */
    private String errorMessage;

    public ExcelExportRuntimeException() {

    }

    public ExcelExportRuntimeException(String message) {
        super(message);
        this.errorMessage = message;
    }


    /**
     * 构造函数
     *
     * @param errorCode
     * @param errorMessage
     */
    public ExcelExportRuntimeException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
    public ExcelExportRuntimeException(Integer errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode.toString();
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BusinessRuntimeException{");
        sb.append("errorCode='").append(errorCode).append('\'');
        sb.append(", errorMessage='").append(errorMessage).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
