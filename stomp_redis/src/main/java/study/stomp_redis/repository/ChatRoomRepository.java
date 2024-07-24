package study.stomp_redis.repository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Repository;
import study.stomp_redis.model.ChatMessage;
import study.stomp_redis.model.ChatRoom;
import study.stomp_redis.service.RedisSubscriber;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepository {
    // 채널별 리스너 넣어주기 위해서
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    // 구독 처리용
    private final RedisSubscriber redisSubscriber;

    private static final String CHAT_ROOMS = "CHAT_ROOM";
    private final RedisTemplate<String, Object> redisTemplate;
    // 레디스 내부 저장소
    // 키, 해시키, value
    // key -> hash key -> value
    private HashOperations<String, String, ChatRoom> opsHashChatRoom;
    // 채팅방(Topic)을 roomID로 찾기위해서 만든 Map
    private Map<String, ChannelTopic> topics;

    @PostConstruct
    private void init() {
        // 내부 저장소 초기화
        opsHashChatRoom = redisTemplate.opsForHash();
        // 토픽도 초기화
        topics = new HashMap<>();
    }

    public List<ChatRoom> findAllRooms() {
        return opsHashChatRoom.values(CHAT_ROOMS);
    }

    public ChatRoom findRoomById(String id) {
        return opsHashChatRoom.get(CHAT_ROOMS, id);
    }

    public ChatRoom createChatRoom(String name) {
        ChatRoom chatRoom = ChatRoom.create(name);
        opsHashChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
        return chatRoom;
    }

    public void enterChatRoom(String roomId){
        ChannelTopic topic = topics.get(roomId);
        if(topic == null){
            topic = new ChannelTopic(roomId);
            redisMessageListenerContainer.addMessageListener(redisSubscriber, topic);
            topics.put(roomId, topic);
        }
    }

    public ChannelTopic getTopic(String roomId){
        return topics.get(roomId);
    }


}