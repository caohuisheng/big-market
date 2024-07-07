package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.Strategy;
import cn.bugstack.infrastructure.persistent.po.StrategyRule;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: chs
 * @CreateTime: 2024-07-05
 * @Description: 策略规则Dao
 * @Version: 1.0
 */
@Mapper
public interface StrategyRuleDao {
    /**
     * 查询所有策略规则的列表
     * @return
     */
    List<StrategyRule> queryStrategyRuleList();

    /**
     * 根据策略id和规则模型查询策略规则
     * @return
     */
    StrategyRule queryStrategyRule(Long strategyId, String ruleModel);

    /**
     * 查询规则值
     * @return
     */
    String queryStrategyRuleValue(Long strategyId,Integer awardId, String ruleModel);

}
