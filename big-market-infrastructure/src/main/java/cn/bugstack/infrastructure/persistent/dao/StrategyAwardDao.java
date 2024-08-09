package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;
import cn.bugstack.infrastructure.persistent.po.Strategy;
import cn.bugstack.infrastructure.persistent.po.StrategyAward;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
     * 根据活动id和奖品id查询单个奖品
     * @param strategyId 策略id
     * @param awardId 奖品id
     * @return
     */
    StrategyAward queryStrategyAward(Long strategyId, Integer awardId);

    /**
     * 根据策略id查询策略奖品列表
     * @return
     */
    List<StrategyAward> queryStrategyAwardListByStrategyId(Long strategyId);

    /**
     * 根据策略id和奖品id查询规则模型
     * @param strategyId
     * @param awardId
     * @return
     */
    String queryStrategyAwardRuleModels(@Param("strategyId") Long  strategyId,@Param("awardId") Integer awardId);

    void updateStrategyAwardStock(@Param("strategyId") Long strategyId,@Param("awardId") Integer awardId);
}
