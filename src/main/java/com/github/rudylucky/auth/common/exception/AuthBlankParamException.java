package com.github.rudylucky.auth.common.exception;

import java.util.Arrays;

public class AuthBlankParamException extends CustomException {

    public AuthBlankParamException(String... templateParams) {
        super(ErrorCode.INPUT_NOT_VALID, ReturnMessageAndTemplateDef.Errors.EMPTY_PARAM.getMessage(Arrays.toString(templateParams)));
    }

}
