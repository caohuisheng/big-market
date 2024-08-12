package cn.bugstack.domain.strategy.service.armory;

import java.util.Date;

/**
 * @Author: chs
 * @Description: 策略抽奖调度
 * @CreateTime: 2024-07-06
 */
public interface IStrategyDispatch {

    /**
     * 获取抽奖策略装配的随机结果
     * @param strategyId
     * @return 奖品id
     */
    Integer getRandomAwardId(Long strategyId);

    /**
     * 获取抽奖策略装配的随机结果
     * @param strategyId
     * @return 奖品id
     */
    Integer getRandomAwardId(Long strategyId, String ruleWeightValue);

    /**
     * 根据策略id和奖品id扣减奖品库存
     * @param strategyId 策略id
     * @param awardId 奖品id
     * @return 扣减结果
     */
    Boolean subtractionAwardStock(Long strategyId, Integer awardId, Date endDatetime);
}
