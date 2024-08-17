package cn.bugstack.domain.award.service.distribute;

import cn.bugstack.domain.award.model.entity.DistributeAwardEntity;

/**
 * Author: chs
 * Description: 分发奖品接口
 * CreateTime: 2024-08-17
 */
public interface IDistributeAward {

    void giveOutAward(DistributeAwardEntity distributeAwardEntity);

}
