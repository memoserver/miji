package com.mijimemo.server.service;

import com.alibaba.fastjson.JSONObject;
import com.mijimemo.server.entity.Private;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface PrivateService {

    /**
     * 发布
     */
    JSONObject publish(Private pri);

    /**
     * 增加分类
     */
    JSONObject addToTag(Long privateId,
                         List<String> tags);

    /**
     * 删除
     */
    JSONObject delete(Long privateId);

    /**
     * 删除分类
     */
    JSONObject deleteFromTag(Long privateId, List<String> tags);

    /**
     * 更改
     */
    JSONObject update(Private pri);

    /**
     * 移动目录
     */
    JSONObject updateToTag(Long privateId,
                         String from,
                         String to);

    /**
     * 获取
     */
    JSONObject select(Long privateId,
                      Long userId,
                      String title,
                      Date publishTime,
                      Date alarmTime,
                      Integer urgent);
}
