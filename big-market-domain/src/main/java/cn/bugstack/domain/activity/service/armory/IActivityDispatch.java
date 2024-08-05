package cn.bugstack.domain.activity.service.armory;

import java.util.Date;

/**
 * Author: chs
 * Description: 活动调度【扣减库存】
 * CreateTime: 2024-07-31
 */
public interface IActivityDispatch {

    /**
     * 扣减活动SKU库存
     * @param sku 活动SKU
     * @param endDate 活动结束时间
     */
    boolean subtractionActivitySkuStock(Long sku, Date endDate);

}
