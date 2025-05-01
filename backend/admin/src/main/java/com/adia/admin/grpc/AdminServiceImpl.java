package com.adia.admin.grpc;

import com.adia.admin.AdminServiceGrpc;
import com.adia.admin.CheckRequest;
import com.adia.admin.CheckResponse;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class AdminServiceImpl extends AdminServiceGrpc.AdminServiceImplBase {

    @Override
    public void check(CheckRequest req, StreamObserver<CheckResponse> responseObserver) {
        CheckResponse res = CheckResponse.newBuilder()
                .setMessage(req.getData())
                .setStatus(true)
                .build();
        
        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }

}
