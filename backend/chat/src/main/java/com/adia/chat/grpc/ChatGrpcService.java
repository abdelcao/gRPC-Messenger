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

import java.time.format.DateTimeFormatter;

@GrpcService
public class ChatGrpcService extends com.adia.chat.grpc.ChatServiceGrpc.ChatServiceImplBase {

    @Autowired
    private ChatService chatService;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

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
        chatService.getConversationMessages(Integer.valueOf((int) request.getConversationId()))
            .forEach(entityMessage -> {
                Message grpcMessage = mapToGrpcMessage(entityMessage);
                responseObserver.onNext(grpcMessage);
            });
        responseObserver.onCompleted();
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
    public void addMemberToGroup(com.adia.chat.grpc.AddMemberToGroupRequest request, StreamObserver<GroupMember> responseObserver) {
        chatService.addMemberToGroup(Integer.valueOf((int) request.getGroupId()), Integer.valueOf((int) request.getUserId()));
        responseObserver.onCompleted();
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