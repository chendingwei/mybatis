package com.chen.mapper;

import com.chen.pojo.Teacher;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TeacherMapper {
//    List<Teacher> getTeacher();

//    获取指定老师下的所有学生，及老师信息
    Teacher getTeacher(@Param("tid") int id);
}
