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
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Service
@Slf4j
public class NotificationService {


    private static final Long DEFAULT_TIMEOUT=60L*1000 *30;
    private final ExecutorService sseMvcExecutor = Executors.newSingleThreadExecutor();


    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;
    @Autowired
    public NotificationService(EmitterRepository emitterRepository, NotificationRepository notificationRepository) {
        this.emitterRepository = emitterRepository;
        this.notificationRepository = notificationRepository;
    }
// ------------------------- SSE 연결 -----------------------------

    public SseEmitter subscribe(String Id) {


        Boolean emitterCheck = emitterRepository.findById(Id);
// 1
        if(emitterCheck)
        {
           emitterRepository.deleteById(Id);
        }
        // emitter 생성, 유효 시간만큼 sse 연결 유지, 만료시 자동으로 클라이언트에서 재요청
        SseEmitter emitter =new SseEmitter(DEFAULT_TIMEOUT);

        emitterRepository.save(Id,emitter);
        // 비동기 요청이 완료될 때
        // 시간초과, 네트워크 오류를 포함한 어던 이유로든 비동기 요청이 완료-> 레퍼지토리 삭제
        emitter.onCompletion(()->emitterRepository.deleteById(Id));
        //비동기 요청 시간이 초과 -> 레퍼지토리 삭제
        emitter.onTimeout(() -> {
            emitterRepository.deleteById(Id);
            log.info("Emitter : {} 만료", Id);
            emitter.complete();
            throw new CustomException(ErrorCode.WRONG_FILE_TYPE);
        });
        log.info("emitter 생성");
        // sseEmitter의 유효시간동안 데이터 전송이 없으면-> 503에러
        // 맨 처음 연결을 진행한다면 dummy데이터 전송
        sendNotification(emitter,
                "Created",
                Id,
                new NotificationDetailsDto(
                        null,
                        "Created" + Id,
                        "https://tuttimusic.shop",
                        false
                )
        );
//        sendNotification(emitter, Id, "{EventStream Created. userId = " + Id);


        return emitter;

    }




    public void sendNotification(SseEmitter emitter,String name, String eventId, Object data) {

        sseMvcExecutor.execute( () -> {
            try{
                emitter.send(SseEmitter.event()
                        .id(eventId)
                        .name(name)
                        .data(data));
                Thread.sleep( 1000);
                log.info("실제 전송 메서드: {}", data);
                int coreCount = Runtime.getRuntime().availableProcessors();
                log.info("활성 스레드 : {}", coreCount);
            }catch (IOException exception){
                emitterRepository.deleteById(eventId);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

// ------------------------- 데이터 전송 -----------------------------

    // 알림 받을 회원을 찾고 emitter들을 모두 찾아 send
    @Transactional
    public void send(User receiver, LiveRoom liveRoom, String content){
        Notification notification = new Notification(
                receiver,
                liveRoom,
                content,
                "/live/"+liveRoom.getUser().getArtist(),
                false);
        String id =receiver.getArtist();


        notificationRepository.save(notification);


        Map<String,SseEmitter> sseEmitters = emitterRepository.findAllStartWithById(id);
        log.info("이미터 이벤트 생성");
        sseEmitters.forEach(
                (key,emitter)->{
                    sendNotification(emitter,"live",key,new NotificationDetailsDto(notification));

                }
        );
        log.info("이벤트 송신 완료");
    }



}
