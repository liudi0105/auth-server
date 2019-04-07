package com.github.rudylucky.auth.enums;

import com.github.rudylucky.auth.common.exception.AuthEnumParseException;
import com.github.rudylucky.auth.common.exception.ReturnMessageAndTemplateDef;
import org.apache.commons.lang3.StringUtils;


public enum UserTypeEnum {
    NORMAL,
    SCRIPT;

    public static UserTypeEnum of(String userType){
        try{
            return UserTypeEnum.valueOf(StringUtils.upperCase(userType));
        } catch (IllegalArgumentException e){
            throw new AuthEnumParseException(ReturnMessageAndTemplateDef.Errors.INVALID_USER_TYPE, userType);
        }
    }
}
