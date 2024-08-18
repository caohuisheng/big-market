package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.RaffleActivityOrder;
import cn.bugstack.middleware.db.router.annotation.DBRouter;
import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 抽奖活动订单Dao
 */
@Mapper
@DBRouterStrategy(splitTable = true)
public interface RaffleActivityOrderDao {

    @DBRouter(key = "userId")
    void insert(RaffleActivityOrder raffleActivityOrderReq);

    @DBRouter(key = "userId")
    RaffleActivityOrder queryRaffleActivityOrderByUserId(RaffleActivityOrder raffleActivityOrder);

    int updateOrderCompleted(RaffleActivityOrder raffleActivityOrderReq);

}
