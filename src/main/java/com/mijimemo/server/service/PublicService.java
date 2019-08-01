package com.mijimemo.server.service;

import com.alibaba.fastjson.JSONObject;
import com.mijimemo.server.entity.Comment;
import com.mijimemo.server.entity.Public;

import java.util.Date;
import java.util.List;

public interface PublicService {

    /**
     * 添加
     *
     * @param pub
     * @return 发布成功 status=0&Public pub / 服务器异常 -1
     */
    JSONObject insert(Public pub);

    /**
     * 删除
     *
     * @param publicId
     * @return 删除成功0 / 不存在 1 / 服务器异常 -1
     */
    JSONObject delete(Long publicId);

    /**
     * 更新
     *
     * @param pub
     * @return 更新成功0 / 不存在1 / 服务器异常-1
     */
    JSONObject update(Public pub);

    /**
     * 搜索
     *
     * @return 查找成功 status=1 List pubs / 服务器异常 status=-1
     */
    JSONObject select(Long publicId,
                      Long userId,
                      String title,
                      Date publishTime,
                      Date eventTime,
                      String location,
                      List<String> tags,
                      Long collectorId,
                      Long commentatorId,
                      Long joinerId);

    JSONObject insertComment(Comment comment);

    JSONObject deleteComment(Long publicId, Long userId);

    JSONObject insertCollect(Long publicId, Long userId);

    JSONObject deleteCollect(Long publicId, Long userId);

    JSONObject insertJoin(Long publicId, Long userId);

    JSONObject deleteJoin(Long publicId, Long userId);
}
