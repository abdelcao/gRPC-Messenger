-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL COMMENT 'Should store only hashed passwords',
    is_admin BOOLEAN DEFAULT FALSE,
    is_email_verified BOOLEAN DEFAULT FALSE,
    is_activated BOOLEAN DEFAULT TRUE,
    is_suspended BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create conversations table
CREATE TABLE IF NOT EXISTS conversations (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    owner_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES users(id)
);

-- Tokens table
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(512) NOT NULL UNIQUE,
    user_id INTEGER NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create messages table
CREATE TABLE IF NOT EXISTS messages (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    text TEXT NOT NULL,
    user_id INTEGER NOT NULL,
    conversation_id INTEGER NOT NULL,
    status ENUM('sent', 'delivered', 'read') DEFAULT 'sent',
    is_edited BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE
);

-- Create private_conversations table
CREATE TABLE IF NOT EXISTS private_conversations (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    conversation_id INTEGER NOT NULL,
    receiver_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create group_conversations table
CREATE TABLE IF NOT EXISTS group_conversations (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    conversation_id INTEGER NOT NULL,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE
);

-- Create group_members table
CREATE TABLE IF NOT EXISTS group_members (
    user_id INTEGER NOT NULL,
    group_id INTEGER NOT NULL,
    is_admin BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, group_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (group_id) REFERENCES group_conversations(id)
);

-- Create notifications table
CREATE TABLE IF NOT EXISTS notifications (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  receiver_id INTEGER NOT NULL,
  sender_id INTEGER NOT NULL,
  content TEXT NOT NULL,
  type VARCHAR(50) NOT NULL,
  title VARCHAR(255) NOT NULL,
  link VARCHAR(2048),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  unread BOOLEAN NOT NULL DEFAULT TRUE,
  FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Creating indexes for common query patterns
CREATE INDEX idx_notifications_receiver_id ON notifications(receiver_id);
CREATE INDEX idx_notifications_unread ON notifications(receiver_id, unread);
CREATE INDEX idx_notifications_created_at ON notifications(created_at DESC);


-- Create reports table
CREATE TABLE IF NOT EXISTS reports (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    text TEXT NOT NULL,
    reporter_id INTEGER NOT NULL,
    reported_id INTEGER NOT NULL,
    cause TEXT NOT NULL,
    status ENUM('pending', 'resolved', 'dismissed') DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (reporter_id) REFERENCES users(id),
    FOREIGN KEY (reported_id) REFERENCES users(id)
);

-- Create indexes for refresh_tokens
CREATE INDEX idx_refresh_token_user ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_token_expiry ON refresh_tokens(expiry_date);
CREATE INDEX idx_messages_conversation ON messages(conversation_id);
CREATE INDEX idx_messages_user ON messages(user_id);
CREATE INDEX idx_conversations_owner ON conversations(owner_id);

-- Insert sample users // password => password
INSERT INTO users (username, email, password, is_admin, is_email_verified, is_activated, is_suspended)
VALUES 
('admin', 'admin@gmail.com', '$2a$10$xDvcMBovSUWCkCVufKFjLOzpFf6bbXTfYCBy1F9gYkOlg5p.UGjpe', TRUE, TRUE, TRUE, FALSE),
('yassine', 'yassine@gmail.com', '$2a$10$xDvcMBovSUWCkCVufKFjLOzpFf6bbXTfYCBy1F9gYkOlg5p.UGjpe', FALSE, TRUE, TRUE, FALSE),
('youssef', 'youssef@gmail.com', '$2a$10$xDvcMBovSUWCkCVufKFjLOzpFf6bbXTfYCBy1F9gYkOlg5p.UGjpe', FALSE, TRUE, TRUE, FALSE),
('hamid', 'hamid@gmail.com', '$2a$10$xDvcMBovSUWCkCVufKFjLOzpFf6bbXTfYCBy1F9gYkOlg5p.UGjpe', FALSE, TRUE, TRUE, FALSE),
('yasser', 'yasser@gmail.com', '5e884898da28047151d0e56f8$3d0d6aabbdd62a11ef721d1542d8', FALSE, FALSE, TRUE, FALSE);

-- Insert sample conversations
INSERT INTO conversations (owner_id)
VALUES 
(1), -- admin conversation
(2), -- yassine conversation
(1), -- admin group conversation
(3); -- youssef conversation

-- Insert sample private conversations
INSERT INTO private_conversations (conversation_id, receiver_id)
VALUES 
(1, 2), -- admin and yassine
(2, 1), -- 
(4, 4); -- 

-- Insert sample group conversations
INSERT INTO group_conversations (conversation_id, name)
VALUES 
(3, 'Project Team'); -- admin's group conversation

-- Insert sample group members
INSERT INTO group_members (user_id, group_id, is_admin)
VALUES 
(1, 1, TRUE),  -- admin is admin in Project Team
(2, 1, FALSE), -- yassine is member in Project Team
(3, 1, FALSE), -- 
(4, 1, FALSE); --

-- Insert sample messages
INSERT INTO messages (text, user_id, conversation_id, status, is_edited)
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

-- Fixed notifications insert
INSERT INTO notifications (content, receiver_id, sender_id, type, title)
VALUES 
('You have a new message', 1, 2, 'message', 'New Message'),
('You have a new message', 2, 1, 'message', 'New Message'),
('You were added to Project Team group', 3, 1, 'group', 'Added to Group'),
('You have a new message', 1, 4, 'message', 'New Message'),
('Your account email has been verified', 5, 1, 'system', 'Email Verified');

-- Insert sample reports
INSERT INTO reports (text, reporter_id, reported_id, cause, status)
VALUES 
('This user is sending spam messages', 2, 5, 'spam', 'pending'),
('Inappropriate content in messages', 3, 5, 'inappropriate content', 'resolved'),
('Harassment in private messages', 4, 5, 'harassment', 'pending');