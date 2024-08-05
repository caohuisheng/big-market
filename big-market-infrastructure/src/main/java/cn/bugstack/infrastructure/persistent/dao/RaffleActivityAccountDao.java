package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.RaffleActivityAccount;
import cn.bugstack.middleware.db.router.annotation.DBRouter;
import org.apache.ibatis.annotations.Mapper;

/**
 * Author: chs
 * Description: 抽奖活动账户表
 * CreateTime: 2024-07-30
 */
@Mapper
public interface RaffleActivityAccountDao {

    void insert(RaffleActivityAccount raffleActivityAccount);

    @DBRouter
    RaffleActivityAccount queryActivityAccountByUserId(String userId, Long activityId);

    int updateAccountQuota(RaffleActivityAccount raffleActivityAccount);

    int updateActivityAccountSubtractionQuota(RaffleActivityAccount raffleActivityAccount);

    void updateActivityAccountMonthSurplusImageQuota(RaffleActivityAccount raffleActivityAccount);

    void updateActivityAccountDaySurplusImageQuota(RaffleActivityAccount raffleActivityAccount);

}
