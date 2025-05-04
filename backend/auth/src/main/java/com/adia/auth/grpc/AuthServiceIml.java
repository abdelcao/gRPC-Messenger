package com.adia.auth.grpc;

import com.adia.auth.AuthServiceGrpc;
import com.adia.auth.RegisterRequest;
import com.adia.auth.AuthResponse;
import com.adia.auth.User;
import com.adia.auth.entity.RefreshToken;
import com.adia.auth.service.RefreshTokenService;
import com.adia.auth.util.JwtUtil;
import com.adia.user.CreateUserRequest;
import com.adia.user.UserResponse;
import com.adia.user.UserServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class AuthServiceIml extends AuthServiceGrpc.AuthServiceImplBase {

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userService;

    public AuthServiceIml(
            JwtUtil jwtUtil,
            RefreshTokenService refreshTokenService
    ) {
        super();
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public void register(RegisterRequest request, StreamObserver<AuthResponse> responseObserver) {

        try {
            CreateUserRequest userRequest = CreateUserRequest.newBuilder()
                    .setEmail(request.getEmail())
                    .setUsername(request.getUsername())
                    .setPassword(request.getPassword())
                    .setIsActivated(true)
                    .setIsEmailVerified(false)
                    .setIsAdmin(false)
                    .setIsSuspended(false)
                    .build();

            // user service is going to check if user already exist, then save it
            UserResponse userResponse = userService.createUser(userRequest);

            if (!userResponse.getSuccess()) {
                responseObserver.onNext(AuthResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("User creation failed: " + userResponse.getMessage())
                        .build());
                responseObserver.onCompleted();
                return;
            }

            // 2. Generate access + refresh tokens
            User user = User.newBuilder()
                    .setId(userResponse.getUser().getId())
                    .setEmail(userResponse.getUser().getEmail())
                    .setUsername(userResponse.getUser().getUsername())
                    .setIsAdmin(userResponse.getUser().getIsAdmin())
                    .setIsEmailVerified(userResponse.getUser().getIsEmailVerified())
                    .setIsActivated(userResponse.getUser().getIsActivated())
                    .setIsSuspended(userResponse.getUser().getIsSuspended())
                    .build();

            String accessToken = jwtUtil.generateToken(user);
            String refreshTokenStr = jwtUtil.generateRefreshToken(user);

            // 4. Create and persist refresh token
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(
                    userResponse.getUser().getId(),
                    7 * 24 * 60 * 60 * 1000L // 7 days in milliseconds
            );

            AuthResponse response = AuthResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("User created successfully")
                    .setToken("jwt_token_here")
                    .setUser(user)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error: " + e.getMessage())
                    .asRuntimeException());
        }
    }
}
