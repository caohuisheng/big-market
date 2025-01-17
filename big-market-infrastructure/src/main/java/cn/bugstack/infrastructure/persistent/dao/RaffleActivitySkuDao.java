package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.RaffleActivitySku;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Author: chs
 * Description: 商品sku Dao
 * CreateTime: 2024-07-29
 */
@Mapper
public interface RaffleActivitySkuDao {

    RaffleActivitySku queryActivitySku(Long sku);

    List<RaffleActivitySku> queryActivitySkuByActivityId(Long activityId);

    void updateActivitySkuStock(Long sku);

    void clearActivitySkuStock(Long sku);

}
