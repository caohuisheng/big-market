package cn.bugstack.trigger.api.dto;

import lombok.Data;

import java.util.List;

/**
 * Author: chs
 * Description: 抽奖策略规则，权重配置响应对象
 * CreateTime: 2024-08-15
 */
@Data
public class RaffleStrategyRuleWeightResponseDTO {

    //权重规则配置的抽奖次数
    private Integer ruleWeightCount;
    //用户在一个活动下完成的总抽奖次数
    private Integer userActivityAccountTotalCount;
    //当前权重可抽奖范围
    private List<StrategyAward> strategyAwards;

    @Data
    public static class StrategyAward{
        //奖品ID
        private Integer awardId;
        //奖品标题
        private String awardTitle;
    }

}
