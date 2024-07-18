package cn.bugstack.domain.strategy.service;

import cn.bugstack.domain.strategy.model.valobj.StrategyAwardStockKeyVO;

/**
 * Author: chs
 * Description: 抽奖库存相关服务，获取库存消耗队列
 * CreateTime: 2024-07-17
 */
public interface IRaffleStock {

    /**
     * 获取奖品库存消耗队列
     * @return
     */
    StrategyAwardStockKeyVO takeQueueValue() throws InterruptedException;

    /**
     * 更新奖品库存消耗记录
     * @param strategyId 策略id
     * @param awardId 奖品id
     */
    void updateStrategyAwardStock(Long strategyId, Integer awardId);
}
