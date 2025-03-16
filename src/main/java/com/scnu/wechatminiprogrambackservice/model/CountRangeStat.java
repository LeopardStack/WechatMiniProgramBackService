package com.scnu.wechatminiprogrambackservice.model;

import lombok.Data;

import java.util.List;

@Data
public class CountRangeStat {

    //第一个图需要数据
    private Integer normal;
    private Double normalRate;
    private Double dropRate;
    private Integer drop;
    private Integer damage;
    private Double damageRate;

    //第二个图需要数据
    private Integer maleCount;
    private Double maleCountRate;
    private Integer femaleCount;
    private Double femaleCountRate;

    private Double malenormalRate;
    private Double maledropRate;
    private Double maledamageRate;
    private Double femalenormalRate;
    private Double femaledropRate;
    private Double femaledamageRate;

    //第三张图需要数据
    private List<String> province;
    private List<Integer> provinceCount;


}
