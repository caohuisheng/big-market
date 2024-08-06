package cn.bugstack.domain.award.service;

import cn.bugstack.domain.award.model.entity.UserAwardRecordEntity;

/**
 * Author: chs
 * Description: 奖品服务接口
 * CreateTime: 2024-08-06
 */
public interface IAwardService {

    void saveUserAwardRecord(UserAwardRecordEntity userAwardRecordEntity);

}
