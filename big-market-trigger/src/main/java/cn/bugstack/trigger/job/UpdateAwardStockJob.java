package cn.bugstack.trigger.job;

import cn.bugstack.domain.strategy.model.valobj.StrategyAwardStockKeyVO;
import cn.bugstack.domain.strategy.service.IRaffleStock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.util.concurrent.CountDownLatch;

/**
 * Author: chs
 * Description:更新奖品库存任务
 * CreateTime: 2024-07-17
 */
@Slf4j
@Component
public class UpdateAwardStockJob {

    @Resource
    private IRaffleStock raffleStock;

    @Scheduled(cron = "0/10 * * * * ?")
    public void exec(){
        try {
            log.info("定时任务，更新奖品库存【延迟队列获取，降低对数据库的更新频次，避免产生竞争】");
            StrategyAwardStockKeyVO strategyAwardStockKeyVO = raffleStock.takeQueueValue();
            if(null == strategyAwardStockKeyVO){
                return;
            }

            Long strategyId = strategyAwardStockKeyVO.getStrategyId();
            Integer awardId = strategyAwardStockKeyVO.getAwardId();
            log.info("定时任务，更新奖品库存 strategyId:{}, awardId:{}", strategyId, awardId);
            raffleStock.updateStrategyAwardStock(strategyId, awardId);
        } catch (Exception e) {
            log.error("定时任务，更新奖品库存失败", e);
        }
    }

}
