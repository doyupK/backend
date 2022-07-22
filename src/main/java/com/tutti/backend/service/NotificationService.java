package com.tutti.backend.service;

import com.tutti.backend.domain.Follow;
import com.tutti.backend.domain.LiveRoom;
import com.tutti.backend.domain.Notification;
import com.tutti.backend.domain.User;
import com.tutti.backend.dto.Notification.NotificationDetailsDto;
import com.tutti.backend.repository.EmitterRepository;
import com.tutti.backend.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Transactional
@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private static final Long DEFAULT_TIMEOUT=-1L;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @PostConstruct
    public void init() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executor.shutdown();
            try {
                executor.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error(e.toString());
            }
        }));
    }

    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;

// ------------------------- SSE 연결 -----------------------------

    public SseEmitter subscribe(String id, String lastEventId) {
        // 현재시간 포함 id
        String emitterId=makeTimeId(id);
        // emitter 생성, 유효 시간만큼 sse 연결 유지, 만료시 자동으로 클라이언트에서 재요청
        SseEmitter emitter =new SseEmitter(DEFAULT_TIMEOUT);
        log.info("2");
        emitterRepository.save(emitterId,emitter);
        // 비동기 요청이 완료될 때
        // 시간초과, 네트워크 오류를 포함한 어던 이유로든 비동기 요청이 완료-> 레퍼지토리 삭제
        emitter.onCompletion(()->emitterRepository.deleteById(emitterId));
        //비동기 요청 시간이 초과 -> 레퍼지토리 삭제
        emitter.onTimeout(()->emitterRepository.deleteById(emitterId));

        // sseEmitter의 유효시간동안 데이터 전송이 없으면-> 503에러
        // 맨 처음 연결을 진행한다면 dummy데이터 전송
            sendNotification(emitter, emitterId, "EventStream Created. userId = " + id+"\n\n");
        log.info("3");


        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 event 유실 예방
        if(!lastEventId.isEmpty()){
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithId(String.valueOf(id));
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> sendNotification(emitter, entry.getKey(), entry.getValue()));

//                        sendLostData(lastEventId,id,emitter);
        }
        return emitter;

    }

    private void sendLostData(String lastEventId, String id, SseEmitter emitter) {
        Map<String,Object>eventCaches=emitterRepository.findAllEventCacheStartWithId(String.valueOf(id));
        eventCaches.entrySet().stream()
                .filter(entry->lastEventId.compareTo(entry.getKey())<0)
                .forEach(entry->sendNotification(emitter, entry.getKey(),entry.getValue()));
        log.info("4");

    }



    public void sendNotification(SseEmitter emitter, String eventId, Object data) {
        try{
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .name("Live")
                    .data(data));
//            sleep(1, emitter);
            log.info("1");
        }catch (IOException exception){
            emitterRepository.deleteById(eventId);
//            log.error("연결오류",exception);
            throw new RuntimeException("연결 오류");
        }

    }
// ------------------------- 데이터 전송 -----------------------------

    // 알림 받을 회원을 찾고 emitter들을 모두 찾아 send
    @Transactional
    public void send(User receiver, LiveRoom liveRoom, String content){
        Notification notification = new Notification(
                receiver,
                liveRoom,
                content,
                "/chatRoom/"+liveRoom.getUser().getArtist(),
                false);
//        Notification notification = createNotification(receiver, liveRoom, content);
        String id =receiver.getArtist();
        notificationRepository.save(notification);
        Map<String,SseEmitter> sseEmitters = emitterRepository.findAllStartWithById(id);
        log.info("6");
        executor.execute(()-> sseEmitters.forEach(
                (key,emitter)->{
                    emitterRepository.saveEventCache(key,notification);
                    sendNotification(emitter,key,new NotificationDetailsDto(notification));

                }
        ));
        log.info("7");
    }


    private String makeTimeId(String id) {
        return id+"_"+System.currentTimeMillis();
    }

    private void sleep(int seconds, SseEmitter sseEmitter) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            sseEmitter.completeWithError(e);
        }
    }


}
