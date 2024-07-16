package cn.bugstack.domain.strategy.service.rule.tree.factory;

import cn.bugstack.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import cn.bugstack.domain.strategy.model.valobj.RuleTreeVO;
import cn.bugstack.domain.strategy.service.rule.ILogicTreeNode;
import cn.bugstack.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import cn.bugstack.domain.strategy.service.rule.tree.factory.engine.impl.DecisionTreeEngine;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Author: chs
 * Description:
 * CreateTime: 2024-07-14
 */
@Service
public class DefaultTreeFactory {
    // 节点名-节点映射
    private final Map<String, ILogicTreeNode> logicTreeNodeGroup;

    public DefaultTreeFactory(Map<String, ILogicTreeNode> logicTreeNodeGroup){
        this.logicTreeNodeGroup = logicTreeNodeGroup;
    }

    /**
     * 获取决策树引擎
     * @param ruleTreeVO
     * @return
     */
    public IDecisionTreeEngine openLogicTree(RuleTreeVO ruleTreeVO){
        return new DecisionTreeEngine(logicTreeNodeGroup, ruleTreeVO);
    }

    @Data
    @Builder
    public static class TreeActionEntity{
        private RuleLogicCheckTypeVO ruleLogicCheckTypeVO;
        private StrategyAwardVO strategyAwardData;
    }

    @Data
    @Builder
    public static class StrategyAwardVO{
        // 抽奖奖品id
        private Integer awardId;
        // 抽奖奖品规则
        private String awardRuleValue;
    }

}
