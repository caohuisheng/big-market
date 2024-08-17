package cn.bugstack.domain.award.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: chs
 * Description: 分发奖品实体
 * CreateTime: 2024-08-17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistributeAwardEntity {
    //用户id
    private String userId;
    //订单id
    private String orderId;
    //奖品id
    private Integer awardId;
    //奖品配置信息
    private String awardConfig;
}
