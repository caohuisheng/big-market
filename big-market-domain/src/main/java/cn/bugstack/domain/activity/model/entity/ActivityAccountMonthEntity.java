package cn.bugstack.domain.activity.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 抽奖活动账户表-月次数
 * @author chs
 * @since 2024-08-04
 */
@Data
public class ActivityAccountMonthEntity {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 月（yyyy-mm）
     */
    private String month;

    /**
     * 月次数
     */
    private Integer monthCount;

    /**
     * 月次数-剩余
     */
    private Integer monthCountSurplus;

}
