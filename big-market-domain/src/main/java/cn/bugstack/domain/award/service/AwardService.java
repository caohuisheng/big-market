package cn.bugstack.domain.award.service;

import cn.bugstack.domain.award.event.SendAwardMessageEvent;
import cn.bugstack.domain.award.model.aggregate.UserAwardRecordAggregate;
import cn.bugstack.domain.award.model.entity.TaskEntity;
import cn.bugstack.domain.award.model.entity.UserAwardRecordEntity;
import cn.bugstack.domain.award.model.vo.TaskStateVO;
import cn.bugstack.domain.award.repository.IAwardRepository;
import cn.bugstack.types.event.BaseEvent;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Author: chs
 * Description:
 * CreateTime: 2024-08-06
 */
@Service
public class AwardService implements IAwardService{

    @Resource
    private IAwardRepository awardRepository;
    @Resource
    private SendAwardMessageEvent sendAwardMessageEvent;

    @Override
    public void saveUserAwardRecord(UserAwardRecordEntity userAwardRecordEntity) {
        //构建消息对象
        SendAwardMessageEvent.SendAwardMessage sendAwardMessage = SendAwardMessageEvent.SendAwardMessage.builder()
                .userId(userAwardRecordEntity.getUserId())
                .awardId(userAwardRecordEntity.getAwardId())
                .awardTitle(userAwardRecordEntity.getAwardTitle())
                .build();

        BaseEvent.EventMessage<SendAwardMessageEvent.SendAwardMessage> sendAwardMessageEventMessage = sendAwardMessageEvent.buildEventMessage(sendAwardMessage);

        //构建任务对象
        TaskEntity taskEntity = TaskEntity.builder()
                .userId(userAwardRecordEntity.getUserId())
                .topic(sendAwardMessageEvent.topic())
                .messageId(sendAwardMessageEventMessage.getId())
                .message(sendAwardMessageEventMessage)
                .state(TaskStateVO.create)
                .build();

        //构建聚合对象
        UserAwardRecordAggregate userAwardRecordAggregate = UserAwardRecordAggregate.builder()
                .taskEntity(taskEntity)
                .userAwardRecordEntity(userAwardRecordEntity)
                .build();

        //存储聚合对象
        awardRepository.saveUserAwardRecord(userAwardRecordAggregate);
    }
}
