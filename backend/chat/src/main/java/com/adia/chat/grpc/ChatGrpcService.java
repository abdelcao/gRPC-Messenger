package com.adia.chat.grpc;

import com.adia.chat.entity.*;
import com.adia.chat.repository.*;
import com.adia.chat.service.ChatService;
import com.adia.chat.grpc.AddMemberToGroupRequest;
import com.adia.chat.grpc.Conversation;
import com.adia.chat.grpc.CreateConversationRequest;
import com.adia.chat.grpc.CreateGroupConversationRequest;
import com.adia.chat.grpc.CreatePrivateConversationRequest;
import com.adia.chat.grpc.EditMessageRequest;
import com.adia.chat.grpc.Empty;
import com.adia.chat.grpc.GetConversationMessagesRequest;
import com.adia.chat.grpc.GetConversationRequest;
import com.adia.chat.grpc.GetGroupConversationRequest;
import com.adia.chat.grpc.GetPrivateConversationRequest;
import com.adia.chat.grpc.GroupConversation;
import com.adia.chat.grpc.GroupMember;
import com.adia.chat.grpc.MakeGroupAdminRequest;
import com.adia.chat.grpc.Message;
import com.adia.chat.grpc.MessageStatus;
import com.adia.chat.grpc.PrivateConversation;
import com.adia.chat.grpc.RemoveMemberFromGroupRequest;
import com.adia.chat.grpc.SendMessageRequest;
import com.adia.chat.grpc.UpdateMessageStatusRequest;
import com.adia.chat.grpc.ChatProto.*;
import com.adia.chat.grpc.ChatServiceGrpc.ChatServiceImplBase;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;

