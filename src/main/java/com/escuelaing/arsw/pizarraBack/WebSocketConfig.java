package com.escuelaing.arsw.pizarraBack;


import com.escuelaing.arsw.pizarraBack.handler.EmojiHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private DrawingHandler drawingHandler;

    @Autowired
    private EmojiHandler emojiHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(drawingHandler, "/bbService").setAllowedOrigins("*");
        registry.addHandler(emojiHandler, "/emojiService").setAllowedOrigins("*");
    }
}