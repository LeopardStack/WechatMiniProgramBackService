package com.scnu.wechatminiprogrambackservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnu.wechatminiprogrambackservice.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户实体
     */
    @Select("SELECT * FROM user WHERE username = #{username} AND is_deleted = 0")
    User selectByUsername(@Param("username") String username);

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户实体
     */
    @Select("SELECT * FROM user WHERE phone = #{phone} AND is_deleted = 0")
    User selectByPhone(@Param("phone") String phone);
}