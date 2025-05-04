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
