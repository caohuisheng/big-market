package cn.bugstack.domain.strategy.service;

import cn.bugstack.domain.strategy.model.entity.RaffleAwardEntity;
import cn.bugstack.domain.strategy.model.entity.RaffleFactorEntity;
import cn.bugstack.domain.strategy.model.entity.RuleActionEntity;
import cn.bugstack.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import cn.bugstack.domain.strategy.model.valobj.StrategyAwardRuleModelVO;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.armory.IStrategyDispatch;
import cn.bugstack.domain.strategy.service.rule.chain.ILogicChain;
import cn.bugstack.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
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
public abstract class AbstractRaffleStrategy implements IRaffleStrategy {

    @Resource
    private IStrategyRepository repository;
    @Resource
    private IStrategyDispatch strategyDispatch;
    @Resource
    private DefaultChainFactory defaultChainFactory;

    public AbstractRaffleStrategy(IStrategyRepository repository,IStrategyDispatch strategyDispatch){
        this.repository = repository;
        this.strategyDispatch = strategyDispatch;
    }

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
        if(null == strategyId || StringUtils.isBlank(userId)){
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(),ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }

        // 2. 获取抽奖责任链 - 前置规则的责任链处理
        ILogicChain logicChain = defaultChainFactory.openLogicChain(strategyId);

        // 3.抽奖前执行规则过滤
        Integer randomAwardId = logicChain.logic(userId, strategyId);

        // 4.查询奖品规则[抽奖中（拿到奖品id时，过滤规则）、抽奖后（扣减万奖品库存后过滤，抽奖中拦截和无库存则走兜底）]
        StrategyAwardRuleModelVO strategyAwardRuleModelVO = repository.queryStrategyAwardRuleModels(strategyId, randomAwardId);

        // 5.抽奖中 - 规则过滤
        raffleFactorEntity.setAwardId(randomAwardId);
        RuleActionEntity<RuleActionEntity.RaffleCenterEntity> centerRuleActionEntity = this.doCheckRaffleCenterLogic(
                raffleFactorEntity, strategyAwardRuleModelVO.raffleCenterRuleModelList());

        if(RuleLogicCheckTypeVO.TAKE_OVER.getCode().equals(centerRuleActionEntity.getCode())){
            log.info("【临时日志】抽奖中规则过滤，通过抽奖后规则 rule_luck_award 走兜底奖励");
            return RaffleAwardEntity.builder()
                    .awardDesc("抽奖中规则过滤，通过抽奖后规则 rule_luck_award 走兜底奖励")
                    .build();
        }

        return RaffleAwardEntity.builder()
                .awardId(randomAwardId)
                .build();
    }
}
