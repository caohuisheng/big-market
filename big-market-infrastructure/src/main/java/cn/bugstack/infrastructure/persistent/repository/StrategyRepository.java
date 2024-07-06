package cn.bugstack.infrastructure.persistent.repository;

import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;
import cn.bugstack.domain.strategy.model.entity.StrategyEntity;
import cn.bugstack.domain.strategy.model.entity.StrategyRuleEntity;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.infrastructure.persistent.dao.StrategyAwardDao;
import cn.bugstack.infrastructure.persistent.dao.StrategyDao;
import cn.bugstack.infrastructure.persistent.dao.StrategyRuleDao;
import cn.bugstack.infrastructure.persistent.po.Strategy;
import cn.bugstack.infrastructure.persistent.po.StrategyAward;
import cn.bugstack.infrastructure.persistent.po.StrategyRule;
import cn.bugstack.infrastructure.persistent.redis.IRedisService;
import cn.bugstack.types.common.Constants;
import io.jsonwebtoken.lang.Collections;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMap;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.annotation.Resources;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: chs
 * @Description: 策略服务仓储实现
 * @CreateTime: 2024-07-06
 */
@Repository
public class StrategyRepository implements IStrategyRepository {

    @Resource
    private IRedisService redisService;
    @Resource
    private StrategyAwardDao strategyAwardDao;
    @Resource
    private StrategyDao strategyDao;
    @Resource
    private StrategyRuleDao strategyRuleDao;

    @Override
    public List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId) {
        // 先从缓存中查询
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_KEY + strategyId;
        List<StrategyAwardEntity> strategyAwardEntities = redisService.getValue(cacheKey);
        if(!Collections.isEmpty(strategyAwardEntities)){
            return strategyAwardEntities;
        }
        // 若缓存中没有, 再从数据库查询
        List<StrategyAward> strategyAwards = strategyAwardDao.queryStrategyAwardListByStrategyId(strategyId);
        strategyAwardEntities = strategyAwards.stream().map(strategyAward -> {
            StrategyAwardEntity strategyAwardEntity = new StrategyAwardEntity();
            BeanUtils.copyProperties(strategyAward, strategyAwardEntity);
            return strategyAwardEntity;
        }).collect(Collectors.toList());
        // 将查询结果保存到缓存
        redisService.setValue(cacheKey, strategyAwardEntities);

        return strategyAwardEntities;
    }

    @Override
    public void storeStrategyAwardSearchRateTable(String keySuffix, Integer rateRange, Map<Integer, Integer> strategyAwardSearchRateTable) {
        // 存储抽奖范围
        redisService.setValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + keySuffix, rateRange);
        // 存储概率查找表
        RMap<Object, Object> cacheRateTable = redisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + keySuffix);
        cacheRateTable.putAll(strategyAwardSearchRateTable);
    }

    @Override
    public Integer getStrategyAwardAssemble(Long strategyId, Integer randomIndex) {
        return redisService.getFromMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + strategyId, randomIndex);
    }

    @Override
    public Integer getStrategyAwardAssemble(String keySuffix, Integer randomIndex) {
        return redisService.getFromMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + keySuffix, randomIndex);
    }

    @Override
    public int getRateRange(Long strategyId) {
        return redisService.getValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + String.valueOf(strategyId));
    }

    @Override
    public int getRateRange(String keySuffix) {
        return redisService.getValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + keySuffix);
    }

    @Override
    public StrategyEntity queryStrategyEntityByStrategyId(Long strategyId) {
        // 首先从缓存中查询
        String cacheKey = Constants.RedisKey.STRATEGY_KEY + strategyId;
       StrategyEntity strategyEntity = redisService.getValue(cacheKey);
        if(strategyEntity != null){
            return strategyEntity;
        }
        // 从数据库中查询
        Strategy strategy = strategyDao.queryStrategyByStrategyId(strategyId);
        strategyEntity = new StrategyEntity();
        BeanUtils.copyProperties(strategy, strategyEntity);
        // 将结果保存到缓存
        redisService.setValue(cacheKey, strategyEntity);

        return strategyEntity;
    }

    @Override
    public StrategyRuleEntity queryStrategyRuleEntity(Long strategyId, String ruleWeight) {
        StrategyRule strategyRule = strategyRuleDao.queryStrategyRule(strategyId, ruleWeight);
        StrategyRuleEntity strategyRuleEntity = new StrategyRuleEntity();
        BeanUtils.copyProperties(strategyRule, strategyRuleEntity);
        return strategyRuleEntity;
    }
}
