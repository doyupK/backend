package com.tutti.backend.service;

import com.tutti.backend.domain.Follow;
import com.tutti.backend.domain.LiveRoom;
import com.tutti.backend.domain.LiveRoomMessage;
import com.tutti.backend.domain.User;
import com.tutti.backend.dto.chatDto.Message;
import com.tutti.backend.dto.chatDto.messageChannel;
import com.tutti.backend.dto.liveRoom.AddRoomRequestDto;
import com.tutti.backend.dto.liveRoom.LiveRoomSearchDetailDto;
import com.tutti.backend.dto.liveRoom.LiveRoomSearchDto;
import com.tutti.backend.dto.user.FileRequestDto;
import com.tutti.backend.exception.CustomException;
import com.tutti.backend.exception.ErrorCode;
import com.tutti.backend.repository.*;
import com.tutti.backend.security.UserDetailsImpl;
import com.tutti.backend.security.jwt.HeaderTokenExtractor;
import com.tutti.backend.security.jwt.JwtDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Transactional
@Slf4j
@Service
public class LiveRoomService {
    private final S3Service service;
    private final LiveRoomRepository liveRoomRepository;
    private final LiveRoomMessageRepository liveRoomMessageRepository;
    private final static String defaultThumbnailImageUrl =
            "https://file-bucket-seyeol.s3.ap-northeast-2.amazonaws.com/198f9660-dc02-48a3-a851-f1b57ed2ba88.jpg";

    private final static ConcurrentHashMap<String, Object> checkingMap = new ConcurrentHashMap<>();

    private RedisTemplate<String, messageChannel> conversationTemplate;
    private StringRedisTemplate canversationTemplate;

    private final NotificationService notificationService;
    private final FollowRepository followRepository;

    @Autowired
    public LiveRoomService(
                       S3Service service,
                       LiveRoomRepository liveRoomRepository,
                       RedisTemplate<String, messageChannel> conversationTemplate,
                       StringRedisTemplate canversationTemplate,
                       NotificationService notificationService,
                       LiveRoomMessageRepository liveRoomMessageRepository,
                       FollowRepository followRepository
    ) {
        this.service = service;
        this.liveRoomRepository = liveRoomRepository;
        this.conversationTemplate = conversationTemplate;
        this.canversationTemplate = canversationTemplate;
        this.notificationService=notificationService;
        this.followRepository=followRepository;
        this.liveRoomMessageRepository=liveRoomMessageRepository;
    }

    @Transactional
    public Object add(AddRoomRequestDto addRoomRequestDto, MultipartFile thumbNailImage, UserDetailsImpl userDetails) {
        HashOperations<String, String, messageChannel> ho = conversationTemplate.opsForHash();

        User user = userDetails.getUser();

        if(checkingMap.containsKey(user.getArtist())){
            throw new CustomException(ErrorCode.MAKING_LIVEROOM);
        }

        checkingMap.put(user.getArtist(),true);

        List<LiveRoom> liveRooms = liveRoomRepository.findAllByUserAndOnAirTrue(user);
        if (!liveRooms.isEmpty()) {
            throw new CustomException(ErrorCode.ENOUGH_LIVE_ROOM);
        }
        String thumbNailImageUrl;

        if(thumbNailImage == null){
            thumbNailImageUrl = defaultThumbnailImageUrl;
        }else {
            FileRequestDto albumDto = service.upload(thumbNailImage);
            thumbNailImageUrl = albumDto.getImageUrl();
        }
        LiveRoom liveRoom = new LiveRoom(addRoomRequestDto.getRoomTitle(),
                userDetails.getUser(),
                addRoomRequestDto.getDescription(),
                thumbNailImageUrl
        );

        LiveRoom saveLiveRoom=liveRoomRepository.save(liveRoom);
        List<Follow>followList=followRepository.findAllByFollowingUser(user);
        if(followList.size()!=0) {
            for (Follow follower : followList) {
                notificationService.send(
                        follower.getUser()
                        , saveLiveRoom
                        , user.getArtist() + "님이 Live를 시작했습니다.");
            }
        }

        // 방 생성 시 Redis 첫번째 메시지 Set "방송을 시작합니다"
        List<Message> emptyMessageList = new ArrayList<>();
        messageChannel newMessageChannel =
                new messageChannel(UUID.randomUUID().toString()
                        ,userDetails.getUser().getArtist()
                        ,userDetails.getUser().getArtist()
                        ,emptyMessageList);
        Message message = new Message();
        message.setMessage("방송을 시작합니다.");
        message.setSenderName(userDetails.getUser().getArtist());
        message.setProfileImage(userDetails.getUser().getProfileUrl());
        newMessageChannel.getMessageList().add(message);
        ho.put(userDetails.getUser().getArtist(), userDetails.getUser().getArtist(), newMessageChannel);

        checkingMap.remove(user.getArtist());

        return ResponseEntity.ok().body("라이브 생성 완료");
    }


    public Object liveRoomSearch() {
        LiveRoomSearchDto liveRoomSearchDto = new LiveRoomSearchDto();

        liveRoomSearchDto.setResults(liveRoomRepository.searchAllLiveRooms());
        liveRoomSearchDto.setSuccess(200);
        liveRoomSearchDto.setMessage("성공");
        return liveRoomSearchDto;
    }

    public Object liveRoomDetail(String artist) {
        LiveRoomSearchDetailDto liveRoomSearchDetailDto = new LiveRoomSearchDetailDto();

        liveRoomSearchDetailDto.setLiveRoomListDto(liveRoomRepository.searchLiveRoom(artist));
        liveRoomSearchDetailDto.setSuccess(200);
        liveRoomSearchDetailDto.setMessage("성공");
        return liveRoomSearchDetailDto;
    }

    //방송 종료
    public void liveRoomDelete(String artist, User user) {
        LiveRoom liveRoom = liveRoomRepository.findByUserAndOnAirTrue(user);
        if (artist.equals(liveRoom.getUser().getArtist())) {
            HashOperations<String, String, messageChannel> ho = conversationTemplate.opsForHash();
            ValueOperations<String, String> usernameCount = canversationTemplate.opsForValue();
            messageChannel messageChannel = ho.get(artist, artist);
            List<Message> messageList = messageChannel.getMessageList();
            for (Message message : messageList) {
                LiveRoomMessage liveRoomMessage = new LiveRoomMessage(message, liveRoom);
                liveRoomMessageRepository.save(liveRoomMessage);
            }
            liveRoom.setOnAir(false);
            Long num = ho.delete(artist, artist);
            usernameCount.getAndDelete(artist+"2");
            log.info(num.toString());
        }else {
            throw new CustomException(ErrorCode.WRONG_USER);
        }
    }
}
