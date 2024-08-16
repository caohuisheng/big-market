package cn.bugstack.trigger.api.dto;

import lombok.Data;

/**
 * Author: chs
 * Description: 抽奖策略规则，权重配置请求对象
 * CreateTime: 2024-08-15
 */
@Data
public class RaffleStrategyRuleWeightRequestDTO {

    //用户id
    private String userId;
    //活动id
    private Long activityId;

}
