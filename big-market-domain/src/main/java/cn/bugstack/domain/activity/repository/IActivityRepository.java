package cn.bugstack.domain.activity.repository;

import cn.bugstack.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import cn.bugstack.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import cn.bugstack.domain.activity.model.entity.*;
import cn.bugstack.domain.activity.model.valobj.ActivitySkuStockKeyVO;

import java.util.Date;
import java.util.List;

/**
 * Author: chs
 * Description: 活动仓储接口
 * CreateTime: 2024-07-29
 */
public interface IActivityRepository {
    ActivitySkuEntity queryActivitySku(Long sku);

    List<ActivitySkuEntity> queryActivitySkuByActivityId(Long activityId);

    ActivityEntity queryRaffleActivityById(Long activityId);

    ActivityCountEntity queryRaffleActivityCountById(Long activityCountId);

    void doSaveOrder(CreateQuotaOrderAggregate createOrderAggregate);

    void doSaveCreditPayOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate);

    /**
     * 保存返利兑换订单
     * @param createQuotaOrderAggregate
     */
    void doSaveNoPayOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate);

    boolean subtractionActivitySkuStock(Long sku, String cacheKey, Date endDate);

    void cacheActivitySkuStockCount(String cacheKey, Integer stockCount);

    void activitySkuStockConsumeSendQueue(ActivitySkuStockKeyVO activitySkuStockKeyVO);

    ActivitySkuStockKeyVO takeQueueValue();

    void clearQueueValue();

    void updateActivitySkuStock(Long sku);

    void clearActivitySkuStock(Long sku);

    UserRaffleOrderEntity queryNoUsedRaffleOrder(String userId, Long activityId);

    ActivityAccountEntity queryActivityAccountByUserId(String userId, Long activityId);

    ActivityAccountMonthEntity queryActivityAccountMonthByUserId(String userId, Long activityId, String month);

    ActivityAccountDayEntity queryActivityAccountDayByUserId(String userId, Long activityId, String day);

    void saveCreatePartakeOrderAggregate(CreatePartakeOrderAggregate createPartakeOrderAggregate);

    Integer queryRaffleActivityAccountDayPartakeCount(Long activityId, String userId);

    ActivityAccountEntity queryActivityAccountEntity(String userId, Long activityId);

    void updateOrder(DeliveryOrderEntity deliveryOrderEntity);

    UnpaidActivityOrderEntity queryUnpaidActivityOrder(SkuRechargeEntity skuRechargeEntity);

    List<SkuProductEntity> querySkuProductEntitiesByActivityId(Long activityId);

}
