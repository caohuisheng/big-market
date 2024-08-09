package cn.bugstack.infrastructure.persistent.repository;

import cn.bugstack.domain.award.model.aggregate.UserAwardRecordAggregate;
import cn.bugstack.domain.award.model.entity.TaskEntity;
import cn.bugstack.domain.award.model.entity.UserAwardRecordEntity;
import cn.bugstack.domain.award.repository.IAwardRepository;
import cn.bugstack.infrastructure.event.EventPublisher;
import cn.bugstack.infrastructure.persistent.dao.TaskDao;
import cn.bugstack.infrastructure.persistent.dao.UserAwardRecordDao;
import cn.bugstack.infrastructure.persistent.po.Task;
import cn.bugstack.infrastructure.persistent.po.UserAwardRecord;
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

    @Override
    public void saveUserAwardRecord(UserAwardRecordAggregate userAwardRecordAggregate) {
        UserAwardRecordEntity userAwardRecordEntity = userAwardRecordAggregate.getUserAwardRecordEntity();
        TaskEntity taskEntity = userAwardRecordAggregate.getTaskEntity();
        String userId = userAwardRecordEntity.getUserId();
        Long activityId = userAwardRecordEntity.getActivityId();
        Integer awardId = userAwardRecordEntity.getAwardId();

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
            dbRouter.doRouter("userId");
            transactionTemplate.execute(status -> {
                try {
                    //写入记录
                    userAwardRecordDao.insert(userAwardRecord);
                    //写入任务
                    taskDao.insert(task);
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
            taskDao.updateTaskSendMessageCompleted(task.getUserId(), task.getMessageId());
        } catch (Exception e) {
            log.error("写入中将记录，发送MQ消息失败 userId:{} topic:{}", userId, task.getTopic());
            taskDao.updateTaskSendMessageFail(task.getUserId(), task.getMessageId());
        }
    }
}