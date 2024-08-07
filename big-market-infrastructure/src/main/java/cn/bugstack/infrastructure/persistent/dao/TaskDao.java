package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.Task;
import cn.bugstack.middleware.db.router.annotation.DBRouter;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Author: chs
 * Description: 任务Dao
 * CreateTime: 2024-08-07
 */
@Mapper
public interface TaskDao {

    void insert(Task task);

    @DBRouter
    void updateTaskSendMessageCompleted(String userId, String messageId);

    @DBRouter
    void updateTaskSendMessageFail(String userId, String messageId);

    List<Task> queryNoSendMessageTaskList();
}
