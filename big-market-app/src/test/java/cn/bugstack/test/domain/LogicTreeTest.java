package cn.bugstack.test.domain;

import cn.bugstack.domain.strategy.model.valobj.*;
import cn.bugstack.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import cn.bugstack.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Author: chs
 * Description:
 * CreateTime: 2024-07-14
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class LogicTreeTest {

    @Resource
    private DefaultTreeFactory defaultTreeFactory;

    /**
     * rule_lock --左--> rule_luck_award
     *           --右--> rule_stock --右--> rule_luck_award
     */
    @Test
    public void test_tree_rule(){
        // 构建参数
        String treeId = "1000000001";
        RuleTreeNodeLineVO nodeLine1 = RuleTreeNodeLineVO.builder()
                .treeId(treeId)
                .ruleNodeFrom("rule_lock")
                .ruleNodeTo("rule_luck_award")
                .ruleLimitType(RuleLimitTypeVO.EQUAL)
                .ruleLimitValue(RuleLogicCheckTypeVO.TAKE_OVER)
                .build();
        RuleTreeNodeLineVO nodeLine2 = RuleTreeNodeLineVO.builder()
                .treeId(treeId)
                .ruleNodeFrom("rule_lock")
                .ruleNodeTo("rule_stock")
                .ruleLimitType(RuleLimitTypeVO.EQUAL)
                .ruleLimitValue(RuleLogicCheckTypeVO.ALLOW)
                .build();

        RuleTreeNodeVO rule_lock = RuleTreeNodeVO.builder()
                .treeId(treeId)
                .ruleKey("rule_lock")
                .ruleDesc("限定用户已完成N次抽奖后解锁")
                .ruleValue("1")
                .treeNodeLineVOList(new ArrayList<RuleTreeNodeLineVO>(){{
                    add(nodeLine1);
                    add(nodeLine2);
                }})
                .build();
        RuleTreeNodeVO rule_luck_award = RuleTreeNodeVO.builder()
                .treeId(treeId)
                .ruleKey("rule_luck_award")
                .ruleDesc("限定用户已完成N次抽奖后解锁")
                .ruleValue("1")
                .treeNodeLineVOList(null)
                .build();
        RuleTreeNodeVO rule_stock = RuleTreeNodeVO.builder()
                .treeId(treeId)
                .ruleKey("rule_stock")
                .ruleDesc("库存处理规则")
                .ruleValue(null)
                .treeNodeLineVOList(new ArrayList<RuleTreeNodeLineVO>(){{
                    add(RuleTreeNodeLineVO.builder()
                            .treeId(treeId)
                            .ruleNodeFrom("rule_stock")
                            .ruleNodeTo("rule_luck_award")
                            .ruleLimitType(RuleLimitTypeVO.EQUAL)
                            .ruleLimitValue(RuleLogicCheckTypeVO.TAKE_OVER)
                            .build());
                }})
                .build();

        RuleTreeVO ruleTreeVO = new RuleTreeVO();
        ruleTreeVO.setTreeId(treeId);
        ruleTreeVO.setTreeName("决策树规则：增加dall-e-3画图模型");
        ruleTreeVO.setTreeDesc("决策树规则：增加dall-e-3画图模型");
        ruleTreeVO.setTreeRootRuleNode("rule_lock");

        ruleTreeVO.setTreeNodeMap(new HashMap<String, RuleTreeNodeVO>(){{
            put("rule_lock", rule_lock);
            put("rule_stock", rule_stock);
            put("rule_luck_award", rule_luck_award);
        }});

        IDecisionTreeEngine treeEngine = defaultTreeFactory.openLogicTree(ruleTreeVO);
        DefaultTreeFactory.StrategyAwardVO data = treeEngine.process("chs", 100001L, 100);
        log.info("测试结果：{}", JSON.toJSONString(data));
    }
}