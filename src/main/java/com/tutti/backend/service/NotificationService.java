package com.tutti.backend.service;

import com.tutti.backend.domain.Follow;
import com.tutti.backend.domain.Notification;
import com.tutti.backend.domain.User;
import com.tutti.backend.repository.EmitterRepository;
import com.tutti.backend.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private static final Long DEFAULT_TIMEOUT=60L*1000*60;

    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;

// ------------------------- SSE 연결 -----------------------------

    public SseEmitter subscribe(String id, String lastEventId) {
        // 현재시간 포함 id
        String emitterId=makeTimeId(id);
        // emitter 생성, 유효 시간만큼 sse 연결 유지, 만료시 자동으로 클라이언트에서 재요청
        SseEmitter emitter =emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));
        // 비동기 요청이 완료될 때
        // 시간초과, 네트워크 오류를 포함한 어던 이유로든 비동기 요청이 완료-> 레퍼지토리 삭제
        emitter.onCompletion(()->emitterRepository.deleteById(emitterId));
        //비동기 요청 시간이 초과 -> 레퍼지토리 삭제
        emitter.onTimeout(()->emitterRepository.deleteById(emitterId));

        // sseEmitter의 유효시간동안 데이터 전송이 없으면-> 503에러
        // 맨 처음 연결을 진행한다면 dummy데이터 전송
        String eventId=makeTimeId(id);
        sendNotification(emitter,eventId,emitterId,"EventStream Created. userId = "+id);
        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 event 유실 예방
        if(!lastEventId.isEmpty()){
            sendLostData(lastEventId,id,emitterId,emitter);
        }
        return emitter;

    }

    private void sendLostData(String lastEventId, String id, String emitterId, SseEmitter emitter) {
        Map<String,Object>eventCaches=emitterRepository.findAllEventCacheStartWithId(String.valueOf(id));
        eventCaches.entrySet().stream()
                .filter(entry->lastEventId.compareTo(entry.getKey())<0)
                .forEach(entry->sendNotification(emitter, entry.getKey(),emitterId,entry.getValue()));
    }

    private void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {
        try{
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .data(data));
        }catch (IOException exception){
            emitterRepository.deleteById(emitterId);
            throw new RuntimeException("연결 오류");
        }
    }
// ------------------------- 데이터 전송 -----------------------------

//    // 알림을 보낼 회원을 찾고 emitter들을 모두 찾아 send
//    public void send(User receiver, Follow following, String content){
//        Notification notification = new Notification(receiver,following,content,url,false);
//        String id =String.valueOf(receiver.getId());
//        notificationRepository.save(notification);
//        Map<String,SseEmitter>sseEmitters=emitterRepository.findAllStartWithById(id);
//        sseEmitters.forEach(
//                (key,emitter)->{
//                    emitterRepository.saveEventCache(key,notification);
//                    sendNotification(emitter,key,NotificationResponseDto.from(notification));
//
//                }
//        );
//    }

//    private Notification createNotification(User receiver, Follow following, String content,String url) {
//        return new Notification(receiver,following,content,url,false);
//    }




    private String makeTimeId(String id) {
        return id+"_"+System.currentTimeMillis();
    }


}
