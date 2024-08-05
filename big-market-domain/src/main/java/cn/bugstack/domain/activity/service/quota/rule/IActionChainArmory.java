package cn.bugstack.domain.activity.service.quota.rule;

/**
 * Author: chs
 * Description: 活动责任链装配
 * CreateTime: 2024-07-28
 */
public interface IActionChainArmory {

    IActionChain next();

    IActionChain appendNext(IActionChain next);

}
