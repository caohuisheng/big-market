package cn.bugstack.trigger.listener;

import cn.bugstack.domain.activity.model.entity.SkuRechargeEntity;
import cn.bugstack.domain.activity.service.IRaffleActivityAccountQuotaService;
import cn.bugstack.domain.rebate.model.event.SendRebateMessageEvent;
import cn.bugstack.domain.rebate.model.vo.RebateTypeVO;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.event.BaseEvent;
import cn.bugstack.types.exception.AppException;
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
 * Description: 监听：返利消息
 * CreateTime: 2024-08-14
 */
@Slf4j
@Component
public class RebateMessageCustomer {

    @Value("${spring.rabbitmq.topic.send_rebate}")
    private String topic;

    @Resource
    private IRaffleActivityAccountQuotaService raffleActivityAccountQuotaService;

    @RabbitListener(queuesToDeclare = @Queue(value = "${spring.rabbitmq.topic.send_rebate}"))
    public void listen(String message){
        try {
            log.info("监听用户行为返利消息 topic:{} message:{}", topic, message);
            //1.转换消息
            BaseEvent.EventMessage<SendRebateMessageEvent.RebateMessage> eventMessage = JSON.parseObject(message, new TypeReference<BaseEvent.EventMessage<SendRebateMessageEvent.RebateMessage>>() {
            }.getType());
            SendRebateMessageEvent.RebateMessage rebateMessage = eventMessage.getData();
            if(!RebateTypeVO.SKU.getCode().equals(rebateMessage.getRebateType())){
                log.info("监听用户行为返利消息 - 监听用户行为返利消息，非sku返利消息暂不处理");
                return;
            }

            //2.返利入账
            SkuRechargeEntity skuRechargeEntity = SkuRechargeEntity.builder()
                    .userId(rebateMessage.getUserId())
                    .sku(Long.parseLong(rebateMessage.getRebateConfig()))
                    .outBusinessNo(rebateMessage.getBizId())
                    .build();
            raffleActivityAccountQuotaService.createOrder(skuRechargeEntity);
        } catch (AppException e) {
            if(ResponseCode.INDEX_DUP.getCode().equals(e.getCode())){
                log.warn("监听用户行为返利消息, 消费重复 topic:{} message:{}", topic, message, e);
                return;
            }
            throw e;
        }catch(Exception e){
            log.error("监听用户行为返利消息, 消费失败 topic:{} message:{}", topic, message, e);
        }
    }
}
