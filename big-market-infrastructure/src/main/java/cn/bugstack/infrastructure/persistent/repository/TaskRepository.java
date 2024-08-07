package cn.bugstack.infrastructure.persistent.repository;

import cn.bugstack.domain.task.model.entity.TaskEntity;
import cn.bugstack.domain.task.repository.ITaskRepository;
import cn.bugstack.infrastructure.event.EventPublisher;
import cn.bugstack.infrastructure.persistent.dao.TaskDao;
import cn.bugstack.infrastructure.persistent.po.Task;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: chs
 * Description: 任务服务仓储实现
 * CreateTime: 2024-08-07
 */
@Repository
public class TaskRepository implements ITaskRepository {

    @Resource
    private TaskDao taskDao;
    @Resource
    private EventPublisher eventPublisher;

    @Override
    public List<TaskEntity> queryNOSendMessageTaskList() {
        List<Task> tasks = taskDao.queryNoSendMessageTaskList();
        List<TaskEntity> taskEntities = tasks.stream().map(task -> {
            TaskEntity taskEntity = new TaskEntity();
            BeanUtils.copyProperties(task, taskEntity);
            return taskEntity;
        }).collect(Collectors.toList());
        return taskEntities;
    }

    @Override
    public void sendMessage(TaskEntity taskEntity) {
        eventPublisher.publish(taskEntity.getTopic(), taskEntity.getMessage());
    }

    @Override
    public void updateTaskSendMessageCompleted(String userId, String messageId) {
        taskDao.updateTaskSendMessageCompleted(userId, messageId);
    }

    @Override
    public void updateTaskSendMessageFail(String userId, String messageId) {
        taskDao.updateTaskSendMessageFail(userId, messageId);
    }
}
