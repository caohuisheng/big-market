package cn.bugstack.infrastructure.persistent.po;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 抽奖活动次数配置表
 */
@Data
public class RaffleActivityCount implements Serializable {

    /**
     * 自增ID
     */
    private Long id;

    /**
     * 活动次数编号
     */
    private Long activityCountId;

    /**
     * 总次数
     */
    private Integer totalCount;

    /**
     * 日次数
     */
    private Integer dayCount;

    /**
     * 月次数
     */
    private Integer monthCount;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
