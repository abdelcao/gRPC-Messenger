package com.adia.chat.entity;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationWithLastMessage {
    private Conversation conversation;
    private Message lastMessage;
} 