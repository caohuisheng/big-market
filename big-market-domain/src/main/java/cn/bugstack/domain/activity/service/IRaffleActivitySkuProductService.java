package cn.bugstack.domain.activity.service;

import cn.bugstack.domain.activity.model.entity.SkuProductEntity;

import java.util.List;

/**
 * Author: chs
 * Description: sku商品服务接口
 * CreateTime: 2024-08-20
 */
public interface IRaffleActivitySkuProductService {

    List<SkuProductEntity> querySkuProductEntitiesByActivityId(Long ActivityId);

}
