package cn.bugstack.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

/**
 * @Author: chs
 * @CreateTime: 2024-07-05
 * @Description: 策略规则实体类
 * @Version: 1.0
 */
@Data
public class StrategyRule {

    // 自增ID
    private Long id;
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
    // 创建时间
    private Date createTime;
    // 更新时间
    private Date updateTime;
}
