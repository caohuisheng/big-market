package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.RaffleActivity;
import org.apache.ibatis.annotations.Mapper;

/**
 * Author: chs
 * Description: 活动Dao
 * CreateTime: 2024-07-29
 */
@Mapper
public interface RaffleActivityDao {

    RaffleActivity queryRaffleActivityById(Long activityId);

}
