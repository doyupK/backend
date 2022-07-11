package com.tutti.backend.service;

import com.tutti.backend.domain.User;
import com.tutti.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomService {

    private final RedisTemplate<String, String> redisTemplate;

    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1) {
            return destination.substring(lastIndex + 1);
        } else {
            return "";
        }
    }

    public EnterRes enterRoom(String roomId, UserDetailsImpl userDetails, HttpResponse response) throws ExistSessionException, OpenViduJavaClientException, OpenViduHttpException {

        Debate debate = getDebate(roomId);
        User user = userDetails.getUser();
        String userEmail = userDetails.getUser().getEmail();

        EnterUser enterUser = setEnterUser(debate, user);
        System.out.println("userName: " + user.getUserName());

        OpenViduRole role = (getPanel(debate, userEmail)) ? OpenViduRole.PUBLISHER:OpenViduRole.SUBSCRIBER;

        String token = getToken(user, role, roomId, httpSession);

        // todo: publisher가 모두 나가면 session 삭제하기 위한 token 저장
        // todo: 발표자(publisher)가 입장한 현황에 따라서 발표방 상태 설정
        if(role.equals(OpenViduRole.PUBLISHER)) {
            log.info("PUBLISHER일 때만 들어오는지?");
            saveToken(roomId, userEmail, token);
            setDebateStatus(debate);
        }
        saveChat(debate);

        boolean roomKing = debate.getUser().getEmail().equals(userEmail);

        return new EnterRes(role, token, enterUser, debate, roomKing);

    }

    private void saveToken(String roomId, String userEmail, String token){
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();

        hashOperations.put(roomId, userEmail, token);
    }
    private void saveChat(Debate debate){
        log.info("saveDebate 진입");
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        String redisKey = String.valueOf(debate.getDebateId());
        log.info("rediskey: {}", redisKey);
        hashOperations.put(redisKey, DEBATE_STATUS, debate.getStatusEnum().getName());
        log.info("저장 된 값 확인: {}", hashOperations.get(redisKey, DEBATE_STATUS));
        redisTemplate.expire(redisKey, DEFAULT_TIMEOUT, TimeUnit.SECONDS);
    }

}
