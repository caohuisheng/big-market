package cn.bugstack.domain.strategy.model.entity;

import cn.bugstack.types.common.Constants;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author: chs
 * @Description: 策略实体
 * @CreateTime: 2024-07-06
 */
@Data
public class StrategyEntity {

    // 抽奖策略ID
    private Long strategyId;
    // 抽奖策略描述
    private String strategyDesc;
    // 规则模型
    private String ruleModels;

    /**
     * 获取规则模型数组
     * @return
     */
    public String[] getRuleModelArr(){
        if(StringUtils.isBlank(ruleModels)){
            return null;
        }
        return ruleModels.split(Constants.SPLIT);
    }

    // 查看规则模型中是否包含rule_weight，若是则返回rule_weight
    public String getRuleWeight(){
        if(StringUtils.isBlank(ruleModels)) return null;
        String[] ruleModelArr = ruleModels.split(Constants.SPLIT);
        for(String ruleModel:ruleModelArr){
            if(ruleModel.equals("rule_weight")) return ruleModel;
        }
        return null;
    }

}
