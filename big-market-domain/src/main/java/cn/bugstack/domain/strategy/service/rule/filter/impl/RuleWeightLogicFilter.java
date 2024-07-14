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
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Author: chs
 * @Description: 【抽奖前规则】根据权重值返回可抽奖奖品范围
 * @CreateTime: 2024-07-07
 */
@Slf4j
//@Component
@LogicStrategy(logicModel = DefaultLogicFactory.LogicModel.RULE_WEIGHT)
public class RuleWeightLogicFilter implements ILogicFilter<RuleActionEntity.RaffleBeforeEntity> {

    @Resource
    private IStrategyRepository repository;

    public Long userScore = 4500L;

    @Override
    public RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> filter(RuleMatterEntity ruleMatterEntity) {
        log.info("规则过滤-权重 userid:{}, strategyId:{}, ruleModel:{}", ruleMatterEntity.getUserId(),
                ruleMatterEntity.getStrategyId(),ruleMatterEntity.getRuleModel());

        String userId = ruleMatterEntity.getUserId();
        Long strategyId = ruleMatterEntity.getStrategyId();
        Integer awardId = ruleMatterEntity.getAwardId();
        String ruleModel = ruleMatterEntity.getRuleModel();

        //1.查询策略对应的策略规则的规则值
        String ruleValue = repository.queryStrategyRuleValue(strategyId,awardId, ruleModel);
        //将ruleValue转换为scoreToAwards映射
        Map<Long, String> scoreToAwards = getScoreToAwards(ruleValue);
        //如果scoreToAwards为空，直接返回对应规则动作实体（放行）
        if(scoreToAwards.isEmpty()){
            return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .build();
        }

        //2.获取所有的积分阈值
        List<Long> scores = new ArrayList<>(scoreToAwards.keySet());
        //找到最后一个小于用户当前积分的积分阈值
        Long targetScore = scores.stream()
                .filter(score -> userScore >= score)
                .max(Long::compareTo)
                .orElse(null);

        //3.执行权重规则过滤
        //如果targetScore不为null，返回对应规则动作实体（根据权重规则过滤）
        if(null != targetScore){
            return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                    .data(RuleActionEntity.RaffleBeforeEntity.builder()
                            .strategyId(strategyId)
                            .ruleWeightValue(String.valueOf(targetScore))
                            .build())
                    .ruleModel(DefaultLogicFactory.LogicModel.RULE_WEIGHT.getCode())
                    .code(RuleLogicCheckTypeVO.TAKE_OVER.getCode())
                    .info(RuleLogicCheckTypeVO.TAKE_OVER.getCode())
                    .build();
        }

        //targetScore为null，返回规则动作实体（放行）
        return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                .info(RuleLogicCheckTypeVO.ALLOW.getCode())
                .build();
    }

    /**
     * 将权重规则值转换为scoreToAwards映射（如：4000：101，102，103，104）
     * @param ruleValue 规则权重值
     * @return
     */
    private Map<Long, String> getScoreToAwards(String ruleValue){
        // 将规则值按空格分隔
        String[] splitRuleValue = ruleValue.split(Constants.SPACE);
        // scoreToAwards映射
        Map<Long, String> scoreToAwards = new HashMap<>();
        for(String scoreToAwardsStr:splitRuleValue){
            // scoreToAwards字符串是否为空
            if(StringUtils.isBlank(scoreToAwardsStr)){
                return scoreToAwards;
            }
            // 将scoreToAwards字符串分为score和awards
            String[] parts = scoreToAwardsStr.split(Constants.COLON);
            if(parts.length != 2){
                throw new IllegalArgumentException("invalid ruleValue:"+ruleValue);
            }
            scoreToAwards.put(Long.parseLong(parts[0]),scoreToAwardsStr);
        }
        return scoreToAwards;
    }
}
