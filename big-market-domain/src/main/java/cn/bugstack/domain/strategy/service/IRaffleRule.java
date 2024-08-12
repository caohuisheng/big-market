package cn.bugstack.domain.strategy.service;

import java.util.Map;

/**
 * Author: chs
 * Description: 抽奖规则接口
 * CreateTime: 2024-08-11
 */
public interface IRaffleRule {

    /**
     * 根据规则树ID集合查询奖品中加锁数量的配置
     * @param treeIds
     * @return
     */
    Map<String, Integer> queryAwardRuleLockCount(String[] treeIds);
}
