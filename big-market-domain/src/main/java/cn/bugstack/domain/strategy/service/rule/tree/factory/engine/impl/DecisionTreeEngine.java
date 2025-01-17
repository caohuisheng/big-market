package cn.bugstack.domain.strategy.service.rule.tree.factory.engine.impl;

import cn.bugstack.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import cn.bugstack.domain.strategy.model.valobj.RuleTreeNodeLineVO;
import cn.bugstack.domain.strategy.model.valobj.RuleTreeNodeVO;
import cn.bugstack.domain.strategy.model.valobj.RuleTreeVO;
import cn.bugstack.domain.strategy.service.rule.ILogicTreeNode;
import cn.bugstack.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import cn.bugstack.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Author: chs
 * Description: 决策树引擎
 * CreateTime: 2024-07-14
 */
@Slf4j
public class DecisionTreeEngine implements IDecisionTreeEngine {
    // 责任节点名-节点映射
    private final Map<String, ILogicTreeNode> logicTreeNodeMap;

    private RuleTreeVO ruleTreeVO;

    public DecisionTreeEngine(Map<String, ILogicTreeNode> logicTreeNodeMap, RuleTreeVO ruleTreeVO) {
        this.logicTreeNodeMap = logicTreeNodeMap;
        this.ruleTreeVO = ruleTreeVO;
    }

    @Override
    public DefaultTreeFactory.StrategyAwardVO process(String userId, Long strategyId, Integer awardId, Date endDatetime) {
        DefaultTreeFactory.StrategyAwardVO strategyAwardData = null;

        // 获取决策树根节点名
        String nextNode = ruleTreeVO.getTreeRootRuleNode();
        // 树节点名-节点映射
        Map<String, RuleTreeNodeVO> treeNodeMap = ruleTreeVO.getTreeNodeMap();

        // 规则树节点
        RuleTreeNodeVO ruleTreeNode;
        while(null != nextNode){
            // 获取决策节点
            ruleTreeNode = treeNodeMap.get(nextNode);
            ILogicTreeNode logicTreeNode = logicTreeNodeMap.get(ruleTreeNode.getRuleKey());
            String ruleValue = ruleTreeNode.getRuleValue();

            // 决策节点计算
            DefaultTreeFactory.TreeActionEntity logicEntity = logicTreeNode.logic(userId, strategyId, awardId, ruleValue, endDatetime);
            RuleLogicCheckTypeVO ruleLogicCheckTypeVO = logicEntity.getRuleLogicCheckTypeVO();
            strategyAwardData = logicEntity.getStrategyAwardData();
            log.info("决策树引擎【{}】treeId:{}, node:{}, code:{}",ruleTreeVO.getTreeName(), ruleTreeVO.getTreeId(),
                    nextNode, ruleLogicCheckTypeVO.getCode());

            // 获取下个节点
            nextNode = nextNode(ruleLogicCheckTypeVO.getCode(), ruleTreeNode.getTreeNodeLineVOList());
        }

        // 返回最终结果
        return strategyAwardData;
    }

    /**
     * 获取决策的下一个节点
     * @param matterValue
     * @param treeNodeLineVOList
     * @return
     */
    public String nextNode(String matterValue, List<RuleTreeNodeLineVO> treeNodeLineVOList){
        if(null == treeNodeLineVOList || treeNodeLineVOList.isEmpty()) return null;
        for(RuleTreeNodeLineVO nodeLine:treeNodeLineVOList){
            if(decisionLogic(matterValue, nodeLine)){
                return nodeLine.getRuleNodeTo();
            }
        }
        return null;
        //throw new RuntimeException("决策树引擎，nextNode计算失败，未找到可执行节点！");
    }

    /**
     * 决策规则
     * @param matterValue
     * @param nodeLine
     * @return
     */
    public boolean decisionLogic(String matterValue, RuleTreeNodeLineVO nodeLine){
        switch(nodeLine.getRuleLimitType()){
            case EQUAL:return matterValue.equals(nodeLine.getRuleLimitValue().getCode());
            case GT:
            case LT:
            case GE:
            case LE:
            default:return false;
        }
    }

}
