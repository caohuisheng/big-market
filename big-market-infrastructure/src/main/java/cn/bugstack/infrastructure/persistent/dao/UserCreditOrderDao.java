package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.UserCreditOrder;
import cn.bugstack.middleware.db.router.annotation.DBRouter;
import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import org.apache.ibatis.annotations.Mapper;

/**
 * Author: chs
 * Description: 积分订单dao
 * CreateTime: 2024-08-18
 */
@Mapper
@DBRouterStrategy(splitTable = true)
public interface UserCreditOrderDao {

    @DBRouter(key = "userId")
    void insert(UserCreditOrder userCreditOrder);

}
