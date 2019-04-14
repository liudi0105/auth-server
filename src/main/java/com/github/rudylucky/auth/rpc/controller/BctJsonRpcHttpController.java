package com.github.rudylucky.auth.rpc.controller;

import com.github.rudylucky.auth.common.api.request.JsonRpcRequest;
import com.github.rudylucky.auth.common.api.response.JsonRpcResponse;
import com.github.rudylucky.auth.common.util.JsonUtils;
import com.github.rudylucky.auth.rpc.service.BctJsonRpcHttpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller
public class BctJsonRpcHttpController {
    private BctJsonRpcHttpService bctJsonRpcHttpService;

    @Autowired
    public BctJsonRpcHttpController(BctJsonRpcHttpService bctJsonRpcHttpService) {
        this.bctJsonRpcHttpService = bctJsonRpcHttpService;
    }

    @PostMapping(value = "/api/upload/rpc", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonRpcResponse> uploadRpc(
            @RequestParam("method") String method,
            @RequestParam("params") String params,
            @RequestParam("file") MultipartFile file) {
        Map<String, Object> stringObjectMap = JsonUtils.mapFromJsonString(params);
        Map<String, Object> newParams = new HashMap<>(stringObjectMap);
        newParams.put("file", file);
        JsonRpcRequest req = new JsonRpcRequest(method, newParams);
        return new ResponseEntity<>(bctJsonRpcHttpService.rpc(req), HttpStatus.OK);
    }

    @PostMapping(value = "/api/rpc", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonRpcResponse> postRpc(@RequestBody JsonRpcRequest req) {
        return new ResponseEntity<>(bctJsonRpcHttpService.rpc(req), HttpStatus.OK);
    }

    @GetMapping(value = "/api/list", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getList() {
        return new ResponseEntity<>(bctJsonRpcHttpService.list(), HttpStatus.OK);
    }

    @GetMapping(value = "/api/info/{method}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonRpcResponse> info(@PathVariable String method) {
        return new ResponseEntity<>(bctJsonRpcHttpService.info(method), HttpStatus.OK);
    }
}