package com.example.community.dao;

import com.example.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts(@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit);

//    @Param()注解用于给参数取别名
//    如果方法中只有一个参数，并且在mpper.xml中的<if>里使用，则必须加别名
    int selectDiscussPostRows(@Param("userId") int userId);
}
