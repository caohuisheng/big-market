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
    List<StrategyRule> queryStrategyList();
}
