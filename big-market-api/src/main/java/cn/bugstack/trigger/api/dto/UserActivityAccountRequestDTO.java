package cn.bugstack.trigger.api.dto;

import lombok.Data;

/**
 * Author: chs
 * Description: 用户活动账户请求对象
 * CreateTime: 2024-08-15
 */
@Data
public class UserActivityAccountRequestDTO {

    //用户id
    private String userId;
    //活动id
    private Long activityId;

}
