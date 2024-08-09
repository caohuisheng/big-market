package cn.bugstack.domain.activity.service.armory;

import cn.bugstack.domain.activity.model.entity.ActivitySkuEntity;
import cn.bugstack.domain.activity.repository.IActivityRepository;
import cn.bugstack.types.common.Constants;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Author: chs
 * Description: 活动装配
 * CreateTime: 2024-07-31
 */
@Service
public class ActivityArmory implements IActivityArmory, IActivityDispatch {

    @Resource
    private IActivityRepository activityRepository;

    @Override
    public boolean assembleActivitySku(Long sku) {
        //预热活动SKU库存
        ActivitySkuEntity activitySkuEntity = activityRepository.queryActivitySku(sku);
        cacheActivitySkuStockCount(sku, activitySkuEntity.getStockCount());

        //预热活动
        activityRepository.queryRaffleActivityById(activitySkuEntity.getActivityId());

        //预热活动次数
        activityRepository.queryRaffleActivityCountById(activitySkuEntity.getActivityCountId());
        return true;
    }

    @Override
    public boolean assembleActivitySkuByActivityId(Long activityId) {
        List<ActivitySkuEntity> activitySkuEntities = activityRepository.queryActivitySkuByActivityId(activityId);
        for (ActivitySkuEntity activitySkuEntity : activitySkuEntities) {
            cacheActivitySkuStockCount(activitySkuEntity.getSku(), activitySkuEntity.getStockCount());
            //预热活动次数
            activityRepository.queryRaffleActivityCountById(activitySkuEntity.getActivityCountId());
        }

        //预热活动
        activityRepository.queryRaffleActivityById(activityId);
        return false;
    }

    @Override
    public boolean subtractionActivitySkuStock(Long sku, Date endDate) {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_COUNT_KEY + sku;
        return activityRepository.subtractionActivitySkuStock(sku, cacheKey, endDate);
    }

    private void cacheActivitySkuStockCount(Long sku, Integer stockCount){
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_COUNT_KEY + sku;
        activityRepository.cacheActivitySkuStockCount(cacheKey, stockCount);
    }

}
