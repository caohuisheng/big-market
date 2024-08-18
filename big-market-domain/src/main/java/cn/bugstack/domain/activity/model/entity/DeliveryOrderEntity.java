package cn.bugstack.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: chs
 * Description: 发放订单实体
 * CreateTime: 2024-08-18
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryOrderEntity {

    //用户id
    private String userId;
    //业务防重id - 外部透传
    private String outBusinessNo;

}