@GrpcService
public class ChatGrpcService extends com.adia.chat.grpc.ChatServiceGrpc.ChatServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(ChatGrpcService.class);

    @Autowired
    private ChatService chatService;

    @Autowired
    private GroupeMemberRepository groupeMemberRepository;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // Add broadcaster instance
    private static final MessageBroadcaster messageBroadcaster = new MessageBroadcaster();

    @Override
    public void createConversation(com.adia.chat.grpc.CreateConversationRequest request, StreamObserver<Conversation> responseObserver) {
        com.adia.chat.entity.Conversation entityConversation = chatService.createConversation(Integer.valueOf((int) request.getOwnerId()));
        Conversation grpcConversation = mapToGrpcConversation(entityConversation);
        responseObserver.onNext(grpcConversation);
        responseObserver.onCompleted();
    }

    @Override
    public void getConversation(com.adia.chat.grpc.GetConversationRequest request, StreamObserver<Conversation> responseObserver) {
        com.adia.chat.entity.Conversation entityConversation = chatService.getConversation(Integer.valueOf((int) request.getId()));
        Conversation grpcConversation = mapToGrpcConversation(entityConversation);
        responseObserver.onNext(grpcConversation);
        responseObserver.onCompleted();
    }

    @Override
    public void sendMessage(com.adia.chat.grpc.SendMessageRequest request, StreamObserver<Message> responseObserver) {
        com.adia.chat.entity.Message entityMessage = chatService.sendMessage(
            Integer.valueOf((int) request.getUserId()),
            Integer.valueOf((int) request.getConversationId()),
            request.getText()
        );
        Message grpcMessage = mapToGrpcMessage(entityMessage);
        responseObserver.onNext(grpcMessage);
        responseObserver.onCompleted();

        // Broadcast to all active streams for this conversation
        messageBroadcaster.broadcast(entityMessage.getConversationId(), grpcMessage);
    }

    @Override
    public void editMessage(com.adia.chat.grpc.EditMessageRequest request, StreamObserver<Message> responseObserver) {
        com.adia.chat.entity.Message entityMessage = chatService.editMessage(
            Integer.valueOf((int) request.getMessageId()),
            request.getNewText()
        );
        Message grpcMessage = mapToGrpcMessage(entityMessage);
        responseObserver.onNext(grpcMessage);
        responseObserver.onCompleted();
    }

    @Override
    public void updateMessageStatus(com.adia.chat.grpc.UpdateMessageStatusRequest request, StreamObserver<Message> responseObserver) {
        chatService.updateMessageStatus(
            Integer.valueOf((int) request.getMessageId()),
            com.adia.chat.entity.Message.MessageStatus.valueOf(request.getStatus().name())
        );
        responseObserver.onCompleted();
    }

    @Override
    public void getConversationMessages(com.adia.chat.grpc.GetConversationMessagesRequest request, StreamObserver<Message> responseObserver) {
        int conversationId = (int) request.getConversationId();

        // 1. Send existing messages
        chatService.getConversationMessages(conversationId)
            .forEach(entityMessage -> {
                Message grpcMessage = mapToGrpcMessage(entityMessage);
                responseObserver.onNext(grpcMessage);
            });

        // 2. Register for new messages
        messageBroadcaster.register(conversationId, responseObserver);
        // 3. Do NOT call responseObserver.onCompleted() here!
        // Optionally, handle client disconnects (not shown here)
    }

    @Override
    public void createPrivateConversation(com.adia.chat.grpc.CreatePrivateConversationRequest request, StreamObserver<PrivateConversation> responseObserver) {
        com.adia.chat.entity.PrivateConversation entityConversation = chatService.createPrivateConversation(
            Integer.valueOf((int) request.getOwnerId()),
            Integer.valueOf((int) request.getReceiverId())
        );
        PrivateConversation grpcConversation = mapToGrpcPrivateConversation(entityConversation);
        responseObserver.onNext(grpcConversation);
        responseObserver.onCompleted();
    }

    @Override
    public void getPrivateConversation(com.adia.chat.grpc.GetPrivateConversationRequest request, StreamObserver<PrivateConversation> responseObserver) {
        com.adia.chat.entity.PrivateConversation entityConversation = chatService.getPrivateConversation(Integer.valueOf((int) request.getConversationId()));
        PrivateConversation grpcConversation = mapToGrpcPrivateConversation(entityConversation);
        responseObserver.onNext(grpcConversation);
        responseObserver.onCompleted();
    }

    @Override
    public void getUserPrivateConversations(com.adia.chat.grpc.GetUserConversationsRequest request, StreamObserver<PrivateConversation> responseObserver) {
        chatService.getUserPrivateConversations(Integer.valueOf((int) request.getUserId()))
            .forEach(entityConversation -> {
                PrivateConversation grpcConversation = mapToGrpcPrivateConversation(entityConversation);
                responseObserver.onNext(grpcConversation);
            });
        responseObserver.onCompleted();
    }

    @Override
    public void createGroupConversation(com.adia.chat.grpc.CreateGroupConversationRequest request, StreamObserver<GroupConversation> responseObserver) {
        com.adia.chat.entity.GroupeConversation entityConversation = chatService.createGroupConversation(
            Integer.valueOf((int) request.getOwnerId()),
            request.getName()
        );
        GroupConversation grpcConversation = mapToGrpcGroupConversation(entityConversation);
        responseObserver.onNext(grpcConversation);
        responseObserver.onCompleted();
    }

    @Override
    public void getGroupConversation(com.adia.chat.grpc.GetGroupConversationRequest request, StreamObserver<GroupConversation> responseObserver) {
        com.adia.chat.entity.GroupeConversation entityConversation = chatService.getGroupConversation(Integer.valueOf((int) request.getConversationId()));
        GroupConversation grpcConversation = mapToGrpcGroupConversation(entityConversation);
        responseObserver.onNext(grpcConversation);
        responseObserver.onCompleted();
    }

    @Override
    public void getUserGroupConversations(com.adia.chat.grpc.GetUserConversationsRequest request, StreamObserver<GroupConversation> responseObserver) {
        chatService.getUserGroupConversations(Integer.valueOf((int) request.getUserId()))
            .forEach(entityConversation -> {
                GroupConversation grpcConversation = mapToGrpcGroupConversation(entityConversation);
                responseObserver.onNext(grpcConversation);
            });
        responseObserver.onCompleted();
    }

    @Override
    public void addMemberToGroup(com.adia.chat.grpc.AddMemberToGroupRequest request, StreamObserver<GroupMember> responseObserver) {
        try {
            chatService.addMemberToGroup(Integer.valueOf((int) request.getGroupId()), Integer.valueOf((int) request.getUserId()));
            
            // Get the created member and map it to gRPC response
            com.adia.chat.entity.GroupeMember member = groupeMemberRepository.findByUserIdAndGroupeId(
                Integer.valueOf((int) request.getUserId()),
                Integer.valueOf((int) request.getGroupId())
            ).orElseThrow(() -> new RuntimeException("Failed to find created group member"));
            
            GroupMember grpcMember = GroupMember.newBuilder()
                .setUserId(member.getUserId())
                .setGroupId(member.getGroupeId())
                .setAdmin(member.isAdmin())
                .setCreatedAt(member.getCreatedAt().format(DATE_TIME_FORMATTER))
                .build();
                
            responseObserver.onNext(grpcMember);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void removeMemberFromGroup(com.adia.chat.grpc.RemoveMemberFromGroupRequest request, StreamObserver<Empty> responseObserver) {
        chatService.removeMemberFromGroup(Integer.valueOf((int) request.getGroupId()), Integer.valueOf((int) request.getUserId()));
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void makeGroupAdmin(com.adia.chat.grpc.MakeGroupAdminRequest request, StreamObserver<GroupMember> responseObserver) {
        chatService.makeGroupAdmin(Integer.valueOf((int) request.getGroupId()), Integer.valueOf((int) request.getUserId()));
        responseObserver.onCompleted();
    }

    @Override
    public void getUserConversations(com.adia.chat.grpc.GetUserConversationsRequest request, StreamObserver<Conversation> responseObserver) {
        try {
            chatService.getUserConversations(Integer.valueOf((int) request.getUserId()))
                .forEach(entityConversation -> {
                    try {
                        Conversation grpcConversation = mapToGrpcConversation(entityConversation);
                        responseObserver.onNext(grpcConversation);
                    } catch (Exception e) {
                        // Log error but continue processing other conversations
                        logger.error("Error processing conversation {}: {}", entityConversation.getId(), e.getMessage());
                    }
                });
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    // Helper methods to map between entity and gRPC objects
    private Conversation mapToGrpcConversation(com.adia.chat.entity.Conversation entity) {
        return Conversation.newBuilder()
            .setId(entity.getId())
            .setOwnerId(entity.getOwnerId())
            .setCreatedAt(entity.getCreatedAt().format(DATE_TIME_FORMATTER))
            .setUpdatedAt(entity.getUpdatedAt().format(DATE_TIME_FORMATTER))
            .build();
    }

    private Message mapToGrpcMessage(com.adia.chat.entity.Message entity) {
        return Message.newBuilder()
            .setId(entity.getId())
            .setUserId(entity.getUserId())
            .setConversationId(entity.getConversationId())
            .setText(entity.getText())
            .setEdited(entity.isEdited())
            .setStatus(MessageStatus.valueOf(entity.getStatus().name().toUpperCase()))
            .setCreatedAt(entity.getCreatedAt().format(DATE_TIME_FORMATTER))
            .setUpdatedAt(entity.getUpdatedAt().format(DATE_TIME_FORMATTER))
            .build();
    }

    private PrivateConversation mapToGrpcPrivateConversation(com.adia.chat.entity.PrivateConversation entity) {
        return PrivateConversation.newBuilder()
            .setId(entity.getId())
            .setConversationId(entity.getConversationId())
            .setReceiverId(entity.getReceiverId())
            .setCreatedAt(entity.getCreatedAt().format(DATE_TIME_FORMATTER))
            .setUpdatedAt(entity.getUpdatedAt().format(DATE_TIME_FORMATTER))
            .build();
    }

    private GroupConversation mapToGrpcGroupConversation(com.adia.chat.entity.GroupeConversation entity) {
        return GroupConversation.newBuilder()
            .setId(entity.getId())
            .setConversationId(entity.getConversationId())
            .setName(entity.getName())
            .setCreatedAt(entity.getCreatedAt().format(DATE_TIME_FORMATTER))
            .setUpdatedAt(entity.getUpdatedAt().format(DATE_TIME_FORMATTER))
            .build();
    }
} 