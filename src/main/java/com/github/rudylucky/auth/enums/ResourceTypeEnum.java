package com.github.rudylucky.auth.enums;

import com.github.rudylucky.auth.common.exception.AuthEnumParseException;
import com.github.rudylucky.auth.common.exception.ReturnMessageAndTemplateDef;
import org.apache.commons.lang3.StringUtils;

public enum ResourceTypeEnum {
    BOOK("交易簿"), TRADE("交易"), PORTFOLIO("投资组合"), NAMESPACE("资源"), ROOT("根资源"),
    DEPT("部门"), COMPANY("公司"), USER("用户"), ROLE("角色"), MARGIN("保证金"), CLIENT_INFO("客户信息")
    ;

    private String alias;
    ResourceTypeEnum(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public static ResourceTypeEnum of(String resourceType){
        try{
            return ResourceTypeEnum.valueOf(StringUtils.upperCase(resourceType));
        } catch (IllegalArgumentException e){
            throw new AuthEnumParseException(ReturnMessageAndTemplateDef.Errors.INVALID_RESOURCE_TYPE, resourceType);
        }
    }
}
