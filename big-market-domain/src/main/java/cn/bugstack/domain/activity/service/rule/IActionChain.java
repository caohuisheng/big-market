package cn.bugstack.domain.activity.service.rule;

import cn.bugstack.domain.activity.model.entity.ActivityCountEntity;
import cn.bugstack.domain.activity.model.entity.ActivityEntity;
import cn.bugstack.domain.activity.model.entity.ActivitySkuEntity;
import cn.bugstack.domain.activity.service.rule.IActionChainArmory;

/**
 * Author: chs
 * Description: 下单规则过滤接口
 * CreateTime: 2024-07-28
 */
public interface IActionChain extends IActionChainArmory {

    boolean action(ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity);

}
