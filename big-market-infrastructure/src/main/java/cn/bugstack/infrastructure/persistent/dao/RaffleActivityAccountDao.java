package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.RaffleActivityAccount;
import org.apache.ibatis.annotations.Mapper;

/**
 * Author: chs
 * Description: 抽奖活动账户表
 * CreateTime: 2024-07-30
 */
@Mapper
public interface RaffleActivityAccountDao {

    void insert(RaffleActivityAccount raffleActivityAccount);

    int updateAccountQuota(RaffleActivityAccount raffleActivityAccount);

}
