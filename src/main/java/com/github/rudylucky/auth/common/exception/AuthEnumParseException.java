package com.github.rudylucky.auth.common.exception;

public class AuthEnumParseException extends CustomException {
    public AuthEnumParseException(ReturnMessageAndTemplateDef.Errors error, Object... templateParams) {
        super(ErrorCode.SERVICE_FAILED, error.getMessage(templateParams));
    }
}
