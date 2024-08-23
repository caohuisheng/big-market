package cn.bugstack.domain.activity.service.quota;

import cn.bugstack.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import cn.bugstack.domain.activity.model.entity.*;
import cn.bugstack.domain.activity.repository.IActivityRepository;
import cn.bugstack.domain.activity.service.IRaffleActivityAccountQuotaService;
import cn.bugstack.domain.activity.service.quota.policy.ITradePolicy;
import cn.bugstack.domain.activity.service.quota.rule.IActionChain;
import cn.bugstack.domain.activity.service.quota.rule.factory.DefaultActivityChainFactory;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Author: chs
 * Description: 抽奖活动抽象类（定义标准的流程）
 * CreateTime: 2024-07-29
 */
public abstract class AbstractRaffleActivityAccountQuota extends RaffleActivityAccountQuotaSupport implements IRaffleActivityAccountQuotaService {

    private Map<String, ITradePolicy> tradePolicyMap;

    public AbstractRaffleActivityAccountQuota(IActivityRepository activityRepository, DefaultActivityChainFactory defaultActivityChainFactory, Map<String, ITradePolicy> tradePolicyMap){
        super(activityRepository, defaultActivityChainFactory);
        this.tradePolicyMap = tradePolicyMap;
    }

    @Override
    public UnpaidActivityOrderEntity createOrder(SkuRechargeEntity skuRechargeEntity) {
        //1.参数校验
        String userId = skuRechargeEntity.getUserId();
        Long sku = skuRechargeEntity.getSku();
        String outBusinessNo = skuRechargeEntity.getOutBusinessNo();
        if(null == sku || StringUtils.isBlank(userId) || StringUtils.isBlank(outBusinessNo)){
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }
        UnpaidActivityOrderEntity unpaidActivityOrderEntity = activityRepository.queryUnpaidActivityOrder(skuRechargeEntity);
        if(null != unpaidActivityOrderEntity){
            return unpaidActivityOrderEntity;
        }

        //2.查询基础信息
        //2.1 通过sku查询活动sku信息
        ActivitySkuEntity activitySkuEntity = queryActivitySku(sku);
        //2.2 查询活动信息
        ActivityEntity activityEntity = queryRaffleActivityById(activitySkuEntity.getActivityId());
        //2.3 查询次数信息（用户在活动上可参与的次数）
        ActivityCountEntity activityCountEntity = queryRaffleActivityCountById(activitySkuEntity.getActivityCountId());

        //3.活动动作规则校验
        IActionChain actionChain = defaultActivityChainFactory.openActionChain();
        boolean success = actionChain.action(activitySkuEntity, activityEntity, activityCountEntity);

        //4.构建订单聚合规则
        CreateQuotaOrderAggregate createOrderAggregate = buildOrderAggregate(skuRechargeEntity, activitySkuEntity, activityEntity, activityCountEntity);

        // 5.保存订单
        //doSaveOrder(createOrderAggregate);
        ITradePolicy tradePolicy = tradePolicyMap.get(skuRechargeEntity.getOrderTradeType().getCode());
        tradePolicy.trade(createOrderAggregate);

        //6.返回单号
        ActivityOrderEntity activityOrderEntity = createOrderAggregate.getActivityOrderEntity();
        return UnpaidActivityOrderEntity.builder()
                .userId(activityOrderEntity.getUserId())
                .orderId(activityOrderEntity.getOrderId())
                .outBusinessNo(activityOrderEntity.getOutBusinessNo())
                .payAmount(activityOrderEntity.getPayAmount())
                .build();
    }

    protected abstract CreateQuotaOrderAggregate buildOrderAggregate(SkuRechargeEntity skuRechargeEntity, ActivitySkuEntity activitySkuEntity,
                                                                     ActivityEntity activityEntity, ActivityCountEntity activityCountEntity);

    protected abstract void doSaveOrder(CreateQuotaOrderAggregate createOrderAggregate);

}
