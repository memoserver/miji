package com.mijimemo.server.controller;

import com.alibaba.fastjson.JSONObject;
import com.mijimemo.server.config.Config;
import com.mijimemo.server.constant.ErrorMessage;
import com.mijimemo.server.entity.User;
import com.mijimemo.server.service.UserService;
import com.mijimemo.server.util.JSONUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/user", produces = Config.CONTENT_TYPE)
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 登录
     */
    @GetMapping(value = "")
    public JSONObject login(@RequestBody JSONObject loginJson) {
        String account = loginJson.getString("account");
        String password = loginJson.getString("password");
        if(StringUtils.isEmpty(account))
            return JSONUtils.getJson(1, ErrorMessage.USER_ACCOUNT_EMPTY);
        if(StringUtils.isEmpty(password))
            return JSONUtils.getJson(1, ErrorMessage.USER_PASSWORD_EMPTY);
        return userService.login(account,password);
    }

    /**
     * 注册
     */
    @PostMapping(value = "", consumes = Config.CONTENT_TYPE)
    public JSONObject register(@RequestBody JSONObject userJson) {
        String account = userJson.getString("account");
        String password = userJson.getString("password");
        if(StringUtils.isEmpty(account))
            return JSONUtils.getJson(1, ErrorMessage.USER_ACCOUNT_EMPTY);
        if(StringUtils.isEmpty(password))
            return JSONUtils.getJson(1, ErrorMessage.USER_PASSWORD_EMPTY);

        return userService.register(account,password);
    }

    /**
     * 修改信息
     */
    @PostMapping(value = "/update", consumes = Config.CONTENT_TYPE)
    public JSONObject modifyInfo(@RequestBody JSONObject userJson) {
        User user = JSONObject.parseObject(userJson.toJSONString(),User.class);
        if(user.getUserId() == null)
            return JSONUtils.getJson(1, ErrorMessage.USER_ID_EMPTY);
        return userService.update(user);
    }

    /**
     * 修改密码
     */
    @PostMapping(value = "/password/update", consumes = Config.CONTENT_TYPE)
    public JSONObject modifyPassword(@RequestBody JSONObject userJson) {
        Long userId = userJson.getLong("userId");
        String oldPassword = userJson.getString("oldPassword");
        String newPassword = userJson.getString("newPassword");

        if(userId == null)
            return JSONUtils.getJson(1, ErrorMessage.USER_ID_EMPTY);

        if(oldPassword == null)
            return JSONUtils.getJson(1, "旧密码为空");

        if(newPassword == null)
            return JSONUtils.getJson(1, ErrorMessage.USER_PASSWORD_EMPTY);

        if(oldPassword.equals(newPassword))
            return JSONUtils.getJson(1, "新旧密码相同");

        return userService.updatePassword(userId,oldPassword,newPassword);
    }

    /**
     * 添加tags
     */
    @PostMapping(value = "/tags", consumes = Config.CONTENT_TYPE)
    public JSONObject addTags(@RequestBody JSONObject tagsJson) {
        Long userId = tagsJson.getLong("userId");
        List<String> tags = (List<String>) tagsJson.get("tags");

        if(userId == null)
            return JSONUtils.getJson(1, ErrorMessage.USER_ID_EMPTY);

        return userService.addTags(userId,tags);
    }

    /**
     * 删除tags
     */
    @DeleteMapping(value = "/tags", consumes = Config.CONTENT_TYPE)
    public JSONObject deleteTags(@RequestBody JSONObject tagsJson) {
        Long userId = tagsJson.getLong("userId");
        List<String> tags = (List<String>) tagsJson.get("tags");

        if(userId == null)
            return JSONUtils.getJson(1, ErrorMessage.USER_ID_EMPTY);

        return userService.deleteTags(userId,tags);
    }

    /**
     * 添加关注
     */
    @PostMapping(value = "/follow", consumes = Config.CONTENT_TYPE)
    public JSONObject addFollow(@RequestBody JSONObject followJson) {
        Long followingId = followJson.getLong("followingId");
        Long followedId = followJson.getLong("followedId");

        if(followingId == null)
            return JSONUtils.getJson(1, ErrorMessage.USER_ID_EMPTY);
        if(followedId == null)
            return JSONUtils.getJson(1, ErrorMessage.USER_ID_EMPTY);

        return userService.addFollow(followingId,followedId);
    }

    /**
     * 删除关注
     */
    @DeleteMapping(value = "/follow", consumes = Config.CONTENT_TYPE)
    public JSONObject deleteFollow(@RequestBody JSONObject followJson) {
        Long followingId = followJson.getLong("followingId");
        Long followedId = followJson.getLong("followedId");

        if(followingId == null)
            return JSONUtils.getJson(1, ErrorMessage.USER_ID_EMPTY);
        if(followedId == null)
            return JSONUtils.getJson(1, ErrorMessage.USER_ID_EMPTY);

        return userService.deleteFollow(followingId,followedId);
    }

    /**
     * 根据账号查询用户
     */
    @GetMapping(value = "/account/{account}", produces = Config.CONTENT_TYPE)
    public JSONObject getUserByAccount(@PathVariable("account") String account) {
        return userService.get(null,account,null,null,null,null,null,null,null);
    }

    /**
     * 根据id查询用户
     */
    @GetMapping(value = "/userId/{userId}", produces = Config.CONTENT_TYPE)
    public JSONObject getUserById(@PathVariable("userId") Long userId) {
        return userService.get(userId,null,null,null,null,null,null,null,null);
    }
}
