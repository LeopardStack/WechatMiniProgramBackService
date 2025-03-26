package com.scnu.wechatminiprogrambackservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnu.wechatminiprogrambackservice.entity.Question;
import com.scnu.wechatminiprogrambackservice.model.CountRangeStatSummary;
import com.scnu.wechatminiprogrambackservice.model.Location;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface QuestionMapper extends BaseMapper<Question> {

    @Select("SELECT " +
            "SUM(CASE WHEN count IN (1, 2) THEN 1 ELSE 0 END) AS normal, " +
            "SUM(CASE WHEN count IN (3, 4) THEN 1 ELSE 0 END) AS 'drop', " +
            "SUM(CASE WHEN count >= 5 THEN 1 ELSE 0 END) AS damage, " +
            "SUM(CASE WHEN gender = 0 AND count IN (1, 2) THEN 1 ELSE 0 END) AS maleNormal, " +
            "SUM(CASE WHEN gender = 1 AND count IN (1, 2) THEN 1 ELSE 0 END) AS femaleNormal, " +
            "SUM(CASE WHEN gender = 0 AND count IN (3, 4) THEN 1 ELSE 0 END) AS maleDrop, " +
            "SUM(CASE WHEN gender = 1 AND count IN (3, 4) THEN 1 ELSE 0 END) AS femaleDrop, " +
            "SUM(CASE WHEN gender = 0 AND count >= 5 THEN 1 ELSE 0 END) AS maleDamage, " +
            "SUM(CASE WHEN gender = 1 AND count >= 5 THEN 1 ELSE 0 END) AS femaleDamage, " +
            "SUM(CASE WHEN gender = 0 THEN 1 ELSE 0 END) AS maleCount, " +
            "SUM(CASE WHEN gender = 1 THEN 1 ELSE 0 END) AS femaleCount " +
            "FROM question")
    CountRangeStatSummary getCountSummary();

    @Select("SELECT COUNT(DISTINCT id) AS people_count FROM question " +
            "WHERE count IN (1, 2)")
    Integer countNormal();

    @Select("SELECT COUNT(DISTINCT id) AS people_count FROM question " +
            "WHERE count IN (3, 4)")
    Integer countDrop();

    @Select("SELECT COUNT(DISTINCT id) AS people_count FROM question WHERE count >= 5")
    Integer countDamage();


    @Select("SELECT COUNT(DISTINCT id) FROM question WHERE gender = 0;")
    Integer countMale();

    @Select("SELECT COUNT(DISTINCT id)  FROM question WHERE gender = 1;")
    Integer countFemale();


    @Select("SELECT COUNT(DISTINCT id) AS people_count  FROM question " +
            "WHERE gender = 0 AND count IN (1, 2);")
    Integer countMaleNormal();

    @Select("SELECT COUNT(DISTINCT id) AS people_count  FROM question " +
            "WHERE gender = 1 AND count IN (1, 2);")
    Integer countFemaleNormal();

    @Select("SELECT COUNT(DISTINCT id) AS people_count  FROM question " +
            "WHERE gender = 0 AND count IN (3, 4);")
    Integer countMaleDrop();

    @Select("SELECT COUNT(DISTINCT id) AS people_count  FROM question " +
            "WHERE gender = 1 AND count IN (3, 4);")
    Integer countFemaleDrop();

    @Select("SELECT COUNT(DISTINCT id) AS people_count  FROM question " +
            "WHERE gender = 0 AND count IN (5, 6);")
    Integer countMaleDamage();

    @Select("SELECT COUNT(DISTINCT id) AS people_count  FROM question " +
            "WHERE gender = 1 AND count IN (5, 6);")
    Integer countFemaleDamage();

    @Select("SELECT province,COUNT(*) as count FROM question group by province;")
    List<Location> countLocation();


    @Select("SELECT COUNT(*) AS daily_count FROM question WHERE DATE(time) = CURDATE();")
    Integer countDailyQuestions();

    @Select("SELECT COUNT(*) AS monthly_count FROM question WHERE YEAR(time) = YEAR(CURDATE()) AND MONTH(time) = MONTH(CURDATE());")
    Integer countMonthlyQuestions();

    @Select("SELECT COUNT(*)  FROM question;")
    Integer countAllQuestions();
}