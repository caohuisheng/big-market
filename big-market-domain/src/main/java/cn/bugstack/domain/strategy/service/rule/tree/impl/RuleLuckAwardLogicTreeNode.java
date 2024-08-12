package cn.bugstack.domain.strategy.service.rule.tree.impl;

import cn.bugstack.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import cn.bugstack.domain.strategy.service.rule.ILogicTreeNode;
import cn.bugstack.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import cn.bugstack.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Author: chs
 * Description: 次数锁节点
 * CreateTime: 2024-07-14
 */
@Slf4j
@Component("rule_luck_award")
public class RuleLuckAwardLogicTreeNode implements ILogicTreeNode {

    @Override
    public DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Integer awardId, String ruleValue, Date endDatetime) {
        log.info("规则过滤-兜底奖品 userId:{}, strategyId:{}, awardId:{}, ruleValue:{}", userId, strategyId, awardId,ruleValue);

        String[] splitRuleValue = ruleValue.split(Constants.SPLIT);
        if(splitRuleValue.length == 0){
            throw new RuntimeException("兜底奖品未配置，ruleValue:" + ruleValue);
        }

        // 获取兜底奖品
        Integer luckAwardId = Integer.parseInt(splitRuleValue[0]);
        String awardRuleValue = splitRuleValue.length > 1?splitRuleValue[1]:"";
        log.info("规则过滤-兜底奖品 userId:{}, strategyId:{}, luckAwardId:{}, awardRuleValue:{}", userId, strategyId, luckAwardId, awardRuleValue);
        // 返回兜底奖品
        return DefaultTreeFactory.TreeActionEntity.builder()
                .ruleLogicCheckTypeVO(RuleLogicCheckTypeVO.TAKE_OVER)
                .strategyAwardData(DefaultTreeFactory.StrategyAwardVO.builder()
                        .awardId(luckAwardId)
                        .awardRuleValue(awardRuleValue)
                        .build())
                .build();
    }

}
