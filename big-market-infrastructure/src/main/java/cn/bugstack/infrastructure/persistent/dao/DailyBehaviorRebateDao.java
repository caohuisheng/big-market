package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.DailyBehaviorRebate;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Author: chs
 * Description: 日常行为返利Dao
 * CreateTime: 2024-08-13
 */
@Mapper
public interface DailyBehaviorRebateDao {

    /**
     * 根据行为类型查询日常行为返利列表
     * @param behaviorType
     * @return
     */
    List<DailyBehaviorRebate> queryDailyBehaviorRebateByBehaviorType(String behaviorType);

}
