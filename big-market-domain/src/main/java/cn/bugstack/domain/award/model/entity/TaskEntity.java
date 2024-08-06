package cn.bugstack.domain.award.model.entity;

import cn.bugstack.domain.award.event.SendAwardMessageEvent;
import cn.bugstack.domain.award.model.vo.TaskStateVO;
import cn.bugstack.types.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: chs
 * Description: 任务实体对象
 * CreateTime: 2024-08-06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskEntity {

    //用户id
    private String userId;
    //消息主题
    private String topic;
    //消息id
    private String messageId;
    //消息主体
    private BaseEvent.EventMessage<SendAwardMessageEvent.SendAwardMessage> message;
    //任务状态
    private TaskStateVO state;

}
