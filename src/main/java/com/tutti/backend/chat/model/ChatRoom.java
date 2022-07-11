package com.tutti.backend.chat.model;


import com.tutti.backend.domain.VideoChatPost;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class ChatRoom implements Serializable {

    private static final long serialVersionUID = 6494678977089006639L;
    public String roomId;
    public String title;

    public static ChatRoom create(VideoChatPost post) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = String.valueOf(post.getId());
        chatRoom.title = post.getTitle();
        return chatRoom;
    }
}
