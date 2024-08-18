package cn.bugstack.domain.activity.service.quota.policy.impl;

import cn.bugstack.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import cn.bugstack.domain.activity.model.valobj.OrderStateVO;
import cn.bugstack.domain.activity.repository.IActivityRepository;
import cn.bugstack.domain.activity.service.quota.policy.ITradePolicy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Author: chs
 * Description:
 * CreateTime: 2024-08-18
 */
@Service("credit_pay_trade")
public class CreditPayTradePolicy implements ITradePolicy {

    @Resource
    private IActivityRepository activityRepository;

    @Override
    public void trade(CreateQuotaOrderAggregate createQuotaOrderAggregate) {
        createQuotaOrderAggregate.setActivityOrderState(OrderStateVO.wait_pay);
        activityRepository.doSaveOrder(createQuotaOrderAggregate);
    }
}
