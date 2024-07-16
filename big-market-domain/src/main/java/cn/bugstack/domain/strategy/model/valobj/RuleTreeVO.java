package cn.bugstack.domain.strategy.model.valobj;

import lombok.Data;

import java.util.Map;

/**
 * Author: chs
 * Description: 规则树对象
 * CreateTime: 2024-07-14
 */
@Data
public class RuleTreeVO {

    // 规则树id
    private String treeId;
    // 规则树名称
    private String treeName;
    // 规则树描述
    private String treeDesc;
    // 规则树根节点
    private String treeRootRuleNode;
    // 规则树节点
    private Map<String, RuleTreeNodeVO> treeNodeMap;
}
