package cn.bugstack.trigger.listener;

import cn.bugstack.domain.activity.model.entity.SkuRechargeEntity;
import cn.bugstack.domain.activity.model.valobj.OrderTradeTypeVO;
import cn.bugstack.domain.activity.service.IRaffleActivityAccountQuotaService;
import cn.bugstack.domain.credit.model.entity.TradeEntity;
import cn.bugstack.domain.credit.model.vo.TradeNameVO;
import cn.bugstack.domain.credit.model.vo.TradeTypeVO;
import cn.bugstack.domain.credit.service.ICreditAdjustService;
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
import java.math.BigDecimal;

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
    @Resource
    private ICreditAdjustService creditAdjustService;

    @RabbitListener(queuesToDeclare = @Queue(value = "${spring.rabbitmq.topic.send_rebate}"))
    public void listen(String message){
        try {
            log.info("监听用户行为返利消息 topic:{} message:{}", topic, message);
            //1.转换消息
            BaseEvent.EventMessage<SendRebateMessageEvent.RebateMessage> eventMessage = JSON.parseObject(message, new TypeReference<BaseEvent.EventMessage<SendRebateMessageEvent.RebateMessage>>() {
            }.getType());
            SendRebateMessageEvent.RebateMessage rebateMessage = eventMessage.getData();

            //入账奖励
            switch(rebateMessage.getRebateType()){
                //签到返利-sku额度
                case "sku":
                    SkuRechargeEntity skuRechargeEntity = SkuRechargeEntity.builder()
                            .userId(rebateMessage.getUserId())
                            .sku(Long.parseLong(rebateMessage.getRebateConfig()))
                            .outBusinessNo(rebateMessage.getBizId())
                            .orderTradeType(OrderTradeTypeVO.rebate_no_pay_trade)
                            .build();
                    raffleActivityAccountQuotaService.createOrder(skuRechargeEntity);
                    break;
                //签到返利-积分
                case "integral":
                    TradeEntity tradeEntity = TradeEntity.builder()
                            .userId(rebateMessage.getUserId())
                            .tradeName(TradeNameVO.REBATE)
                            .tradeType(TradeTypeVO.FORWARD)
                            .amount(new BigDecimal(rebateMessage.getRebateConfig()))
                            .outBusinessNo(rebateMessage.getBizId())
                            .build();
                    creditAdjustService.createOrder(tradeEntity);
                    break;
            }
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
