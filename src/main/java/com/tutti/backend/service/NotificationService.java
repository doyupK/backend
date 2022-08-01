package com.tutti.backend.service;

import com.tutti.backend.domain.LiveRoom;
import com.tutti.backend.domain.Notification;
import com.tutti.backend.domain.User;
import com.tutti.backend.dto.Notification.NotificationCacheDto;
import com.tutti.backend.dto.Notification.NotificationDetailsDto;
import com.tutti.backend.exception.CustomException;
import com.tutti.backend.exception.ErrorCode;
import com.tutti.backend.repository.EmitterRepository;
import com.tutti.backend.repository.NotificationRepository;
import com.tutti.backend.security.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bytecode.Throw;
import org.apache.tomcat.jni.Time;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Service
@Slf4j
public class NotificationService extends Thread {

    private final ExecutorService sseMvcExecutor = Executors.newSingleThreadExecutor();

    private static final Long DEFAULT_TIMEOUT=60L*1000;

    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(EmitterRepository emitterRepository, NotificationRepository notificationRepository) {
        this.emitterRepository = emitterRepository;
        this.notificationRepository = notificationRepository;
    }
// ------------------------- SSE 연결 -----------------------------

    public SseEmitter subscribe(String userId, String lastEventId) {

        SimpleDateFormat format1 = new SimpleDateFormat ( "HHmmss");

        Calendar time = Calendar.getInstance();

        String format_time1 = format1.format(time.getTime());


        String emitterId = userId + "_" + format_time1;

        Map<String, SseEmitter> emitterCheck = emitterRepository.findAllStartWithById(userId);
//        log.info(emitterCheck + "이미터 체크");
        if(!emitterCheck.isEmpty())
        {
            emitterRepository.deleteById(userId);
        }
        // emitter 생성, 유효 시간만큼 sse 연결 유지, 만료시 자동으로 클라이언트에서 재요청
        SseEmitter emitter =new SseEmitter(DEFAULT_TIMEOUT);
//        log.info("emitter 생성 : {}, ID : {}",emitter, Id);





        emitterRepository.save(emitterId,emitter);
        // 비동기 요청이 완료될 때
        // 시간초과, 네트워크 오류를 포함한 어던 이유로든 비동기 요청이 완료(end)-> 레퍼지토리 삭제
        emitter.onCompletion(()-> {
//            log.info("emitter completion : {}, ID : {} ",emitter, Id);
            emitterRepository.deleteById(emitterId);
        }
        );
        //비동기 요청 시간이 초과 -> 레퍼지토리 삭제
        emitter.onTimeout(() -> {
            emitterRepository.deleteById(emitterId);
//            log.info("Emitter : {} 만료, ID : {}",emitter, Id);
//            emitter.complete();
//            throw new CustomException(ErrorCode.EXIST_ARTIST);
        });

        // sseEmitter의 유효시간동안 데이터 전송이 없으면-> 503에러
        // 맨 처음 연결을 진행한다면 dummy데이터 전송
        sendNotification(emitter,
                "Created",
                emitterId,
                new NotificationDetailsDto(
                        null,
                        "Created" + emitterId,
                        "https://tuttimusic.shop",
                        false
                )
        );
        if (!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithId(String.valueOf(userId));
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> lostSendNotification(emitter,"live_lost", entry.getKey(), entry.getValue()));
        }
        return emitter;

    }
    private void lostSendNotification(SseEmitter emitter,String name, String id, Object data) {
        sseMvcExecutor.execute(() -> {
            try {
                emitter.send(SseEmitter.event()
                        .id(id)
                        .name(name)
                        .data(data));
                Thread.sleep( 500);
                log.info("Emitter Send / type : {}, to : {}, data : {}", name, id, data);

            } catch (IOException exception) {
                emitterRepository.deleteById(id);
//                emitter.completeWithError(exception);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // 3
    private void sendNotification(SseEmitter emitter,String name, String id, Object data) {
        sseMvcExecutor.execute(() -> {
            try {
                emitter.send(SseEmitter.event()
                        .id(id)
                        .name(name)
                        .data(data));
                Thread.sleep( 500);
                log.info("Emitter Send / type : {}, to : {}, data : {}", name, id, data);

            } catch (IOException exception) {
                emitterRepository.deleteById(id);
//                emitter.completeWithError(exception);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }



//    public void sendNotification(SseEmitter emitter,String name, String eventId, Object data) {
//
//
//        sseMvcExecutor.execute( () -> {
//            try{
//                emitter.send(SseEmitter.event()
//                        .id(eventId)
//                        .name(name)
//                        .data(data));
//                Thread.sleep( 500);
//                log.info("Emitter Send / type : {}, to : {}, data : {}", name, eventId, data);
//
//
//        });
//
//
//    }

// ------------------------- 데이터 전송 -----------------------------

    // 알림 받을 회원을 찾고 emitter들을 모두 찾아 send
    @Transactional
    public void send(User receiver, LiveRoom liveRoom, String content){

        SimpleDateFormat format1 = new SimpleDateFormat ( "HHmmss");

        Calendar time = Calendar.getInstance();

        String format_time1 = format1.format(time.getTime());


        Notification notification = new Notification(
                receiver,
                liveRoom,
                content,
                "/live/"+liveRoom.getUser().getArtist(),
                false);
        String id = receiver.getId()+"_";


        notificationRepository.save(notification);


//        Map<String,SseEmitter> emitters = emitterRepository.findAllStartWithById(id);
//        log.info("이미터 이벤트 생성");
//        emitters.forEach(
//                (key,emitter)->{
//                    sendNotification(emitter,"live",key,new NotificationDetailsDto(notification));
////                    log.info("receiver : {}, Streamer : {}", receiver.getArtist(), liveRoom.getUser().getArtist());
//                }
//        );

        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllStartWithById(id);
        sseEmitters.forEach(
                (key, emitter) -> {
                    // 데이터 캐시 저장(유실된 데이터 처리하기 위함)
                    emitterRepository.saveEventCache(key, notification);
                    log.info("event save Time : {}", key );
                    // 데이터 전송
                    sendNotification(emitter,"live",key,new NotificationDetailsDto(notification));
                }

        );

//        log.info("이벤트 송신 완료");

    }



}
