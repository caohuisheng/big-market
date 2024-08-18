package cn.bugstack.infrastructure.persistent.po;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Author: chs
 * Description: 积分订单
 * CreateTime: 2024-08-18
 */
@Data
public class UserCreditOrder {

    //自增ID
    private Long id;
    //用户ID
    private String userId;
    //订单ID
    private String orderId;
    //交易名称
    private String tradeName;
    //易类型
    private String tradeType;
    //交易金额
    private BigDecimal tradeAmount;
    //业务仿重ID
    private String outBusinessNo;
    //创建时间
    private Date createTime;
    //更新时间
    private Date updateTime;

}
