package com.atp.fwfe.repository.chat;

import com.atp.fwfe.model.chat.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT m FROM ChatMessage m WHERE " +
            "((m.sender = :user1 AND m.receiver = :user2) OR " +
            "(m.sender = :user2 AND m.receiver = :user1)) AND m.type = 'PRIVATE'")
    List<ChatMessage> findPrivateChatBetweenUsers(
            @Param("user1") String user1,
            @Param("user2") String user2,
            Pageable pageable);


    List<ChatMessage> findByReceiverOrderByTimestampDesc(String receiver);

    @Query("SELECT DISTINCT m.receiver FROM ChatMessage m WHERE m.receiver IS NOT NULL")
    List<String> findAllReceivers();

    @Query("SELECT m.id FROM ChatMessage m WHERE m.receiver = :receiver ORDER BY m.timestamp DESC OFFSET 50")
    List<Long> findOldMessageIdsToDelete(@Param("receiver") String receiver);

    void deleteByIdIn(List<Long> ids);

    @Query("SELECT DISTINCT m.sender FROM ChatMessage m WHERE m.receiver = :receiver")
    List<String> findDistinctSenderUsernamesTo(@Param("receiver") String receiver);


}
