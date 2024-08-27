package cn.bugstack.infrastructure.persistent.repository;

import cn.bugstack.domain.award.model.vo.AccountStatusVO;
import cn.bugstack.domain.credit.model.aggregate.TradeAggregate;
import cn.bugstack.domain.credit.model.entity.CreditAccountEntity;
import cn.bugstack.domain.credit.model.entity.CreditOrderEntity;
import cn.bugstack.domain.credit.model.entity.TaskEntity;
import cn.bugstack.domain.credit.repository.ICreditRepository;
import cn.bugstack.infrastructure.event.EventPublisher;
import cn.bugstack.infrastructure.persistent.dao.TaskDao;
import cn.bugstack.infrastructure.persistent.dao.UserCreditAccountDao;
import cn.bugstack.infrastructure.persistent.dao.UserCreditOrderDao;
import cn.bugstack.infrastructure.persistent.po.Task;
import cn.bugstack.infrastructure.persistent.po.UserCreditAccount;
import cn.bugstack.infrastructure.persistent.po.UserCreditOrder;
import cn.bugstack.infrastructure.persistent.redis.IRedisService;
import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import cn.bugstack.types.common.Constants;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.redisson.api.RLock;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
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
    private EventPublisher eventPublisher;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private UserCreditAccountDao userCreditAccountDao;
    @Resource
    private UserCreditOrderDao userCreditOrderDao;
    @Resource
    private TaskDao taskDao;

    @Override
    public void saveUserCreditTradeOrder(TradeAggregate tradeAggregate) {
        String userId = tradeAggregate.getUserId();
        CreditAccountEntity creditAccountEntity = tradeAggregate.getCreditAccountEntity();
        CreditOrderEntity creditOrderEntity = tradeAggregate.getCreditOrderEntity();
        TaskEntity taskEntity = tradeAggregate.getTaskEntity();

        //积分账户
        UserCreditAccount userCreditAccountReq = new UserCreditAccount();
        userCreditAccountReq.setUserId(creditAccountEntity.getUserId());
        userCreditAccountReq.setAvailableAmount(creditAccountEntity.getAdjustAmount());
        userCreditAccountReq.setTotalAmount(creditAccountEntity.getAdjustAmount());
        userCreditAccountReq.setAccountStatus(AccountStatusVO.OPEN.getCode());

        //积分订单
        UserCreditOrder userCreditOrder = new UserCreditOrder();
        userCreditOrder.setUserId(creditOrderEntity.getUserId());
        userCreditOrder.setOrderId(creditOrderEntity.getOrderId());
        userCreditOrder.setTradeName(creditOrderEntity.getTradeName().getName());
        userCreditOrder.setTradeType(creditOrderEntity.getTradeType().getCode());
        userCreditOrder.setTradeAmount(creditOrderEntity.getTradeAmount());
        userCreditOrder.setOutBusinessNo(creditOrderEntity.getOutBusinessNo());

        //任务
        Task task = new Task();
        task.setUserId(taskEntity.getUserId());
        task.setTopic(taskEntity.getTopic());
        task.setMessageId(taskEntity.getMessageId());
        task.setMessage(JSON.toJSONString(taskEntity.getMessage()));
        task.setState(taskEntity.getState().getCode());

        RLock lock = redisService.getLock(Constants.RedisKey.USER_CREDIT_ACCOUNT_LOCK + userId + Constants.UNDERLINE + creditOrderEntity.getOutBusinessNo());
        try {
            lock.lock(3, TimeUnit.SECONDS);
            dbRouter.doRouter(userId);
            transactionTemplate.execute(status -> {
                try {
                    //1.保存账户积分
                    UserCreditAccount userCreditAccount = userCreditAccountDao.queryUserCreditAccount(userCreditAccountReq);
                    if(null == userCreditAccount){
                        userCreditAccountDao.insert(userCreditAccount);
                    }else{
                        if(userCreditAccountReq.getAvailableAmount().compareTo(BigDecimal.ZERO) > 0){
                            userCreditAccountDao.updateAddAmount(userCreditAccountReq);
                        }else{
                            int count = userCreditAccountDao.updateSubtractionAmount(userCreditAccountReq);
                            if(0 == count){
                                status.setRollbackOnly();
                                throw new AppException(ResponseCode.USER_CREDIT_ACCOUNT_NO_AVAILABLE_AMOUNT.getCode(),ResponseCode.USER_CREDIT_ACCOUNT_NO_AVAILABLE_AMOUNT.getInfo());
                            }
                        }
                    }

                    //2.新增积分订单
                    userCreditOrderDao.insert(userCreditOrder);

                    //3.添加任务
                    if(userCreditAccountReq.getAvailableAmount().compareTo(BigDecimal.ZERO)<0){
                        taskDao.insert(task);
                    }
                } catch(AppException e){
                    log.error("调整积分账户额度，唯一索引冲突 userId:{} orderId:{}", userId, userCreditOrder.getOrderId(),e);
                    status.setRollbackOnly();
                    throw new AppException(ResponseCode.INDEX_DUP.getCode());
                } catch(Exception e){
                    log.error("调整积分账户额度异常 userId:{} orderId:{}", userId, userCreditOrder.getOrderId(),e);
                    status.setRollbackOnly();
                    throw e;
                }
                return 1;
            });
        } finally {
            lock.unlock();
            dbRouter.clear();
        }

        if(userCreditAccountReq.getAvailableAmount().compareTo(BigDecimal.ZERO)<0){
            try {
                //发送消息
                eventPublisher.publish(taskEntity.getTopic(), taskEntity.getMessage());
                //更新任务状态
                taskDao.updateTaskSendMessageCompleted(task);
                log.info("调整积分账户记录，发送消息成功 userId:{} topic:{}", userId, taskEntity.getTopic());
            } catch (Exception e) {
                log.info("调整积分账户记录，发送消息失败 userId:{} topic:{}", userId, taskEntity.getTopic(), e);
            }
        }
    }

    @Override
    public CreditAccountEntity queryUserCreditAccount(String userId) {
        //查询用户积分账户
        UserCreditAccount userCreditAccountReq = new UserCreditAccount();
        userCreditAccountReq.setUserId(userId);
        UserCreditAccount userCreditAccount = userCreditAccountDao.queryUserCreditAccount(userCreditAccountReq);

        return CreditAccountEntity.builder()
                .userId(userCreditAccount.getUserId())
                .adjustAmount(userCreditAccount.getAvailableAmount())
                .build();
    }
}
