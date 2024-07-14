package cn.bugstack.domain.strategy.service.rule.chain;

/**
 * Author: chs
 * Description: 责任链装配
 * CreateTime: 2024-07-14
 */
public interface ILogicChainArmory {

    ILogicChain next();

    ILogicChain appendNext(ILogicChain next);

}
