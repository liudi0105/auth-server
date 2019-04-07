package com.github.rudylucky.auth.common.api.response;

public interface RpcResponseWithDiagnostics<R, D> {
    R getResult();
    D getDiagnostics();
}
