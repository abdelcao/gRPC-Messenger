package com.adia.auth.grpc;

import com.adia.auth.AuthServiceGrpc;
import com.adia.auth.RegisterRequest;
import com.adia.auth.RegisterResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class AuthServiceIml extends AuthServiceGrpc.AuthServiceImplBase {

    @Override
    public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver) {

        RegisterResponse res = RegisterResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Auth Success")
                .setUserId("1")
                .build();

        responseObserver.onNext(res);
        responseObserver.onCompleted();

    }

}
