package cn.bugstack.domain.strategy.model.entity;

import cn.bugstack.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: chs
 * @Description: 规则动作实体
 * @CreateTime: 2024-07-07
 */
@Data
@Builder
public class RuleActionEntity<T extends RuleActionEntity.RaffleEntity> {

    //代码
    private String code = RuleLogicCheckTypeVO.ALLOW.getCode();
    //信息
    private String info = RuleLogicCheckTypeVO.ALLOW.getInfo();
    //规则模型
    private String ruleModel;
    //数据
    private T data;

    public static class RaffleEntity{
    }

    /* 抽奖之前 */
    @Data
    @Builder
    public static class RaffleBeforeEntity extends RaffleEntity{
        //策略id
        private Long strategyId;
        //规则权重值
        private String ruleWeightValue;
        //奖品id
        private Integer awardId;
    }

    /* 抽奖之中 */
    public static class RaffleCenterEntity extends RaffleEntity{

    }

    /* 抽奖之后 */
    public class RaffleAfterEntity extends RaffleEntity{

    }

}
