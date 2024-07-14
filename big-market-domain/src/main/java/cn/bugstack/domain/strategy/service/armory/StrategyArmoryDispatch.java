package cn.bugstack.domain.strategy.service.armory;

import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;
import cn.bugstack.domain.strategy.model.entity.StrategyEntity;
import cn.bugstack.domain.strategy.model.entity.StrategyRuleEntity;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.types.common.Constants;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: chs
 * @Description: 策略装配库的实现类
 * @CreateTime: 2024-07-06
 */
@Slf4j
@Service
public class StrategyArmoryDispatch implements IStrategyArmory,IStrategyDispatch {

    @Resource
    private IStrategyRepository repository;

    @Override
    public boolean assembleLotteryStrategy(Long strategyId) {
        // 查询策略配置
        List<StrategyAwardEntity> strategyAwardEntities = repository.queryStrategyAwardList(strategyId);
        assembleLotteryStrategy(String.valueOf(strategyId),strategyAwardEntities);

        // 权重策略配置 - 适用于rule_weight权重规则配置
        StrategyEntity strategyEntity = repository.queryStrategyEntityByStrategyId(strategyId);
        String ruleWeight = strategyEntity.getRuleWeight();
        if(null == ruleWeight) return true;

        // 根据策略id和权重规则查询策略规则
        StrategyRuleEntity strategyRuleEntity = repository.queryStrategyRuleEntity(strategyId, ruleWeight);
        if(null == strategyRuleEntity){
            throw new AppException(ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getCode(), ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getInfo());
        }

        // 获取积分阈值-积分阈值to奖品列表映射
        Map<String, List<Integer>> scoreToAwards = strategyRuleEntity.getScoreToAwards();
        // 遍历每个积分阈值，执行权重规则配置
        for(String score: scoreToAwards.keySet()){
            List<Integer> awards = scoreToAwards.get(score);
            List<StrategyAwardEntity> strategyAwardEntitiesClone = new ArrayList<>(strategyAwardEntities);
            strategyAwardEntitiesClone.removeIf(item -> !awards.contains(item.getAwardId()));
            assembleLotteryStrategy(strategyId+"_"+score, strategyAwardEntitiesClone);
        }

        return true;
    }

    private void assembleLotteryStrategy(String keySuffix, List<StrategyAwardEntity> strategyAwardEntities){
        // 获取最小概率值
        BigDecimal minAwardRate = strategyAwardEntities.stream().map(StrategyAwardEntity::getAwardRate)
                .min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        // 计算概率总和
        BigDecimal totalAwardRate = strategyAwardEntities.stream().map(StrategyAwardEntity::getAwardRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        log.debug(strategyAwardEntities.toString());
        // 计算概率范围
        BigDecimal rateRange = totalAwardRate.divide(minAwardRate, 0, RoundingMode.CEILING);
        // 生成策略奖品概率查找表
        List<Integer> strategyAwardSearchRateTable = new ArrayList<>();
        for(StrategyAwardEntity strategyAwardEntity:strategyAwardEntities){
            Integer awardId = strategyAwardEntity.getAwardId();
            BigDecimal awardRate = strategyAwardEntity.getAwardRate();
            // 每个奖品需要添加到查找表中的数量（根据奖品概率计算）
            int count = awardRate.multiply(rateRange).setScale(0, RoundingMode.CEILING).intValue();
            for(int i=0;i<count;i++) {
                strategyAwardSearchRateTable.add(awardId);
            }
        }
        // 对概率查找表中的奖品id进行打乱操作
        Collections.shuffle(strategyAwardSearchRateTable);
        // 将概率查找表转换为map集合
        Map<Integer,Integer> shuffleStrategyAwardSearchRateTable = new HashMap<>();
        for(int i=0;i<strategyAwardSearchRateTable.size();i++){
            shuffleStrategyAwardSearchRateTable.put(i, strategyAwardSearchRateTable.get(i));
        }
        // 将概率查找表保存到redis
        repository.storeStrategyAwardSearchRateTable(keySuffix,strategyAwardSearchRateTable.size(),shuffleStrategyAwardSearchRateTable);
    }

    @Override
    public Integer getRandomAwardId(Long strategyId) {
        int rateRange = repository.getRateRange(strategyId);
        return repository.getStrategyAwardAssemble(strategyId, new SecureRandom().nextInt(rateRange));
    }

    @Override
    public Integer getRandomAwardId(Long strategyId, String ruleWeightValue) {
        String keySuffix = strategyId + "_" + ruleWeightValue;
        int rateRange = repository.getRateRange(keySuffix);
        return repository.getStrategyAwardAssemble(keySuffix, new SecureRandom().nextInt(rateRange));
    }
}
