package com.tutti.backend.service;

import com.tutti.backend.domain.Feed;
import com.tutti.backend.domain.User;
import com.tutti.backend.dto.Feed.*;
import com.tutti.backend.dto.user.FileRequestDto;
import com.tutti.backend.exception.CustomException;
import com.tutti.backend.exception.ErrorCode;
import com.tutti.backend.repository.CommentRepository;
import com.tutti.backend.repository.FeedRepository;
import com.tutti.backend.repository.HeartRepository;
import com.tutti.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class FeedService {
    
    private final FeedRepository feedRepository;
    private final UserRepository userRepository;

    private final S3Service service;

    private final CommentRepository commentRepository;

    private final HeartRepository heartRepository;

    @Autowired
    public FeedService(FeedRepository feedRepository, UserRepository userRepository, S3Service service, CommentRepository commentRepository,HeartRepository heartRepository) {
        this.feedRepository = feedRepository;
        this.userRepository = userRepository;
        this.service = service;
        this.commentRepository = commentRepository;
        this. heartRepository = heartRepository;
    }

    @Transactional
    public void createFeed(FeedRequestDto feedRequestDto, MultipartFile albumImage, MultipartFile song, User user) {
        FileRequestDto albumDto = service.upload(albumImage);
        FileRequestDto songDto = service.upload(song);
        String albumImageUrl = albumDto.getImageUrl();
        String songUrl = songDto.getImageUrl();

        Feed feed = new Feed(feedRequestDto.getTitle(),
                feedRequestDto.getDescription(),
                albumImageUrl,
                songUrl,
                feedRequestDto.getGenre(),
                feedRequestDto.getPostType(),
                feedRequestDto.getColor(),
                user
                );

        feedRepository.save(feed);
    }
    @Transactional
    public void updateFeed(Long feedId, FeedUpdateRequestDto feedUpdateRequestDto,User user) {
        Feed feed = feedRepository.findById(feedId).orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_FEED));
        if(user!=feed.getUser()){
            throw new CustomException(ErrorCode.WRONG_USER);
        }
        feed.update(feedUpdateRequestDto);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getFeed(Long feedId) {
        Feed feed = feedRepository.findById(feedId).orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_FEED));
        String artist = feed.getUser().getArtist();
        List<FeedCommentDtoMapping> commentList = commentRepository.findAllByFeed(feed);

        FeedDetailDto feedDetailDto = new FeedDetailDto(feed, artist);
        FeedDetailResponseDto feedDetailResponseDto =  new FeedDetailResponseDto(feedDetailDto,commentList);

        FeedAll3Dto feedAll3Dto = new FeedAll3Dto();

        feedAll3Dto.setSuccess(200);
        feedAll3Dto.setMessage("성공");
        feedAll3Dto.setData(feedDetailResponseDto);

        return  ResponseEntity.ok().body(feedAll3Dto);

    }
    @Transactional
    public void deleteFeed(Long feedId,User user) {
        Feed feed = feedRepository.findById(feedId).orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_FEED));
        if(user!=feed.getUser()){
            throw new CustomException(ErrorCode.WRONG_USER);
        }

        String albumImgUrl = feed.getAlbumImageUrl();
        String songUrl = feed.getSongUrl();
        service.deleteImageUrl(albumImgUrl);
        service.deleteImageUrl(songUrl);

        feedRepository.delete(feed);
    }

    public ResponseEntity<?> getMainPage() {
        List<SearchTitleDtoMapping> lastestList = feedRepository.findAllByOrderByCreatedAtDesc();

        List<Feed> randomList = feedRepository.findAll();

        List<MainPageFeedDto> feedDtos = new ArrayList<>();

        List<MainPageFeedDto> feedDtoList = new ArrayList<>();

        for(Feed feed: randomList){
            MainPageFeedDto mainPageFeedDto = new MainPageFeedDto(feed,feed.getUser());
            feedDtos.add(mainPageFeedDto);
            feedDtoList.add(mainPageFeedDto);
        }



        List<MainPageFeedDto> likeList = new ArrayList<>();

        Map<Long,MainPageFeedDto> sortMap = new HashMap<>();

        for(MainPageFeedDto feed : feedDtoList) {
            Long Hearts = heartRepository.countByFeedIdAndIsHeartTrue(feed.getFeedId());
            sortMap.put(Hearts,feed);
        }

        // 키로 정렬
        Object[] mapkey = sortMap.keySet().toArray();
        Arrays.sort(mapkey);
        // 결과 출력
        for (Long nKey : sortMap.keySet())
        {
            likeList.add(sortMap.get(nKey));
        }
        MainPageListDto mainPageListDto = new MainPageListDto(lastestList,likeList,feedDtos);

        FeedAllDto feedAllDto = new FeedAllDto();

        feedAllDto.setSuccess(200);
        feedAllDto.setMessage("성공");
        feedAllDto.setData(mainPageListDto);

        return ResponseEntity.ok().body(feedAllDto);

    }

    public ResponseEntity<?> getMainPageByUser(String user) {
        User findUser = userRepository.findByEmail(user).orElseThrow(
                ()-> new CustomException(ErrorCode.NOT_FOUND_USER)
        );

        List<SearchTitleDtoMapping> lastestList = feedRepository.findAllByOrderByCreatedAtDesc();

        List<SearchTitleDtoMapping> interestedList = feedRepository.findAllByGenre(findUser.getFavoriteGenre1());



        List<MainPageFeedDto> likeList = new ArrayList<>();
        List<Feed> likes = feedRepository.findAll();
        for(Feed feed: likes){
            MainPageFeedDto mainPageFeedDto = new MainPageFeedDto(feed,feed.getUser());
            likeList.add(mainPageFeedDto);
        }

        Map<Long,MainPageFeedDto> sortMap = new HashMap<>();

        for(MainPageFeedDto feed : likeList) {
            Long Hearts = heartRepository.countByFeedIdAndIsHeartTrue(feed.getFeedId());
            sortMap.put(Hearts,feed);
        }

        // 키로 정렬
        Object[] mapkey = sortMap.keySet().toArray();
        Arrays.sort(mapkey);
        // 결과 출력
        for (Long nKey : sortMap.keySet())
        {
            likeList.add(sortMap.get(nKey));
        }
//        if(interestedList.size() < 1){
//            List<Feed> randomList = feedRepository.findAll();
//            List<MainPageFeedDto> feedDtos = new ArrayList<>();
//            for(Feed feed: randomList){
//
//                MainPageFeedDto mainPageFeedDto = new MainPageFeedDto(feed,feed.getUser());
//                feedDtos.add(mainPageFeedDto);
//            }
//
//            MainPageListDto mainPageListDto = new MainPageListDto(lastestList,likeList,feedDtos);
//            FeedAll1Dto feedAll1Dto = new FeedAll1Dto();
//
//            feedAll1Dto.setSuccess(200);
//            feedAll1Dto.setMessage("성공");
//            feedAll1Dto.setData(mainPageListDto);
//
//            return ResponseEntity.ok().body(feedAll1Dto);
//        }

        MainPageListUserDto mainPageListUserDto = new MainPageListUserDto(lastestList,likeList,interestedList);




        FeedAll2Dto feedAll2Dto = new FeedAll2Dto();

        feedAll2Dto.setSuccess(200);
        feedAll2Dto.setMessage("성공");
        feedAll2Dto.setData(mainPageListUserDto);

        return ResponseEntity.ok().body(feedAll2Dto);

    }

    public ResponseEntity<?> searchFeed(String keyword) {
        SearchFeedResponseDto searchFeedResponseDto = new SearchFeedResponseDto();
        User user = userRepository.findByArtistLike(keyword);

        searchFeedResponseDto.setTitle(feedRepository.findAllByTitleLike(keyword));
        searchFeedResponseDto.setArtist(feedRepository.findAllByUser(user));
        searchFeedResponseDto.setSuccess(200);
        searchFeedResponseDto.setMessage("성공");
        return ResponseEntity.ok().body(searchFeedResponseDto);
    }

    public ResponseEntity<?> getFeedPage() {
        List<SearchTitleDtoMapping> lastestList = feedRepository.findAllByOrderByCreatedAtDesc();

        FeedAll4Dto feedAll4Dto = new FeedAll4Dto();

        feedAll4Dto.setSuccess(200);
        feedAll4Dto.setMessage("성공");
        feedAll4Dto.setData(lastestList);

        return ResponseEntity.ok().body(feedAll4Dto);
    }

    public ResponseEntity<?> getFeedByGenrePage(String genre) {
        List<SearchTitleDtoMapping> genreList = feedRepository.findAllByGenreOrderByCreatedAtDesc(genre);

        FeedAll5Dto feedAll5Dto = new FeedAll5Dto();

        List<SearchTitleDtoMapping> likes = feedRepository.findAllByGenre(genre);

        List<SearchTitleDtoMapping> likeList = new ArrayList<>();

        Map<Long,SearchTitleDtoMapping> sortMap = new HashMap<>();

        for(SearchTitleDtoMapping feed : likes) {
            Long Hearts = heartRepository.countByFeedIdAndIsHeartTrue(feed.getId());
            sortMap.put(Hearts,feed);
        }

        // 키로 정렬
        Object[] mapkey = sortMap.keySet().toArray();
        Arrays.sort(mapkey);
        // 결과 출력
        for (Long nKey : sortMap.keySet())
        {
            likeList.add(sortMap.get(nKey));
        }

        feedAll5Dto.setSuccess(200);
        feedAll5Dto.setMessage("성공");
        feedAll5Dto.setData(genreList);
        feedAll5Dto.setLike(likeList);

        return ResponseEntity.ok().body(feedAll5Dto);
    }
}
