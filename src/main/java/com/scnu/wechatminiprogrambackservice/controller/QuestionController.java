package com.scnu.wechatminiprogrambackservice.controller;


import cn.dev33.satoken.util.SaResult;
import com.scnu.wechatminiprogrambackservice.entity.Question;
import com.scnu.wechatminiprogrambackservice.mapper.QuestionMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/question")
public class QuestionController {

    @Resource
    private QuestionMapper questionMapper;

    @PostMapping("/save")
    public SaResult save(@RequestBody Question question) {
        // 校验参数
        SaResult validationResult = validParams(question);
        if (validationResult != null) {
            return validationResult;
        }

        // 计算总分
        int totalScore = calculateScore(question);
        question.setCount(totalScore);
        // 插入数据库
        int insertResult = questionMapper.insert(question);
        if (insertResult > 0) {
            log.info("用户填写记录入库成功: " + question);
            return SaResult.ok("总分为：" + totalScore);
        } else {
            return SaResult.error("记录入库失败");
        }
    }

    private int calculateScore(Question question) {
        int score = 0;

        // 使用循环遍历所有问题字段并计算分数
        Integer[] questions = {
                question.getQuestion1(),
                question.getQuestion2(),
                question.getQuestion3(),
                question.getQuestion4(),
                question.getQuestion5(),
                question.getQuestion6(),
                question.getQuestion7(),
                question.getQuestion8()
        };

        for (Integer q : questions) {
            if (isValidAnswer(q)) {
                score += getScoreForAnswer(q);
            }
        }

        return score;
    }

    private boolean isValidAnswer(Integer answer) {
        return answer != null && (answer == 0 || answer == 1 || answer == 2);
    }

    private int getScoreForAnswer(Integer answer) {
        return answer == 0 ? 1 : 0; // "是" 计1分，其他情况（包括"否"和"不知道"）计0分
    }

    private SaResult validParams(Question question) {
        // 验证每个问题字段
        Integer[] questions = {
                question.getQuestion1(),
                question.getQuestion2(),
                question.getQuestion3(),
                question.getQuestion4(),
                question.getQuestion5(),
                question.getQuestion6(),
                question.getQuestion7(),
                question.getQuestion8()
        };

        for (int i = 0; i < questions.length; i++) {
            if (!isValidQuestion(questions[i])) {
                return SaResult.error("Invalid value for question" + (i + 1));
            }
        }

        // 验证其他字段
        if (question.getPhone() != null && !isValidPhone(question.getPhone())) {
            return SaResult.error("Invalid value for phone");
        }
        if (question.getGender() != null && !isValidGender(question.getGender())) {
            return SaResult.error("Invalid value for gender");
        }
        if (question.getAge() != null && !isValidAge(question.getAge())) {
            return SaResult.error("Invalid value for age");
        }
        if (question.getCount() != null && !isValidCount(question.getCount())) {
            return SaResult.error("Invalid value for count");
        }

        return null;
    }

    private boolean isValidQuestion(Integer answer) {
        return answer != null && (answer == 0 || answer == 1 || answer == 2);
    }

    private boolean isValidPhone(String phone) {
        return phone != null && phone.length() == 11;
    }

    private boolean isValidGender(Integer gender) {
        return gender == 0 || gender == 1;
    }

    private boolean isValidAge(Integer age) {
        return age >= 0 && age <= 10;
    }

    private boolean isValidCount(Integer count) {
        return count >= 0 && count <= 1000;
    }

}
