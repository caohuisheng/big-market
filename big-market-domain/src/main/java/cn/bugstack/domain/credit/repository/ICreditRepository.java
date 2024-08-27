package cn.bugstack.domain.credit.repository;

import cn.bugstack.domain.credit.model.aggregate.TradeAggregate;
import cn.bugstack.domain.credit.model.entity.CreditAccountEntity;

/**
 * Author: chs
 * Description: 用户积分仓储接口
 * CreateTime: 2024-08-18
 */
public interface ICreditRepository {

    /**
     * 保存用户积分交易订单
     * @param tradeAggregate 交易聚合对象
     */
    void saveUserCreditTradeOrder(TradeAggregate tradeAggregate);

    /**
     * 查询用户积分账户
     * @param userId 用户id
     * @return 用户积分账户
     */
    CreditAccountEntity queryUserCreditAccount(String userId);

}
