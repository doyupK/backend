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

    // 피드 작성
    @Transactional
    public void createFeed(FeedRequestDto feedRequestDto, MultipartFile albumImage, MultipartFile song, User user) {
        // 파일 업로드
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

    // 피드 수정
    @Transactional
    public void updateFeed(Long feedId, FeedUpdateRequestDto feedUpdateRequestDto,User user) {
        Feed feed = feedRepository.findById(feedId).orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_FEED));
        // 로그인한 유저 정보와 피드의 유저 정보가 다르면 예외처리
        if(user!=feed.getUser()){
            throw new CustomException(ErrorCode.WRONG_USER);
        }
        feed.update(feedUpdateRequestDto);
    }

    // 피드 상세 조회
    @Transactional(readOnly = true)
    public ResponseEntity<?> getFeed(Long feedId) {
        Feed feed = feedRepository.findById(feedId).orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_FEED));
        String artist = feed.getUser().getArtist();

        List<FeedCommentDtoMapping> commentList = commentRepository.findAllByFeed(feed);

        FeedDetailDto feedDetailDto = new FeedDetailDto(feed, artist);

        //feedDetail+commentList
        FeedDetailResponseDto feedDetailResponseDto =  new FeedDetailResponseDto(feedDetailDto,commentList);
        FeedResponseDto feedResponseDto = new FeedResponseDto();

        feedResponseDto.setSuccess(200);
        feedResponseDto.setMessage("성공");
        feedResponseDto.setData(feedDetailResponseDto);
        return  ResponseEntity.ok().body(feedResponseDto);

    }

    // 피드 삭제
    @Transactional
    public void deleteFeed(Long feedId,User user) {
        Feed feed = feedRepository.findById(feedId).orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_FEED));
        if(user!=feed.getUser()){
            throw new CustomException(ErrorCode.WRONG_USER);
        }
        // 파일 삭제
        String albumImgUrl = feed.getAlbumImageUrl();
        String songUrl = feed.getSongUrl();
        service.deleteImageUrl(albumImgUrl);
        service.deleteImageUrl(songUrl);

        feedRepository.delete(feed);
    }

    // 비 로그인 Main 페이지(3번째 리스트 랜덤순)
    public ResponseEntity<?> getMainPage() {
        // 최신 순
        List<SearchTitleDtoMapping> lastestList = feedRepository.findAllByOrderByCreatedAtDesc();
        List<Feed> randomList = feedRepository.findAll();
        // 랜덤 순
        List<MainPageFeedDto> feedDtoList = new ArrayList<>();

        for(Feed feed: randomList){
            MainPageFeedDto mainPageFeedDto = new MainPageFeedDto(feed,feed.getUser());
            feedDtoList.add(mainPageFeedDto);
        }
        // 좋아요 높은 순
        List<MainPageFeedDto> likeList = new ArrayList<>();
        Map<Long,MainPageFeedDto> sortMap = new HashMap<>();
        // 각 피드 좋아요 카운트
        for(MainPageFeedDto feed : feedDtoList) {
            Long Hearts = heartRepository.countByFeedIdAndIsHeartTrue(feed.getFeedId());
            sortMap.put(Hearts,feed);
        }

        // 키로 정렬(좋아요 높은 순으로 정렬)
        Object[] mapkey = sortMap.keySet().toArray();
        Arrays.sort(mapkey);
        // 결과 출력
        for (Long nKey : sortMap.keySet()){
            likeList.add(sortMap.get(nKey));
        }

        MainPageListDto mainPageListDto = new MainPageListDto(lastestList,likeList,feedDtoList);
        FeedMainNotLoginResponseDto feedMainNotLoginResponseDto = new FeedMainNotLoginResponseDto();
        feedMainNotLoginResponseDto.setSuccess(200);
        feedMainNotLoginResponseDto.setMessage("성공");
        feedMainNotLoginResponseDto.setData(mainPageListDto);
        return ResponseEntity.ok().body(feedMainNotLoginResponseDto);

    }

    // 로그인 Main 페이지 (3번째 리스트 User Genre 1 출력 )
    public ResponseEntity<?> getMainPageByUser(String user) {
        User findUser = userRepository.findByEmail(user).orElseThrow(
                ()-> new CustomException(ErrorCode.NOT_FOUND_USER)
        );
        List<SearchTitleDtoMapping> lastestList = feedRepository.findAllByOrderByCreatedAtDesc();
        // 관심 장르 별
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

        MainPageListUserDto mainPageListUserDto = new MainPageListUserDto(lastestList,likeList,interestedList);

        FeedMainLoginResponseDto feedMainLoginResponseDto = new FeedMainLoginResponseDto();

        feedMainLoginResponseDto.setSuccess(200);
        feedMainLoginResponseDto.setMessage("성공");
        feedMainLoginResponseDto.setData(mainPageListUserDto);
        return ResponseEntity.ok().body(feedMainLoginResponseDto);

    }

    // 피드 검색
    public ResponseEntity<?> searchFeed(String keyword) {
        SearchFeedResponseDto searchFeedResponseDto = new SearchFeedResponseDto();
        User user = userRepository.findByArtistLike(keyword);

        searchFeedResponseDto.setTitle(feedRepository.findAllByTitleLike(keyword));
        searchFeedResponseDto.setArtist(feedRepository.findAllByUser(user));
        searchFeedResponseDto.setSuccess(200);
        searchFeedResponseDto.setMessage("성공");
        return ResponseEntity.ok().body(searchFeedResponseDto);
    }

    // 최신 순 전체 피드 따로 가져오기
    public ResponseEntity<?> getFeedPage() {
        List<SearchTitleDtoMapping> lastestList = feedRepository.findAllByOrderByCreatedAtDesc();
        FeedPageResponseDto feedPageResponseDto = new FeedPageResponseDto();
        feedPageResponseDto.setSuccess(200);
        feedPageResponseDto.setMessage("성공");
        feedPageResponseDto.setData(lastestList);
        return ResponseEntity.ok().body(feedPageResponseDto);
    }

    // 장르 별 피드 따로 가져오기
    public ResponseEntity<?> getFeedByGenrePage(String genre) {
        List<SearchTitleDtoMapping> genreList = feedRepository.findAllByGenreOrderByCreatedAtDesc(genre);
        FeedPageResponseDto feedPageResponseDto = new FeedPageResponseDto();

        feedPageResponseDto.setSuccess(200);
        feedPageResponseDto.setMessage("성공");
        feedPageResponseDto.setData(genreList);
        return ResponseEntity.ok().body(feedPageResponseDto);
    }
}
