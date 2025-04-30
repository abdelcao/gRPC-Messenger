package com.adia.backend.grpc;

import com.adia.admin.AdminServiceGrpc;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class AdminServiceImpl extends AdminServiceGrpc.AdminServiceImplBase {
}
