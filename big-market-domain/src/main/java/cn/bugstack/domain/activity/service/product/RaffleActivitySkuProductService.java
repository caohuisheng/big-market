package cn.bugstack.domain.activity.service.product;

import cn.bugstack.domain.activity.model.entity.SkuProductEntity;
import cn.bugstack.domain.activity.repository.IActivityRepository;
import cn.bugstack.domain.activity.service.IRaffleActivitySkuProductService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Author: chs
 * Description: sku商品服务
 * CreateTime: 2024-08-20
 */
@Service
public class RaffleActivitySkuProductService implements IRaffleActivitySkuProductService {

    @Resource
    private IActivityRepository activityRepository;

    @Override
    public List<SkuProductEntity> querySkuProductEntitiesByActivityId(Long activityId) {
        return activityRepository.querySkuProductEntitiesByActivityId(activityId);
    }
}
