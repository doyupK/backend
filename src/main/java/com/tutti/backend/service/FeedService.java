package com.tutti.backend.service;

import com.sun.org.apache.xpath.internal.operations.Mult;
import com.tutti.backend.domain.Comment;
import com.tutti.backend.domain.Feed;
import com.tutti.backend.domain.User;
import com.tutti.backend.dto.Feed.FeedDetailResponseDto;
import com.tutti.backend.dto.Feed.FeedRequestDto;
import com.tutti.backend.dto.Feed.FeedUpdateRequestDto;
import com.tutti.backend.dto.user.FileRequestDto;
import com.tutti.backend.exception.CustomException;
import com.tutti.backend.exception.ErrorCode;
import com.tutti.backend.repository.CommentRepository;
import com.tutti.backend.repository.FeedRepository;
import com.tutti.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {
    
    private final FeedRepository feedRepository;

    private final S3Service service;

    private final CommentRepository commentRepository;

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
    public Object getFeed(Long feedId) {
        Feed feed = feedRepository.findById(feedId).orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_FEED));
        String artist = feed.getUser().getArtist();
        List<Comment> commentList = commentRepository.findAllByFeed(feed);

        return new FeedDetailResponseDto(feed,artist,commentList);

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

    public Object getMainPage() {
        List<Feed> lastestList = feedRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Feed> randomList = feedRepository.findAll();


        return null;

    }

    public Object getMainPageByUser(User user) {
    return null;
    }
}
