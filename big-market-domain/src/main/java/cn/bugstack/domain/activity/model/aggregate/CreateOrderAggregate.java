package cn.bugstack.domain.activity.model.aggregate;

import cn.bugstack.domain.activity.model.entity.ActivityOrderEntity;
import lombok.Builder;
import lombok.Data;

/**
 * Author: chs
 * Description: 下单聚合对象
 * CreateTime: 2024-07-29
 */
@Data
@Builder
public class CreateOrderAggregate {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 增加；总次数
     */
    private Integer totalCount;

    /**
     * 增加；日次数
     */
    private Integer dayCount;

    /**
     * 增加；月次数
     */
    private Integer monthCount;

    /**
     * 活动订单实体
     */
    private ActivityOrderEntity activityOrderEntity;
}
