package cn.bugstack.domain.activity.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: chs
 * Description: 活动sku库存key值对象
 * CreateTime: 2024-08-02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivitySkuStockKeyVO {

    //商品sku
    private Long sku;
    //活动ID
    private Long activityId;

}
