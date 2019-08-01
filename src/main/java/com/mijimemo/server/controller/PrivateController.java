package com.mijimemo.server.controller;

import com.alibaba.fastjson.JSONObject;
import com.mijimemo.server.config.Config;
import com.mijimemo.server.constant.ErrorMessage;
import com.mijimemo.server.entity.Private;
import com.mijimemo.server.service.PrivateService;
import com.mijimemo.server.util.JSONUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/private",produces = Config.CONTENT_TYPE)
public class PrivateController {

    @Autowired
    private PrivateService privateService;

    /**
     * 发布
     */
    @PostMapping(value = "",consumes = Config.CONTENT_TYPE)
    public JSONObject publish(@RequestBody JSONObject privateJson) {
        Private pri = JSONObject.parseObject(privateJson.toJSONString(), Private.class);

        if(pri.getUserId() == null) {
            return JSONUtils.getJson(1, ErrorMessage.USER_ID_EMPTY);
        }

        if(StringUtils.isEmpty(pri.getTitle())) {
            return JSONUtils.getJson(1, ErrorMessage.TITLE_ERROR);
        }

        if(StringUtils.isEmpty(pri.getContent())) {
            return JSONUtils.getJson(1, ErrorMessage.CONTENT_ERROR);
        }

        pri.setPublishTime(new Date());
        return new JSONObject(privateService.publish(pri));
    }

    /**
     * 增加分类
     */
    @PostMapping(value = "/tags",consumes = Config.CONTENT_TYPE)
    public JSONObject addToTag(@RequestBody JSONObject tagsJson) {
        Long privateId = tagsJson.getLong("privateId");
        List<String> tags = tagsJson.getObject("tags",List.class);

        if(privateId == null)
            return JSONUtils.getJson(1, ErrorMessage.PRIVATE_ID_ERROR);
        if(tags == null || tags.isEmpty())
            return JSONUtils.getJson(1, ErrorMessage.TAG_ERROR);

        return privateService.addToTag(privateId,tags);
    }

    /**
     * 删除
     */
    @DeleteMapping(value = "/{privateId}")
    public JSONObject delete(@PathVariable("privateId") Long privateId) {
        return privateService.delete(privateId);
    }

    /**
     * 删除分类
     */
    @DeleteMapping(value = "/tag")
    public JSONObject deleteFromTag(@RequestBody JSONObject tagJson) {
        Long privateId = tagJson.getLong("privateId");
        List<String> tags = (List<String>) tagJson.get("tags");
        return privateService.deleteFromTag(privateId,tags);
    }

    /**
     * 更改
     */
    @PostMapping(value = "/update", consumes = Config.CONTENT_TYPE)
    public JSONObject update(@RequestBody JSONObject privateJson) {
        Private pri = JSONObject.parseObject(privateJson.toJSONString(),Private.class);
        if(pri.getPrivateId() == null)
            return JSONUtils.getJson(1, ErrorMessage.PRIVATE_ID_ERROR);

        if(StringUtils.isEmpty(pri.getTitle())) {
            return JSONUtils.getJson(1,ErrorMessage.TITLE_ERROR);
        }
        if(StringUtils.isEmpty(pri.getContent())) {
            return JSONUtils.getJson(1,ErrorMessage.CONTENT_ERROR);
        }

        pri.setPublishTime(new Date());
        return privateService.update(pri);
    }

    /**
     * 移动目录
     */
    @PostMapping(value = "/tag/update",consumes = Config.CONTENT_TYPE)
    public JSONObject updateToTag(@RequestBody JSONObject tagJson) {
        Long privateId = tagJson.getLong("privateId");
        String from = tagJson.getString("from");
        String to = tagJson.getString("to");

        if(privateId == null)
            return JSONUtils.getJson(1, ErrorMessage.PRIVATE_ID_ERROR);
        if(StringUtils.isEmpty(from) || StringUtils.isEmpty(to))
            return JSONUtils.getJson(1, ErrorMessage.TAG_ERROR);

        return privateService.updateToTag(privateId,from,to);
    }

    /**
     * 获取
     */
    @GetMapping(value = "/{userId}")
    public JSONObject selectPrivatesByUserId(@PathVariable("userId") Long userId) {
        return privateService.select(null,userId,null,null,null,null);
    }
}
