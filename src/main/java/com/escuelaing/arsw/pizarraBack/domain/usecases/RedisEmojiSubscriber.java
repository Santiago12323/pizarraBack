package com.escuelaing.arsw.pizarraBack.domain.usecases;


import com.escuelaing.arsw.pizarraBack.infrastructure.dto.EmojiEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;

@Component
public class RedisEmojiSubscriber implements MessageListener {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String json = new String(message.getBody(), StandardCharsets.UTF_8);
        try {
            ObjectMapper mapper = new ObjectMapper();
            EmojiEvent event = mapper.readValue(json, EmojiEvent.class);
            messagingTemplate.convertAndSend("/topic/emojis", event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


