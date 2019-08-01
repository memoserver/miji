package com.mijimemo.server.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Comment {

    private Long commentId;

    private Long userId;

    private User user;

    private Long publicId;

    private Public pub;

    private String content;

    private Date time;
}
