package cn.bugstack.domain.strategy.service.rule.chain;

/**
 * Author: chs
 * Description: 抽奖策略规则责任链接口
 * CreateTime: 2024-07-14
 */
public interface ILogicChain extends ILogicChainArmory {

    /**
     * 责任链接口
     * @param userId 用户id
     * @param strategyId 策略id
     * @return 奖品id
     */
    Integer logic(String userId, Long strategyId);
}
