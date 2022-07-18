package com.tutti.backend.service;

import com.tutti.backend.domain.DeletedFeed;
import com.tutti.backend.domain.Feed;
import com.tutti.backend.domain.Heart;
import com.tutti.backend.domain.User;
import com.tutti.backend.dto.Feed.*;
import com.tutti.backend.dto.user.FileRequestDto;
import com.tutti.backend.exception.CustomException;
import com.tutti.backend.exception.ErrorCode;
import com.tutti.backend.repository.*;
import com.tutti.backend.security.jwt.HeaderTokenExtractor;
import com.tutti.backend.security.jwt.JwtDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
@Transactional
public class FeedService {
    
    private final FeedRepository feedRepository;
    private final UserRepository userRepository;
    private final S3Service service;
    private final CommentRepository commentRepository;
    private final HeaderTokenExtractor headerTokenExtractor;
    private final JwtDecoder jwtDecoder;
    private final DeletedFeedRepository deletedFeedRepository;
    private final HeartRepository heartRepository;


    @Autowired
    public FeedService(FeedRepository feedRepository,
                       UserRepository userRepository,
                       S3Service service,
                       CommentRepository commentRepository,
                       DeletedFeedRepository deletedFeedRepository,
                       HeaderTokenExtractor headerTokenExtractor,
                       HeartRepository heartRepository,
                       JwtDecoder jwtDecoder
                       ) {
        this.feedRepository = feedRepository;
        this.userRepository = userRepository;
        this.service = service;
        this.commentRepository = commentRepository;
        this.headerTokenExtractor =headerTokenExtractor;
        this.heartRepository = heartRepository;
        this.jwtDecoder=jwtDecoder;
        this.deletedFeedRepository = deletedFeedRepository;
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
                feedRequestDto.getMusicTitle(),
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
        if(!user.getArtist().equals(feed.getUser().getArtist())){
            throw new CustomException(ErrorCode.WRONG_USER);
        }
        feed.update(feedUpdateRequestDto);
    }

