package cn.bugstack.domain.strategy.service.raffle;

import cn.bugstack.domain.strategy.model.entity.RaffleFactorEntity;
import cn.bugstack.domain.strategy.model.entity.RuleActionEntity;
import cn.bugstack.domain.strategy.model.entity.RuleMatterEntity;
import cn.bugstack.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.armory.IStrategyDispatch;
import cn.bugstack.domain.strategy.service.rule.ILogicFilter;
import cn.bugstack.domain.strategy.service.rule.factory.DefaultLogicFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: chs
 * @Description: 默认抽奖策略实现
 * @CreateTime: 2024-07-07
 */
@Slf4j
@Service
public class DefaultRaffleStrategy extends AbstractRaffleStrategy{

    @Resource
    private DefaultLogicFactory logicFactory;

    public DefaultRaffleStrategy(IStrategyRepository repository, IStrategyDispatch strategyDispatch){
        super(repository, strategyDispatch);
    }

    @Override
    protected RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> doCheckRaffleBeforeLogic(
            RaffleFactorEntity raffleFactorEntity, String[] ruleModelArr) {
        Map<String, ILogicFilter<RuleActionEntity.RaffleBeforeEntity>> logicFilterGroup = logicFactory.openLogicFilter();

        //1.黑名单规则优先过滤
        String rule_blacklist = Arrays.stream(ruleModelArr)
                .filter(ruleModel -> ruleModel.equals(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode()))
                .findFirst()
                .orElse(null);
        /* 判断黑名单规则是否为空 */
        if(!StringUtils.isBlank(rule_blacklist)){
            //根据规则代码获取对应的过滤器
            ILogicFilter<RuleActionEntity.RaffleBeforeEntity> logicFilter = logicFilterGroup.get(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode());
            //创建RuleMatterEntity对象
            RuleMatterEntity ruleMatterEntity = new RuleMatterEntity();
            ruleMatterEntity.setUserId(raffleFactorEntity.getUserId());
            ruleMatterEntity.setStrategyId(raffleFactorEntity.getStrategyId());
            ruleMatterEntity.setRuleModel(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode());
            //执行过滤规则，返回规则行为实体
            RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> ruleActionEntity = logicFilter.filter(ruleMatterEntity);
            //如果规则动作的code不为放行，直接返回该规则动作实体
            if(!RuleLogicCheckTypeVO.ALLOW.getCode().equals(ruleActionEntity.getCode())){
                return ruleActionEntity;
            }
        }

        //2.顺序过滤剩余规则
        List<String> ruleModels = Arrays.stream(ruleModelArr)
                .filter(ruleModel -> !ruleModel.equals(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode()))
                .collect(Collectors.toList());

        RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> ruleActionEntity = null;
        for(String ruleModel:ruleModels){
            //根据规则代码获取对应的过滤器
            ILogicFilter<RuleActionEntity.RaffleBeforeEntity> logicFilter = logicFilterGroup.get(ruleModel);
            //创建RuleMatterEntity对象
            RuleMatterEntity ruleMatterEntity = new RuleMatterEntity();
            ruleMatterEntity.setUserId(raffleFactorEntity.getUserId());
            ruleMatterEntity.setStrategyId(raffleFactorEntity.getStrategyId());
            ruleMatterEntity.setRuleModel(ruleModel);
            //执行过滤规则，返回规则行为实体
            ruleActionEntity = logicFilter.filter(ruleMatterEntity);
            log.info("前置过滤userId:{},ruleModel:{},code:{},info:{}",raffleFactorEntity.getUserId(),ruleModel,
                    ruleActionEntity.getCode(),ruleActionEntity.getInfo());
            //如果规则行为为放行，则返回规则行为
            if(!RuleLogicCheckTypeVO.ALLOW.getCode().equals(ruleActionEntity.getCode())){
                return ruleActionEntity;
            }
        }

        //所有规则都过滤完成，返回最终规则动作实体
        return ruleActionEntity;
    }
}