package cn.bugstack.domain.award.model.aggregate;

import cn.bugstack.domain.award.model.entity.UserAwardRecordEntity;
import cn.bugstack.domain.award.model.entity.UserCreditAwardEntity;
import cn.bugstack.domain.award.model.vo.AwardStateVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Author: chs
 * Description: 分发奖品聚合对象
 * CreateTime: 2024-08-17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GiveOutPrizesAggregate {
    //用户id
    private String userId;
    //用户发奖记录
    private UserAwardRecordEntity userAwardRecordEntity;
    //用户积分奖品
    private UserCreditAwardEntity userCreditAwardEntity;

    public static UserAwardRecordEntity buildDistributeUserAwardRecordEntity(String userId, String orderId, Integer awardId, AwardStateVO awardStateVO){
        return UserAwardRecordEntity.builder()
                .userId(userId)
                .orderId(orderId)
                .awardId(awardId)
                .awardState(awardStateVO)
                .build();
    }

    public static UserCreditAwardEntity buildUserCreditAwardEntity(String userId, BigDecimal creditAmount){
        return UserCreditAwardEntity.builder().userId(userId).creditAmount(creditAmount).build();
    }
}
