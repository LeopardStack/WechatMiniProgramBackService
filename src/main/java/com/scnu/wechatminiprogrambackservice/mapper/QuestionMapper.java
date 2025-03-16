package com.scnu.wechatminiprogrambackservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnu.wechatminiprogrambackservice.entity.Question;
import com.scnu.wechatminiprogrambackservice.model.Location;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface QuestionMapper extends BaseMapper<Question> {

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


}