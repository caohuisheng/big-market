package cn.bugstack.domain.task.service;

import cn.bugstack.domain.task.model.entity.TaskEntity;

import java.util.List;

/**
 * Author: chs
 * Description: 消息任务服务接口
 * CreateTime: 2024-08-07
 */
public interface ITaskService {

    /**
     * 查询发送MQ失败和超时1分钟未发送的消息
     * @return 未发送的任务消息列表10条
     */
    List<TaskEntity> queryNoSendMessageTaskList();

    void sendMessage(TaskEntity taskEntity);

    void updateTaskSendMessageCompleted(String userId, String messageId);

    void updateTaskSendMessageFail(String userId, String messageId);
}
