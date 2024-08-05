package cn.bugstack.domain.activity.service.quota.rule;

/**
 * Author: chs
 * Description: 下单规则责任链抽象类
 * CreateTime: 2024-07-28
 */
public abstract class AbstractActionChain implements IActionChain {

    private IActionChain next;

    @Override
    public IActionChain next() {
        return next;
    }

    @Override
    public IActionChain appendNext(IActionChain next) {
        this.next = next;
        return next;
    }
}
