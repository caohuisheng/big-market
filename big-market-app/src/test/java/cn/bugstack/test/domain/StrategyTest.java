package cn.bugstack.test.domain;

import cn.bugstack.domain.strategy.service.IRaffleStrategy;
import cn.bugstack.domain.strategy.service.armory.IStrategyArmory;
import cn.bugstack.domain.strategy.service.armory.IStrategyDispatch;
import cn.bugstack.infrastructure.persistent.dao.StrategyRuleDao;
import cn.bugstack.infrastructure.persistent.po.StrategyRule;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @Author: chs
 * @Description: 策略领域测试
 * @CreateTime: 2024-07-06
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class StrategyTest {

    @Resource
    IStrategyArmory strategyArmory;
    @Resource
    IStrategyDispatch strategyDispatch;

    @Test
    public void test_strategyArmory(){
        boolean res = strategyArmory.assembleLotteryStrategy(100001L);
        log.info("装配结果：{}",res);
    }

    @Test
    public void test_getRandomAwardId(){
        //for(int i=0;i<10;i++){
        //    Integer randomAwardId = strategyArmory.getRandomAwardId(100001L);
        //    log.info("award_id:{}",randomAwardId);
        //}
    }

    @Test
    public void test_strategyWeightArmory(){
        boolean res = strategyArmory.assembleLotteryStrategy(100001L);
        log.info("装配结果：{}",res);
    }

    /**
     * 根据策略id+权重值，从装配的结果中随机获取奖品id
     */
    @Test
    public void test_getRandomAwardIdWeight(){
        int[] weightValues = new int[]{4000,5000,6000};
        for(int weightValue:weightValues){
            Integer award_id = strategyDispatch.getRandomAwardId(100001L, ""+weightValue);
            log.info("测试结果：{} - weight_value:{}",award_id,weightValue);
        }
    }

    @Test
    public void test_performRaffle(){

    }
}
