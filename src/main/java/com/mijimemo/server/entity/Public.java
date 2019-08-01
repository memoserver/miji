package com.mijimemo.server.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Public {

    private Long publicId;

    private Long userId;

    private User user;

    private String title;

    private String content;

    private Date publishTime;

    private Date eventTime;

    private String location;

    private List<String> images;

    private List<String> tags;

    private List<Comment> comments;

    private List<User> collectors;

    private List<User> joiners;
}
