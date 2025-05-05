package com.adia.user.grpc;

import com.adia.user.*;
import com.adia.user.entity.UserEntity;
import com.adia.user.repository.UserRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@GrpcService
public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void createUser(CreateUserRequest request, StreamObserver<UserResponse> responseObserver) {
        try {

            System.out.println(request.toString());

            if (userRepository.existsByEmail(request.getEmail())) {
                responseObserver.onError(Status.ALREADY_EXISTS
                        .withDescription("Email already in use")
                        .asRuntimeException());
                return;
            }

            if (userRepository.existsByUsername(request.getUsername())) {
                responseObserver.onError(Status.ALREADY_EXISTS
                        .withDescription("Username already in use")
                        .asRuntimeException());
                return;
            }

            // Map request to entity
            UserEntity userEntity = UserEntity.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword())) // hash this in real use!
                    .isAdmin(request.getIsAdmin())
                    .isEmailVerified(request.getIsEmailVerified())
                    .isActivated(request.getIsActivated())
                    .isSuspended(request.getIsSuspended())
                    .build();

            // Save to DB
            userEntity = userRepository.save(userEntity);

            // Build response
            UserResponse response = UserResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("User created successfully")
                    .setUser(toProtoUser(userEntity))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {

            System.out.println(e.getMessage());

            responseObserver.onNext(UserResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Error: " + e.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void listUsers(Empty request, StreamObserver<ListUsersResponse> responseObserver) {
        try {
            List<UserEntity> userEntities = userRepository.findAll();

            ListUsersResponse.Builder responseBuilder = ListUsersResponse.newBuilder();

            for (UserEntity entity : userEntities) {
                User user = this.toProtoUser(entity).build();
                responseBuilder.addUsers(user);
            }

            responseBuilder.setMessage("Users retrived successfully");
            responseBuilder.setSuccess(true);

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onNext(ListUsersResponse.newBuilder()
                    .setMessage("Error: " + e.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getUser(GetUserRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            Optional<UserEntity> userEntity = userRepository.getUserEntityById(request.getId());

            if (userEntity.isEmpty()) {
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("User not found")
                        .asRuntimeException());
                return;
            }

            UserResponse response = UserResponse.newBuilder()
                    .setMessage("User retrived")
                    .setSuccess(true)
                    .setUser(toProtoUser(userEntity.get()))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onNext(UserResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Error: " + e.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    private User.Builder toProtoUser(UserEntity user) {
        return User.newBuilder()
                .setId(user.getId())
                .setUsername(user.getUsername())
                .setEmail(user.getEmail())
                .setIsAdmin(user.getIsAdmin())
                .setIsEmailVerified(user.getIsEmailVerified())
                .setIsActivated(user.getIsActivated())
                .setIsSuspended(user.getIsSuspended())
                .setCreatedAt(user.getCreatedAt().toString())
                .setUpdatedAt(user.getUpdatedAt().toString());
    }

}

