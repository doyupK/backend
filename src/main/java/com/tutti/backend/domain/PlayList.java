package com.tutti.backend.domain;

import lombok.*;

import javax.persistence.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PlayList {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    private Feed feed;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

}
