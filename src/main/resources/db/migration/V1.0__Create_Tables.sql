DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'users') THEN
            CREATE TABLE users (
                                   id SERIAL PRIMARY KEY,
                                   username VARCHAR(50) NOT NULL UNIQUE,
                                   password VARCHAR(255) NOT NULL,
                                   email VARCHAR(100) NOT NULL UNIQUE,
                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
        END IF;

        IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'chat_message') THEN
            CREATE TABLE chat_message (
                                          id SERIAL PRIMARY KEY,
                                          sender_id INT NOT NULL,
                                          receiver_id INT,
                                          content TEXT NOT NULL,
                                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                          FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
                                          FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE
            );
        END IF;
    END $$;