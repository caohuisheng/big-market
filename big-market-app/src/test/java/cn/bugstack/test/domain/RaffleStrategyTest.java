package cn.bugstack.test.domain;

import cn.bugstack.domain.strategy.model.entity.RaffleAwardEntity;
import cn.bugstack.domain.strategy.model.entity.RaffleFactorEntity;
import cn.bugstack.domain.strategy.service.IRaffleStrategy;
import cn.bugstack.domain.strategy.service.armory.IStrategyArmory;
import cn.bugstack.domain.strategy.service.rule.impl.RuleLockLogicFilter;
import cn.bugstack.domain.strategy.service.rule.impl.RuleWeightLogicFilter;
import cn.bugstack.infrastructure.persistent.dao.StrategyDao;
import cn.bugstack.infrastructure.persistent.dao.StrategyRuleDao;
import cn.bugstack.infrastructure.persistent.po.StrategyRule;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: chs
 * @Description: 抽奖策略测试
 * @CreateTime: 2024-07-07
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RaffleStrategyTest {

    @Resource
    IRaffleStrategy raffleStrategy;
    @Resource
    RuleWeightLogicFilter ruleWeightLogicFilter;
    @Resource
    StrategyRuleDao strategyRuleDao;

    @Resource
    IStrategyArmory strategyArmory;
    @Resource
    RuleLockLogicFilter ruleLockLogicFilter;

    @Before
    public void setUp(){
        //ReflectionTestUtils.setField(ruleWeightLogicFilter,"userScore",4500L);
        //策略装配 100001、100001、100003
        log.info("测试结果：{}",strategyArmory.assembleLotteryStrategy(100001L));
        log.info("测试结果：{}",strategyArmory.assembleLotteryStrategy(100002L));
        log.info("测试结果：{}",strategyArmory.assembleLotteryStrategy(100003L));
        //通过反射mock规则中的值
        ReflectionTestUtils.setField(ruleWeightLogicFilter, "userScore",40500L);
        ReflectionTestUtils.setField(ruleLockLogicFilter,"userRaffleCount",0L);
    }

    @Test
    public void test1(){

    }

    @Test
    public void test_raffle_center_rule_lock(){
        RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder()
                .userId("chs")
                .strategyId(100003L)
                .build();
        RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(raffleFactorEntity);
        log.info("请求参数：{}",JSON.toJSONString(raffleFactorEntity));
        log.info("测试结果：{}",JSON.toJSONString(raffleAwardEntity));
    }

    @Test
    public void test_performRaffle(){
        RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder()
                .userId("chs")
                .strategyId(100001L)
                .build();

        RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(raffleFactorEntity);

        log.info("请求参数：{}", JSON.toJSONString(raffleFactorEntity));
        log.info("测试结果：{}", JSON.toJSONString(raffleAwardEntity));
    }

    @Test
    public void test_performRaffle_blacklist() {
        RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder()
                .userId("user003")  // 黑名单用户 user001,user002,user003
                .strategyId(100001L)
                .build();

        RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(raffleFactorEntity);

        log.info("请求参数：{}", JSON.toJSONString(raffleFactorEntity));
        log.info("测试结果：{}", JSON.toJSONString(raffleAwardEntity));
    }

    @Test
    public void test(){
        List<StrategyRule> strategyRules = strategyRuleDao.queryStrategyRuleList();
        log.info("strategyRules:{}",strategyRules);
        StrategyRule strategyRule = strategyRuleDao.queryStrategyRule(100001L, "rule_weight");
        log.info("strategyRule:{}",strategyRule);
        String ruleValue = strategyRuleDao.queryStrategyRuleValue(100001L, null, "rule_weight");
        log.info("ruleValue:{}",ruleValue);
    }
}
