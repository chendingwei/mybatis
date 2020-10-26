package com.chen.mapper;

import com.chen.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {

    User getUserByID(@Param("id") int id);
}
