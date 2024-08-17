package cn.bugstack.domain.award.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Author: chs
 * Description: 用户积分奖品实体
 * CreateTime: 2024-08-17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreditAwardEntity {

    //用户id
    private String userId;
    //积分数量
    private BigDecimal creditAmount;

}
