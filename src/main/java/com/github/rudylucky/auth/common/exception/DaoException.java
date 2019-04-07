package com.github.rudylucky.auth.common.exception;

public class DaoException extends CustomException {
    public DaoException() {
        super(ErrorCode.SERVICE_FAILED, ReturnMessageAndTemplateDef.Errors.DATABASE_ERROR.getMessage());
    }
}
