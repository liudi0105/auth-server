package com.github.rudylucky.auth.enums;

import com.github.rudylucky.auth.common.exception.AuthEnumParseException;
import com.github.rudylucky.auth.common.exception.ReturnMessageAndTemplateDef;
import org.apache.commons.lang3.StringUtils;


public enum ItemTypeEnum {
    LONG(0),
    DOUBLE(1),
    RADIO(2),
    CHECKBOX(3),
    TEXT(4),
    COMPLEX(5);

    private Integer code;

    ItemTypeEnum(int code){
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public static ItemTypeEnum of(String itemType){
        try{
            return ItemTypeEnum.valueOf(StringUtils.upperCase(itemType));
        } catch (IllegalArgumentException e){
            throw new AuthEnumParseException(ReturnMessageAndTemplateDef.Errors.INVALID_ITEM_TYPE, itemType);
        }
    }
}
