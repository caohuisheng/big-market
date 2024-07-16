package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.RuleTree;
import org.apache.ibatis.annotations.Mapper;

/**
 * Author: chs
 * Description: 规则树Dao
 * CreateTime: 2024-07-16
 */
@Mapper
public interface RuleTreeDao {

    RuleTree queryRuleTreeByTreeId(String treeId);

}
