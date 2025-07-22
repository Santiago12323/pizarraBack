package com.escuelaing.arsw.pizarraBack.infrastructure.controller;

import com.escuelaing.arsw.pizarraBack.domain.usecases.RedisPublisher;
import com.escuelaing.arsw.pizarraBack.infrastructure.dto.EmojiEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/emoji")
public class EmojiController {

    @Autowired
    private RedisPublisher redisPublisher;

    @PostMapping("/send")
    public ResponseEntity<Void> sendEmoji(@RequestBody EmojiEvent emoji) {
        redisPublisher.publish("emoji:event", emoji);
        return ResponseEntity.ok().build();
    }
}

