package cn.bugstack.infrastructure.persistent.po;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Author: chs
 * Description:
 * CreateTime: 2024-08-17
 */
@Data
public class UserCreditAccount {
    /**
     * 自增id
     */
    private Long id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 总积分
     */
    private BigDecimal totalAmount;

    /**
     * 可用积分
     */
    private BigDecimal availableAmount;

    /**
     * 账户状态
     */
    private String accountStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
