package study.stomp_redis.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import study.stomp_redis.model.ChatMessage;
import study.stomp_redis.repository.ChatRoomRepository;
import study.stomp_redis.service.RedisPublisher;

@RequiredArgsConstructor
@Controller
public class ChatController {

    private final RedisPublisher redisPublisher;
    private final ChatRoomRepository chatRoomRepository;

    @MessageMapping("/chat/message")
    public void message(ChatMessage message) {
        if (ChatMessage.MessageType.ENTER.equals(message.getType())){
            chatRoomRepository.enterChatRoom(message.getRoomId());
            message.setMessage(message.getSender() + "님이 입장하셨습니다.");
        }
        //이제 Redis로 발행할꺼임.
//        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
        redisPublisher.publish(chatRoomRepository.getTopic(message.getRoomId()), message);
    }
}