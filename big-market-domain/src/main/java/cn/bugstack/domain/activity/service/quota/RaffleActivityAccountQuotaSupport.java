package cn.bugstack.domain.activity.service.quota;

import cn.bugstack.domain.activity.model.entity.ActivityCountEntity;
import cn.bugstack.domain.activity.model.entity.ActivityEntity;
import cn.bugstack.domain.activity.model.entity.ActivitySkuEntity;
import cn.bugstack.domain.activity.repository.IActivityRepository;
import cn.bugstack.domain.activity.service.quota.rule.factory.DefaultActivityChainFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author: chs
 * Description: 抽奖活动的支撑类
 * CreateTime: 2024-07-28
 */
public class RaffleActivityAccountQuotaSupport {

    protected DefaultActivityChainFactory defaultActivityChainFactory;

    protected IActivityRepository activityRepository;

    public RaffleActivityAccountQuotaSupport(IActivityRepository activityRepository, DefaultActivityChainFactory defaultActivityChainFactor) {
        this.defaultActivityChainFactory = defaultActivityChainFactor;
        this.activityRepository = activityRepository;
    }

    public ActivitySkuEntity queryActivitySku(Long sku){
        return activityRepository.queryActivitySku(sku);
    }

    public ActivityEntity queryRaffleActivityById(Long activityId){
        return activityRepository.queryRaffleActivityById(activityId);
    }

    public ActivityCountEntity queryRaffleActivityCountById(Long activityCountId) {
        return activityRepository.queryRaffleActivityCountById(activityCountId);
    }

}
