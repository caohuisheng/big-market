package cn.bugstack.domain.strategy.repository;

import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;
import cn.bugstack.domain.strategy.model.entity.StrategyEntity;
import cn.bugstack.domain.strategy.model.entity.StrategyRuleEntity;
import cn.bugstack.domain.strategy.model.valobj.RuleTreeVO;
import cn.bugstack.domain.strategy.model.valobj.RuleWeightVO;
import cn.bugstack.domain.strategy.model.valobj.StrategyAwardRuleModelVO;
import cn.bugstack.domain.strategy.model.valobj.StrategyAwardStockKeyVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 策略服务仓储接口
 * @create 2023-12-23 09:33
 */
public interface IStrategyRepository {
    Long queryStrategyIdByActivityId(Long activityId);

    List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId);

    StrategyAwardEntity queryStrategyAwardEntity(Long strategyId, Integer awardId);

    void storeStrategyAwardSearchRateTable(String key, Integer rateRange, Map<Integer, Integer> strategyAwardSearchRateTable);

    Integer getStrategyAwardAssemble(Long strategyId, Integer randomIndex);

    Integer getStrategyAwardAssemble(String keySuffix, Integer randomIndex);

    int getRateRange(Long strategyId);

    int getRateRange(String keySuffix);

    StrategyEntity queryStrategyEntityByStrategyId(Long strategyId);

    StrategyRuleEntity queryStrategyRuleEntity(Long strategyId,String ruleWeight);

    String queryStrategyRuleValue(Long strategyId, Integer awardId, String ruleModel);

    String queryStrategyRuleValue(Long strategyId, String ruleModel);

    StrategyAwardRuleModelVO queryStrategyAwardRuleModels(Long StrategyId, Integer awardId);

    /**
     * 根据规则树id查询树结构信息
     * @param treeId 树id
     * @return 树结构信息
     */
    RuleTreeVO queryRuleTreeVOByTreeId(String treeId);

    /**
     * 缓存奖品库存
     * @param cacheKey 键
     * @param count 库存值
     */
    void cacheStrategyAwardCount(String cacheKey, Integer count);

    /**
     * 通过decr扣减库存
     * @param cacheKey 键
     * @return 扣减结果
     */
    Boolean subtractionAwardStock(String cacheKey, Date endDatetime);

    /**
     * 写入奖品库存消费队列
     * @param strategyAwardStockKeyVO 对象值对象
     */
    void awardStockConsumeSendQueue(StrategyAwardStockKeyVO strategyAwardStockKeyVO);

    /**
     * 获取奖品库存消费队列
     * @return
     */
    StrategyAwardStockKeyVO takeQueueValue();

    /**
     * 更新奖品库存
     * @param strategyId 策略id
     * @param awardId 奖品id
     */
    void updateStrategyAwardStock(Long strategyId, Integer awardId);

    Map<String, Integer> queryAwardRuleLockCount(String[] treeIds);

    /**
     * 根据策略id查询奖品规则权重值
     * @param strategyId 策略id
     * @return
     */
    List<RuleWeightVO> queryAwardRuleWeight(Long strategyId);

    /**
     * 查询用户活动账户总抽奖次数
     * @param userId 用户id
     * @param strategyId 策略id
     * @return
     */
    Integer queryActivityAccountTotalUseCount(String userId, Long strategyId);

    /**
     * 查询用户今日抽奖次数
     * @param userId 用户id
     * @param strategyId 策略id
     * @return
     */
    Integer queryTodayUserRaffleCount(String userId, Long strategyId);

}
