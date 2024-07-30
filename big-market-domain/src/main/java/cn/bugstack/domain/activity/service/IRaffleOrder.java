package cn.bugstack.domain.activity.service;

import cn.bugstack.domain.activity.model.entity.SkuRechargeEntity;

/**
 * 抽奖活动订单接口
 */
public interface IRaffleOrder {

    /**
     * 创建 sku 账户充值订单，给用户增加抽奖次数
     * @param skuRechargeEntity 活动商品充值实体对象
     * @return 活动ID
     */
    String createSkuRechargeOrder(SkuRechargeEntity skuRechargeEntity);
}
