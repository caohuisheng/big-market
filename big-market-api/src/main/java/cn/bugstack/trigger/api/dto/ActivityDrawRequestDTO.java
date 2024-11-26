package cn.bugstack.trigger.api.dto;

import lombok.Data;

/**
 * Author: chs
 * Description: 活动抽奖请求对象
 * CreateTime: 2024-08-08
 */
@Data
public class ActivityDrawRequestDTO {

    //用户id
    private String userId;
    //活动id
    private Long activityId;
    //抽奖次数
    private Integer raffleCount;

}
