package cn.bugstack.trigger.api.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Author: chs
 * Description: 活动抽奖响应对象
 * CreateTime: 2024-08-08
 */
@Data
@Builder
public class ActivityDrawResponseDTO {

    //奖品id
    private Integer awardId;
    //奖品标题
    private String awardTitle;
    //排序编号
    private Integer awardIndex;

}
