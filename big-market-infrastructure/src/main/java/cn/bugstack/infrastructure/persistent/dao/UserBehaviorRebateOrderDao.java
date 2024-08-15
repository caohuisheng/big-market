package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.UserBehaviorRebateOrder;
import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import org.apache.ibatis.annotations.Mapper;

/**
 * Author: chs
 * Description: 用户行为返利Dao
 * CreateTime: 2024-08-13
 */
@Mapper
@DBRouterStrategy(splitTable = true)
public interface UserBehaviorRebateOrderDao {

    void insert(UserBehaviorRebateOrder userBehaviorRebateOrder);

}
