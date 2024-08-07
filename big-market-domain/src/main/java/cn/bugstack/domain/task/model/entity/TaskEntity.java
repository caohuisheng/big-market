package cn.bugstack.domain.task.model.entity;

import lombok.Data;

/**
 * Author: chs
 * Description: 任务实体对象
 * CreateTime: 2024-08-07
 */
@Data
public class TaskEntity {

    //用户id
    private String userId;
    //消息主题
    private String topic;
    //消息id
    private String messageId;
    //消息主体
    private String message;

}
