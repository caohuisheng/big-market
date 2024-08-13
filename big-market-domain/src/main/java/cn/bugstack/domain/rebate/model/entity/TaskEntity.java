package cn.bugstack.domain.rebate.model.entity;

import cn.bugstack.domain.rebate.model.event.SendRebateMessageEvent;
import cn.bugstack.domain.rebate.model.vo.TaskStateVO;
import cn.bugstack.types.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: chs
 * Description: 任务实体
 * CreateTime: 2024-08-13
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
    //消息编号
    private String messageId;
    //消息主体
    private BaseEvent.EventMessage<SendRebateMessageEvent.RebateMessage> message;
    //任务状态（create-创建、completed-完成、fail-失败）
    private TaskStateVO state;

}
