package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;
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
    /**
     * 查询所有策略奖品列表
     * @return
     */
    List<StrategyAward> queryStrategyAwardList();

    /**
     * 根据策略id查询策略奖品列表
     * @return
     */
    List<StrategyAward> queryStrategyAwardListByStrategyId(Long strategyId);
}
