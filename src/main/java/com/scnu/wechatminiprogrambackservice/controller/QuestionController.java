package com.scnu.wechatminiprogrambackservice.controller;


import cn.dev33.satoken.util.SaResult;
import com.scnu.wechatminiprogrambackservice.entity.Question;
import com.scnu.wechatminiprogrambackservice.mapper.QuestionMapper;
import com.scnu.wechatminiprogrambackservice.model.CountRangeStat;
import com.scnu.wechatminiprogrambackservice.model.CountRangeStatSummary;
import com.scnu.wechatminiprogrambackservice.model.Location;
import com.scnu.wechatminiprogrambackservice.service.RateLimitService;
import com.scnu.wechatminiprogrambackservice.util.IpUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/question")
@EnableAsync
public class QuestionController {

    @Resource
    private QuestionMapper questionMapper;

    @Resource
    private RateLimitService rateLimitService;

    @PostMapping("/getCountResult")
    public SaResult getCountResult() {
        try {
            CountRangeStat countRangeStat = new CountRangeStat();
            CountRangeStatSummary summary = questionMapper.getCountSummary();

            // 计算总数
            int total = calculateAndSetRates(countRangeStat,
                    summary.getNormal(),
                    summary.getDrop(),
                    summary.getDamage());

            // 计算男女数量及比率
            int totalGender = calculateAndSetGenderRates(countRangeStat,
                    summary.getMaleCount(),
                    summary.getFemaleCount());

            // 设置男女性别下的正常、掉落、损坏比率
            setGenderSpecificRates(countRangeStat,
                    summary.getMaleNormal(),
                    summary.getFemaleNormal(),
                    summary.getMaleDrop(),
                    summary.getFemaleDrop(),
                    summary.getMaleDamage(),
                    summary.getFemaleDamage());

            // 获取位置统计数据
            List<Location> locations = questionMapper.countLocation();
            countRangeStat.setLocations(locations);
            countRangeStat.setDayCount(questionMapper.countDailyQuestions());
            countRangeStat.setMonthCount(questionMapper.countMonthlyQuestions());
            countRangeStat.setAllConut(questionMapper.countAllQuestions());

            return SaResult.data(countRangeStat);
        } catch (Exception e) {
            log.error("Error occurred while getting count result", e);
            return SaResult.error("Internal server error");
        }
    }

    private int calculateAndSetRates(CountRangeStat stat, Integer normal, Integer drop, Integer damage) {
        int total = normal + drop + damage;
        if (total > 0) {
            stat.setNormalRate(formatToTwoDecimalPlaces((double) normal / total));
            stat.setDropRate(formatToTwoDecimalPlaces((double) drop / total));
            stat.setDamageRate(formatToTwoDecimalPlaces((double) damage / total));
        } else {
            stat.setNormalRate(0.0);
            stat.setDropRate(0.0);
            stat.setDamageRate(0.0);
        }
        stat.setNormal(normal);
        stat.setDrop(drop);
        stat.setDamage(damage);
        return total;
    }

    private int calculateAndSetGenderRates(CountRangeStat stat, Integer maleCount, Integer femaleCount) {
        int total = maleCount + femaleCount;
        if (total > 0) {
            stat.setMaleCountRate(formatToTwoDecimalPlaces((double) maleCount / total));
            stat.setFemaleCountRate(formatToTwoDecimalPlaces((double) femaleCount / total));
        } else {
            stat.setMaleCountRate(0.0);
            stat.setFemaleCountRate(0.0);
        }
        stat.setMaleCount(maleCount);
        stat.setFemaleCount(femaleCount);
        return total;
    }

    private void setGenderSpecificRates(CountRangeStat stat, Integer maleNormal, Integer femaleNormal,
                                        Integer maleDrop, Integer femaleDrop,
                                        Integer maleDamage, Integer femaleDamage) {
        int maleTotal = stat.getMaleCount();
        int femaleTotal = stat.getFemaleCount();

        stat.setMaleNormalRate(formatToTwoDecimalPlaces((double) maleNormal / maleTotal));
        stat.setFemaleNormalRate(formatToTwoDecimalPlaces((double) femaleNormal / femaleTotal));
        stat.setMaleDropRate(formatToTwoDecimalPlaces((double) maleDrop / maleTotal));
        stat.setFemaleDropRate(formatToTwoDecimalPlaces((double) femaleDrop / femaleTotal));
        stat.setMaleDamageRate(formatToTwoDecimalPlaces((double) maleDamage / maleTotal));
        stat.setFemaleDamageRate(formatToTwoDecimalPlaces((double) femaleDamage / femaleTotal));
    }

