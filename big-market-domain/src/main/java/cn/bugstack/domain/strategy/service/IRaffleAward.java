package cn.bugstack.domain.strategy.service;

import cn.bugstack.domain.strategy.model.entity.RaffleAwardEntity;
import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;

import java.util.List;

/**
 * Author: chs
 * Description: 策略奖品接口
 * CreateTime: 2024-07-19
 */
public interface IRaffleAward {

    /**
     * 根据策略id查询抽奖奖品列表
     * @param strategyId 策略id
     * @return 奖品列表
     */
    List<StrategyAwardEntity> queryRaffleStrategyAwardList(Long strategyId);

}
