package com.scnu.wechatminiprogrambackservice;

import com.scnu.wechatminiprogrambackservice.entity.User;
import com.scnu.wechatminiprogrambackservice.mapper.UserMapper;
import com.scnu.wechatminiprogrambackservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class WechatMiniProgramBackServiceApplicationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    void contextLoads() {
    }

    /**
     * 修改某个用户的密码
     * 使用@Transactional注解可以在测试完成后自动回滚事务，不影响数据库
     */
    @Test
    @Transactional
    public void testUpdateUserPassword() {
        // 1. 定义要修改的用户ID和新密码
        Long userId = 1507984800071950336L; // admin用户的ID
        String newPassword = "newPassword123";

        // 2. 构建User对象，只设置ID和新密码
        User user = new User();
        user.setId(userId);
        user.setPassword(passwordEncoder.encode(newPassword));

        // 3. 调用服务层方法更新密码
        boolean updated = userService.updateById(user);

        // 4. 验证更新是否成功
        assertTrue(updated, "密码更新应该成功");

        // 5. 可选：验证新密码是否正确设置
        User updatedUser = userMapper.selectById(userId);
        boolean passwordMatches = passwordEncoder.matches(newPassword, updatedUser.getPassword());
        assertTrue(passwordMatches, "新密码应该能够通过验证");

        // 打印结果
        System.out.println("用户ID: " + userId + " 的密码已成功更新!");
        System.out.println("密码加密后的值: " + updatedUser.getPassword());
    }

    @Test
    public void testDeleteUser() {
        String encoded = new BCryptPasswordEncoder().encode("123456");
        System.out.println(encoded);
    }
}