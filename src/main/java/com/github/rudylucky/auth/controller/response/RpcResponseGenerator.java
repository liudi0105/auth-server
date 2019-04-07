package com.github.rudylucky.auth.controller.response;

import com.github.rudylucky.auth.common.AuthConstants;
import com.github.rudylucky.auth.common.exception.CustomException;
import com.github.rudylucky.auth.common.util.JsonUtils;
import com.github.rudylucky.auth.common.exception.ReturnMessageAndTemplateDef;
import com.google.common.collect.Maps;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class RpcResponseGenerator {

    public static ResponseEntity<String> getErrorResponseEntity(ReturnMessageAndTemplateDef.Errors error, Object... templateParams){
        return new ResponseEntity<>(
                new RpcErrorResponse<>(
                        RpcReturnCode.SERVICE_FAILED
                        , AuthConstants.SERVICE_ID
                        , error.getDetailedErrorCode()
                        , error.getMessage(templateParams)
                ).toString(),
                HttpStatus.OK
        );
    }

    public static ResponseEntity<String> getErrorResponseEntity(CustomException e){
        return new ResponseEntity<>(
                new RpcErrorResponse<>(
                        RpcReturnCode.SERVICE_FAILED
                        , AuthConstants.SERVICE_ID
                        , ""
                        , e.getMessage()
                ).toString(),
                HttpStatus.OK
        );
    }

    public static ResponseEntity<String> getResponseEntity(String entity){
        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    public static ResponseEntity<String> getOldResponseEntity(String entity){
        Map<String, Object> map = Maps.newHashMap();
        map.put("id", 1);
        map.put("jsonrpc", "2.0");
        map.put("result", JsonUtils.fromJson(entity));
        return new ResponseEntity<>(JsonUtils.toJson(map), HttpStatus.OK);
    }
}
