package cn.bugstack.domain.strategy.model.valobj;

import cn.bugstack.domain.strategy.service.rule.filter.factory.DefaultLogicFactory;
import cn.bugstack.types.common.Constants;
import lombok.Builder;
import lombok.Getter;

import java.util.Arrays;

/**
 * Author: chs
 * Description: 抽奖策略规则的规则值对象
 * CreateTime: 2024-07-09
 */
@Getter
@Builder
public class StrategyAwardRuleModelVO {

    private String ruleModels;

    /**
     * 获取抽奖中规则
     * @return 抽奖中规则数组
     */
    public String[] raffleCenterRuleModelList(){
        if(ruleModels == null) return null;
        String[] ruleModelArr = ruleModels.split(Constants.SPLIT);
        return Arrays.stream(ruleModelArr)
                .filter(ruleModel -> !ruleModel.equals("rule_random") && DefaultLogicFactory.LogicModel.isCenter(ruleModel))
                .toArray(String[]::new);
    }

    /**
     * 获取抽奖后规则
     * @return 抽奖后规则数组
     */
    public String[] raffleAfterRuleModelList(){
        String[] ruleModelArr = ruleModels.split(Constants.SPLIT);
        return Arrays.stream(ruleModelArr)
                .filter(ruleModel -> DefaultLogicFactory.LogicModel.isAfter(ruleModel))
                .toArray(String[]::new);
    }

}
