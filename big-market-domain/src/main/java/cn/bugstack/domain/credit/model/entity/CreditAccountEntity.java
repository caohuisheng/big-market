package cn.bugstack.domain.credit.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Author: chs
 * Description: 积分账户实体
 * CreateTime: 2024-08-18
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditAccountEntity {

    //用户id
    private String userId;
    //可用积分，每次扣减的值
    private BigDecimal adjustAmount;

}
