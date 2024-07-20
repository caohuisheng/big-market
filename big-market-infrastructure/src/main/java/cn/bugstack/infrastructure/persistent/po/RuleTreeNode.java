package cn.bugstack.infrastructure.persistent.po;

import cn.bugstack.domain.strategy.model.valobj.RuleTreeNodeLineVO;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Author: chs
 * Description: 规则树节点
 * CreateTime: 2024-07-16
 */
@Data
public class RuleTreeNode {

    // 自增id
    private Long id;
    // 规则树id
    private String treeId;
    // 规则key
    private String ruleKey;
    // 规则描述
    private String ruleDesc;
    // 规则值
    private String ruleValue;
    // 创建时间
    private Date createTime;
    // 修改时间
    private Date updateTime;

}
