package com.github.rudylucky.auth.enums;

import com.github.rudylucky.auth.common.exception.AuthEnumParseException;
import com.github.rudylucky.auth.common.exception.ReturnMessageAndTemplateDef;
import org.apache.commons.lang3.StringUtils;


public enum CategoryTypeEnum {
    UNIVERSAL(0);//通用

    private Integer code;

    CategoryTypeEnum(int code){
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public static CategoryTypeEnum of(String categoryType){
        try{
            return CategoryTypeEnum.valueOf(StringUtils.upperCase(categoryType));
        } catch (IllegalArgumentException e){
            throw new AuthEnumParseException(ReturnMessageAndTemplateDef.Errors.INVALID_CATEGORY_TYPE, categoryType);
        }
    }
}
