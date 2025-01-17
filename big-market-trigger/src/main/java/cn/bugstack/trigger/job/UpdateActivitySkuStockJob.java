package cn.bugstack.trigger.job;

import cn.bugstack.domain.activity.model.valobj.ActivitySkuStockKeyVO;
import cn.bugstack.domain.activity.service.IRaffleActivitySkuStockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Author: chs
 * Description: 更新活动sku库存任务
 * CreateTime: 2024-08-03
 */
@Slf4j
@Component
public class UpdateActivitySkuStockJob {

    @Resource
    private IRaffleActivitySkuStockService skuStock;

    @Scheduled(cron = "0/15 * * * * ?")
    public void exec(){
        try {
            // log.info("定时任务，更新活动sku库存【延迟队列获取，降低对数据库的更新频次，不要产生竞争】");
            // 每次获取10个更新活动sku库存记录
            int count = 10;
            for (int i = 0; i < count; i++) {
                ActivitySkuStockKeyVO activitySkuStockKeyVO = skuStock.takeQueueValue();
                if(null == activitySkuStockKeyVO) return;
                log.info("定时任务，更新活动sku库存，sku:{} activityId:{}", activitySkuStockKeyVO.getSku(), activitySkuStockKeyVO.getActivityId());
                skuStock.updateActivitySkuStock(activitySkuStockKeyVO.getSku());
            }
        } catch (InterruptedException e) {
            log.error("定时任务，更新活动sku库存失败", e);
        }
    }
}
