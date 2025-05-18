package com.adia.notification.grpc;

import com.adia.chat.grpc.ChatServiceGrpc;
import com.adia.chat.grpc.GetGroupConversationRequest;
import com.adia.chat.grpc.GroupConversation;
import com.adia.notification.GroupInviteRequest;
import com.adia.notification.Notification;
import com.adia.notification.NotificationServiceGrpc;
import com.adia.notification.UserRequest;
import com.adia.notification.entity.NotificationEntity;
import com.adia.notification.entity.NotificationMapper;
import com.adia.notification.repository.NotificationRepository;
import com.adia.notification.service.NotificationService;
import com.adia.user.GetUserRequest;
import com.adia.user.UserResponse;
import com.adia.user.UserServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.server.service.GrpcService;
import java.time.Instant;
import java.util.UUID;

@GrpcService
public class NotificationServiceImpl extends NotificationServiceGrpc.NotificationServiceImplBase {
    private final NotificationBroadcaster broadcaster;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userService;

    @GrpcClient("chat-service")
    private ChatServiceGrpc.ChatServiceBlockingStub chatService;

    public NotificationServiceImpl(NotificationBroadcaster broadcaster, NotificationService notificationService, NotificationRepository notificationRepository) {
        this.broadcaster = broadcaster;
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void streamNotifications(UserRequest request, StreamObserver<Notification> responseObserver) {
        String userId = request.getUserId();
        broadcaster.register(userId, responseObserver);
        ServerCallStreamObserver<Notification> serverObserver = (ServerCallStreamObserver<Notification>) responseObserver;
        serverObserver.setOnCancelHandler(() -> broadcaster.unregister(userId, responseObserver));
    }

    @Override
    public void notifyGroupInvite(GroupInviteRequest request, StreamObserver<Notification> responseObserver) {

        try {
            // get group
            GroupConversation gc = chatService.getGroupConversation(
                    GetGroupConversationRequest.newBuilder()
                            .setConversationId(Long.parseLong(request.getGroupId()))
                            .build());

            if(gc == null) {
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("This group does not exist")
                        .asRuntimeException());
                return;
            }

            UserResponse inverterRes = userService.getUser(
                    GetUserRequest.newBuilder()
                            .setId(Long.parseLong(request.getInviteeId()))
                            .build());

            UserResponse userRes = userService.getUser(
                    GetUserRequest.newBuilder()
                            .setId(Long.parseLong(request.getInviteeId()))
                            .build());

            if(!userRes.getSuccess()) {
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("This user does not exist")
                        .asRuntimeException());
            }

            String message = "@" + inverterRes.getUser().getUsername() + " invited you to group: " + gc.getName();
            String link = "/chat/" + request.getGroupId();

            NotificationEntity notification = notificationService.createNotification(Long.parseLong(request.getInviteeId()), message, link);

            // Save to database (optional)
            notificationRepository.save(notification);

            Notification notif = NotificationMapper.toGrpc(notification);

            // Push to frontend
            broadcaster.broadcastToUser(request.getInviteeId(), notif);

            // Return the notification
            responseObserver.onNext(notif);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Something went wrong!")
                    .asRuntimeException());
        }
    }

}