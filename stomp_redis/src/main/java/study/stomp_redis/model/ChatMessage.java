package study.stomp_redis.model;

import lombok.*;
import org.springframework.stereotype.Service;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    public enum MessageType{
        ENTER, TALK
    }

    private MessageType type;
    private String message;
    private String sender;
    private String roomId;
}
