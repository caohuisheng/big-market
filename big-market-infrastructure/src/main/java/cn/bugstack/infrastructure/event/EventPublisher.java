package cn.bugstack.infrastructure.event;

import cn.bugstack.types.event.BaseEvent;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Author: chs
 * Description: 消息发送
 * CreateTime: 2024-07-31
 */
@Slf4j
@Component
public class EventPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publish(String topic, BaseEvent.EventMessage<?> eventMessage){
        try {
            String messageJson = JSON.toJSONString(eventMessage);
            rabbitTemplate.convertAndSend(topic, messageJson);
            log.info("发送MQ消息 topic:{} message:{}", topic, messageJson);
        } catch (AmqpException e) {
            e.printStackTrace();
        }
    }

    public void publish(String topic, String messageJson){
        try {
            rabbitTemplate.convertAndSend(topic, messageJson);
            log.info("发送MQ消息 topic:{} message:{}", topic, messageJson);
        } catch (AmqpException e) {
            e.printStackTrace();
        }
    }

}
