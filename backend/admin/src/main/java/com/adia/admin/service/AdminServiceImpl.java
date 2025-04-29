package com.adia.admin.service;

import com.adia.admin.AdminServiceGrpc;
import com.adia.admin.CheckRequest;
import com.adia.admin.CheckResponse;
import org.springframework.grpc.server.service.GrpcService;
import io.grpc.stub.StreamObserver;

@GrpcService
public class AdminServiceImpl extends AdminServiceGrpc.AdminServiceImplBase {

    @Override
    public void check(CheckRequest request, StreamObserver<CheckResponse> responseObserver) {
        CheckResponse response = CheckResponse.newBuilder()
                .setStatus(true)
                .setMessage(request.getData())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
