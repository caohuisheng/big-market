package cn.bugstack.test.domain;

import cn.bugstack.domain.strategy.service.armory.IStrategyArmory;
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

    @Test
    public void test_strategyArmory(){
        boolean res = strategyArmory.assembleLotteryStrategy(100001L);
        log.info("装配结果：{}",res);
    }

    @Test
    public void test_randomAwardId(){
        for(int i=0;i<10;i++){
            Integer randomAwardId = strategyArmory.getRandomAwardId(100001L);
            log.info("award_id:{}",randomAwardId);
        }
    }
}
