package com.adia.chat.grpc;

import com.adia.chat.entity.ConversationEntity;
import com.adia.chat.entity.ConversationType;
import com.adia.chat.entity.MessageEntity;
import com.adia.chat.entity.MessageStatus;
import com.adia.chat.entity.PrivateConversationEntity;
import com.adia.chat.repository.*;
import com.adia.user.GetUserRequest;
import com.adia.user.User;
import com.adia.user.UserResponse;
import com.adia.user.UserServiceGrpc;
import com.google.protobuf.Timestamp;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneId;

import java.time.ZoneId;
import java.util.*;

@GrpcService
public class ChatGrpcService extends ChatServiceGrpc.ChatServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(ChatGrpcService.class);
    private static final MessageBroadcaster messageBroadcaster = new MessageBroadcaster();

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userService;

    private final ConversationRepository conversationRepo;
    private final PrivateConversationRepository privateConvRepo;

    public ChatGrpcService(ConversationRepository conversationRepo, PrivateConversationRepository privateConvRepo) {
        this.conversationRepo = conversationRepo;
        this.privateConvRepo = privateConvRepo;
    }

    @Override
    public void getPrivateConversations(PrivateConvReq request, StreamObserver<GetPrivConvRes> responseObserver) {
        try {
            Long userId = request.getUserId();
            List<PrivateConversationEntity> privConvs = privateConvRepo.findPrivateConversationsByUser(userId);
            List<PrivateConv> result = new ArrayList<>();

            for (PrivateConversationEntity entity : privConvs) {
                ConversationEntity conversation = entity.getConversation();

                Long otherUserId = conversation.getOwnerId().equals(userId)
                        ? entity.getReceiverId()
                        : conversation.getOwnerId();

                // Fetch other user from user service (stubbed)
                UserResponse userRes = userService.getUser(GetUserRequest.newBuilder()
                        .setId(otherUserId).build());

                // Last message
                Optional<MessageEntity> lastMsgOpt = conversation.getMessages().stream().max(Comparator.comparing(MessageEntity::getCreatedAt));

                String lastMessage = lastMsgOpt.map(MessageEntity::getText).orElse("");

                // Unread count
                long unreadCount = conversation.getMessages().stream()
                        .filter(msg -> !msg.getUserId().equals(userId) && msg.getStatus() != MessageStatus.read)
                        .count();

                // Timestamp conversion
                Instant instant = conversation.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant();
                Timestamp ts = Timestamp.newBuilder()
                        .setSeconds(instant.getEpochSecond())
                        .setNanos(instant.getNano())
                        .build();

                // Build PrivateConv
                PrivateConv grpcConv = PrivateConv.newBuilder()
                        .setId(entity.getId())
                        .setOtherUser(userRes.getUser())
                        .setLastMessage(lastMessage)
                        .setUnreadCount((int) unreadCount)
                        .setLastUpdate(ts)
                        .build();

                result.add(grpcConv);
            }

            // Build response wrapper
            GetPrivConvRes response = GetPrivConvRes.newBuilder()
                    .setMessage("Fetched successfully")
                    .setSuccess(true)
                    .addAllPrivateConvList(result)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            logger.error("Error in getPrivateConversations: {}", e.getMessage(), e);

            GetPrivConvRes errorResponse = GetPrivConvRes.newBuilder()
                    .setMessage("Failed to fetch private conversations")
                    .setSuccess(false)
                    .build();

            responseObserver.onNext(errorResponse);
            responseObserver.onCompleted(); // Still complete after sending the error response
        }
    }

    @Override
    public void createPrivateConversation(CreatePrivConvReq request, StreamObserver<PrivateConv> responseObserver) {
        try {
            Long ownerId = request.getCurrUserId();
            Long receiverId = request.getOtherUserId();

            // Step 1: Check if a conversation already exists
            Optional<PrivateConversationEntity> existing = privateConvRepo
                    .findByOwnerIdAndReceiverIdOrReceiverIdAndOwnerId(ownerId, receiverId, ownerId, receiverId);

            if (existing.isPresent()) {
                responseObserver.onNext(mapToGrpcPrivateConv(existing.get()));
                responseObserver.onCompleted();
                return;
            }

            // Step 2: Create conversation
            ConversationEntity conversation = new ConversationEntity();
            conversation.setOwnerId(ownerId);
            conversation.setType(ConversationType.PRIVATE);
            conversation = conversationRepo.save(conversation);

            // Step 3: Create private conversation
            PrivateConversationEntity privConv = new PrivateConversationEntity();
            privConv.setConversation(conversation);
            privConv.setReceiverId(receiverId);
            privConv = privateConvRepo.save(privConv);

            // Step 4: Respond with gRPC object
            responseObserver.onNext(mapToGrpcPrivateConv(privConv));
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription("Failed to create conversation").withCause(e).asRuntimeException());
        }
    }

}