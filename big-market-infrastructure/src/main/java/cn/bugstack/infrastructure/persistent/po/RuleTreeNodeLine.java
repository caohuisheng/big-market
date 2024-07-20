package cn.bugstack.infrastructure.persistent.po;

import cn.bugstack.domain.strategy.model.valobj.RuleLimitTypeVO;
import cn.bugstack.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Author: chs
 * Description:规则树节点连线
 * CreateTime: 2024-07-16
 */
@Data
public class RuleTreeNodeLine {

    // 自增id
    private Long id;
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
    // 创建时间
    private Date createTime;
    // 修改时间
    private Date updateTime;
}
