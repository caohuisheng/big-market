package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.Award;
import cn.bugstack.infrastructure.persistent.po.Strategy;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: chs
 * @CreateTime: 2024-07-05
 * @Description: 奖品策略Dao
 * @Version: 1.0
 */
@Mapper
public interface StrategyDao {
    /**
     * 查询所有策略
     * @return
     */
    List<Strategy> queryStrategyList();

    /**
     * 根据id查询策略
     * @param strategyId
     * @return
     */
    Strategy queryStrategyByStrategyId(Long strategyId);
}
