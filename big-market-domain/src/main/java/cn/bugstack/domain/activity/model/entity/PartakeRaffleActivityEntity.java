package cn.bugstack.domain.activity.model.entity;

import lombok.Data;

/**
 * Author: chs
 * Description: 参加抽奖活动实体对象
 * CreateTime: 2024-08-04
 */
@Data
public class PartakeRaffleActivityEntity {
    /**
     * 用户id
     */
    private String userId;

    /**
     * 活动id
     */
    private Long activityId;
}
