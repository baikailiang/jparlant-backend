package com.jparlant.mapper;

import com.jparlant.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户数据访问层
 */
@Mapper
public interface UserMapper {

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT id, username, password, nickname, avatar, status, created_at, updated_at " +
            "FROM jparlant_user WHERE username = #{username} LIMIT 1")
    User findByUsername(@Param("username") String username);

    /**
     * 根据ID查询用户
     */
    @Select("SELECT id, username, password, nickname, avatar, status, created_at, updated_at " +
            "FROM jparlant_user WHERE id = #{id}")
    User findById(@Param("id") Long id);
}
