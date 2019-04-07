package com.github.rudylucky.auth.common.exception;

public class ServiceException extends CustomException {
    public ServiceException(ReturnMessageAndTemplateDef.Errors error, Object...params) {
        super(ErrorCode.SERVICE_FAILED, error.getMessage(params));
    }
}
