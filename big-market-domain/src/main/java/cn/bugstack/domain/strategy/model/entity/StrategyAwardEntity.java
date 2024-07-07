package cn.bugstack.domain.strategy.model.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: chs
 * @CreateTime: 2024-07-06
 * @Description: 策略奖品实体
 */
@Data
public class StrategyAwardEntity {

    // 抽奖策略ID
    private Long strategyId;
    // 抽奖奖品ID - 内部流转使用
    private Integer awardId;
    // 抽奖奖品标题
    private String awardTitle;
    // 抽奖奖品副标题
    private String awardSubtitle;
    // 奖品库存总量
    private Integer awardCount;
    // 奖品库存剩余
    private Integer awardCountSurplus;
    // 奖品中奖概率
    private BigDecimal awardRate;
    // 规则模型
    private String ruleModels;
    // 排序
    private Integer sort;

}
