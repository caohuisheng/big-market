package cn.bugstack.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Author: chs
 * Description: 未完成支付地活动单
 * CreateTime: 2024-08-19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnpaidActivityOrderEntity {

    // 用户id
    private String userId;
    // 订单id
    private String orderId;
    // 外部透传id
    private String outBusinessNo;
    // 订单金额
    private BigDecimal payAmount;

}
