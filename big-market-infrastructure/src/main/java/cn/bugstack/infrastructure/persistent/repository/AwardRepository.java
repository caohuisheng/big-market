package cn.bugstack.infrastructure.persistent.repository;

import cn.bugstack.domain.award.model.aggregate.GiveOutPrizesAggregate;
import cn.bugstack.domain.award.model.aggregate.UserAwardRecordAggregate;
import cn.bugstack.domain.award.model.entity.TaskEntity;
import cn.bugstack.domain.award.model.entity.UserAwardRecordEntity;
import cn.bugstack.domain.award.model.entity.UserCreditAwardEntity;
import cn.bugstack.domain.award.model.vo.AccountStatusVO;
import cn.bugstack.domain.award.repository.IAwardRepository;
import cn.bugstack.domain.award.service.distribute.impl.UserCreditAward;
import cn.bugstack.infrastructure.event.EventPublisher;
import cn.bugstack.infrastructure.persistent.dao.*;
import cn.bugstack.infrastructure.persistent.po.Task;
import cn.bugstack.infrastructure.persistent.po.UserAwardRecord;
import cn.bugstack.infrastructure.persistent.po.UserCreditAccount;
import cn.bugstack.infrastructure.persistent.po.UserRaffleOrder;
import cn.bugstack.middleware.db.router.annotation.DBRouter;
import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;

/**
 * Author: chs
 * Description:
 * CreateTime: 2024-08-06
 */
@Slf4j
@Component
public class AwardRepository implements IAwardRepository {

    @Resource
    private TaskDao taskDao;
    @Resource
    private UserAwardRecordDao userAwardRecordDao;
    @Resource
    private IDBRouterStrategy dbRouter;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private EventPublisher eventPublisher;
    @Resource
    private UserRaffleOrderDao userRaffleOrderDao;
    @Resource
    private AwardDao awardDao;
    @Resource
    private UserCreditAccountDao userCreditAccountDao;

    @Override
    public void saveUserAwardRecord(UserAwardRecordAggregate userAwardRecordAggregate) {
        UserAwardRecordEntity userAwardRecordEntity = userAwardRecordAggregate.getUserAwardRecordEntity();
        TaskEntity taskEntity = userAwardRecordAggregate.getTaskEntity();
        String userId = userAwardRecordEntity.getUserId();
        Long activityId = userAwardRecordEntity.getActivityId();
        Integer awardId = userAwardRecordEntity.getAwardId();
        String orderId = userAwardRecordEntity.getOrderId();

        //构建中奖记录
        UserAwardRecord userAwardRecord = new UserAwardRecord();
        BeanUtils.copyProperties(userAwardRecordEntity, userAwardRecord);
        userAwardRecord.setAwardState(userAwardRecordEntity.getAwardState().getCode());
        //构建任务
        Task task = new Task();
        task.setUserId(taskEntity.getUserId());
        task.setTopic(taskEntity.getTopic());
        task.setMessageId(taskEntity.getMessageId());
        task.setMessage(JSON.toJSONString(taskEntity.getMessage()));
        task.setState(taskEntity.getState().getCode());

        try {
            dbRouter.doRouter(userId);
            transactionTemplate.execute(status -> {
                try {
                    //写入记录
                    userAwardRecordDao.insert(userAwardRecord);
                    //写入任务
                    taskDao.insert(task);
                    // 更新抽奖订单状态（完成）
                    UserRaffleOrder userRaffleOrderReq = UserRaffleOrder.builder()
                            .userId(userId)
                            .orderId(orderId).build();
                    int success = userRaffleOrderDao.updateUserRaffleOrderUsed(userRaffleOrderReq);
                    if(1 != success){
                        log.info("更新抽奖订单异常 userId:{} orderId:{}", userId, orderId);
                        throw new AppException(ResponseCode.USER_RAFFLE_ORDER_ERROR.getCode(), ResponseCode.USER_RAFFLE_ORDER_ERROR.getInfo());
                    }
                    return 1;
                } catch (Exception e) {
                    status.setRollbackOnly();
                    log.error("写入中奖记录，唯一索引冲突 userId:{} activityId:{} awardId:{}", userId, activityId, awardId);
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(), e);
                }
            });
        } finally {
            dbRouter.clear();
        }

        try {
            //发送消息【在事务外执行，如果失败还有任务补偿】
            eventPublisher.publish(taskEntity.getTopic(), taskEntity.getMessage());
            //更新数据库记录，task任务表
            taskDao.updateTaskSendMessageCompleted(task);

        } catch (Exception e) {
            log.error("写入中将记录，发送MQ消息失败 userId:{} topic:{}", userId, task.getTopic());
            taskDao.updateTaskSendMessageFail(task);
        }
    }

    @Override
    public String queryAwardConfig(Integer awardId) {
        return awardDao.queryAwardConfigById(awardId);
    }

    @Override
    public void saveGiveOutPrizedAggregate(GiveOutPrizesAggregate giveOutPrizesAggregate) {
        String userId = giveOutPrizesAggregate.getUserId();
        UserAwardRecordEntity userAwardRecordEntity = giveOutPrizesAggregate.getUserAwardRecordEntity();
        UserCreditAwardEntity userCreditAwardEntity = giveOutPrizesAggregate.getUserCreditAwardEntity();

        //用户奖品记录请求对象
        UserAwardRecord userAwardRecord = new UserAwardRecord();
        userAwardRecord.setUserId(userId);
        userAwardRecord.setOrderId(userAwardRecordEntity.getOrderId());

        //用户积分账户请求对象
        UserCreditAccount userCreditAccount = new UserCreditAccount();
        userCreditAccount.setUserId(userId);
        userCreditAccount.setTotalAmount(userCreditAwardEntity.getCreditAmount());
        userCreditAccount.setAvailableAmount(userCreditAwardEntity.getCreditAmount());
        userCreditAccount.setAccountStatus(AccountStatusVO.OPEN.getCode());

        try {
            dbRouter.doRouter(userId);
            transactionTemplate.execute(status -> {
                try {
                    //更新积分 || 创建积分账户
                    int count = userCreditAccountDao.updateAddAmount(userCreditAccount);
                    if(0 == count){
                        userCreditAccountDao.insert(userCreditAccount);
                    }

                    //更新奖品记录的状态
                    count = userAwardRecordDao.setStatusCompleted(userAwardRecord);
                    if(0 == count){
                        log.warn("更新中奖记录, 重复更新拦截 userId:{} userAwardRecord:{}", userId, JSON.toJSONString(userAwardRecord));
                        status.setRollbackOnly();
                    }
                    return 1;
                } catch (Exception e) {
                    status.setRollbackOnly();
                    log.error("更新中奖记录,唯一索引冲突 userId:{}", userId);
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(), e);
                }
            });
        } finally {
            dbRouter.clear();
        }
    }

    @Override
    public String queryAwardKey(Integer awardId) {
        return awardDao.queryAwardKey(awardId);
    }
}
