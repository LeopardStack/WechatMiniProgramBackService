package com.scnu.wechatminiprogrambackservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnu.wechatminiprogrambackservice.entity.UserPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<UserPO> {

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户对象
     */
    @Select("SELECT * FROM user WHERE username = #{username} AND is_deleted = 0")
    UserPO selectByUsername(@Param("username") String username);
}