package com.mijimemo.server.service.implement;

import com.alibaba.fastjson.JSONObject;
import com.mijimemo.server.constant.ErrorMessage;
import com.mijimemo.server.entity.Private;
import com.mijimemo.server.mapper.PrivateMapper;
import com.mijimemo.server.mapper.TagMapper;
import com.mijimemo.server.service.PrivateService;
import com.mijimemo.server.util.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class PrivateServiceImpl implements PrivateService {

    private static final Logger log = LoggerFactory.getLogger(PrivateServiceImpl.class);

    @Autowired
    private PrivateMapper privateMapper;

    @Autowired
    private TagMapper tagMapper;

    /**
     * 发布
     */
    @Override
    public JSONObject publish(Private pri) {
        try {
            // insert into private
            privateMapper.insert(pri);

            // 插入图片失败，回滚
            if(!this.insertImages(pri.getPrivateId(),pri.getImages())) {
                privateMapper.delete(pri.getPrivateId());
                return JSONUtils.getJson(-1, ErrorMessage.ERROR);
            }

            // 插入tag失败，回滚
            if(!this.insertTags(pri.getPrivateId(),pri.getTags())) {
                privateMapper.delete(pri.getPrivateId());
                this.deleteImages(pri.getPrivateId());
                return JSONUtils.getJson(-1, ErrorMessage.ERROR);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return JSONUtils.getJson(-1, ErrorMessage.ERROR);
        }

        return JSONUtils.getJson(0,pri);
    }

    /**
     * 增加分类
     */
    @Override
    public JSONObject addToTag(Long privateId, List<String> tags) {
        try {
            Private pri = privateMapper.selectByPrivateId(privateId);
            if(privateId == null)
                return JSONUtils.getJson(1, ErrorMessage.PRIVATE_ERROR);

            if(!this.insertTags(pri.getPrivateId(),tags))
                return JSONUtils.getJson(-1, ErrorMessage.ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            return JSONUtils.getJson(-1, ErrorMessage.ERROR);
        }

        return JSONUtils.getJson(0, ErrorMessage.SUCCESS);
    }

    /**
     * 删除
     */
    @Override
    public JSONObject delete(Long privateId) {
        try {
            Private original = privateMapper.selectByPrivateId(privateId);
            if(original == null)
                return JSONUtils.getJson(1, ErrorMessage.PRIVATE_ERROR);

            // 删除tag
            if(!deleteTags(privateId))
                return JSONUtils.getJson(-1, ErrorMessage.ERROR);

            // 删除图片,回滚
            if(!this.deleteImages(privateId)) {
                insertTags(privateId,original.getTags());
                return JSONUtils.getJson(-1, ErrorMessage.ERROR);
            }

            if(!this.deletePrivate(privateId)) {
                this.insertTags(privateId,original.getTags());
                this.insertImages(privateId,original.getImages());
                return JSONUtils.getJson(-1, ErrorMessage.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JSONUtils.getJson(-1, ErrorMessage.ERROR);
        }

        return JSONUtils.getJson(0, ErrorMessage.SUCCESS);
    }

    /**
     * 删除分类
     */
    @Override
    public JSONObject deleteFromTag(Long privateId, List<String> tags) {
        try {
            if(privateMapper.selectByPrivateId(privateId) == null)
                return JSONUtils.getJson(1, ErrorMessage.PRIVATE_ERROR);

            List<Long> tagIds = tagMapper.selectIdsByContents(tags);
            privateMapper.deleteTags(privateId,tagIds);
        } catch (Exception e) {
            e.printStackTrace();
            return JSONUtils.getJson(-1, ErrorMessage.ERROR);
        }
        return JSONUtils.getJson(0, ErrorMessage.SUCCESS);
    }

    /**
     * 更改
     */
    @Override
    public JSONObject update(Private pri) {
        try {
            // 修改private
            privateMapper.update(pri);

            // 修改图片
            this.deleteImages(pri.getPrivateId());
            this.insertImages(pri.getPrivateId(),pri.getImages());

            // 修改分组
            this.deleteTags(pri.getPrivateId());
            this.insertImages(pri.getPrivateId(),pri.getTags());
        } catch (Exception e) {
            log.error(e.getMessage());
            return JSONUtils.getJson(-1, ErrorMessage.ERROR);
        }

        return JSONUtils.getJson(0, ErrorMessage.SUCCESS);
    }

    /**
     * 移动目录
     */
    @Override
    public JSONObject updateToTag(Long privateId, String from, String to) {
        try {
            if(privateMapper.selectByPrivateId(privateId) == null)
                return JSONUtils.getJson(1, ErrorMessage.PRIVATE_ERROR);

            Long oldTagId = tagMapper.selectIdByContent(from);
            if(oldTagId == null)
                return JSONUtils.getJson(1, ErrorMessage.TAG_ERROR);

            privateMapper.deleteTags(oldTagId,Arrays.asList(oldTagId));

            if(!this.insertTags(privateId,Arrays.asList(to)))
                return JSONUtils.getJson(-1, ErrorMessage.ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            return JSONUtils.getJson(-1, ErrorMessage.ERROR);
        }
        return JSONUtils.getJson(0, ErrorMessage.SUCCESS);
    }


    private Boolean insertImages(Long privateId,
                                 List<String> images) {
        try {
            if(images != null && !images.isEmpty())
                privateMapper.insertImages(privateId,images);
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }

        return true;
    }

    private Boolean insertTags(Long privateId,
                               List<String> tags) {
        try {
            if(tags != null && !tags.isEmpty()) {
                tagMapper.insertTag(tags);
                List<Long> tagIds = tagMapper.selectIdsByContents(tags);
                List<Long> newTagIds = new ArrayList<>();
                for(Long tagId : tagIds) {
                    if(privateMapper.selectTag(privateId,tagId) == 0)
                        newTagIds.add(tagId);
                }
                privateMapper.insertTags(privateId,newTagIds);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }

        return true;
    }

    private Boolean deleteImages(Long privateId){
        try {
            privateMapper.deleteImages(privateId);
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }

        return true;
    }

    private Boolean deleteTags(Long privateId) {
        try {
            privateMapper.deleteTags(privateId,null);
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }

        return true;
    }

    private Boolean deletePrivate(Long privateId) {
        try {
            privateMapper.delete(privateId);
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public JSONObject select(Long privateId, Long userId, String title, Date publishTime, Date alarmTime, Integer urgent) {
        List<Private> privates;
        try {
            if(privateId != null || userId != null || StringUtils.isEmpty(title) || publishTime != null || alarmTime != null || urgent != null) {
                privates = privateMapper.select(privateId,userId,title,publishTime,alarmTime,urgent);
                for(Private pri : privates) {
                    // 图片
                    pri.setImages(privateMapper.selectImages(pri.getPrivateId()));
                    // 分类
                    List<Long> tags = privateMapper.selectTagIdByPrivateId(pri.getPrivateId());
                    if(tags != null && !tags.isEmpty())
                        pri.setTags(tagMapper.selectContentsById(tags));
                }
            }
            else
                return JSONUtils.getJson(1,ErrorMessage.CONDITION_ERROE);
        } catch (Exception e) {
            log.error(e.getMessage());
            return JSONUtils.getJson(-1, ErrorMessage.ERROR);
        }

        return JSONUtils.getJson(0, privates);
    }
}
