package com.github.rudylucky.auth.common.exception;


public class AuthServiceException extends CustomException {

    public AuthServiceException(ReturnMessageAndTemplateDef.Errors error, Object... templateParams) {
        super(ErrorCode.SERVICE_FAILED, error.getMessage(templateParams));
    }
}
