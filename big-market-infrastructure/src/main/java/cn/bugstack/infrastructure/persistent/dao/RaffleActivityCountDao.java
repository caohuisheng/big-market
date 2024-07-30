package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.RaffleActivityCount;
import org.apache.ibatis.annotations.Mapper;

/**
 * Author: chs
 * Description:
 * CreateTime: 2024-07-30
 */
@Mapper
public interface RaffleActivityCountDao {

    RaffleActivityCount queryRaffleActivityCountById(Long activityCountId);

}
