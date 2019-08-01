package com.mijimemo.server.service.implement;

import com.alibaba.fastjson.JSONObject;
import com.mijimemo.server.constant.ErrorMessage;
import com.mijimemo.server.entity.Comment;
import com.mijimemo.server.entity.Public;
import com.mijimemo.server.mapper.PublicMapper;
import com.mijimemo.server.mapper.TagMapper;
import com.mijimemo.server.mapper.UserMapper;
import com.mijimemo.server.service.PublicService;
import com.mijimemo.server.util.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class PublicServiceImpl implements PublicService {

    private static final Logger log = LoggerFactory.getLogger(PublicServiceImpl.class);

    @Autowired
    private PublicMapper publicMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public JSONObject insert(Public pub) {
        try {
            publicMapper.insert(pub);

            // 添加图片，回滚后要删除添加的public
            if (!insertImgs(pub.getPublicId(), pub.getImages())) {
                publicMapper.delete(pub.getPublicId());
                return JSONUtils.getJson(-1, ErrorMessage.ERROR);
            }

            // 添加tag，回滚后要删除添加的public和image
            if (!insertTags(pub.getPublicId(), pub.getTags())) {
                publicMapper.delete(pub.getPublicId());
                this.deleteImgs(pub.getPublicId());
                return JSONUtils.getJson(-1, ErrorMessage.ERROR);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return JSONUtils.getJson(-1, e.getMessage());
        }

        return JSONUtils.getJson(0, pub);
    }

    @Override
    public JSONObject delete(Long publicId) {
        try {
            // 获取当前所有信息
            JSONObject json = this.selectByPublicId(publicId);
            if (json.getInteger("status") != 0)
                return json;

            Public pub = (Public) json.get("data");

            // 删除评论,不做回滚处理
            this.deleteComment(publicId, null);
            // 删除收藏,不做回滚处理
            this.deleteCollect(publicId, null);
            // 删除加入,不做回滚处理
            this.deleteJoin(publicId, null);

            // 删除tag
            if (!deleteTags(publicId)) {
                return JSONUtils.getJson(-1, ErrorMessage.ERROR);
            }

            // 删除图片
            if (!deleteImgs(publicId)) {
                insertTags(publicId, pub.getTags());
                return JSONUtils.getJson(-1, ErrorMessage.ERROR);
            }

            // 删除
            if (!deletePublic(publicId)) {
                insertTags(publicId, pub.getTags());
                insertImgs(publicId, pub.getImages());
                return JSONUtils.getJson(-1, ErrorMessage.ERROR);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return JSONUtils.getJson(-1, ErrorMessage.ERROR);
        }

        return JSONUtils.getJson(0, ErrorMessage.SUCCESS);
    }

    @Override
    public JSONObject update(Public pub) {
        try {
            JSONObject json = this.selectByPublicId(pub.getPublicId());
            if (json.getInteger("status") != 0)
                return json;

            Public original = (Public) json.get("data");
            if (original == null)
                return JSONUtils.getJson(1, ErrorMessage.PUBLIC_ERROR);

            publicMapper.update(pub);
            if (!deleteImgs(pub.getPublicId())) {
                publicMapper.update(original);
                return JSONUtils.getJson(-1, ErrorMessage.ERROR);
            }
            if (!deleteTags(pub.getPublicId())) {
                publicMapper.update(original);
                insertImgs(pub.getPublicId(), original.getImages());
                return JSONUtils.getJson(-1, ErrorMessage.ERROR);
            }
            if (!insertImgs(pub.getPublicId(), pub.getImages())) {
                publicMapper.update(original);
                insertImgs(pub.getPublicId(), original.getImages());
                insertTags(pub.getPublicId(), original.getTags());
                return JSONUtils.getJson(-1, ErrorMessage.ERROR);
            }
            if (!insertImgs(pub.getPublicId(), pub.getTags())) {
                publicMapper.update(original);
                deleteImgs(pub.getPublicId());
                insertImgs(pub.getPublicId(), original.getImages());
                insertTags(pub.getPublicId(), original.getTags());
                return JSONUtils.getJson(-1, ErrorMessage.ERROR);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return JSONUtils.getJson(-1, e.getMessage());
        }

        return JSONUtils.getJson(0, pub);
    }

    @Override
    public JSONObject select(Long publicId,
                             Long userId,
                             String title,
                             Date publishTime,
                             Date eventTime,
                             String location,
                             List<String> tags,
                             Long collectorId,
                             Long commentatorId,
                             Long joinerId) {
        List<Public> publics = new ArrayList<>();
        try {
            List<Long> publicIds = new ArrayList<>();
            if (publicId != null)
                publicIds.add(publicId);
            // 根据public信息查找的publicId
            else if (userId != null || !StringUtils.isEmpty(title) || publishTime != null || eventTime != null || !StringUtils.isEmpty(location)) {
                publicIds.addAll(publicMapper.select(userId, title, publishTime, eventTime, location));
            }
            // 根据tag查找的publicId
            else if (tags != null && tags.size() > 0) {
                List<Long> tagId = tagMapper.selectIdsByContents(tags);
                if (tagId == null || tagId.size() > 0)
                    publicIds.addAll(publicMapper.selectPublicIdByTagId(tagId));
            }
            else if (collectorId != null) {
                publicIds.addAll(publicMapper.selectPublicIdByCollectorId(collectorId));
            }
            else if (commentatorId != null) {
                publicIds.addAll(publicMapper.selectPublicIdByCommentatorId(commentatorId));
            }
            else if (joinerId != null) {
                publicIds.addAll(publicMapper.selectPublicIdByJoinerId(joinerId));
            }

            for (Long id : publicIds) {
                JSONObject json = this.selectByPublicId(id);
                if (json.getInteger("status") == 0)
                    publics.add((Public) json.get("data"));
            }
        } catch (Exception e) {
            return JSONUtils.getJson(-1, e.getMessage());
        }

        return JSONUtils.getJson(0, publics);
    }

    @Override
    public JSONObject insertComment(Comment comment) {
        try {
            if (publicMapper.selectByPublicId(comment.getPublicId()) == null)
                return JSONUtils.getJson(1, ErrorMessage.PUBLIC_ERROR);
            if (userMapper.selectUserById(comment.getUserId(),null) == null)
                return JSONUtils.getJson(1, ErrorMessage.USER_ERROR);
            publicMapper.insertComment(comment);
        } catch (Exception e) {
            log.error(e.getMessage());
            return JSONUtils.getJson(-1, ErrorMessage.ERROR);
        }

        return JSONUtils.getJson(0, comment);
    }

    @Override
    public JSONObject deleteComment(Long publicId, Long userId) {
        try {
            publicMapper.deleteComment(publicId, userId);
        } catch (Exception e) {
            log.error(e.getMessage());
            return JSONUtils.getJson(-1, ErrorMessage.ERROR);
        }

        return JSONUtils.getJson(0, ErrorMessage.SUCCESS);
    }

    @Override
    public JSONObject insertCollect(Long publicId, Long userId) {
        try {
            if (publicMapper.selectByPublicId(publicId) == null)
                return JSONUtils.getJson(1, ErrorMessage.PUBLIC_ERROR);
            if (userMapper.selectUserById(userId,null) == null)
                return JSONUtils.getJson(1, ErrorMessage.USER_ERROR);
            if(publicMapper.selectCollectByPublicIdAndUserId(publicId,userId) == 0)
                publicMapper.insertCollect(publicId, userId, new Date());
        } catch (Exception e) {
            log.error(e.getMessage());
            return JSONUtils.getJson(-1, ErrorMessage.ERROR);
        }

        return JSONUtils.getJson(0, ErrorMessage.SUCCESS);
    }

    @Override
    public JSONObject deleteCollect(Long publicId, Long userId) {
        try {
            publicMapper.deleteCollect(publicId, userId);
        } catch (Exception e) {
            log.error(e.getMessage());
            return JSONUtils.getJson(-1, ErrorMessage.ERROR);
        }

        return JSONUtils.getJson(0, ErrorMessage.SUCCESS);
    }

    @Override
    public JSONObject insertJoin(Long publicId, Long userId) {
        try {
            if (publicMapper.selectByPublicId(publicId) == null)
                return JSONUtils.getJson(1, ErrorMessage.PUBLIC_ERROR);
            if (userMapper.selectUserById(userId,null) == null)
                return JSONUtils.getJson(1, ErrorMessage.USER_ERROR);
            if(publicMapper.selectJoinByPublicIdAndUserId(publicId,userId) == 0)
                publicMapper.inserJoin(publicId, userId, new Date());
        } catch (Exception e) {
            log.error(e.getMessage());
            return JSONUtils.getJson(-1, ErrorMessage.ERROR);
        }

        return JSONUtils.getJson(0, ErrorMessage.SUCCESS);
    }

    @Override
    public JSONObject deleteJoin(Long publicId, Long userId) {
        try {
            publicMapper.deleteJoin(publicId, userId);
        } catch (Exception e) {
            log.error(e.getMessage());
            return JSONUtils.getJson(-1, ErrorMessage.ERROR);
        }

        return JSONUtils.getJson(0, ErrorMessage.SUCCESS);
    }

    private Boolean insertImgs(Long publicId, List<String> imgs) {
        try {
            if (imgs != null && imgs.size() > 0) {
                publicMapper.insertImages(publicId, imgs);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }

        return true;
    }

    private Boolean insertTags(Long publicId, List<String> tags) {
        try {
            if (tags != null && tags.size() > 0) {
                tagMapper.insertTag(tags);
                List<Long> tagsId = tagMapper.selectIdsByContents(tags);
                publicMapper.insertTags(publicId, tagsId);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }

        return true;
    }

    private Boolean deleteImgs(Long publicId) {
        try {
            publicMapper.deleteImages(publicId);
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }

        return true;
    }

    private Boolean deleteTags(Long publicId) {
        try {
            publicMapper.deleteTags(publicId);
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }

        return true;
    }

    private Boolean deletePublic(Long publicId) {
        try {
            publicMapper.delete(publicId);
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }

        return true;
    }

    private JSONObject selectByPublicId(Long publicId) {
        Public pub;
        try {
            pub = publicMapper.selectByPublicId(publicId);
            if (pub == null)
                return JSONUtils.getJson(1, ErrorMessage.PUBLIC_ERROR);
            // 图片
            pub.setImages(publicMapper.selectImgsByPublicId(publicId));
            // tags
            List<Long> tags = publicMapper.selectTagIdByPublicId(publicId);
            if (tags != null && tags.size() > 0)
                pub.setTags(tagMapper.selectContentsById(tags));
            // comment
            List<Comment> comments = publicMapper.selectComment(publicId, null);
            for (Comment comment : comments) {
                comment.setUser(userMapper.selectUserById(comment.getUserId(),null));
            }
            pub.setComments(comments);
            // collector
            List<Long> collector = publicMapper.selectCollectorIdByPublicId(publicId);
            if (collector != null && collector.size() > 0)
                pub.setCollectors(userMapper.selectUsersByUserId(collector));
            // joiner
            List<Long> joiner = publicMapper.selectJoinerIdByPublicId(publicId);
            if (joiner != null && joiner.size() > 0)
                pub.setJoiners(userMapper.selectUsersByUserId(joiner));
        } catch (Exception e) {
            log.error(e.getMessage());
            return JSONUtils.getJson(-1, e.getMessage());
        }

        return JSONUtils.getJson(0, pub);
    }
}
