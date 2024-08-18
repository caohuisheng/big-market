package cn.bugstack.domain.activity.model.entity;

import cn.bugstack.domain.activity.model.valobj.OrderTradeTypeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: chs
 * Description: 活动商品充值实体对象
 * CreateTime: 2024-07-28
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkuRechargeEntity {
    //用户id
    private String userId;
    //商品sku（activity + activity count）
    private Long sku;
    //幂等业务号（外部谁充值谁透传，从而保证幂等）
    private String outBusinessNo;

    private OrderTradeTypeVO orderTradeType;
}
