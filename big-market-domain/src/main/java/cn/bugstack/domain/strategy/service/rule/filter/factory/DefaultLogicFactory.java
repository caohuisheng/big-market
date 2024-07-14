package cn.bugstack.domain.strategy.service.rule.filter.factory;

import cn.bugstack.domain.strategy.model.entity.RuleActionEntity;
import cn.bugstack.domain.strategy.service.annotation.LogicStrategy;
import cn.bugstack.domain.strategy.service.rule.filter.ILogicFilter;
import com.alibaba.fastjson2.util.AnnotationUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: chs
 * @Description: 规则工厂
 * @CreateTime: 2024-07-07
 */
@Service
public class DefaultLogicFactory {

    //规则代码-规则过滤器Map
    private Map<String,ILogicFilter<?>> logicFilterMap = new ConcurrentHashMap<>();

    public DefaultLogicFactory(List<ILogicFilter<?>> logicFilters){
        //将定义的规则过滤器添加到logicFilterMap中
        logicFilters.forEach(logicFilter -> {
            if(null != logicFilter){
                LogicStrategy strategy = AnnotationUtils.findAnnotation(logicFilter.getClass(), LogicStrategy.class);
                logicFilterMap.put(strategy.logicModel().getCode(), logicFilter);
            }
        });
    }

    /**
     * 获取所有规则过滤器
     * @param <T>
     * @return 返回规则过滤器映射
     */
    public <T extends RuleActionEntity.RaffleEntity> Map<String, ILogicFilter<T>> openLogicFilter() {
        return (Map<String, ILogicFilter<T>>) (Map<?, ?>) logicFilterMap;
    }

    /**
     * 逻辑规则模型
     */
    @Getter
    @AllArgsConstructor
    public enum LogicModel{
        RULE_WEIGHT("rule_weight","【抽奖前规则】根据权重值返回可抽奖的奖品范围", "before"),
        RULE_BLACKLIST("rule_blacklist", "【抽奖前规则】黑名单规则过滤，命中黑名单则直接返回","before"),
        RULE_LOCK("rule_lock", "【抽奖中规则】抽奖n次后，对应奖品可解锁抽奖","center"),
        RULE_LUCK_AWARD("rule_luck_award", "【抽奖后规则】抽奖n次后，对应奖品可解锁抽奖","after"),
        ;

        private final String code;
        private final String info;
        private final String type;

        public static boolean isCenter(String code){
            return "center".equals(LogicModel.valueOf(code.toUpperCase()).type);
        }

        public static boolean isAfter(String code){
            return "after".equals(LogicModel.valueOf(code.toUpperCase()).type);
        }
    }
}
