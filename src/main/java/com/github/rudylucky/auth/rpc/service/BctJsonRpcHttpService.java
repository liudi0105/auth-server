package com.github.rudylucky.auth.rpc.service;

import com.github.rudylucky.auth.common.api.annotation.BctMethodArg;
import com.github.rudylucky.auth.common.api.annotation.BctMethodInfo;
import com.github.rudylucky.auth.common.api.request.JsonRpcRequest;
import com.github.rudylucky.auth.common.api.response.JsonRpcResponse;
import com.github.rudylucky.auth.common.api.response.RpcResponseWithDiagnostics;
import com.github.rudylucky.auth.common.util.SpringBeanUtils;
import com.google.common.collect.Maps;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class BctJsonRpcHttpService {
    private static Logger logger = LoggerFactory.getLogger(BctJsonRpcHttpService.class);

    private Map<String, Method> methodCache;

    {
        methodCache = Maps.newConcurrentMap();
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .setUrls(ClasspathHelper.forPackage("tech.tongyu.bct"))
                        .setScanners(new MethodAnnotationsScanner())
        );
        Set<Method> apis = reflections.getMethodsAnnotatedWith(BctMethodInfo.class);
        for(Method m : apis){
            methodCache.put(m.getName(), m);
        }
    }

    // excel api handling is crammed into this function
    // this is a hack for now. should be separated out.
    public JsonRpcResponse rpc(JsonRpcRequest req){
        if (req == null || req.getMethod() == null || req.getParams() == null ) {
            return new JsonRpcResponse(JsonRpcResponse.ErrorCode.MALFORMED_INPUT, "输入为空");
        }

        /*String uuid = null;
        if(logger.isDebugEnabled()) {
            logger.debug("call {} with params: {}", uuid, JsonUtils.toJson(data));
        }*/

        Object result;
        String methodName = req.getMethod();
        try {
            Map<String, Object> inputs = req.getParams();
            Method method = methodCache.get(methodName);
            if(method == null) {
                return new JsonRpcResponse(JsonRpcResponse.ErrorCode.METHOD_NOT_FOUND, "API不存在");
            }

            List<Object> params = new ArrayList<>();
            for(Parameter param: method.getParameters()) {
                if(inputs.containsKey(param.getName())){
                    params.add(inputs.get(param.getName()));
                }
                else {
                    BctMethodArg[] args = param.getDeclaredAnnotationsByType(BctMethodArg.class);
                    if (args[0].required()) {
                        return new JsonRpcResponse(JsonRpcResponse.ErrorCode.MISSING_PARAM,
                                "参数不存在: " + param.getName());
                    } else {
                        //pass null if not exist in inputs
                        params.add(null);
                    }
                }
            }

            result = method.invoke(SpringBeanUtils.getBean(method.getDeclaringClass()), params.toArray());

            if(result instanceof RpcResponseWithDiagnostics) {
                return new JsonRpcResponse<>(((RpcResponseWithDiagnostics) result).getResult(),
                        ((RpcResponseWithDiagnostics) result).getDiagnostics());
            } else {
                return new JsonRpcResponse<>(result);
            }

            /*if(logger.isDebugEnabled()){
                logger.debug("result of call {} : {}", uuid, resultInfo);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
            String msg;
            if (e.getCause() != null){
                msg = e.getCause().getMessage();
            } else {
                msg = e.getMessage();
            }
            return new JsonRpcResponse(JsonRpcResponse.ErrorCode.RUNTIME_ERROR, msg);
        }
    }

    public List<String> list() {
        List<String> l = new ArrayList<>(this.methodCache.keySet());
        Collections.sort(l);
        return l;
    }

    public JsonRpcResponse info(String methodName) {
        Method method = methodCache.get(methodName);
        if(method == null) {
            return new JsonRpcResponse(JsonRpcResponse.ErrorCode.METHOD_NOT_FOUND, "API不存在");
        }
        // get return info
        BctMethodInfo apiInfo = method.getAnnotation(BctMethodInfo.class);
        String retName = apiInfo.retName();
        String retType = method.getReturnType().getSimpleName();
        String retDescription = apiInfo.description();
        // get each argument's info
        List<Map<String, String>> args = new ArrayList<>();
        for(Parameter param: method.getParameters()) {
            BctMethodArg[] params = param.getDeclaredAnnotationsByType(BctMethodArg.class);
            String name = param.getName();
            String type = param.getType().getName();
            String description = params[0].description();
            Map<String, String>  arg = new HashMap<>();
            arg.put("name", name);
            arg.put("type", type);
            arg.put("description", description);
            args.add(arg);
        }
        // resulting json
        Map<String, Object> ret = new HashMap<>();
        ret.put("args", args);
        ret.put("retName", retName);
        ret.put("retType", retType);
        ret.put("description", retDescription);
        ret.put("method", methodName);
        return new JsonRpcResponse<>(ret);
    }

    public JsonRpcResponse parallel(List<JsonRpcRequest> requests) {
        List<CompletableFuture<JsonRpcResponse>> tasks = requests.stream().map(
                t -> CompletableFuture.supplyAsync(() ->{
                    try {
                        return rpc(t);
                    } catch (Exception e) {
                        return new JsonRpcResponse<>(JsonRpcResponse.ErrorCode.RUNTIME_ERROR,
                                Objects.isNull(e.getMessage()) ? "内部错误" : e.getMessage());
                    }
                })
        ).collect(Collectors.toList());
        List<JsonRpcResponse> results = tasks.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
        return new JsonRpcResponse<>(results);
    }
}
