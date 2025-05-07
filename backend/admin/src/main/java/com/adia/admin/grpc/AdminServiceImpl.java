package com.adia.admin.grpc;

import com.adia.admin.AdminServiceGrpc;
import com.adia.admin.CheckRequest;
import com.adia.admin.CheckResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.logging.Logger;

@GrpcService
public class AdminServiceImpl extends AdminServiceGrpc.AdminServiceImplBase {


    @Override
    public void doCheck(CheckRequest req, StreamObserver<CheckResponse> responseObserver) {
        CheckResponse res = CheckResponse.newBuilder()
                .setMessage(req.getData())
                .setStatus(true)
                .build();

        System.out.println("\n" + req.getData() + "\n");

        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }
}
