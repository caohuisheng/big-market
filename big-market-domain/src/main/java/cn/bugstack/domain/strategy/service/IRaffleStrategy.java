package cn.bugstack.domain.strategy.service;

import cn.bugstack.domain.strategy.model.entity.RaffleAwardEntity;
import cn.bugstack.domain.strategy.model.entity.RaffleFactorEntity;

/**
 * @Author: chs
 * @Description: 抽奖策略
 * @CreateTime: 2024-07-07
 */
public interface IRaffleStrategy {

    /**
     * 执行抽奖
     * @param raffleFactorEntity 抽奖因子实体
     * @return 抽奖奖品实体
     */
    RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity);
}
