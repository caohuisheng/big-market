package cn.bugstack.domain.strategy.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: chs
 * Description:
 * CreateTime: 2024-07-14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleTreeNodeLineVO {

    // 规则树id
    private String treeId;
    // 规则key节点 from
    private String ruleNodeFrom;
    // 规则树key节点 to
    private String ruleNodeTo;
    // 限定类型
    private RuleLimitTypeVO ruleLimitType;
    // 限定值（到下个节点）
    private RuleLogicCheckTypeVO ruleLimitValue;

}
