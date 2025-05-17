package com.adia.chat.grpc;

import com.adia.chat.grpc.Conversation;
import io.grpc.stub.StreamObserver;
import io.grpc.stub.ServerCallStreamObserver;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConversationBroadcaster {
    private static final Logger logger = LoggerFactory.getLogger(ConversationBroadcaster.class);

    // Map: userId -> list of observers
    private final Map<Integer, List<StreamObserver<Conversation>>> observers = new ConcurrentHashMap<>();

    public void register(int userId, StreamObserver<Conversation> observer) {
        if (observer instanceof ServerCallStreamObserver) {
            ServerCallStreamObserver<Conversation> serverObserver = (ServerCallStreamObserver<Conversation>) observer;
            serverObserver.setOnCancelHandler(() -> {
                logger.debug("Client cancelled stream for user {}", userId);
                unregister(userId, observer);
            });
        }
        observers.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(observer);
    }

    public void unregister(int userId, StreamObserver<Conversation> observer) {
        List<StreamObserver<Conversation>> list = observers.get(userId);
        if (list != null) {
            list.remove(observer);
            if (list.isEmpty()) {
                observers.remove(userId);
            }
        }
    }

    public void broadcast(int userId, Conversation conversation) {
        List<StreamObserver<Conversation>> list = observers.get(userId);
        if (list != null) {
            for (StreamObserver<Conversation> observer : list) {
                try {
                    observer.onNext(conversation);
                } catch (Exception e) {
                    logger.warn("Failed to send conversation to client for user {}: {}", userId, e.getMessage());
                    unregister(userId, observer);
                }
            }
        }
    }
} 