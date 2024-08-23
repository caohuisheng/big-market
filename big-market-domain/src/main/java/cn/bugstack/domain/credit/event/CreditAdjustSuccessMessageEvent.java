package cn.bugstack.domain.credit.event;

import cn.bugstack.types.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Author: chs
 * Description: 积分账户调整成功消息
 * CreateTime: 2024-08-18
 */
@Component
public class CreditAdjustSuccessMessageEvent extends BaseEvent<CreditAdjustSuccessMessageEvent.CreditAdjustSuccessMessage> {

    @Value("${spring.rabbitmq.topic.credit_adjust_success}")
    private String topic;

    @Override
    public EventMessage<CreditAdjustSuccessMessage> buildEventMessage(CreditAdjustSuccessMessage data) {
        return EventMessage.<CreditAdjustSuccessMessage>builder()
                .id(RandomStringUtils.randomNumeric(11))
                .timestamp(new Date())
                .data(data)
                .build();
    }

    @Override
    public String topic() {
        return this.topic;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreditAdjustSuccessMessage{
        //用户id
        private String userId;
        //订单id
        private String orderId;
        //金额
        private BigDecimal amount;
        //外部透传业务id
        private String outBusinessNo;
    }

}
