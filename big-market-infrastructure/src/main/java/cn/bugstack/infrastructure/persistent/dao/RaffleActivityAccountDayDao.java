package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.RaffleActivityAccountDay;
import cn.bugstack.infrastructure.persistent.po.RaffleActivityAccountMonth;
import cn.bugstack.middleware.db.router.annotation.DBRouter;
import org.apache.ibatis.annotations.Mapper;

/**
 * Author: chs
 * Description: 抽奖活动账户天次数Dao
 * CreateTime: 2024-08-04
 */
@Mapper
public interface RaffleActivityAccountDayDao {

    void insertActivityAccountDay(RaffleActivityAccountDay raffleActivityAccountDay);

    @DBRouter
    RaffleActivityAccountDay queryActivityAccountDayByUserId(String userId, Long activityId, String day);

    int updateActivityAccountDay(String userId, Long activityId, String day);
}
