package cn.bugstack.infrastructure.persistent.po;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: chs
 * @CreateTime: 2024-07-05
 * @Description: 策略奖品实体类
 * @Version: 1.0
 */
@Data
public class StrategyAward {
    // 自增ID
    private Long id;
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
    // 创建时间
    private Date createTime;
    // 更新时间
    private Date updateTime;

}
