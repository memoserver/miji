package com.mijimemo.server.mapper;

import com.mijimemo.server.entity.Comment;
import com.mijimemo.server.entity.Public;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface PublicMapper {

    void insert(@Param("pub") Public pub);

    void update(@Param("pub") Public pub);

    void insertImages(@Param("publicId") Long publicId,
                      @Param("images") List<String> images);

    void insertTags(@Param("publicId") Long publicId,
                    @Param("tags") List<Long> tags);

    void insertComment(@Param("comment") Comment comment);

    void insertCollect(@Param("publicId") Long publicId,
                       @Param("userId") Long userId,
                       @Param("time") Date time);

    void inserJoin(@Param("publicId") Long publicId,
                   @Param("userId") Long userId,
                   @Param("time") Date time);

    void delete(@Param("publicId") Long publicId);

    void deleteImages(@Param("publicId") Long publicId);

    void deleteTags(@Param("publicId") Long publicId);

    void deleteComment(@Param("publicId") Long publicId,
                       @Param("userId") Long userId);

    void deleteCollect(@Param("publicId") Long publicId,
                       @Param("userId") Long userId);

    void deleteJoin(@Param("publicId") Long publicId,
                    @Param("userId") Long userId);

    List<Long> select(@Param("userId") Long userId,
                      @Param("title") String title,
                      @Param("publishTime") Date publishTime,
                      @Param("eventTime") Date eventTime,
                      @Param("location") String location);

    Public selectByPublicId(@Param("publicId") Long publicId);

    List<String> selectImgsByPublicId(@Param("publicId") Long publicId);

    List<Long> selectTagIdByPublicId(@Param("publicId") Long publicId);

    List<Long> selectPublicIdByTagId(@Param("tags") List<Long> tags);

    // 评论
    List<Comment> selectComment(@Param("publicId") Long publicId,
                                @Param("userId") Long userId);

    List<Long> selectPublicIdByCommentatorId(@Param("userId") Long userId);

    // 收藏
    List<Long> selectPublicIdByCollectorId(@Param("userId") Long userId);

    List<Long> selectCollectorIdByPublicId(@Param("publicId") Long publicId);

    Integer selectCollectByPublicIdAndUserId(@Param("publicId") Long publicId,
                                             @Param("userId") Long userId);

    // 加入
    List<Long> selectPublicIdByJoinerId(@Param("userId") Long userId);

    List<Long> selectJoinerIdByPublicId(@Param("publicId") Long publicId);

    Integer selectJoinByPublicIdAndUserId(@Param("publicId") Long publicId,
                                             @Param("userId") Long userId);
}
