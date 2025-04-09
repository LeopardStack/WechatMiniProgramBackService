package com.scnu.wechatminiprogrambackservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("question")
public class Question {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID，使用雪花算法生成
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    //0是 1否 2不知道
    private Integer question1;

    private Integer question2;

    private Integer question3;

    private Integer question4;

    private Integer question5;

    private Integer question6;

    private Integer question7;

    private Integer question8;

    private String province;
    private String city;

    //限制11位数字
    private String phone;

    private String name;

    //0男1女
    private Integer gender;
    //0：40以下 1：40-49岁 2:50-29岁 3:60-64 4:65-69
    //5：70-74 6:75-79 7：80-84 8:85-90  9:90以上
    private Integer age;
    //只能0-1000
    private Integer count;

    //无教育、小学、初中、高中、大专、本科、硕士及以上
    private String degree;

    private String organization;

    //多留字段防止多次上线
    private String param1;

    private String param2;

    private String param3;

    private Date time;
}
