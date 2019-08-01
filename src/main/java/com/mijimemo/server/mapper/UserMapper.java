package com.mijimemo.server.mapper;

import com.mijimemo.server.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Mapper
@Component
public interface UserMapper {

    /**
     * 添加user
     */
    void insertUser(@Param("account") String account,
                    @Param("password") String password,
                    @Param("accountType") Integer accountType);

    /**
     * 添加tags
     */
    void insertTags(@Param("userId") Long userId,
                    @Param("tagIds") List<Long> tagIds);

    /**
     * 添加follow
     *
     * @param followingId Long
     * @param followedId  Long
     */
    void insertFollow(@Param("followingId") Long followingId,
                      @Param("followedId") Long followedId);

    /**
     * 删除tags
     */
    void deleteTags(@Param("userId") Long userId,
                    @Param("tagIds") List<Long> tagIds);

    /**
     * 删除follow
     *
     * @param followingId Long
     * @param followedId  Long
     */
    void deleteFollow(@Param("followingId") Long followingId,
                      @Param("followedId") Long followedId);

    /**
     * 更新user
     *
     * @param user User
     */
    void updateUser(@Param("user") User user);

    /**
     * 更新password
     *
     * @param password String
     */
    void updatePassword(@Param("userId") Long userId,
                        @Param("password") String password);

    /**
     * 根据user_id查询user
     *
     * @param userId Long
     * @return User user
     */
    User selectUserById(@Param("userId") Long userId,
                        @Param("account") String account);

    List<User> selectUsersByUserId(@Param("userIds") List<Long> userIds);

    List<Long> selectUserId(@Param("userId") Long userId,
                            @Param("account") String account,
                            @Param("name") String name,
                            @Param("gender") Boolean gender,
                            @Param("description") String description,
                            @Param("job") String job);

    /**
     * 根据account&password查询user
     *
     * @param account  String
     * @param password String
     * @return User user
     */
    User selectUserByAccountAndPassword(@Param("account") String account,
                                        @Param("password") String password);

    /**
     * 根据userId查询tagId
     *
     * @param userId Long
     * @return List<Long> tags
     */
    List<Long> selectTagsIdByUserId(@Param("userId") Long userId);
    List<Long> selectUserIdByTagId(@Param("tagIds") List<Long> tagsId);

    Integer selectTag(@Param("userId") Long userId,
                      @Param("tagId") Long tagId);

    /**
     * 根据followed_id查询follow的following_id
     *
     * @param followedId Long
     * @return List<Long> following_ids
     */
    List<Long> selectFollowingIdByFollowedId(@Param("followedId") Long followedId);

    /**
     * 根据following_id查询follow的followed_id
     *
     * @param followingId Long
     * @return List<Long> followed_id
     */
    List<Long> selectFollowedIdByFollowingId(@Param("followingId") Long followingId);

    Integer selectFollow(@Param("followingId") Long followingId,
                         @Param("followedId") Long followedId);
}
