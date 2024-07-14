package cn.bugstack.domain.strategy.service.rule;

import cn.bugstack.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;

/**
 * Author: chs
 * Description: 规则树节点接口
 * CreateTime: 2024-07-14
 */
public interface ILogicTreeNode {

    DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Integer awardId);

}
