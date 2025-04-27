package com.adia.admin.grpc;

import com.chat.admin.CheckRequest;
import com.chat.admin.CheckResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import com.chat.admin.AdminServiceGrpc;

@GrpcService
public class AdminServiceImpl extends AdminServiceGrpc.AdminServiceImplBase {
    @Override
    public void check(CheckRequest request, StreamObserver<CheckResponse> responseObserver) {
        String data = request.getData();

        CheckResponse response = CheckResponse.newBuilder()
                .setStatus(true)
                .setMessage("Received data: " + data)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
