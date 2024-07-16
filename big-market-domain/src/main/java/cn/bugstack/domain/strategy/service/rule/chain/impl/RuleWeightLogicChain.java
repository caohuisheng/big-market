package cn.bugstack.domain.strategy.service.rule.chain.impl;

import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.armory.IStrategyDispatch;
import cn.bugstack.domain.strategy.service.rule.chain.AbstractLogicChain;
import cn.bugstack.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import cn.bugstack.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: chs
 * Description: 权重抽奖责任链
 * CreateTime: 2024-07-14
 */
@Slf4j
@Component("rule_weight")
public class RuleWeightLogicChain extends AbstractLogicChain {

    @Resource
    private IStrategyRepository repository;
    @Resource
    private IStrategyDispatch strategyDispatch;

    // 根据用户id查询用户抽奖消耗的积分值
    public Long userScore = 0L;

    @Override
    public DefaultChainFactory.StrategyAwardVO logic(String userId, Long strategyId) {
        log.info("抽奖责任链-权重开始 userId:{}, strategyId:{}, ruleModel:{}", userId, strategyId, ruleModel());

        String ruleValue = repository.queryStrategyRuleValue(strategyId, ruleModel());
        // 获取scoreToAwards映射（4000：102,103,104 5000:102,103,104,105）
        Map<Long, String> scoreToAwards = getScoreToAwards(ruleValue);
        List<Long> scores = new ArrayList<>(scoreToAwards.keySet());

        // 找到最后一个比当前消耗积分userScore小的score
        Long targetScore = scores.stream()
                .filter(score -> userScore >= score)
                .max(Long::compare)
                .orElse(null);

        if(null != targetScore){
            Integer randomAwardId = strategyDispatch.getRandomAwardId(strategyId, String.valueOf(targetScore));
            log.info("抽奖责任链-权重接管 userId:{}, strategyId:{}, ruleModel:{}, awardId:{}", userId, strategyId, ruleModel(), randomAwardId);
            return new DefaultChainFactory.StrategyAwardVO(randomAwardId, ruleModel());
        }

        log.info("抽奖责任链-权重放行 userId:{}, strategyId:{}, ruleModel:{}", userId, strategyId, ruleModel());
        return next().logic(userId, strategyId);
    }

    /**
     * 将权重规则值转换为scoreToAwards映射（如：4000：101，102，103，104）
     * @param ruleValue 规则权重值
     * @return scoreToAwards映射
     */
    private Map<Long, String> getScoreToAwards(String ruleValue){
        String[] splitRuleValue = ruleValue.split(Constants.SPACE);
        Map<Long, String> scoreToAwards = new HashMap<>();
        for(String scoreToAwardsStr:splitRuleValue){
            if(StringUtils.isEmpty(scoreToAwardsStr)){
                return scoreToAwards;
            }
            String[] parts = scoreToAwardsStr.split(Constants.COLON);
            if(parts.length != 2){
                throw new IllegalArgumentException("invalid ruleValue:" + ruleValue);
            }
            scoreToAwards.put(Long.parseLong(parts[0]), parts[1]);
        }
        return scoreToAwards;
    }

    @Override
    protected String ruleModel() {
        return "rule_weight";
    }

}
