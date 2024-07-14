package cn.bugstack.domain.strategy.model.valobj;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Author: chs
 * Description: 规则树节点对象
 * CreateTime: 2024-07-14
 */
@Data
@Builder
public class RuleTreeNodeVO {

    // 规则树id
    private Integer treeId;
    // 规则key
    private String ruleKey;
    // 规则描述
    private String ruleDesc;
    // 规则值
    private String ruleValue;

    // 规则连线
    private List<RuleTreeNodeLineVO> treeNodeLineVOList;

}
