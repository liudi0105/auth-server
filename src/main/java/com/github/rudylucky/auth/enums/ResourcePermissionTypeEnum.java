package com.github.rudylucky.auth.enums;

import com.github.rudylucky.auth.common.exception.AuthEnumParseException;
import com.github.rudylucky.auth.common.exception.ReturnMessageAndTemplateDef;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

public enum ResourcePermissionTypeEnum {

    /*------------------------ role related  --------------------------*/
    CREATE_ROLE("创建角色"),
    UPDATE_ROLE("修改角色"),
    DELETE_ROLE("删除角色"),
    /*------------------------ role related  --------------------------*/
    /*------------------------ common  --------------------------*/
    GRANT_ACTION("赋权"),
    READ_RESOURCE("查看资源"),
    /*------------------------ common  --------------------------*/
    /*------------------------ user related  --------------------------*/
    CREATE_USER("创建用户"),
    UPDATE_USER("修改用户"),
    DELETE_USER("删除用户"),
    READ_USER("查看用户"),
    LOCK_USER("锁定用户"),
    UNLOCK_USER("解锁用户"),
    EXPIRE_USER("使用户到期"),
    UNEXPIRE_USER("解锁用户到期操作"),
    CHANGE_PASSWORD("修改密码"),
    /*------------------------ user related  --------------------------*/
    /*------------------------ namespace related  --------------------------*/
    CREATE_NAMESPACE("创建资源组"),
    DELETE_NAMESPACE("删除资源组"),
    UPDATE_NAMESPACE("修改资源组"),
    CREATE_PORTFOLIO("修改投资组合"),
    CREATE_BOOK("创建交易簿"),
    /*------------------------ namespace related  --------------------------*/
    /*------------------------ trade related  --------------------------*/
    CREATE_TRADE("创建交易"),
    UPDATE_TRADE("更新"),
    DELETE_TRADE("删除交易"),
    READ_TRADE("查看交易"),
    /*------------------------ trade related  --------------------------*/
    /*------------------------ book related  --------------------------*/
    UPDATE_BOOK("更新交易簿"),
    DELETE_BOOK("删除交易簿"),
    READ_BOOK("查看交易簿"),
    /*------------------------ book related  --------------------------*/
    /*------------------------ portfolio related  --------------------------*/
    READ_PORTFOLIO("查看投资组合"),
    UPDATE_PORTFOLIO("修改投资组合"),
    DELETE_PORTFOLIO("删除投资组合"),

    /*------------------------ portfolio related  --------------------------*/
    /*------------------------ department related  --------------------------*/
    CREATE_DEPARTMENT("创建部门"),
    UPDATE_DEPARTMENT("更新部门"),
    DELETE_DEPARTMENT("删除部门"),
    /*------------------------ department related  --------------------------*/
    /*------------------------ instrument related  --------------------------*/
    CREATE_INSTRUMENT("创建标的物"),
    DELETE_INSTRUMENT("删除标的物"),
    UPDATE_INSTRUMENT("修改标的物"),
    /*------------------------ instrument related  --------------------------*/

    /*------------------------ margin related ----------------------------- */
    UPDATE_MARGIN("修改保证金"),
    /*------------------------ margin related ----------------------------- */
    /*------------------------ client info related ----------------------------- */
    READ_CLIENT("读取客户信息"),
    CREATE_CLIENT("创建客户信息"),
    UPDATE_CLIENT("修改客户信息"),
    DELETE_CLIENT("删除客户信息"),
    /*------------------------ client info related ----------------------------- */

    ;

    public interface Arrays{

        List<ResourcePermissionTypeEnum> WHEN_CREATE_DEPARTMENT = Lists.newArrayList(
                GRANT_ACTION
                , CREATE_DEPARTMENT
                , UPDATE_DEPARTMENT
                , DELETE_DEPARTMENT
                , CREATE_NAMESPACE, READ_RESOURCE, DELETE_NAMESPACE, UPDATE_NAMESPACE
                , CREATE_BOOK
                , CREATE_PORTFOLIO
                , READ_USER
        );

        List<ResourcePermissionTypeEnum> WHEN_CREATE_NAMESPACE = Lists.newArrayList(
                GRANT_ACTION
                , CREATE_NAMESPACE, READ_RESOURCE, DELETE_NAMESPACE, UPDATE_NAMESPACE
                , CREATE_BOOK
                , CREATE_PORTFOLIO
        );

        List<ResourcePermissionTypeEnum> WHEN_CREATE_BOOK = Lists.newArrayList(
                GRANT_ACTION
                , UPDATE_BOOK, DELETE_BOOK, READ_BOOK
                , CREATE_TRADE, DELETE_TRADE, UPDATE_TRADE, READ_TRADE
        );

        List<ResourcePermissionTypeEnum> WHEN_CREATE_PORTFOLIO = Lists.newArrayList(
                GRANT_ACTION
                , UPDATE_PORTFOLIO, DELETE_PORTFOLIO, READ_PORTFOLIO
        );

        List<ResourcePermissionTypeEnum> ADMIN_ON_COMPANY = Lists.newArrayList(
                GRANT_ACTION, READ_USER
        );
    }


    String alias;

    ResourcePermissionTypeEnum(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public static ResourcePermissionTypeEnum of(String resourcePermissionType){
        try{
            return ResourcePermissionTypeEnum.valueOf(StringUtils.upperCase(resourcePermissionType));
        } catch (IllegalArgumentException e){
            throw new AuthEnumParseException(ReturnMessageAndTemplateDef.Errors.INVALID_RESOURCE_PERMISSION_TYPE, resourcePermissionType);
        }
    }

    public static List<ResourcePermissionTypeEnum> ofList(List<String> permissions){
        return permissions.stream()
                .map(ResourcePermissionTypeEnum::of)
                .collect(Collectors.toList());
    }
}
