package cn.bugstack.infrastructure.persistent.repository;

import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;
import cn.bugstack.domain.strategy.model.entity.StrategyEntity;
import cn.bugstack.domain.strategy.model.entity.StrategyRuleEntity;
import cn.bugstack.domain.strategy.model.valobj.RuleTreeNodeLineVO;
import cn.bugstack.domain.strategy.model.valobj.RuleTreeNodeVO;
import cn.bugstack.domain.strategy.model.valobj.RuleTreeVO;
import cn.bugstack.domain.strategy.model.valobj.StrategyAwardRuleModelVO;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.infrastructure.persistent.dao.*;
import cn.bugstack.infrastructure.persistent.po.*;
import cn.bugstack.infrastructure.persistent.redis.IRedisService;
import cn.bugstack.types.common.Constants;
import io.jsonwebtoken.lang.Collections;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMap;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.annotation.Resources;
import java.util.ArrayList;
import java.util.HashMap;
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
    @Resource
    private RuleTreeDao ruleTreeDao;
    @Resource
    private RuleTreeNodeDao ruleTreeNodeDao;
    @Resource
    private RuleTreeNodeLineDao ruleTreeNodeLineDao;

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

    @Override
    public String queryStrategyRuleValue(Long strategyId, Integer awardId, String ruleModel) {
        return strategyRuleDao.queryStrategyRuleValue(strategyId, awardId, ruleModel);
    }

    @Override
    public String queryStrategyRuleValue(Long strategyId, String ruleModel) {
        return queryStrategyRuleValue(strategyId, null, ruleModel);
    }

    @Override
    public StrategyAwardRuleModelVO queryStrategyAwardRuleModels(Long strategyId, Integer awardId) {
        String ruleModels = strategyAwardDao.queryStrategyAwardRuleModels(strategyId, awardId);
        return StrategyAwardRuleModelVO.builder()
                .ruleModels(ruleModels)
                .build();
    }

    @Override
    public RuleTreeVO queryRuleTreeVOByTreeId(String treeId) {
        //从缓存中查询
        String cacheKey = Constants.RedisKey.RULE_TREE_VO_KEY + treeId;
        RuleTreeVO ruleTreeVOCache = redisService.getValue(cacheKey);
        if(ruleTreeVOCache != null){
            return ruleTreeVOCache;
        }

        // 从数据库中查询
        RuleTree ruleTree = ruleTreeDao.queryRuleTreeByTreeId(treeId);
        List<RuleTreeNode> ruleTreeNodes = ruleTreeNodeDao.queryRuleTreeNodeList(treeId);
        List<RuleTreeNodeLine> ruleTreeNodeLines = ruleTreeNodeLineDao.queryRuleTreeNodeLineList(treeId);

        // 将node_line转换为map结构（节点名 - 所有以对应节点为起点的连线列表）
        Map<String, List<RuleTreeNodeLineVO>> ruleTreeNodeLineMap = new HashMap<>();
        ruleTreeNodeLines.forEach(line -> {
            RuleTreeNodeLineVO ruleTreeNodeLineVO = new RuleTreeNodeLineVO();
            BeanUtils.copyProperties(line, ruleTreeNodeLineVO);
            // 获取与当前连线起点相同的连线列表，并将当前连线加入到列表中
            List<RuleTreeNodeLineVO> ruleTreeNodeLineVOList = ruleTreeNodeLineMap
                    .computeIfAbsent(line.getRuleNodeFrom(), k -> new ArrayList<>());
            ruleTreeNodeLineVOList.add(ruleTreeNodeLineVO);
        });

        // 将node转换为map结构（节点名 - 节点对象）
        Map<String, RuleTreeNodeVO> treeNodeVOMap = new HashMap<>();
        ruleTreeNodes.forEach(node -> {
            RuleTreeNodeVO ruleTreeNodeVO = new RuleTreeNodeVO();
            BeanUtils.copyProperties(node, ruleTreeNodeVO);
            ruleTreeNodeVO.setTreeNodeLineVOList(ruleTreeNodeLineMap.get(node.getRuleKey()));
            treeNodeVOMap.put(node.getRuleKey(), ruleTreeNodeVO);
        });

        // 创建ruleTree对象
        RuleTreeVO ruleTreeVO = new RuleTreeVO();
        BeanUtils.copyProperties(ruleTree, ruleTreeVO);
        ruleTreeVO.setTreeNodeMap(treeNodeVOMap);
        // 将结果保存到缓存中
        redisService.setValue(cacheKey, ruleTreeVOCache);

        return ruleTreeVO;
    }

}
