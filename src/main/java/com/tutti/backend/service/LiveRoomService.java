package com.tutti.backend.service;

import com.tutti.backend.domain.LiveRoom;
import com.tutti.backend.dto.liveRoom.AddRoomRequestDto;
import com.tutti.backend.dto.user.FileRequestDto;
import com.tutti.backend.repository.*;
import com.tutti.backend.security.UserDetailsImpl;
import com.tutti.backend.security.jwt.HeaderTokenExtractor;
import com.tutti.backend.security.jwt.JwtDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LiveRoomService {
    private final UserRepository userRepository;
    private final S3Service service;
    private final HeaderTokenExtractor headerTokenExtractor;
    private final JwtDecoder jwtDecoder;
    private final LiveRoomRepository liveRoomRepository;

    @Autowired
    public LiveRoomService(
                       UserRepository userRepository,
                       S3Service service,
                       HeaderTokenExtractor headerTokenExtractor,
                       JwtDecoder jwtDecoder,
                       LiveRoomRepository liveRoomRepository
    ) {
        this.userRepository = userRepository;
        this.service = service;
        this.headerTokenExtractor =headerTokenExtractor;
        this.jwtDecoder=jwtDecoder;
        this.liveRoomRepository = liveRoomRepository;
    }

    public Object add(AddRoomRequestDto addRoomRequestDto, MultipartFile thumbNailImage, UserDetailsImpl userDetails) {
        FileRequestDto albumDto = service.upload(thumbNailImage);
        String thumbNailImageUrl = albumDto.getImageUrl();


        LiveRoom liveRoom = new LiveRoom(addRoomRequestDto.getRoomTitle(),
                userDetails.getUser(),
                addRoomRequestDto.getDescription(),
                thumbNailImageUrl,
                addRoomRequestDto.getGenre()
        );

        liveRoomRepository.save(liveRoom);
        return ResponseEntity.ok().body("라이브 생성 완료");
    }
}