    private Double formatToTwoDecimalPlaces(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @GetMapping("/{filename}")
    public ResponseEntity<FileSystemResource> getMusic(@PathVariable String filename) {
        try {
            Path musicPath = Paths.get("/music", filename).normalize();
            log.info("Original path: {}", Paths.get("/music", filename));
            log.info("Resolved path: {}", musicPath.toString());
            if (!Files.exists(musicPath)) {
                log.warn("File does not exist: {}", musicPath.toString());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            if (!Files.isRegularFile(musicPath)) {
                log.warn("Path is not a regular file: {}", musicPath.toString());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            File file = musicPath.toFile();
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"");
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType("audio/mpeg"))
                    .body(new FileSystemResource(file));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("/save")
    public SaResult save(@RequestBody Question question, HttpServletRequest request) {
        try {// 校验参数
//            rateLimitService.limitIpInSeconds(IpUtils.getIpAddr(request),60,5);

            question.setTime(new Date());
            SaResult validationResult = validParams(question);
            if (validationResult != null) {
                return validationResult;
            }
            // 计算总分
            int totalScore = calculateScore(question);
            question.setCount(totalScore);

            insertQuestionAsync(question);
            return SaResult.data(totalScore);
        } catch (Exception e) {
            log.error("Error occurred while saving question: {}", e.getMessage(), e);
            return SaResult.error("Internal server error");
        }
    }

    @Async
    public void insertQuestionAsync(Question question) {
        try {
            int insertResult = questionMapper.insert(question);
            if (insertResult > 0) {
                log.info("用户填写记录入库成功: {}", question);
            } else {
                log.error("记录入库失败: {}", question);
            }
        } catch (Exception e) {
            log.error(question + "异步插入数据库失败: {}", e.getMessage(), e);
        }
    }

    private int calculateScore(Question question) {
        List<Integer> answers = Arrays.asList(
                question.getQuestion1(),
                question.getQuestion2(),
                question.getQuestion3(),
                question.getQuestion4(),
                question.getQuestion5(),
                question.getQuestion6(),
                question.getQuestion7(),
                question.getQuestion8()
        );

        return answers.stream()
                .filter(this::isValidAnswer)
                .mapToInt(this::getScoreForAnswer)
                .sum();
    }

    private boolean isValidAnswer(Integer answer) {
        return answer != null && (answer == 0 || answer == 1 || answer == 2);
    }

    private int getScoreForAnswer(Integer answer) {
        return answer == 0 ? 1 : 0; // "是" 计1分，其他情况（包括"否"和"不知道"）计0分
    }

    private SaResult validParams(Question question) {
        // 验证问题字段
        List<Integer> questions = Arrays.asList(
                question.getQuestion1(),
                question.getQuestion2(),
                question.getQuestion3(),
                question.getQuestion4(),
                question.getQuestion5(),
                question.getQuestion6(),
                question.getQuestion7(),
                question.getQuestion8()
        );

        if (!questions.stream().allMatch(this::isValidQuestion)) {
            return SaResult.error("Invalid value for one or more questions");
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
        if (question.getName() != null && !isValidAName(question.getName())) {
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
        // 首先检查电话号码是否为空以及长度是否为11位
        if (phone == null || phone.length() != 11) return false;
        // 检查第一位是否是1
        if (phone.charAt(0) != '1') return false;

        // 检查第二位是否在3到9之间
        int secondDigit = phone.charAt(1) - '0';
        if (secondDigit < 3 || secondDigit > 9) return false;

        // 尝试将剩下的字符全部转换为数字，如果有任何非数字字符，则返回false
        try {
            Long.parseLong(phone.substring(2));
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    private boolean isValidGender(Integer gender) {
        return gender == 0 || gender == 1;
    }

    private boolean isValidAName(String name) {
        return name.length() <= 20;
    }

    private boolean isValidAge(Integer age) {
        return age >= 0 && age <= 10;
    }

    private boolean isValidCount(Integer count) {
        return count >= 0 && count <= 1000;
    }
}
