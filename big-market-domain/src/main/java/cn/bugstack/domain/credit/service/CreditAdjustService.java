package cn.bugstack.domain.credit.service;

import cn.bugstack.domain.credit.model.aggregate.TradeAggregate;
import cn.bugstack.domain.credit.model.entity.CreditAccountEntity;
import cn.bugstack.domain.credit.model.entity.CreditOrderEntity;
import cn.bugstack.domain.credit.model.entity.TradeEntity;
import cn.bugstack.domain.credit.model.vo.TradeNameVO;
import cn.bugstack.domain.credit.model.vo.TradeTypeVO;
import cn.bugstack.domain.credit.repository.ICreditRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * Author: chs
 * Description: 积分调额服务
 * CreateTime: 2024-08-18
 */
@Slf4j
@Service
public class CreditAdjustService implements ICreditAdjustService {

    @Resource
    private ICreditRepository repository;

    @Override
    public String createOrder(TradeEntity tradeEntity) {
        String userId = tradeEntity.getUserId();
        TradeNameVO tradeName = tradeEntity.getTradeName();
        TradeTypeVO tradeType = tradeEntity.getTradeType();
        BigDecimal amount = tradeEntity.getAmount();
        String outBusinessNo = tradeEntity.getOutBusinessNo();
        log.info("增加账户积分额度开始 userId:{} tradeName:{} amount", userId, tradeName, amount);
        //1.创建积分账户实体
        CreditAccountEntity creditAccountEntity = TradeAggregate.createCreditAccountEntity(userId, amount);

        //2.创建账户订单实体
        CreditOrderEntity creditOrderEntity = TradeAggregate.createCreditOrderEntity(userId, tradeName, tradeType, amount, outBusinessNo);

        //3.构建交易聚合对象
        TradeAggregate tradeAggregate = TradeAggregate.builder()
                .userId(userId)
                .creditAccountEntity(creditAccountEntity)
                .creditOrderEntity(creditOrderEntity)
                .build();

        //4.保存积分交易订单
        repository.saveUserCreditTradeOrder(tradeAggregate);
        log.info("增加账户积分额度完成 userId:{} orderId:{}", userId, creditOrderEntity.getOrderId());
        return creditOrderEntity.getOrderId();
    }
}
