package com.github.rudylucky.auth.common.exception;

import com.github.rudylucky.auth.enums.ResourcePermissionTypeEnum;
import com.github.rudylucky.auth.enums.ResourceTypeEnum;

public class AuthorizationException extends CustomException {

    public AuthorizationException(ResourceTypeEnum resourceType, String resourceName, ResourcePermissionTypeEnum operation) {
        super(ErrorCode.SERVICE_FAILED, ReturnMessageAndTemplateDef.Errors.UNAUTHORIZATION_ACTION.getMessage(resourceType.getAlias(), resourceName , operation.getAlias()));

    }
}