package cn.bugstack.domain.rebate.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: chs
 * Description: 用户行为返利订单实体
 * CreateTime: 2024-08-13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BehaviorRebateOrderEntity {

    //用户ID
    private String userId;
    //订单ID
    private String orderId;
    //行为类型
    private String behaviorType;
    //返利描述
    private String rebateDesc;
    //返利类型
    private String rebateType;
    //返利配置
    private String rebateConfig;
    //业务防重ID
    private String outBusinessNo;
    //业务ID
    private String bizId;

}
