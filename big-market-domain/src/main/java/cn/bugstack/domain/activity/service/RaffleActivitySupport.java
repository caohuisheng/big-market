package cn.bugstack.domain.activity.service;

import cn.bugstack.domain.activity.model.entity.ActivityCountEntity;
import cn.bugstack.domain.activity.model.entity.ActivityEntity;
import cn.bugstack.domain.activity.model.entity.ActivitySkuEntity;
import cn.bugstack.domain.activity.repository.IActivityRepository;
import cn.bugstack.domain.activity.service.rule.factory.DefaultActivityChainFactory;

/**
 * Author: chs
 * Description: 抽奖活动的支撑类
 * CreateTime: 2024-07-28
 */
public class RaffleActivitySupport {

    protected DefaultActivityChainFactory defaultActivityChainFactor;

    protected IActivityRepository activityRepository;

    public RaffleActivitySupport(IActivityRepository activityRepository, DefaultActivityChainFactory defaultActivityChainFactor) {
        this.defaultActivityChainFactor = defaultActivityChainFactor;
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
