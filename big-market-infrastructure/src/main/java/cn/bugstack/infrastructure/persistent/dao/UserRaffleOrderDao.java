package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.UserRaffleOrder;
import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import org.apache.ibatis.annotations.Mapper;

/**
 * Author: chs
 * Description: 用户抽奖订单Dao
 * CreateTime: 2024-08-04
 */
@Mapper
@DBRouterStrategy(splitTable = true)
public interface UserRaffleOrderDao {

    void insert(UserRaffleOrder userRaffleOrder);

    UserRaffleOrder queryNoUsedRaffleOrder(String userId, Long activityId);

}
