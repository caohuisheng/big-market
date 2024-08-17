package cn.bugstack.domain.strategy.service;

import cn.bugstack.domain.strategy.model.entity.RaffleAwardEntity;
import cn.bugstack.domain.strategy.model.entity.RaffleFactorEntity;
import cn.bugstack.domain.strategy.model.entity.RuleActionEntity;
import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;
import cn.bugstack.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import cn.bugstack.domain.strategy.model.valobj.StrategyAwardRuleModelVO;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.armory.IStrategyDispatch;
import cn.bugstack.domain.strategy.service.rule.chain.ILogicChain;
import cn.bugstack.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import cn.bugstack.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * @Author: chs
 * @Description: 抽奖策略抽象类（定义抽奖的标准流程）
 * @CreateTime: 2024-07-07
 */
@Slf4j
public abstract class AbstractRaffleStrategy implements IRaffleStrategy {

    // 策略仓储服务
    protected IStrategyRepository repository;
    // 策略调度服务
    protected IStrategyDispatch strategyDispatch;
    // 抽奖的责任链（从抽奖的规则中，解耦出前置规则为责任链处理）
    protected DefaultChainFactory defaultChainFactory;
    // 抽奖的决策树（负责抽奖中到抽奖后的规则过滤）
    protected DefaultTreeFactory defaultTreeFactory;

    public AbstractRaffleStrategy(IStrategyRepository repository, IStrategyDispatch strategyDispatch,
                                  DefaultChainFactory defaultChainFactory, DefaultTreeFactory defaultTreeFactory) {
        this.repository = repository;
        this.strategyDispatch = strategyDispatch;
        this.defaultChainFactory = defaultChainFactory;
        this.defaultTreeFactory = defaultTreeFactory;
    }

    @Override
    public RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity) {
        //1.参数校验
        String userId = raffleFactorEntity.getUserId();
        Long strategyId = raffleFactorEntity.getStrategyId();
        Date endDatetime = raffleFactorEntity.getEndDatetime();
        if(null == strategyId || StringUtils.isBlank(userId)){
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(),ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }

        // 2.责任链抽奖计算
        DefaultChainFactory.StrategyAwardVO chainStrategyAwardVO = this.raffleLogicChain(userId, strategyId);
        Integer awardId = chainStrategyAwardVO.getAwardId();
        String logicModel = chainStrategyAwardVO.getLogicModel();
        log.info("抽奖策略计算-责任链 userId:{}, strategyId:{}, awardId:{}, logicModel:{}", userId, strategyId, awardId, logicModel);
        // 如果是黑名单、权重等非默认抽奖，直接返回结果
        if(!DefaultChainFactory.LogicModel.RULE_DEFAULT.getCode().equals(logicModel)){
            //return RaffleAwardEntity.builder().awardId(awardId).build();
            return buildRaffleAwardEntity(strategyId, awardId, chainStrategyAwardVO.getAwardRuleValue());
        }

        // 3.规则树抽奖计算
        DefaultTreeFactory.StrategyAwardVO treeStrategyAwardVO = this.raffleLogicTree(userId, strategyId, awardId, endDatetime);
        log.info("抽奖策略计算-规则树 userId:{}, strategyId:{}, awardId:{}, awardRuleValue:{}", userId, strategyId, awardId, treeStrategyAwardVO.getAwardRuleValue());

        // 4.返回抽奖结果
        //return RaffleAwardEntity.builder()
        //        .awardId(treeStrategyAwardVO.getAwardId())
        //        .awardConfig(treeStrategyAwardVO.getAwardRuleValue())
        //        .build();
        return buildRaffleAwardEntity(strategyId, treeStrategyAwardVO.getAwardId(), treeStrategyAwardVO.getAwardRuleValue());
    }

    private RaffleAwardEntity buildRaffleAwardEntity(Long strategyId, Integer awardId, String awardConfig){
        StrategyAwardEntity strategyAwardEntity = repository.queryStrategyAwardEntity(strategyId, awardId);
        return RaffleAwardEntity.builder()
                .awardId(awardId)
                .awardTitle(strategyAwardEntity.getAwardTitle())
                .awardConfig(awardConfig)
                .sort(strategyAwardEntity.getSort())
                .build();
    }

    /**
     * 抽奖计算（责任链抽象方法）
     * @param userId 用户id
     * @param strategyId 策略id
     * @return 奖品id
     */
    public abstract DefaultChainFactory.StrategyAwardVO raffleLogicChain(String userId, Long strategyId);

    /**
     * 抽奖计算（规则树象方法）
     * @param userId 用户id
     * @param strategyId 策略id
     * @param awardId 奖品id
     * @return 奖品id
     */
    public abstract DefaultTreeFactory.StrategyAwardVO raffleLogicTree(String userId, Long strategyId, Integer awardId, Date endDatetime);

}