    // 피드 상세 조회
    @Transactional(readOnly = true)
    public ResponseEntity<?> getFeed(Long feedId, HttpServletRequest httpServletRequest) {
        Feed feed = feedRepository.findById(feedId).orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_FEED));
        String artist = feed.getUser().getArtist();
        String jwtToken = httpServletRequest.getHeader("Authorization");
        List<FeedCommentDtoMapping> commentList = commentRepository.findAllByFeed(feed);
        boolean heartCheck = false;
        if(!Objects.equals(jwtToken, "")) {
            // 현재 로그인 한 user 찾기
            String userEmail = jwtDecoder.decodeUsername(headerTokenExtractor.extract(jwtToken, httpServletRequest));
            User user = userRepository.findByEmail(userEmail).orElseThrow(
                    ()->new CustomException(ErrorCode.NOT_FOUND_USER)
            );
            Heart heart = heartRepository.findByUser_IdAndFeed_Id(user.getId(), feedId);
            if(heart != null){
                heartCheck = true;
            }
            FeedDetailDto feedDetailDto = new FeedDetailDto(feed, artist, feed.getUser().getProfileUrl(), heartCheck);
            //feedDetail+commentList
            FeedDetailResponseDto feedDetailResponseDto =  new FeedDetailResponseDto(feedDetailDto,commentList);
            FeedResponseDto feedResponseDto = new FeedResponseDto();

            feedResponseDto.setSuccess(200);
            feedResponseDto.setMessage("성공");
            feedResponseDto.setData(feedDetailResponseDto);
            return  ResponseEntity.ok().body(feedResponseDto);
        }
        FeedDetailDto feedDetailDto = new FeedDetailDto(feed, artist, feed.getUser().getProfileUrl(),heartCheck);

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
        if(!user.getArtist().equals(feed.getUser().getArtist())){
            throw new CustomException(ErrorCode.WRONG_USER);
        }

        DeletedFeed deletedFeed = new DeletedFeed(feed);

        deletedFeedRepository.save(deletedFeed);


        feedRepository.delete(feed);
    }

    // 비 로그인 Main 페이지(3번째 리스트 랜덤순)
    public ResponseEntity<?> getMainPage() {
        List<MainPageFeedDto> feedDtoList = new ArrayList<>();
        /*Collections.shuffle(fd);
        "힙합","발라드","알앤비","연주곡","댄스","어쿠스틱"};*/
        List<String> genres = new ArrayList<>();
        genres.add("힙합");
        genres.add("발라드");
        genres.add("R&B");
        genres.add("연주곡");
        genres.add("댄스");
        genres.add("어쿠스틱");

        Collections.shuffle(genres);

        String recommend = genres.get(0);


        // 최신 순
        List<GetMainPageListDto> latestList = feedRepository.getMainPageLatestList("audio");
        // 랜덤 순
        List<GetMainPageListDto> randomList = feedRepository.getMainPageRandomList("audio",recommend);

//        for(Feed feed: randomList){
//            MainPageFeedDto mainPageFeedDto = new MainPageFeedDto(feed,feed.getUser());
//            feedDtoList.add(mainPageFeedDto);
//        }
        // 좋아요 높은 순
//        List<MainPageFeedDto> likeList = new ArrayList<>();
//        MultiValueMap<Long, MainPageFeedDto> map = new LinkedMultiValueMap<>();
//        for(MainPageFeedDto feed : feedDtoList) {
//            Long Hearts = heartRepository.countByFeedIdAndIsHeartTrue(feed.getFeedId());
//            map.add(Hearts,feed);
//        }
////        Map<List<Long>,MainPageFeedDto> sortMap = new HashMap<>();
////        // 각 피드 좋아요 카운트
////        for(MainPageFeedDto feed : feedDtoList) {
////            List<Long> hearttttt =  new ArrayList<>();
////            Long Hearts = heartRepository.countByFeedIdAndIsHeartTrue(feed.getFeedId());
////            hearttttt.add(Hearts);
////            sortMap.put(hearttttt,feed);
////
////        }
////
//        // 키로 정렬(좋아요 높은 순으로 정렬)
//        Object[] mapkey = map.keySet().toArray();
//        Arrays.sort(mapkey);
//        // 결과 출력
//        for (Long nKey : map.keySet()){
//            List<MainPageFeedDto> dlfma = map.get(nKey);
//            for(MainPageFeedDto dto : dlfma) {
//                likeList.add(dto);
//            }
//        }

       List<SearchTitleDtoMapping> likeList= feedRepository.findAllByPostTypeOrderByLikeCountDesc("audio");
       /* List<GetMainPageListDto> likeList= feedRepository.getMainPagLikeList();*/
        List<GetMainPageListDto> videoList= feedRepository.getMainPageVideoList("video");


        MainPageListDto mainPageListDto = new MainPageListDto(latestList,likeList,randomList,videoList);
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
        List<GetMainPageListDto> latestList = feedRepository.getMainPageLatestList("audio");


        // 관심 장르 별
        String[] list = {findUser.getFavoriteGenre1(),findUser.getFavoriteGenre2()
                ,findUser.getFavoriteGenre3(),findUser.getFavoriteGenre4()};
        List<String> favoriteGenres = new ArrayList<>();
        for(String genre: list){
            /*if(genre!=null){
                favoriteGenres.add(genre);
            }else{
                break;
            }*/
            if (genre == null) {
                break;
            }
            favoriteGenres.add(genre);
        }
        Collections.shuffle(favoriteGenres);

        String recommend = favoriteGenres.get(0);


        List<GetMainPageListDto> interestedList = feedRepository.getMainPageLoginGenreList("audio",recommend);
//
//        List<MainPageFeedDto> likeList = new ArrayList<>();
//        List<Feed> likes = feedRepository.findAll();
//
//        for(Feed feed: likes){
//            MainPageFeedDto mainPageFeedDto = new MainPageFeedDto(feed,feed.getUser());
//            likeList.add(mainPageFeedDto);
//        }
//
//        Map<Long,MainPageFeedDto> sortMap = new HashMap<>();
//
//        for(MainPageFeedDto feed : likeList) {
//            Long Hearts = heartRepository.countByFeedIdAndIsHeartTrue(feed.getFeedId());
//            sortMap.put(Hearts,feed);
//        }
//
//        // 키로 정렬
//        Object[] mapkey = sortMap.keySet().toArray();
//        Arrays.sort(mapkey);
//        // 결과 출력
//        for (Long nKey : sortMap.keySet())
//        {
//            likeList.add(sortMap.get(nKey));
//        }

        List<SearchTitleDtoMapping> likeList= feedRepository.findAllByPostTypeOrderByLikeCountDesc("audio");
        List<GetMainPageListDto> videoList= feedRepository.getMainPageVideoList("video");


        MainPageListUserDto mainPageListUserDto = new MainPageListUserDto(latestList,likeList,interestedList,videoList);

        FeedMainLoginResponseDto feedMainLoginResponseDto = new FeedMainLoginResponseDto();

        feedMainLoginResponseDto.setSuccess(200);
        feedMainLoginResponseDto.setMessage("성공");
        feedMainLoginResponseDto.setData(mainPageListUserDto);
        return ResponseEntity.ok().body(feedMainLoginResponseDto);

    }

    // 피드 검색
    public ResponseEntity<?> searchFeed(String keyword) {
        SearchFeedResponseDto searchFeedResponseDto = new SearchFeedResponseDto();
        /*User user = userRepository.findByArtistLike(keyword);

        searchFeedResponseDto.setTitle(feedRepository.findAllByTitleContainingAndPostTypeContaining(keyword,"audio"));
        searchFeedResponseDto.setArtist(feedRepository.findAllByUser(user));
        searchFeedResponseDto.setVideo(feedRepository.findAllByTitleContainingAndPostTypeContaining(keyword,"video"));
        searchFeedResponseDto.setSuccess(200);
        searchFeedResponseDto.setMessage("성공");*/
        searchFeedResponseDto.setMusicTitle(feedRepository.searchMusicByTitleKeyword(keyword));
        searchFeedResponseDto.setMusicArtist(feedRepository.searchMusicByArtistKeyword(keyword));
        searchFeedResponseDto.setVideoTitle(feedRepository.searchVideoByTitleKeyword(keyword));
        searchFeedResponseDto.setVideoArtist(feedRepository.searchVideoByArtistKeyword(keyword));
        searchFeedResponseDto.setSuccess(200);
        searchFeedResponseDto.setMessage("성공");


        return ResponseEntity.ok().body(searchFeedResponseDto);
    }

    public ResponseEntity<?> searchMoreFeed(String keyword, String category) {
        SearchFeedAllByCategoryAndKeywordDto searchFeedAllByCategoryAndKeywordDto = new SearchFeedAllByCategoryAndKeywordDto();
        searchFeedAllByCategoryAndKeywordDto.setResults(feedRepository.searchCategoryByKeyword(category,keyword));
        searchFeedAllByCategoryAndKeywordDto.setSuccess(200);
        searchFeedAllByCategoryAndKeywordDto.setMessage("성공");
        return ResponseEntity.ok().body(searchFeedAllByCategoryAndKeywordDto);
    }
    // 최신 순 전체 피드 따로 가져오기

    public ResponseEntity<?> getFeedPage(String postType, String genre, Pageable pageable) {
        List<GetFeedByPostTypeDto> latestList = feedRepository.getFeedByPostType(postType,genre);
        List<GetFeedByPostTypeDto> latestList2 = feedRepository.getFeedByPostTypeInfiniteScroll(postType,genre,pageable);

        FeedPageResponseDto feedPageResponseDto = new FeedPageResponseDto();
        feedPageResponseDto.setSuccess(200);
        feedPageResponseDto.setMessage("성공");
        feedPageResponseDto.setData(latestList);
        return ResponseEntity.ok().body(feedPageResponseDto);
    }

    // 장르 별 피드 따로 가져오기
    /*public ResponseEntity<?> getFeedByGenrePage(String genre) {
        List<SearchTitleDtoMapping> genreList = feedRepository.findAllByGenreOrderByCreatedAtDesc(genre);
        FeedPageResponseDto feedPageResponseDto = new FeedPageResponseDto();

        feedPageResponseDto.setSuccess(200);
        feedPageResponseDto.setMessage("성공");
        feedPageResponseDto.setData(genreList);
        return ResponseEntity.ok().body(feedPageResponseDto);
    }
    public ResponseEntity<?> 영상장르(String genre) {
        List<SearchTitleDtoMapping> genreList = feedRepository.findAllByGenreOrderByCreatedAtDesc(genre);
        FeedPageResponseDto feedPageResponseDto = new FeedPageResponseDto();

        feedPageResponseDto.setSuccess(200);
        feedPageResponseDto.setMessage("성공");
        feedPageResponseDto.setData(genreList);
        return ResponseEntity.ok().body(feedPageResponseDto);
    }*/
}
