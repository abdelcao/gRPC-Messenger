package com.adia.chat.grpc;

import com.adia.chat.grpc.Message;
import io.grpc.stub.StreamObserver;
import io.grpc.stub.ServerCallStreamObserver;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageBroadcaster {
    private static final Logger logger = LoggerFactory.getLogger(MessageBroadcaster.class);
    
    // Map: conversationId -> list of observers
    private final Map<Integer, List<StreamObserver<Message>>> observers = new ConcurrentHashMap<>();

    public void register(int conversationId, StreamObserver<Message> observer) {
        if (observer instanceof ServerCallStreamObserver) {
            ServerCallStreamObserver<Message> serverObserver = (ServerCallStreamObserver<Message>) observer;
            serverObserver.setOnCancelHandler(() -> {
                logger.debug("Client cancelled stream for conversation {}", conversationId);
                unregister(conversationId, observer);
            });
        }
        observers.computeIfAbsent(conversationId, k -> new CopyOnWriteArrayList<>()).add(observer);
    }

    public void unregister(int conversationId, StreamObserver<Message> observer) {
        List<StreamObserver<Message>> list = observers.get(conversationId);
        if (list != null) {
            list.remove(observer);
            if (list.isEmpty()) {
                observers.remove(conversationId);
            }
        }
    }

    public void broadcast(int conversationId, Message message) {
        List<StreamObserver<Message>> list = observers.get(conversationId);
        if (list != null) {
            for (StreamObserver<Message> observer : list) {
                try {
                    observer.onNext(message);
                } catch (Exception e) {
                    logger.warn("Failed to send message to client in conversation {}: {}", conversationId, e.getMessage());
                    unregister(conversationId, observer);
                }
            }
        }
    }
} 