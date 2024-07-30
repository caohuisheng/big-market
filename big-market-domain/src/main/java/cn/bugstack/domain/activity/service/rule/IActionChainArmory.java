package cn.bugstack.domain.activity.service.rule;

/**
 * Author: chs
 * Description:
 * CreateTime: 2024-07-28
 */
public interface IActionChainArmory {

    IActionChain next();

    IActionChain appendNext(IActionChain next);

}
