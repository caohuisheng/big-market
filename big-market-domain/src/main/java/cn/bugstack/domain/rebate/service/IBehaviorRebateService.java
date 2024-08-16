package cn.bugstack.domain.rebate.service;

import cn.bugstack.domain.activity.model.entity.ActivityAccountEntity;
import cn.bugstack.domain.rebate.model.entity.BehaviorEntity;

import java.util.List;

/**
 * Author: chs
 * Description: 行为返利服务接口
 * CreateTime: 2024-08-13
 */
public interface IBehaviorRebateService {

    /**
     * 创建行为动作的入账订单
     * @param behaviorEntity 行为实体对象
     * @return 订单ID
     */
    List<String> createOrder(BehaviorEntity behaviorEntity);

    /**
     * 查询用户当前是否完成日历签到
     * @param userId 用户id
     * @param outBusinessNo 外部透传防重id
     * @return
     */
    Boolean isCalendarSignRebate(String userId, String outBusinessNo);

    ActivityAccountEntity queryActivityAccountEntity(String userId, Long activityId);

}
