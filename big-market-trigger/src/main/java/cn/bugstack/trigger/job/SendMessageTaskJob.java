package cn.bugstack.trigger.job;

import cn.bugstack.domain.task.model.entity.TaskEntity;
import cn.bugstack.domain.task.service.ITaskService;
import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Author: chs
 * Description: 发送MQ消息任务队列
 * CreateTime: 2024-08-07
 */
@Slf4j
@Component
public class SendMessageTaskJob {

    @Resource
    private ITaskService taskService;
    @Resource
    private ThreadPoolExecutor executor;
    @Resource
    private IDBRouterStrategy dbRouter;

    @Scheduled(cron = "1/5 * * * * ?")
    public void exec(){
        try {
            //获取分库数量
            int dbCount = dbRouter.dbCount();
            System.out.println("====dbCount:" + dbCount);
            //逐个库扫描，每个库一个任务表
            for (int i = 1; i <= dbCount; i++) {
                final int dbKey = i;
                executor.execute(() -> {
                    try {
                        dbRouter.setDBKey(dbKey);
                        dbRouter.setTBKey(0);
                        List<TaskEntity> taskEntities = taskService.queryNoSendMessageTaskList();
                        taskEntities.forEach(taskEntity -> {
                            //开启线程池发送，提高发送效率
                            executor.execute(() -> {
                                try {
                                    taskService.sendMessage(taskEntity);
                                    taskService.updateTaskSendMessageCompleted(taskEntity.getUserId(), taskEntity.getMessageId());
                                } catch (Exception e) {
                                    log.error("定时任务，发送消息失败 userId:{} topic:{}", taskEntity.getUserId(), taskEntity.getTopic(), e);
                                }
                            });
                        });
                    } finally {
                        dbRouter.clear();
                    }
                });
            }
        } catch (Exception e) {
            log.error("定时任务，扫描任务表发送消息失败", e);
        } finally {
            dbRouter.clear();
        }
    }
}
