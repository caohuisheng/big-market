package cn.bugstack.domain.strategy.service.armory;

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
}
