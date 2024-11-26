package cn.bugstack.domain.strategy.model.entity;

import cn.bugstack.types.common.Constants;
import com.sun.org.apache.bcel.internal.classfile.ConstantString;
import lombok.Data;

import java.util.*;

/**
 * @Author: chs
 * @Description: 策略规则实体
 * @CreateTime: 2024-07-06
 */
@Data
public class StrategyRuleEntity {

    // 抽奖策略ID
    private Long strategyId;
    // 抽奖奖品ID
    private Integer awardId;
    // 抽奖规则类型
    private Integer ruleType;
    // 抽奖规则类型
    private String ruleModel;
    // 抽奖规则比值
    private String ruleValue;
    // 抽奖规则描述
    private String ruleDesc;

    /**
     * 获取每个积分阈值可以抽到的奖品列表
     * ruleValues：4000：101,102,103 return: 4000 --> [101,102,103]
     * @return
     */
    public Map<String, List<Integer>> getScoreToAwards(){
        // 判断规则模型是否为rule_weight
        if(!ruleModel.equals("rule_weight")) return null;
        // 将ruleValue按空格分开
        String[] ruleValueArr = ruleValue.split(Constants.SPACE);
        // 每个积分阈值-可以抽到的奖品列表键值对
        Map<String, List<Integer>> scoreToAwards = new LinkedHashMap<>();
        // 遍历每个ruleValue
        for(String rule:ruleValueArr){
            // 将ruleValue氛围score积分和对应的奖品列表id
            String[] scoreAndAwards = rule.split(Constants.COLON);
            if(scoreAndAwards.length != 2){
                throw new IllegalArgumentException("invalid value of rule_value!"+ruleValue);
            }
            String score = scoreAndAwards[0];
            String[] awardArr = scoreAndAwards[1].split(Constants.SPLIT);
            List<Integer> awards = new ArrayList<>();
            for (String award : awardArr) {
                awards.add(Integer.parseInt(award));
            }
            scoreToAwards.put(score, awards);
        }
        return scoreToAwards;
    }
}
