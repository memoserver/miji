package com.mijimemo.server.controller;

import com.alibaba.fastjson.JSONObject;
import com.mijimemo.server.config.Config;
import com.mijimemo.server.constant.ErrorMessage;
import com.mijimemo.server.entity.Comment;
import com.mijimemo.server.entity.Public;
import com.mijimemo.server.service.PublicService;
import com.mijimemo.server.util.JSONUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private PublicService publicService;

    /**
     * 发布
     */
    @PostMapping(value = "",consumes = Config.CONTENT_TYPE, produces = Config.CONTENT_TYPE)
    public JSONObject publish(@RequestBody JSONObject publicJson) {
        Public pub = JSONObject.parseObject(publicJson.toJSONString(), Public.class);

        if(pub.getUserId() == null) {
            return JSONUtils.getJson(1,ErrorMessage.USER_ID_EMPTY);
        }

        if(StringUtils.isEmpty(pub.getTitle())) {
            return JSONUtils.getJson(1,ErrorMessage.TITLE_ERROR);
        }

        if(StringUtils.isEmpty(pub.getContent())) {
            return JSONUtils.getJson(1,ErrorMessage.CONTENT_ERROR);
        }

        pub.setPublishTime(new Date());
        return publicService.insert(pub);
    }

    /**
     * 删除
     */
    @DeleteMapping(value = "/{publicId}")
    public JSONObject delete(@PathVariable("publicId") Long publicId) {
        if(publicId == null)
            return JSONUtils.getJson(1, ErrorMessage.PUBLIC_ID_ERROR);
        return publicService.delete(publicId);
    }

    /**
     * 修改
     */
    @PostMapping(value = "/update",consumes = Config.CONTENT_TYPE)
    public JSONObject update(@RequestBody JSONObject publicJson) {
        Public pub = JSONObject.parseObject(publicJson.toJSONString(), Public.class);
        if(pub.getPublicId() == null) {
            return JSONUtils.getJson(1, ErrorMessage.PUBLIC_ID_ERROR);
        }
        if(StringUtils.isEmpty(pub.getTitle())) {
            return JSONUtils.getJson(1,ErrorMessage.TITLE_ERROR);
        }
        if(StringUtils.isEmpty(pub.getContent())) {
            return JSONUtils.getJson(1,ErrorMessage.CONTENT_ERROR);
        }
        pub.setPublishTime(new Date());
        return publicService.update(pub);
    }

    /**
     * 根据条件搜索
     */
    @GetMapping(value = "",produces = Config.CONTENT_TYPE)
    public JSONObject get(@RequestBody JSONObject publicConditions) {
        Long publicId = publicConditions.getLong("publicId");
        Long userId = publicConditions.getLong("userId");
        String title = publicConditions.getString("title");
        Date publishTime = publicConditions.getDate("publishTime");
        Date eventTime = publicConditions.getDate("eventTime");
        String location = publicConditions.getString("location");
        List<String> tags = (List<String>) publicConditions.get("tags");
        Long collectorId = publicConditions.getLong("collectorId");
        Long commentatorId = publicConditions.getLong("commentatorId");
        Long joinerId = publicConditions.getLong("joinerId");
        return publicService.select(publicId,userId,title,publishTime,eventTime,
                location,tags,collectorId,commentatorId,joinerId);
    }

    /**
     * 发布评论
     */
    @PostMapping(value = "/comment", consumes = Config.CONTENT_TYPE, produces = Config.CONTENT_TYPE)
    public JSONObject publishComment(@RequestBody JSONObject commentJson) {
        Comment comment = JSONObject.parseObject(commentJson.toJSONString(), Comment.class);
        if(comment.getPublicId() == null)
            return JSONUtils.getJson(1, ErrorMessage.PUBLIC_ID_ERROR);
        if(comment.getUserId() == null)
            return JSONUtils.getJson(1, ErrorMessage.USER_ID_EMPTY);
        if(StringUtils.isEmpty(comment.getContent()))
            return JSONUtils.getJson(1, ErrorMessage.CONTENT_ERROR);
        comment.setTime(new Date());
        return publicService.insertComment(comment);
    }

    /**
     * 删除评论
     */
    @DeleteMapping(value = "/comment/{publicId}/{userId}")
    public JSONObject deleteComment(@PathVariable("publicId") Long publicId, @PathVariable("userId") Long userId) {
        return publicService.deleteComment(publicId,userId);
    }

    /**
     * 收藏
     */
    @PostMapping(value = "/collect", consumes = Config.CONTENT_TYPE)
    public JSONObject collect(@RequestBody JSONObject collect) {
        Long publicId = collect.getLong("publicId");
        Long userId = collect.getLong("userId");
        if(publicId == null)
            return JSONUtils.getJson(1, ErrorMessage.PUBLIC_ID_ERROR);
        if(userId == null)
            return JSONUtils.getJson(1, ErrorMessage.USER_ID_EMPTY);
        return publicService.insertCollect(publicId,userId);
    }

    /**
     * 取消收藏
     */
    @DeleteMapping(value = "/collect/{publicId}/{userId}")
    public JSONObject delete(@PathVariable("publicId") Long publicId, @PathVariable("userId") Long userId) {
        return publicService.deleteCollect(publicId,userId);
    }

    /**
     * 加入
     */
    @PostMapping(value = "/join", consumes = Config.CONTENT_TYPE)
    public JSONObject join(@RequestBody JSONObject join) {
        Long publicId = join.getLong("publicId");
        Long userId = join.getLong("userId");
        if(publicId == null)
            return JSONUtils.getJson(1, ErrorMessage.PUBLIC_ID_ERROR);
        if(userId == null)
            return JSONUtils.getJson(1, ErrorMessage.USER_ID_EMPTY);
        return publicService.insertJoin(publicId,userId);
    }

    /**
     * 取消加入
     */
    @DeleteMapping(value = "/join/{publicId}/{userId}")
    public JSONObject deleteJoin(@PathVariable("publicId") Long publicId, @PathVariable("userId") Long userId) {
        return publicService.deleteJoin(publicId,userId);
    }
}
