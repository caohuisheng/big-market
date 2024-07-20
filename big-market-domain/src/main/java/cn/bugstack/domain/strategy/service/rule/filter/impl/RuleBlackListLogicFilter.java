package cn.bugstack.domain.strategy.service.rule.filter.impl;

import cn.bugstack.domain.strategy.model.entity.RuleActionEntity;
import cn.bugstack.domain.strategy.model.entity.RuleMatterEntity;
import cn.bugstack.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.annotation.LogicStrategy;
import cn.bugstack.domain.strategy.service.rule.filter.ILogicFilter;
import cn.bugstack.domain.strategy.service.rule.filter.factory.DefaultLogicFactory;
import cn.bugstack.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author: chs
 * @Description: 【抽奖前规则】黑名单用户过滤规则
 * @CreateTime: 2024-07-07
 */
@Slf4j
//@Component
@LogicStrategy(logicModel = DefaultLogicFactory.LogicModel.RULE_BLACKLIST)
public class RuleBlackListLogicFilter implements ILogicFilter<RuleActionEntity.RaffleBeforeEntity> {

    @Resource
    private IStrategyRepository repository;

    @Override
    public RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> filter(RuleMatterEntity ruleMatterEntity) {
        log.info("规则过滤-黑名单 userid:{}, strategyId:{}, ruleModel:{}", ruleMatterEntity.getUserId(),
                ruleMatterEntity.getStrategyId(),ruleMatterEntity.getRuleModel());

        String userId = ruleMatterEntity.getUserId();
        Integer awardId = ruleMatterEntity.getAwardId();
        Long strategyId = ruleMatterEntity.getStrategyId();
        String ruleModel = ruleMatterEntity.getRuleModel();

        // 查询策略的规则值
        String ruleValue = repository.queryStrategyRuleValue(strategyId,awardId,ruleModel);
        String[] splitRuleValue = ruleValue.split(Constants.COLON);
        Integer blackAwardId = Integer.parseInt(splitRuleValue[0]);

        // 黑名单用户id列表
        String[] blackUserIds = splitRuleValue[1].split(Constants.SPLIT);
        // 判断当前用户是否在黑名单用户id列表中
        for(String userBlackId:blackUserIds){
            // 如果用户id存在于黑名单中，返回对应规则动作（拦截）
            if(userId.equals(userBlackId)){
                return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                        .ruleModel(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode())
                        .data(RuleActionEntity.RaffleBeforeEntity.builder()
                                .strategyId(strategyId)
                                .awardId(blackAwardId)
                                .build())
                        .code(RuleLogicCheckTypeVO.TAKE_OVER.getCode())
                        .info(RuleLogicCheckTypeVO.TAKE_OVER.getInfo())
                        .build();
            }
        }

        // 用户id不存在于黑名单中，返回对应规则行为（放行）
        return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                .build();
    }
}
