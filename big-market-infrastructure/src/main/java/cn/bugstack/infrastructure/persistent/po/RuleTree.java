package cn.bugstack.infrastructure.persistent.po;

import cn.bugstack.domain.strategy.model.valobj.RuleTreeNodeVO;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * Author: chs
 * Description: 规则树
 * CreateTime: 2024-07-16
 */
@Data
public class RuleTree {

    // 自增id
    private Long id;
    // 规则树id
    private String treeId;
    // 规则树名称
    private String treeName;
    // 规则树描述
    private String treeDesc;
    // 规则树根节点
    private String treeRootRuleNode;
    // 创建时间
    private Date createTime;
    // 修改时间
    private Date updateTime;

}
