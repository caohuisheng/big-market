package cn.bugstack.domain.credit.service;

import cn.bugstack.domain.credit.model.entity.TradeEntity;

/**
 * Author: chs
 * Description: 积分调额接口
 * CreateTime: 2024-08-18
 */
public interface ICreditAdjustService {

    /**
     * 创建增加积分额度订单
     * @param tradeEntity 交易实体对象
     * @return 单号
     */
    String createOrder(TradeEntity tradeEntity);

}
