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
import java.util.*;
import java.util.stream.Collectors;

@GrpcService
public class    ChatGrpcService extends ChatServiceGrpc.ChatServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(ChatGrpcService.class);
    private static final MessageBroadcaster messageBroadcaster = new MessageBroadcaster();

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userService;

    private final ConversationRepository conversationRepo;
    private final PrivateConversationRepository privateConvRepo;
    private final MessageRepository messageRepo;

    public ChatGrpcService(
        ConversationRepository conversationRepo,
        PrivateConversationRepository privateConvRepo,
        MessageRepository messageRepository
    ) {
        this.conversationRepo = conversationRepo;
        this.privateConvRepo = privateConvRepo;
        this.messageRepo = messageRepository;
    }
    
    @Override
    public void getPrivateConversations(PrivateConvsReq request, StreamObserver<GetPrivConvsRes> responseObserver) {
        try {
            Long userId = request.getUserId();
            logger.info("Fetching private conversations for user ID: {}", userId);
            
            // Validate userId
            if (userId == null || userId <= 0) {
                throw new IllegalArgumentException("Invalid user ID");
            }

            // Fetch private conversations for the user
            List<PrivateConversationEntity> privConvs = privateConvRepo.findPrivateConversationsByUser(userId);
            List<PrivateConv> result = new ArrayList<>();

            // Map each entity to a gRPC message
            for (PrivateConversationEntity entity : privConvs) {
                try {
                    PrivateConv grpcConv = mapToGrpcPrivateConv(entity, userId);
                    result.add(grpcConv);
                } catch (Exception e) {
                    logger.warn("Error mapping conversation {}: {}", entity.getId(), e.getMessage());
                    // Continue with next conversation instead of failing the entire request
                }
            }

            // Build and send the response
            GetPrivConvsRes response = GetPrivConvsRes.newBuilder()
                    .setMessage("Fetched " + result.size() + " conversations successfully")
                    .setSuccess(true)
                    .addAllPrivateConvList(result)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
            logger.info("Successfully returned {} private conversations for user ID: {}", result.size(), userId);

        } catch (Exception e) {
            logger.error("Error in getPrivateConversations: {}", e.getMessage(), e);
            GetPrivConvsRes errorResponse = GetPrivConvsRes.newBuilder()
                    .setMessage("Failed to fetch private conversations: " + e.getMessage())
                    .setSuccess(false)
                    .build();
            responseObserver.onNext(errorResponse);
            responseObserver.onCompleted();
        }
    }
    
    @Override
    public void createPrivateConversation(CreatePrivConvReq request, StreamObserver<CreatePrivConvRes> responseObserver) {
        try {
            Long currUserId = request.getCurrUserId();
            Long otherUserId = request.getOtherUserId();
            
            // Check if a conversation already exists between these users
            Optional<PrivateConversationEntity> existingConversation = 
                privateConvRepo.findByConversationOwnerIdAndReceiverId(currUserId, otherUserId);

            PrivateConversationEntity privateConversation;
            
            if (existingConversation.isPresent()) {
                // Use existing conversation
                privateConversation = existingConversation.get();
            } else {
                // Create a new conversation
                ConversationEntity conversation = new ConversationEntity();
                conversation.setOwnerId(currUserId);
                conversation.setType(ConversationType.PRIVATE);
                conversation = conversationRepo.save(conversation);
                
                // Create private conversation details
                privateConversation = new PrivateConversationEntity();
                privateConversation.setConversation(conversation);
                privateConversation.setReceiverId(otherUserId);
                privateConversation = privateConvRepo.save(privateConversation);
                
                // Set the bidirectional relationship
                conversation.setPrivateConversationDetails(privateConversation);
                conversationRepo.save(conversation);
            }
            
            // Build the response
            CreatePrivConvRes response = CreatePrivConvRes.newBuilder()
                .setConversationId(privateConversation.getId())
                .setCurrUserId(privateConversation.getConversation().getOwnerId())
                .setReceiverId(privateConversation.getReceiverId())
                .setCreatedAt(privateConversation.getCreatedAt().toString())
                .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            logger.error("Error in createPrivateConversation: {}", e.getMessage(), e);
            responseObserver.onError(
                Status.INTERNAL
                    .withDescription("Failed to create private conversation: " + e.getMessage())
                    .asRuntimeException()
            );
        }
    }

    private PrivateConv mapToGrpcPrivateConv(PrivateConversationEntity entity, Long currentUserId) {
        ConversationEntity conversation = entity.getConversation();

        // Determine who is the 'other user' in this private conversation
        Long otherUserId;
        if (conversation.getOwnerId().equals(currentUserId)) {
            otherUserId = entity.getReceiverId();
        } else {
            otherUserId = conversation.getOwnerId();
        }

        // Fetch other user details from user service
        User otherUser = null;
        try {
            UserResponse userRes = userService.getUser(GetUserRequest.newBuilder().setId(otherUserId).build());
            if (userRes.getSuccess() && userRes.hasUser()) {
                otherUser = userRes.getUser();
            } else {
                logger.warn("Other user with ID {} not found for conversation {}", otherUserId, entity.getId());
            }
        } catch (Exception e) {
            logger.error("Error fetching user details for ID {}: {}", otherUserId, e.getMessage());
        }

        // Get the last message
        String lastMessageText = "";
        Set<MessageEntity> messages = conversation.getMessages();
        if (messages != null && !messages.isEmpty()) {
            Optional<MessageEntity> lastMsgOpt = messages.stream()
                    .max(Comparator.comparing(MessageEntity::getCreatedAt));
            lastMessageText = lastMsgOpt.map(MessageEntity::getText).orElse("");
        }

        // Calculate unread count
        long unreadCount = 0;
        if (messages != null) {
            unreadCount = messages.stream()
                    .filter(msg -> !msg.getUserId().equals(currentUserId) && msg.getStatus() != MessageStatus.read)
                    .count();
        }

        // Convert LocalDateTime to Protobuf Timestamp
        Instant updatedAtInstant = conversation.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant();
        Timestamp lastUpdateTimestamp = Timestamp.newBuilder()
                .setSeconds(updatedAtInstant.getEpochSecond())
                .setNanos(updatedAtInstant.getNano())
                .build();

        // Build the PrivateConv message
        PrivateConv.Builder builder = PrivateConv.newBuilder()
                .setId(entity.getId())
                .setLastMessage(lastMessageText)
                .setUnreadCount((int) unreadCount)
                .setLastUpdate(lastUpdateTimestamp);
        
        // Set other user if available
        if (otherUser != null) {
            builder.setOtherUser(otherUser);
        }

        return builder.build();
    }
 
    @Override
    public void getPrivateConversation(GetPrivConvReq request, StreamObserver<GetPrivConvRes> responseObserver) {
        try {
            Optional<PrivateConversationEntity> conversationOpt =
                privateConvRepo.findByConversationOwnerIdAndReceiverId(
                        request.getCurrUserId(),
                        request.getOtherUserId());

            if (conversationOpt.isEmpty()) {
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("Conversation not found between the specified users.")
                        .asRuntimeException());
                return;
            }

            PrivateConversationEntity conversation = conversationOpt.get();

            List<MessageEntity> messages = messageRepo.findByConversationId(conversation.getId());

            GetPrivConvRes response = GetPrivConvRes.newBuilder()
                    .setId(conversation.getId())
                    .addAllMessages(messages.stream()
                            .map(this::toGrpcMessage)
                            .collect(Collectors.toList()))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal error")
                    .augmentDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    private Message toGrpcMessage(MessageEntity msg) {
        return Message.newBuilder()
                .setId(msg.getId())
                .setUserId(msg.getUserId())
                .setConversationId(msg.getConversation().getId())
                .setText(msg.getText())
                .setEdited(msg.isEdited())
                .setStatusValue(msg.getStatus().ordinal())
                .setCreatedAt(msg.getCreatedAt().toString())
                .setUpdatedAt(msg.getUpdatedAt().toString())
                .build();
    }
}