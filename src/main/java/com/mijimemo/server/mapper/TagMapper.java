package com.mijimemo.server.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface TagMapper {

    void insertTag(List<String> content);

    Long selectIdByContent(String content);

    List<Long> selectIdsByContents(List<String> contents);

    List<String> selectContentsById(@Param("tagIds") List<Long> tagIds);
}
