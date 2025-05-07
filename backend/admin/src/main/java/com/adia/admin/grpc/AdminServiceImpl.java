package com.adia.admin.grpc;

import com.adia.admin.AdminServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.logging.Logger;

@GrpcService
public class AdminServiceImpl extends AdminServiceGrpc.AdminServiceImplBase {
}
