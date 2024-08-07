package cn.bugstack.domain.task.repository;

import cn.bugstack.domain.task.model.entity.TaskEntity;

import java.util.List;


/**
 * Author: chs
 * Description: 任务仓储服务接口
 * CreateTime: 2024-08-07
 */
public interface ITaskRepository {

    List<TaskEntity> queryNOSendMessageTaskList();

    void sendMessage(TaskEntity taskEntity);

    void updateTaskSendMessageCompleted(String userId, String messageId);

    void updateTaskSendMessageFail(String userId, String messageId);

}
