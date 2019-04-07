package com.github.rudylucky.auth.common.exception;

public class ManagerException extends CustomException {
    public ManagerException(ReturnMessageAndTemplateDef.Errors error, Object...params) {
        super(ErrorCode.SERVICE_FAILED, error.getMessage(params));
    }
}
