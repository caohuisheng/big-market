package cn.bugstack.domain.activity.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 抽奖活动账户表-日次数
 * @author chs
 * @since 2024-08-04
 */
@Data
public class ActivityAccountDayEntity {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 日期（yyyy-mm-dd）
     */
    private String day;

    /**
     * 日次数
     */
    private Integer dayCount;

    /**
     * 日次数-剩余
     */
    private Integer dayCountSurplus;

}
