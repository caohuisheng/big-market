package cn.bugstack.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Author: chs
 * Description: sku商品实体
 * CreateTime: 2024-08-19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkuProductEntity {
    /* 商品sku */
    private Long sku;

    /* 活动ID */
    private Long activityId;

    /* 活动个人参与次数ID */
    private Long activityCountId;

    /* 商品库存 */
    private Integer stockCount;

    /* 剩余库存 */
    private Integer stockCountSurplus;

    /* 商品金额 */
    private BigDecimal productAmount;

    /* 活动配置的次数 */
    private ActivityCount activityCount;

    @Data
    public static class ActivityCount{
        //总次数
        private Integer totalCount;
        //月次数
        private Integer monthCount;
        //日次数
        private Integer dayCount;
    }

}
