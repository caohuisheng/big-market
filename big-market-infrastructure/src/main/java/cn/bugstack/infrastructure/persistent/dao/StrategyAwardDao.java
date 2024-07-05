package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.Strategy;
import cn.bugstack.infrastructure.persistent.po.StrategyAward;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: chs
 * @CreateTime: 2024-07-05
 * @Description: 奖品Dao
 * @Version: 1.0
 */
@Mapper
public interface StrategyAwardDao {
    List<StrategyAward> queryStrategyAwardList();
}
