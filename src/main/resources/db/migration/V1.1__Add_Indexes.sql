CREATE INDEX IF NOT EXISTS idx_chat_message_sender ON chat_message(sender_id);
CREATE INDEX IF NOT EXISTS idx_chat_message_receiver ON chat_message(receiver_id);
