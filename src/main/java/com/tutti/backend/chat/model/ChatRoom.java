package com.tutti.backend.chat.model;


import com.tutti.backend.domain.Channel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ChatRoom implements Serializable {

    private static final long serialVersionUID = 6494678977089006639L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Column
    private String roomId;
    @Column
    private String title;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "videochatpost_id")
    private Channel channel;


    public static ChatRoom create(Channel post) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = String.valueOf(post.getId());
        chatRoom.title = post.getTitle();
        chatRoom.channel = post;
        return chatRoom;
    }
}
