package cn.bugstack.domain.strategy.model.valobj;

import lombok.*;

import java.util.List;

/**
 * Author: chs
 * Description: 权重规则值对象
 * CreateTime: 2024-08-16
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleWeightVO {

    //原始规则值配置
    private String ruleValue;
    //权重值
    private Integer weight;
    //奖品配置
    private List<Integer> awardIds;
    //奖品列表
    private List<Award> awardList;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Award{
        private Integer awardId;
        private String awardTitle;
    }
}
