package com.scnu.wechatminiprogrambackservice.model;

import lombok.Data;

@Data
public class CountRangeStatSummary {

    private Integer normal;
    private Integer drop;
    private Integer damage;
    private Integer maleNormal;
    private Integer femaleNormal;
    private Integer maleDrop;
    private Integer femaleDrop;
    private Integer maleDamage;
    private Integer femaleDamage;
    private Integer maleCount;
    private Integer femaleCount;


}
