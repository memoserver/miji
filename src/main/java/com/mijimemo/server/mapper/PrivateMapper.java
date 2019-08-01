package com.mijimemo.server.mapper;

import com.mijimemo.server.entity.Private;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface PrivateMapper {

    void insert(@Param("pri") Private pri);
    void update(@Param("pri") Private pri);

    void insertImages(@Param("privateId") Long privateId,
                      @Param("images") List<String> images);

    void insertTags(@Param("privateId") Long privateId,
                   @Param("tagId") List<Long> tags);
    void updateTag(@Param("privateId") Long privateId,
                   @Param("fromTagId") Long fromTagId,
                   @Param("toTagId") Long toTagId);

    void delete(@Param("privateId") Long privateId);

    void deleteImages(@Param("privateId") Long privateId);

    void deleteTags(@Param("privateId") Long privateId,
                    @Param("tagIds") List<Long> tagIds);

    List<Private> select(@Param("privateId") Long privateId,
                         @Param("userId") Long userId,
                         @Param("title") String title,
                         @Param("publishTime") Date publishTime,
                         @Param("alarmTime")Date alarmTime,
                         @Param("urgent") Integer urgent);
    Private selectByPrivateId(@Param("privateId") Long privateId);

    List<String> selectImages(@Param("privateId") Long privateId);

    List<Long> selectPrivateIdByTagId(@Param("tagId") Long tagId);
    List<Long> selectTagIdByPrivateId(@Param("privateId") Long privateId);
    Integer selectTag(@Param("privateId") Long privateId,
                      @Param("tagId") Long tagId);
}
