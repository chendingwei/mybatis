package com.chen.mapper;

import com.chen.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface UserMapper {
    @Select("select * from user")
    List<User> getUser();


    @Select("select * from user where id = #{id}")
    User getUserByID(@Param("id") int id, @Param("name") String name);
}
