package cn.bugstack.domain.strategy.service.armory;

import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
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
public class StrategyArmory implements IStrategyArmory {

    @Resource
    private IStrategyRepository repository;

    @Override
    public boolean assembleLotteryStrategy(Long strategyId) {
        // 查询策略配置
        List<StrategyAwardEntity> strategyAwardEntities = repository.queryStrategyAwardList(strategyId);
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
        repository.storeStrategyAwardSearchRateTable(strategyId,strategyAwardSearchRateTable.size(),shuffleStrategyAwardSearchRateTable);

        return true;
    }

    @Override
    public Integer getRandomAwardId(Long strategyId) {
        int rateRange = repository.getRateRange(strategyId);
        return repository.getStrategyAwardAssemble(strategyId, new SecureRandom().nextInt(rateRange));
    }
}
