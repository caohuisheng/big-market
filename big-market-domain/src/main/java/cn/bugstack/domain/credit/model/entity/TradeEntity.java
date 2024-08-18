package cn.bugstack.domain.credit.model.entity;

import cn.bugstack.domain.credit.model.vo.TradeNameVO;
import cn.bugstack.domain.credit.model.vo.TradeTypeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Author: chs
 * Description: 增加额度实体
 * CreateTime: 2024-08-18
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradeEntity {

    //用户id
    private String userId;
    //交易名称
    private TradeNameVO tradeName;
    //交易类型：forward-正向 reverse-反向
    private TradeTypeVO tradeType;
    //交易金额
    private BigDecimal amount;
    //业务防重id - 外部透传
    private String outBusinessNo;

}
