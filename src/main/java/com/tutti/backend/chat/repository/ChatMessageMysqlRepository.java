package com.tutti.backend.chat.repository;


import com.tutti.backend.chat.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageMysqlRepository extends JpaRepository<ChatMessage, Long> {
}
