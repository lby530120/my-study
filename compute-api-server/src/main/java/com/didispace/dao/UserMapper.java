package com.didispace.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.didispace.bean.User;

@Mapper
public interface UserMapper {

    @Select("SELECT user_id as userId, user_kind as userKind, user_status as userStatus FROM t_app_user WHERE user_id = #{userId}")
    User findByUserId(@Param("userId") Long userId);

}