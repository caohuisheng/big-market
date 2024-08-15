package cn.bugstack.domain.rebate.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Author: chs
 * Description: 返利类型
 * CreateTime: 2024-08-14
 */
@Getter
@AllArgsConstructor
public enum RebateTypeVO {

    SKU("sku","活动库存充值奖品"),
    INTEGRAL("integral","用户活动积分")
    ;

    private String code;
    private String info;

}
