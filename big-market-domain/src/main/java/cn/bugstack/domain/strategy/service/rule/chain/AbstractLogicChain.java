package cn.bugstack.domain.strategy.service.rule.chain;

/**
 * Author: chs
 * Description: 抽奖策略责任链（判断走哪种抽奖策略，如默认抽奖、权重抽奖、黑名单抽奖）
 * CreateTime: 2024-07-14
 */
public abstract class AbstractLogicChain implements ILogicChain {

    private ILogicChain next;

    @Override
    public ILogicChain next() {
        return next;
    }

    @Override
    public ILogicChain appendNext(ILogicChain next) {
        this.next = next;
        return next;
    }

    protected abstract String ruleModel();
}
