package com.tutti.backend.chat.controller;


import com.tutti.backend.chat.model.Conversation;
import com.tutti.backend.chat.model.MessageDto;
import com.tutti.backend.domain.User;
import com.tutti.backend.exception.CustomException;
import com.tutti.backend.exception.ErrorCode;
import com.tutti.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/user")
//@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
public class UsersController {
    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);
    private final UserRepository userRepository;
    private final RedisTemplate<String, Conversation> conversationTemplate;

    @Autowired
    public UsersController(UserRepository userRepository, RedisTemplate<String, Conversation> conversationTemplate) {
        this.userRepository = userRepository;
        this.conversationTemplate = conversationTemplate;
    }


    @GetMapping("/search/{myArtist}/{userArtist}")
    public ResponseEntity<Integer> search(@PathVariable String userArtist, @PathVariable String myArtist){

        User user = userRepository.findByArtist(myArtist).orElseThrow(
                ()-> new CustomException(ErrorCode.NOT_FOUND_USER)
        );

        if(!userRepository.findByArtist(userArtist).isPresent()){
            return new ResponseEntity<>(101,HttpStatus.OK); ///////////// 추후 Custom exception 으로 변경 예정
        }
        HashOperations<String, String, Conversation> hashOperations = conversationTemplate.opsForHash();

        if(hashOperations.hasKey(user.getArtist(), userArtist)){
            return new ResponseEntity<>(102, HttpStatus.OK); ///////////// 추후 Custom exception 으로 변경 예정
        }
        hashOperations.put(user.getArtist(), userArtist, new Conversation(userArtist, new ArrayList<>()));
        return new ResponseEntity<>(100, HttpStatus.OK);
    }

    @GetMapping("/fetchAllUsers/{myArtist}")
    public ResponseEntity<Collection<Conversation>> fetchAll(@PathVariable String myArtist){
        HashOperations<String, String, Conversation> hashOperations = conversationTemplate.opsForHash();
        logger.info("user name: {}", myArtist);
        Map<String, Conversation> mapper = hashOperations.entries(myArtist);
        Collection<Conversation> res = mapper.values();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }



    @DeleteMapping("/leaveChat")
    public ResponseEntity<Boolean> leaveChat(@RequestBody MessageDto messageDto){
        HashOperations<String, String, Conversation> hashOperations = conversationTemplate.opsForHash();
        logger.info("Leave chat host: {} , partner: {}", messageDto.getAuthor(), messageDto.getTo());
        hashOperations.delete(messageDto.getAuthor(), messageDto.getTo());
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
}
