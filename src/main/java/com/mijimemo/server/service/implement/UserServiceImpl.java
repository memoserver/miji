package com.mijimemo.server.service.implement;

import com.alibaba.fastjson.JSONObject;
import com.mijimemo.server.constant.ErrorMessage;
import com.mijimemo.server.entity.User;
import com.mijimemo.server.mapper.TagMapper;
import com.mijimemo.server.mapper.UserMapper;
import com.mijimemo.server.service.UserService;
import com.mijimemo.server.util.JSONUtils;
import com.mijimemo.server.util.SHA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TagMapper tagMapper;

    @Override
    public JSONObject login(String account, String password) {
        try {
            // 用户名不存在
            if(userMapper.selectUserById(null,account) == null) {
                return JSONUtils.getJson(1, ErrorMessage.USER_ACCOUNT_ERROR);
            }

            // 密码错误
            if(userMapper.selectUserByAccountAndPassword(account, SHA.getSHA(password)) == null) {
                return JSONUtils.getJson(1, ErrorMessage.USER_PASSWORD_ERROR);
            }

            // 登录成功
            return this.getUser(null,account);
        } catch (Exception e) {
            log.error(e.getMessage());
            return JSONUtils.getJson(-1, ErrorMessage.ERROR);
        }
    }

    @Override
    public JSONObject register(String account, String password) {
        try {
            // 用户已存在
            if(userMapper.selectUserById(null,account) != null)
                return JSONUtils.getJson(1, ErrorMessage.USER_ACCOUNT_EXIST);

            // 插入用户
            userMapper.insertUser(account,SHA.getSHA(password),0);
            return this.getUser(null, account);
        } catch (Exception e) {
            log.error(e.getMessage());
            return JSONUtils.getJson(-1, ErrorMessage.ERROR);
        }
    }

    @Override
    public JSONObject update(User user) {
        try {
            User original = this.getUser(user.getUserId());
            if(original == null)
                return JSONUtils.getJson(1, ErrorMessage.USER_ACCOUNT_ERROR);
            userMapper.updateUser(user);

            // 删除tag再添加
            this.deleteTags(user.getUserId(),null);
            this.addTags(user.getUserId(), user.getTags());
        } catch (Exception e) {
            log.error(e.getMessage());
            return JSONUtils.getJson(-1, ErrorMessage.ERROR);
        }
        return JSONUtils.getJson(0, user);
    }

    @Override
    public JSONObject updatePassword(Long userId, String oldPassword, String newPassword) {
        try {
            User user = this.getUser(userId);
            if(user == null)
                return JSONUtils.getJson(1, ErrorMessage.USER_ERROR);

            String account = user.getAccount();
            if(userMapper.selectUserByAccountAndPassword(account,SHA.getSHA(oldPassword)) == null)
                return JSONUtils.getJson(1, ErrorMessage.USER_PASSWORD_ERROR);

            userMapper.updatePassword(userId,SHA.getSHA(newPassword));
        } catch (Exception e) {
            log.error(e.getMessage());
            return JSONUtils.getJson(-1, ErrorMessage.ERROR);
        }

        return JSONUtils.getJson(0, ErrorMessage.SUCCESS);

    }

    @Override
    public JSONObject addTags(Long userId, List<String> tags) {
        try {
            User user = this.getUser(userId);
            if(user == null)
                return JSONUtils.getJson(1, ErrorMessage.USER_ERROR);

            tagMapper.insertTag(tags);
            List<Long> tagIds = tagMapper.selectIdsByContents(tags);
            List<Long> newTagIds = new ArrayList<>();
            for(Long tagId : tagIds) {
                if(userMapper.selectTag(userId,tagId) == 0)
                    newTagIds.add(tagId);
            }
            if(!newTagIds.isEmpty()) {
                userMapper.insertTags(userId,newTagIds);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return JSONUtils.getJson(-1, ErrorMessage.ERROR);
        }

        return JSONUtils.getJson(0, ErrorMessage.SUCCESS);
    }

    @Override
    public JSONObject deleteTags(Long userId, List<String> tags) {
        try {
            if(tags == null || tags.isEmpty())
                userMapper.deleteTags(userId,null);
            else {
                List<Long> tagIds = tagMapper.selectIdsByContents(tags);
                userMapper.deleteTags(userId, tagIds);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return JSONUtils.getJson(-1, ErrorMessage.ERROR);
        }
        return JSONUtils.getJson(0, ErrorMessage.SUCCESS);
    }

    @Override
    public JSONObject addFollow(Long followingUserId, Long followedUserId) {
        try {
            User followingUser = this.getUser(followedUserId);
            User followedUser = this.getUser(followedUserId);
            if(followingUser == null || followedUser == null)
                return JSONUtils.getJson(1, ErrorMessage.USER_ERROR);

            if(userMapper.selectFollow(followingUserId,followedUserId) == 0)
                userMapper.insertFollow(followingUserId, followedUserId);
        } catch (Exception e) {
            log.error(e.getMessage());
            return JSONUtils.getJson(-1, ErrorMessage.ERROR);
        }
        return JSONUtils.getJson(0, ErrorMessage.SUCCESS);
    }

    @Override
    public JSONObject deleteFollow(Long followingUserId, Long followedUserId) {
        try {
            userMapper.deleteFollow(followingUserId, followedUserId);
        } catch (Exception e) {
            log.error(e.getMessage());
            return JSONUtils.getJson(-1, ErrorMessage.ERROR);
        }
        return JSONUtils.getJson(0, ErrorMessage.SUCCESS);
    }

    @Override
    public JSONObject get(Long userId,
                          String account,
                          String name,
                          Boolean gender,
                          String description,
                          String job,
                          List<String> tags,
                          Long followedUserId,
                          Long followingUserId) {
        List<User> users;
        try {
            List<Long> userIds = new ArrayList<>();
            if(userId != null || StringUtils.isEmpty(account) || StringUtils.isEmpty(name) || gender != null || StringUtils.isEmpty(description) || StringUtils.isEmpty(job))
                userIds.addAll(userMapper.selectUserId(userId,account,name,gender,description,job));
            else if(tags != null && tags.size() > 0) {
                List<Long> tagIds = tagMapper.selectIdsByContents(tags);
                if(tagIds != null && !tagIds.isEmpty())
                    userIds.addAll(userMapper.selectUserIdByTagId(tagIds));
            }
            else if(followedUserId != null) {
                userIds.addAll(userMapper.selectFollowingIdByFollowedId(followedUserId));
            }
            else if(followingUserId != null) {
                userIds.addAll(userMapper.selectFollowedIdByFollowingId(followingUserId));
            }
            users = this.getUsers(userIds);
        } catch (Exception e) {
            log.error(e.getMessage());
            return JSONUtils.getJson(-1, ErrorMessage.ERROR);
        }
        return JSONUtils.getJson(0, users);
    }

    private JSONObject getUser(Long userId, String account) {
        User user;
        try {
            user = userMapper.selectUserById(userId,account);
            if(user == null)
                return JSONUtils.getJson(1, ErrorMessage.USER_ERROR);

            // 获取tag
            List<Long> tagIds = userMapper.selectTagsIdByUserId(user.getUserId());
            if(tagIds != null && !tagIds.isEmpty())
                user.setTags(tagMapper.selectContentsById(tagIds));

            // 获取关注，自己是粉丝，followingId
            List<Long> followedUserIds = userMapper.selectFollowedIdByFollowingId(user.getUserId());
            user.setFollowedUser(this.getUsers(followedUserIds));

            // 获取粉丝, 自己是followedId
            List<Long> followingUserIds = userMapper.selectFollowingIdByFollowedId(user.getUserId());
            user.setFollowingUser(this.getUsers(followingUserIds));
        } catch (Exception e) {
            log.error(e.getMessage());
            return JSONUtils.getJson(-1, ErrorMessage.ERROR);
        }

        return JSONUtils.getJson(0,user);
    }

    private User getUser(Long userId) {
        try {
            return userMapper.selectUserById(userId, null);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private List<User> getUsers(List<Long> userIds) {
        try {
            if(userIds == null || userIds.isEmpty())
                return new ArrayList<>();
            return userMapper.selectUsersByUserId(userIds);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
