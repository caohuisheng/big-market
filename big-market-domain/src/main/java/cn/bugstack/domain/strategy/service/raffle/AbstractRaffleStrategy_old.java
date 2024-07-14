package cn.bugstack.domain.strategy.service.raffle;

import cn.bugstack.domain.strategy.model.entity.RaffleAwardEntity;
import cn.bugstack.domain.strategy.model.entity.RaffleFactorEntity;
import cn.bugstack.domain.strategy.model.entity.RuleActionEntity;
import cn.bugstack.domain.strategy.model.entity.StrategyEntity;
import cn.bugstack.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import cn.bugstack.domain.strategy.model.valobj.StrategyAwardRuleModelVO;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.IRaffleStrategy;
import cn.bugstack.domain.strategy.service.armory.IStrategyDispatch;
import cn.bugstack.domain.strategy.service.rule.filter.factory.DefaultLogicFactory;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;

/**
 * @Author: chs
 * @Description: 抽奖策略抽象类（定义抽奖的标准流程）
 * @CreateTime: 2024-07-07
 */
@Slf4j
public abstract class AbstractRaffleStrategy_old implements IRaffleStrategy {

    @Resource
    private IStrategyRepository repository;
    @Resource
    private IStrategyDispatch strategyDispatch;

    public AbstractRaffleStrategy_old(IStrategyRepository repository, IStrategyDispatch strategyDispatch){
        this.repository = repository;
        this.strategyDispatch = strategyDispatch;
    }

    /**
     * 执行抽奖前置的规则过滤
     * @param raffleFactorEntity 抽奖因子
     * @param ruleModelArr 规则模型数组
     * @return 规则动作实体
     */
    protected abstract RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> doCheckRaffleBeforeLogic(
            RaffleFactorEntity raffleFactorEntity, String[] ruleModelArr);

    /**
     * 执行抽奖中置的规则过滤
     * @param raffleFactorEntity
     * @param ruleModelArr
     * @return
     */
    protected abstract RuleActionEntity<RuleActionEntity.RaffleCenterEntity> doCheckRaffleCenterLogic(
            RaffleFactorEntity raffleFactorEntity,String[] ruleModelArr);

    @Override
    public RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity) {
        //1.参数校验
        String userId = raffleFactorEntity.getUserId();
        Long strategyId = raffleFactorEntity.getStrategyId();
        Integer awardId = raffleFactorEntity.getAwardId();
        if(null == strategyId || StringUtils.isBlank(userId)){
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(),ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }

        // 2.根据策略id查询策略实体
        StrategyEntity strategyEntity = repository.queryStrategyEntityByStrategyId(strategyId);

        // 3.抽奖前执行规则过滤
        RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> ruleActionEntity = this.doCheckRaffleBeforeLogic(
                raffleFactorEntity, strategyEntity.getRuleModelArr());
        /* 判断规则动作实体的code是否为TAKE_OVER(拦截) */
        if(RuleLogicCheckTypeVO.TAKE_OVER.getCode().equals(ruleActionEntity.getCode())){
            String ruleModel = ruleActionEntity.getRuleModel();
            if(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode().equals(ruleModel)){
                //若为黑名单规则，直接返回固定的奖品id
                return RaffleAwardEntity.builder()
                        .awardId(ruleActionEntity.getData().getAwardId())
                        .build();
            }else if(DefaultLogicFactory.LogicModel.RULE_WEIGHT.getCode().equals(ruleModel)){
                //若为权重规则，根据返回的ruleWeightValue进行抽奖
                RuleActionEntity.RaffleBeforeEntity raffleBeforeEntity = ruleActionEntity.getData();
                String ruleWeightValue = raffleBeforeEntity.getRuleWeightValue();
                Integer randomAwardId = strategyDispatch.getRandomAwardId(strategyId, ruleWeightValue);
                return RaffleAwardEntity.builder()
                        .awardId(randomAwardId)
                        .build();
            }
        }

        // 4.默认抽奖流程
        Integer randomAwardId = strategyDispatch.getRandomAwardId(strategyId);

        // 5.查询奖品规则[抽奖中（拿到奖品id时，过滤规则）、抽奖后（扣减万奖品库存后过滤，抽奖中拦截和无库存则走兜底）]
        StrategyAwardRuleModelVO strategyAwardRuleModelVO = repository.queryStrategyAwardRuleModels(strategyId, randomAwardId);

        // 6.抽奖中 - 规则过滤
        raffleFactorEntity.setAwardId(randomAwardId);
        RuleActionEntity<RuleActionEntity.RaffleCenterEntity> centerRuleActionEntity = this.doCheckRaffleCenterLogic(
                raffleFactorEntity, strategyAwardRuleModelVO.raffleCenterRuleModelList());

        if(RuleLogicCheckTypeVO.TAKE_OVER.getCode().equals(centerRuleActionEntity.getCode())){
            log.info("【临时日志】中奖中规则过滤，通过抽奖后规则 rule_luck_award 走兜底奖励");
            return RaffleAwardEntity.builder()
                    .awardDesc("中奖中规则过滤，通过抽奖后规则 rule_luck_award 走兜底奖励")
                    .build();
        }

        return RaffleAwardEntity.builder()
                .awardId(randomAwardId)
                .build();
    }
}
