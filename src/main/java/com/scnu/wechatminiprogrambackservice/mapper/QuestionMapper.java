package com.scnu.wechatminiprogrambackservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnu.wechatminiprogrambackservice.entity.Question;
import com.scnu.wechatminiprogrambackservice.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


@Mapper
public interface QuestionMapper extends BaseMapper<Question> {


    @Select("SELECT * FROM user WHERE username = #{username} AND is_deleted = 0")
    User selectByUsername(@Param("username") String username);


    @Select("SELECT * FROM user WHERE phone = #{phone} AND is_deleted = 0")
    User selectByPhone(@Param("phone") String phone);
}