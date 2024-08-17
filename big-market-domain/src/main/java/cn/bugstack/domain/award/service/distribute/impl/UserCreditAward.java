package cn.bugstack.domain.award.service.distribute.impl;

import cn.bugstack.domain.award.model.aggregate.GiveOutPrizesAggregate;
import cn.bugstack.domain.award.model.entity.DistributeAwardEntity;
import cn.bugstack.domain.award.model.entity.UserAwardRecordEntity;
import cn.bugstack.domain.award.model.entity.UserCreditAwardEntity;
import cn.bugstack.domain.award.model.vo.AwardStateVO;
import cn.bugstack.domain.award.repository.IAwardRepository;
import cn.bugstack.domain.award.service.distribute.IDistributeAward;
import cn.bugstack.types.common.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Author: chs
 * Description: 用户积分奖品
 * CreateTime: 2024-08-17
 */
@Component("user_credit_random")
public class UserCreditAward implements IDistributeAward {

    @Resource
    private IAwardRepository repository;

    @Override
    public void giveOutAward(DistributeAwardEntity distributeAwardEntity) {
        String userId = distributeAwardEntity.getUserId();
        String orderId = distributeAwardEntity.getOrderId();
        Integer awardId = distributeAwardEntity.getAwardId();
        //奖品配置(优先走透传的随机奖品配置)
        String awardConfig = distributeAwardEntity.getAwardConfig();
        if(StringUtils.isBlank(awardConfig)){
            awardConfig = repository.queryAwardConfig(awardId);
        }

        String[] creditRange = awardConfig.split(Constants.SPLIT);
        if(creditRange.length != 2){
            throw new RuntimeException("award_config [" + awardConfig + "]配置不是一个范围值");
        }

        //生成随机积分值
        BigDecimal creditAmount = generateRandom(new BigDecimal(creditRange[0]), new BigDecimal(creditRange[1]));

        //构建聚合对象
        UserAwardRecordEntity userAwardRecordEntity = GiveOutPrizesAggregate.buildDistributeUserAwardRecordEntity(userId, orderId, awardId, AwardStateVO.create);
        UserCreditAwardEntity userCreditAwardEntity = GiveOutPrizesAggregate.buildUserCreditAwardEntity(userId, creditAmount);
        GiveOutPrizesAggregate giveOutPrizesAggregate = GiveOutPrizesAggregate.builder()
                .userId(userId)
                .userAwardRecordEntity(userAwardRecordEntity)
                .userCreditAwardEntity(userCreditAwardEntity)
                .build();

        //存储发奖对象
        repository.saveGiveOutPrizedAggregate(giveOutPrizesAggregate);
    }

    private BigDecimal generateRandom(BigDecimal min, BigDecimal max){
        if(min.equals(max)) return min;
        BigDecimal randomBigDecimal = min.add(max.subtract(min).multiply(BigDecimal.valueOf(Math.random())));
        return randomBigDecimal.round(new MathContext(3));
    }

}
