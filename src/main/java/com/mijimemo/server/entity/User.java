package com.mijimemo.server.entity;

import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
public class User {

    private Long userId;

    private String account;

    private Integer accountType;

    private String photo;

    private String name;

    private Boolean gender;

    private Date birth;

    private String phone;

    private String description;

    private String job;

    private String address;

    private List<String> tags;

    private List<User> followedUser;      // 关注用户id

    private List<User> followingUser;       // 粉丝用户id
}
