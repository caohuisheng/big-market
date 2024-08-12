package cn.bugstack.domain.strategy.model.entity;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @Author: chs
 * @Description: 抽奖因子实体
 * @CreateTime: 2024-07-07
 */
@Data
@Builder
public class RaffleFactorEntity {

    /** 用户ID */
    private String userId;
    /** 策略ID */
    private Long strategyId;
    /** 结束时间 */
    private Date endDatetime;

}
