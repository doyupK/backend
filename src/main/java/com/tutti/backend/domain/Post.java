package com.tutti.backend.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Post extends Timestamped {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;


    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;


    @Column(nullable = false)
    private String albumImage;

    @Column(nullable = false)
    private String songUrl;

    @Column(nullable = false)
    private String genre;

    @Column(nullable = false)
    private String postType;

    @Column(nullable = false)
    private String color;







}
