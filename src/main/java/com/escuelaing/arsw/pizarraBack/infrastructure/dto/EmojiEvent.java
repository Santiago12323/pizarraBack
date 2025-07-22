package com.escuelaing.arsw.pizarraBack.infrastructure.dto;

import lombok.Data;

@Data
public class EmojiEvent {
    private String sender;
    private String emoji;
    private String timestamp;
}
