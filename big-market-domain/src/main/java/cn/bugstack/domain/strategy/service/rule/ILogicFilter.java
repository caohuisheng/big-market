package cn.bugstack.domain.strategy.service.rule;

import cn.bugstack.domain.strategy.model.entity.RuleActionEntity;
import cn.bugstack.domain.strategy.model.entity.RuleMatterEntity;

/**
 * @Author: chs
 * @Description: 抽奖规则过滤接口
 * @CreateTime: 2024-07-07
 */
public interface ILogicFilter<T extends RuleActionEntity.RaffleEntity> {

    /**
     * 执行规则过滤
     * @param ruleMatterEntity 规则物料实体
     * @return 规则动作实体
     */
    RuleActionEntity<T> filter(RuleMatterEntity ruleMatterEntity);

}
