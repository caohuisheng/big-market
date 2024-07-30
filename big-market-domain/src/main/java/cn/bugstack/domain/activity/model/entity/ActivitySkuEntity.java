package cn.bugstack.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: chs
 * Description: 活动sku实体对象
 * CreateTime: 2024-07-28
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivitySkuEntity {

    //商品sku
    private Long sku;
    //活动ID
    private Long activityId;
    //活动个人参与次数ID
    private Long activityCountId;
    //库存总量
    private Integer stockCount;
    //剩余库存
    private Integer stockCountSurplus;

}
