package cn.bugstack.domain.strategy.service.rule.chain.factory;

import cn.bugstack.domain.strategy.model.entity.StrategyEntity;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.rule.chain.ILogicChain;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Author: chs
 * Description: 责任链工厂
 * CreateTime: 2024-07-14
 */
@Service
public class DefaultChainFactory {

    private final Map<String, ILogicChain> logicChainGroup;
    protected IStrategyRepository repository;

    public DefaultChainFactory(Map<String, ILogicChain> logicChainGroup, IStrategyRepository repository) {
        this.logicChainGroup = logicChainGroup;
        this.repository = repository;
    }

    /**
     * 通过策略id，构建责任链
     * @param strategyId 策略id
     * @return 责任链的头节点
     */
    public ILogicChain openLogicChain(Long strategyId){
        StrategyEntity strategyEntity = repository.queryStrategyEntityByStrategyId(strategyId);
        String[] ruleModelArr = strategyEntity.getRuleModelArr();

        // 如果未配置策略规则，之装配一个默认责任链节点
        if(null == ruleModelArr || 0 == ruleModelArr.length){
            return logicChainGroup.get("default");
        }

        // 创建责任链头节点
        ILogicChain logicChain = logicChainGroup.get(ruleModelArr[0]);
        ILogicChain current = logicChain;
        // 将剩余节点连接到头节点上
        for(int i=1;i<ruleModelArr.length;i++){
            ILogicChain nextChain = logicChainGroup.get(ruleModelArr[i]);
            current = current.appendNext(nextChain);
        }
        // 将默认责任链节点加到末尾
        current.appendNext(logicChainGroup.get("default"));

        // 返回责任链头节点
        return logicChain;
    }
}
