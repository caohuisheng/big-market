package cn.bugstack.infrastructure.persistent.repository;

import cn.bugstack.domain.credit.model.aggregate.TradeAggregate;
import cn.bugstack.domain.credit.model.entity.CreditAccountEntity;
import cn.bugstack.domain.credit.model.entity.CreditOrderEntity;
import cn.bugstack.domain.credit.repository.ICreditRepository;
import cn.bugstack.infrastructure.persistent.dao.UserCreditAccountDao;
import cn.bugstack.infrastructure.persistent.dao.UserCreditOrderDao;
import cn.bugstack.infrastructure.persistent.po.UserCreditAccount;
import cn.bugstack.infrastructure.persistent.po.UserCreditOrder;
import cn.bugstack.infrastructure.persistent.redis.IRedisService;
import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import cn.bugstack.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.redisson.api.RLock;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Author: chs
 * Description: 用户积分仓储
 * CreateTime: 2024-08-18
 */
@Slf4j
@Component
public class CreditRepository implements ICreditRepository {

    @Resource
    private IRedisService redisService;
    @Resource
    private IDBRouterStrategy dbRouter;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private UserCreditAccountDao userCreditAccountDao;
    @Resource
    private UserCreditOrderDao userCreditOrderDao;


    @Override
    public void saveUserCreditTradeOrder(TradeAggregate tradeAggregate) {
        String userId = tradeAggregate.getUserId();
        CreditAccountEntity creditAccountEntity = tradeAggregate.getCreditAccountEntity();
        CreditOrderEntity creditOrderEntity = tradeAggregate.getCreditOrderEntity();

        //积分账户
        UserCreditAccount userCreditAccountReq = new UserCreditAccount();
        userCreditAccountReq.setUserId(creditAccountEntity.getUserId());
        userCreditAccountReq.setAvailableAmount(creditAccountEntity.getAdjustAmount());
        userCreditAccountReq.setTotalAmount(creditAccountEntity.getAdjustAmount());

        //积分订单
        UserCreditOrder userCreditOrder = new UserCreditOrder();
        userCreditOrder.setUserId(creditOrderEntity.getUserId());
        userCreditOrder.setOrderId(creditOrderEntity.getOrderId());
        userCreditOrder.setTradeName(creditOrderEntity.getTradeName().getName());
        userCreditOrder.setTradeType(creditOrderEntity.getTradeType().getCode());
        userCreditOrder.setTradeAmount(creditOrderEntity.getTradeAmount());
        userCreditOrder.setOutBusinessNo(creditOrderEntity.getOutBusinessNo());

        RLock lock = redisService.getLock(Constants.RedisKey.USER_CREDIT_ACCOUNT_LOCK + userId + Constants.UNDERLINE + creditOrderEntity.getOutBusinessNo());
        try {
            lock.lock(3, TimeUnit.SECONDS);
            dbRouter.doRouter(userId);
            transactionTemplate.execute(status -> {
                try {
                    //更新 || 创建积分账户
                    int count = userCreditAccountDao.updateAddAmount(userCreditAccountReq);
                    if(0 == count){
                        userCreditAccountReq.setAccountStatus("open");
                        userCreditAccountDao.insert(userCreditAccountReq);
                    }

                    //新增积分订单
                    userCreditOrderDao.insert(userCreditOrder);
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.info("调整积分账户额度异常，唯一索引冲突 userId:{} orderId:{}", userId, userCreditOrder.getOrderId(), e);
                }catch(Exception e){
                    status.setRollbackOnly();
                    log.info("调整积分账户额度异常 userId:{} orderId:{}", userId, userCreditOrder.getOrderId(), e);
                }
                return 1;
            });
        } finally {
            lock.unlock();
            dbRouter.clear();
        }
    }
}
