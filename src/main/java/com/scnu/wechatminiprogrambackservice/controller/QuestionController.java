package com.scnu.wechatminiprogrambackservice.controller;


import cn.dev33.satoken.util.SaResult;
import com.scnu.wechatminiprogrambackservice.entity.Question;
import com.scnu.wechatminiprogrambackservice.mapper.QuestionMapper;
import com.scnu.wechatminiprogrambackservice.model.CountRangeStat;
import com.scnu.wechatminiprogrambackservice.model.Location;
import jakarta.annotation.Resource;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/question")
@EnableAsync
public class QuestionController {

    @Resource
    private QuestionMapper questionMapper;

    @PostMapping("/getCountResult")
    public SaResult getCountResult() {

        Integer normal = questionMapper.countNormal();
        Integer drop = questionMapper.countDrop();
        Integer damage = questionMapper.countDamage();
        // 计算总数
        int total = normal + drop + damage;

        CountRangeStat countRangeStat = new CountRangeStat();
        countRangeStat.setNormal(normal);
        countRangeStat.setDrop(drop);
        countRangeStat.setDamage(damage);

        // 计算图1比率
        if (total > 0) {
            countRangeStat.setNormalRate(formatToTwoDecimalPlaces((double) normal / total));
            countRangeStat.setDropRate(formatToTwoDecimalPlaces((double) drop / total));
            countRangeStat.setDamageRate(formatToTwoDecimalPlaces((double) damage / total));
        }else{
            countRangeStat.setNormalRate(0.0);
            countRangeStat.setDropRate(0.0);
            countRangeStat.setDamageRate(0.0);
        }

        Integer maleCount=questionMapper.countMale();
        Integer femaleCount=questionMapper.countFemale();
        int total1 = maleCount+femaleCount;
        countRangeStat.setMaleCount(maleCount);
        countRangeStat.setFemaleCount(femaleCount);
        countRangeStat.setMaleCountRate(formatToTwoDecimalPlaces((double) maleCount / total1));
        countRangeStat.setFemaleCountRate(formatToTwoDecimalPlaces((double) femaleCount / total1));


        Integer maleNormal = questionMapper.countMaleNormal();
        Integer femaleNormal = questionMapper.countFemaleNormal();
        Integer maleDrop = questionMapper.countMaleDrop();
        Integer femaleDrop = questionMapper.countFemaleDrop();
        Integer maleDamage = questionMapper.countMaleDamage();
        Integer femaleDamage = questionMapper.countFemaleDamage();

        countRangeStat.setMaleNormalRate(formatToTwoDecimalPlaces((double) maleNormal / maleCount));
        countRangeStat.setFemaleNormalRate(formatToTwoDecimalPlaces((double) femaleNormal / femaleCount));
        countRangeStat.setMaleDropRate(formatToTwoDecimalPlaces((double) maleDrop / maleCount));
        countRangeStat.setFemaleDropRate((double) femaleDrop / femaleCount);
        countRangeStat.setMaleDamageRate((double) maleDamage / maleCount);
        countRangeStat.setFemaleDamageRate((double) femaleDamage / femaleCount);

        List<Location> locations = questionMapper.countLocation();
        countRangeStat.setLocations(locations);

        return SaResult.data(countRangeStat);
    }
    private Double formatToTwoDecimalPlaces(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @PostMapping("/{filename}")
    public ResponseEntity<FileSystemResource> getMusic(@PathVariable String filename) {
        File file = new File("/music", filename);

        if (!file.exists() || !file.isFile()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .body(new FileSystemResource(file));

    }

    @PostMapping("/list")
    public ResponseEntity<List<String>> listMusicFiles() {
        File folder = new File("/music");
        if (!folder.exists() || !folder.isDirectory()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3"));
        if (files == null) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }

        List<String> filenames = Arrays.stream(files)
                .map(File::getName)
                .collect(Collectors.toList());

        return ResponseEntity.ok(filenames);
    }

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

        // 异步插入数据库
        insertQuestionAsync(question);

        return SaResult.data(totalScore);
    }

    @Async
    public void insertQuestionAsync(Question question) {
        int insertResult = questionMapper.insert(question);
        if (insertResult > 0) {
            log.info("用户填写记录入库成功: {}", question);
        } else {
            log.error("记录入库失败: {}", question);
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
        return  name.length() <= 20;
    }

    private boolean isValidAge(Integer age) {
        return age >= 0 && age <= 10;
    }

    private boolean isValidCount(Integer count) {
        return count >= 0 && count <= 1000;
    }
}
