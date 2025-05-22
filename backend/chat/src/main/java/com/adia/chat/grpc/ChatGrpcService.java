package com.adia.chat.grpc;

import com.adia.chat.repository.*;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;

@GrpcService
public class ChatGrpcService extends com.adia.chat.grpc.ChatServiceGrpc.ChatServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(ChatGrpcService.class);
    private GroupeMemberRepository groupeMemberRepository;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final MessageBroadcaster messageBroadcaster = new MessageBroadcaster();
    private static final ConversationBroadcaster conversationBroadcaster = new ConversationBroadcaster();

    
} 