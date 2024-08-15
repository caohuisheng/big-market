package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.RaffleActivityAccountMonth;
import cn.bugstack.middleware.db.router.annotation.DBRouter;
import org.apache.ibatis.annotations.Mapper;

/**
 * Author: chs
 * Description: 抽奖活动账户月次数Dao
 * CreateTime: 2024-08-04
 */
@Mapper
public interface RaffleActivityAccountMonthDao {

    void insertActivityAccountMonth(RaffleActivityAccountMonth raffleActivityAccountMonth);

    @DBRouter(key = "userId")
    RaffleActivityAccountMonth queryActivityAccountMonthByUserId(RaffleActivityAccountMonth activityAccountMonthReq);

    int updateActivityAccountMonth(String userId, Long activityId, String month);

    void addAccountQuota(RaffleActivityAccountMonth raffleActivityAccountMonth);
}
