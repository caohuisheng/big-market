package cn.bugstack.domain.activity.service;

import cn.bugstack.domain.activity.model.valobj.ActivitySkuStockKeyVO;

/**
 * Author: chs
 * Description: 活动sku库存处理接口
 * CreateTime: 2024-08-02
 */
public interface IRaffleActivitySkuStockService {

    /**
     * 获取sku库存消耗队列
     * @return 奖品库存key信息
     * @throws InterruptedException
     */
    ActivitySkuStockKeyVO takeQueueValue() throws InterruptedException;

    /**
     * 清空延迟队列
     */
    void clearQueueValue();

    /**
     * 延迟队列 + 任务趋势更新活动库存
     * @param sku 活动商品
     */
    void updateActivitySkuStock(Long sku);

    /**
     * 缓存库存以消耗完毕，清空数据库库存
     * @param sku 活动商品
     */
    void clearActivitySkuStock(Long sku);
}
