-- Create database
CREATE DATABASE IF NOT EXISTS chatdb;

USE chatdb;

-- Create Users table
CREATE TABLE Users IF NOT EXISTS (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    isAdmin BOOLEAN DEFAULT FALSE,
    isEmailVerified BOOLEAN DEFAULT FALSE,
    isActivated BOOLEAN DEFAULT TRUE,
    isSuspended BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tokens
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(512) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_user FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE
);

CREATE INDEX idx_refresh_token_user ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_token_expiry ON refresh_tokens(expiry_date);

-- Create Conversations table
CREATE TABLE Conversations IF NOT EXISTS (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    owner_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES Users(id)
);

-- Create Messages table
CREATE TABLE Messages IF NOT EXISTS (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    text TEXT NOT NULL,
    user_id INTEGER NOT NULL,
    conversation_id INTEGER NOT NULL,
    status ENUM('sent', 'delivered', 'read') DEFAULT 'sent',
    isEdited BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(id),
    FOREIGN KEY (conversation_id) REFERENCES Conversations(id)
);

-- Create PrivateConversations table
CREATE TABLE PrivateConversations IF NOT EXISTS (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    conversation_id INTEGER NOT NULL,
    receiver_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (conversation_id) REFERENCES Conversations(id),
    FOREIGN KEY (receiver_id) REFERENCES Users(id)
);

-- Create GroupeConversations table
CREATE TABLE GroupeConversations IF NOT EXISTS (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    conversation_id INTEGER NOT NULL,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (conversation_id) REFERENCES Conversations(id)
);

-- Create GroupeMembers table
CREATE TABLE GroupeMembers IF NOT EXISTS (
    user_id INTEGER NOT NULL,
    groupe_id INTEGER NOT NULL,
    isAdmin BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, groupe_id),
    FOREIGN KEY (user_id) REFERENCES Users(id),
    FOREIGN KEY (groupe_id) REFERENCES GroupeConversations(id)
);

-- Create Notifications table
CREATE TABLE Notifications IF NOT EXISTS (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    content TEXT NOT NULL,
    user_id INTEGER NOT NULL,
    status ENUM('read', 'unread', 'dismissed') DEFAULT 'unread',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(id)
);

-- Create Reports table
CREATE TABLE Reports IF NOT EXISTS (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    text TEXT NOT NULL,
    reporter_id INTEGER NOT NULL,
    reported_id INTEGER NOT NULL,
    cause TEXT NOT NULL,
    status ENUM('pending', 'resolved', 'dismissed') DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (reporter_id) REFERENCES Users(id),
    FOREIGN KEY (reported_id) REFERENCES Users(id)
);

-- Dummy data

-- Insert sample users
INSERT INTO Users (username, email, password, isAdmin, isEmailVerified, isActivated, isSuspended)
VALUES 
('admin', 'admin@gmail.com', 'admin', TRUE, TRUE, TRUE, FALSE),
('yassine', 'yassine@gmail.com', 'yassine', FALSE, TRUE, TRUE, FALSE),
('youssef', 'youssef@gmail.com', 'youssef', FALSE, TRUE, TRUE, FALSE),
('hamid', 'hamid@gmail.com', 'hamid', FALSE, TRUE, TRUE, FALSE),
('yasser', 'yasser@gmail.com', 'yasser', FALSE, FALSE, TRUE, FALSE);

-- Insert sample conversations
INSERT INTO Conversations (owner_id)
VALUES 
(1), -- admin conversation
(2), -- yassine conversation
(1), -- admin group conversation
(3); -- youssef conversation

-- Insert sample private conversations
INSERT INTO PrivateConversations (conversation_id, receiver_id)
VALUES 
(1, 2), -- admin and yassine
(2, 1), -- 
(4, 4); -- 

-- Insert sample group conversations
INSERT INTO GroupeConversations (conversation_id, name)
VALUES 
(3, 'Project Team'); -- admin's group conversation

-- Insert sample group members
INSERT INTO GroupeMembers (user_id, groupe_id, isAdmin)
VALUES 
(1, 1, TRUE),  -- admin is admin in Project Team
(2, 1, FALSE), -- yassine is member in Project Team
(3, 1, FALSE), -- 
(4, 1, FALSE); --

-- Insert sample messages
INSERT INTO Messages (text, user_id, conversation_id, status, isEdited)
VALUES 
('Hey Jane, how are you?', 1, 1, 'sent', FALSE),
('I am doing well, thanks for asking!', 2, 1, 'read', FALSE),
('Can we meet tomorrow for the project discussion?', 1, 1, 'read', FALSE),
('Sure, what time works for you?', 2, 1, 'delivered', FALSE),
('How about 10 AM?', 1, 1, 'delivered', TRUE),
('Welcome everyone to our project team!', 1, 3, 'delivered', FALSE),
('Thanks for adding me, John.', 2, 3, 'read', FALSE),
('Looking forward to working with everyone.', 3, 3, 'read', FALSE),
('Let me know the meeting schedule.', 4, 3, 'delivered', FALSE);

-- Insert sample notifications
INSERT INTO Notifications (content, user_id, status)
VALUES 
('You have a new message from Jane', 1, 'unread'),
('John sent you a message', 2, 'read'),
('You were added to Project Team group', 3, 'unread'),
('Bob replied to your message', 1, 'unread'),
('Your account email has been verified', 5, 'unread');

-- Insert sample reports
INSERT INTO Reports (text, reporter_id, reported_id, cause, status)
VALUES 
('This user is sending spam messages', 2, 5, 'spam', 'pending'),
('Inappropriate content in messages', 3, 5, 'inappropriate content', 'resolved'),
('Harassment in private messages', 4, 5, 'harassment', 'pending');
