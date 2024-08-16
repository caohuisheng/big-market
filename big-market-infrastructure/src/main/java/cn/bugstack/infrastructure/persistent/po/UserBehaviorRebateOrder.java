package cn.bugstack.infrastructure.persistent.po;

import lombok.Data;

/**
 * Author: chs
 * Description:
 * CreateTime: 2024-08-13
 */
@Data
public class UserBehaviorRebateOrder {

    //自增ID
    private String id;
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
    //创建时间
    private String createTime;
    //更新时间
    private String updateTime;

}
