package com.mijimemo.server.service;

import com.alibaba.fastjson.JSONObject;
import com.mijimemo.server.entity.User;

import java.util.List;

public interface UserService {

    /**
     * 登录
     */
    JSONObject login(String account, String password);

    /**
     * 注册
     */
    JSONObject register(String account, String password);

    /**
     * 完善/修改信息
     */
    JSONObject update(User user);

    /**
     * 修改密码
     */
    JSONObject updatePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 添加tags
     */
    JSONObject addTags(Long userId, List<String> tags);

    /**
     * 删除tags
     */
    JSONObject deleteTags(Long userId, List<String> tags);

    /**
     * 添加关注
     */
    JSONObject addFollow(Long followingUserId, Long followedUserId);

    /**
     * 取消关注
     */
    JSONObject deleteFollow(Long followingUserId, Long followedUserId);

    /**
     * 查询用户
     */
    JSONObject get(Long userId,
                   String account,
                   String name,
                   Boolean gender,
                   String description,
                   String job,
                   List<String> tags,
                   Long followedUserId,
                   Long followingUserId);
}
