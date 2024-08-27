package cn.bugstack.domain.strategy.service.rule.tree.impl;

import cn.bugstack.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import cn.bugstack.domain.strategy.model.valobj.StrategyAwardStockKeyVO;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.rule.ILogicTreeNode;
import cn.bugstack.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import cn.bugstack.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Author: chs
 * Description: 随机积分节点
 * CreateTime: 2024-07-14
 */
@Slf4j
@Component("rule_luck_award")
public class RuleLuckAwardLogicTreeNode implements ILogicTreeNode {

    @Resource
    private IStrategyRepository strategyRepository;

    @Override
    public DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Integer awardId, String ruleValue, Date endDatetime) {
        log.info("规则过滤-兜底奖品 userId:{}, strategyId:{}, awardId:{}, ruleValue:{}", userId, strategyId, awardId, ruleValue);

        String[] splitRuleValue = ruleValue.split(Constants.COLON);
        if(splitRuleValue.length == 0){
            throw new RuntimeException("兜底奖品未配置，ruleValue：" + ruleValue);
        }

        // 获取兜底奖品
        Integer luckAwardId = Integer.parseInt(splitRuleValue[0]);
        String awardRuleValue = (splitRuleValue.length > 1)?splitRuleValue[1]:"";

        //写入延迟队列，延迟消费更新数据库记录
        strategyRepository.awardStockConsumeSendQueue(StrategyAwardStockKeyVO.builder()
                .strategyId(strategyId)
                .awardId(luckAwardId)
                .build());

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
