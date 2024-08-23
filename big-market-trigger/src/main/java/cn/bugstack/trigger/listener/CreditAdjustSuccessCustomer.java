package cn.bugstack.trigger.listener;

import cn.bugstack.domain.activity.model.entity.DeliveryOrderEntity;
import cn.bugstack.domain.activity.service.IRaffleActivityAccountQuotaService;
import cn.bugstack.domain.activity.service.quota.RaffleActivityAccountQuotaService;
import cn.bugstack.domain.credit.event.CreditAdjustSuccessMessageEvent;
import cn.bugstack.types.event.BaseEvent;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Author: chs
 * Description: 积分调额成功消息监听
 * CreateTime: 2024-08-18
 */
@Slf4j
@Component
public class CreditAdjustSuccessCustomer {

    @Value("${spring.rabbitmq.topic.credit_adjust_success}")
    private String topic;

    @Resource
    private IRaffleActivityAccountQuotaService raffleActivityAccountQuotaService;

    @RabbitListener(queuesToDeclare = @Queue(value = "${spring.rabbitmq.topic.credit_adjust_success}"))
    public void listener(String message){
        try {
            log.info("监听积分账户调整成功消息，进行交易商品发货 topic:{} message:{}", topic, message);
            BaseEvent.EventMessage<CreditAdjustSuccessMessageEvent.CreditAdjustSuccessMessage> eventMessage = JSON.parseObject(message, new TypeReference<BaseEvent.EventMessage<CreditAdjustSuccessMessageEvent.CreditAdjustSuccessMessage>>() {
            }.getType());
            CreditAdjustSuccessMessageEvent.CreditAdjustSuccessMessage creditAdjustSuccessMessage = eventMessage.getData();

            //创建发放订单实体
            DeliveryOrderEntity deliveryOrderEntity = DeliveryOrderEntity.builder()
                    .userId(creditAdjustSuccessMessage.getUserId())
                    .outBusinessNo(creditAdjustSuccessMessage.getOutBusinessNo())
                    .build();

            raffleActivityAccountQuotaService.updateOrder(deliveryOrderEntity);
        } catch (Exception e) {
            log.error("监听积分账户调整成功消息,消息处理失败", e);
        }
    }

}
