package cn.bugstack.domain.strategy.service;

import cn.bugstack.domain.strategy.model.valobj.RuleWeightVO;

import java.util.List;
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

    /**
     * 查询奖品权重配置
     * @param strategyId 权重id
     * @return 权重配置
     */
    List<RuleWeightVO> queryAwardRuleWeight(Long strategyId);

    /**
     * 查询奖品权重配置
     * @param activityId 活动id
     * @return 权重配置
     */
    List<RuleWeightVO> queryAwardRuleWeightByActivityId(Long activityId);
}
