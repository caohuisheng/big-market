package cn.bugstack.infrastructure.persistent.repository;

import cn.bugstack.domain.activity.model.aggregate.CreateOrderAggregate;
import cn.bugstack.domain.activity.model.entity.ActivityCountEntity;
import cn.bugstack.domain.activity.model.entity.ActivityEntity;
import cn.bugstack.domain.activity.model.entity.ActivityOrderEntity;
import cn.bugstack.domain.activity.model.entity.ActivitySkuEntity;
import cn.bugstack.domain.activity.repository.IActivityRepository;
import cn.bugstack.infrastructure.persistent.dao.*;
import cn.bugstack.infrastructure.persistent.po.*;
import cn.bugstack.infrastructure.persistent.redis.IRedisService;
import cn.bugstack.infrastructure.persistent.redis.RedissonService;
import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import cn.bugstack.types.common.Constants;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;

/**
 * Author: chs
 * Description: 活动仓储实现
 * CreateTime: 2024-07-29
 */
@Slf4j
@Repository
public class ActivityRepository implements IActivityRepository {

    @Resource
    private IRedisService redisService;
    @Resource
    private IDBRouterStrategy dbRouter;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private RaffleActivitySkuDao raffleActivitySkuDao;
    @Resource
    private RaffleActivityDao raffleActivityDao;
    @Resource
    private RaffleActivityCountDao raffleActivityCountDao;
    @Resource
    private RaffleActivityOrderDao raffleActivityOrderDao;
    @Resource
    private RaffleActivityAccountDao raffleActivityAccountDao;

    @Override
    public ActivitySkuEntity queryActivitySku(Long sku) {
        RaffleActivitySku raffleActivitySku = raffleActivitySkuDao.queryActivitySku(sku);
        ActivitySkuEntity activitySkuEntity = new ActivitySkuEntity();
        BeanUtils.copyProperties(raffleActivitySku, activitySkuEntity);
        return activitySkuEntity;
    }

    @Override
    public ActivityEntity queryRaffleActivityById(Long activityId) {
        //首先从缓存中查询
        String cacheKey = Constants.RedisKey.ACTIVITY_KEY + activityId;
        ActivityEntity activityEntity = redisService.getValue(cacheKey);
        if(activityEntity != null){
            return activityEntity;
        }
        //从数据库中查询, 并保存的缓存中
        RaffleActivity raffleActivity = raffleActivityDao.queryRaffleActivityById(activityId);
        activityEntity = new ActivityEntity();
        BeanUtils.copyProperties(raffleActivity, activityEntity);
        redisService.setValue(cacheKey, activityEntity);

        return activityEntity;
    }

    @Override
    public ActivityCountEntity queryRaffleActivityCountById(Long activityCountId) {
        //首先从缓存中查询
        String cacheKey = Constants.RedisKey.ACTIVITY_COUNT_KEY + activityCountId;
        ActivityCountEntity activityCountEntity = redisService.getValue(cacheKey);
        if(activityCountEntity != null){
            return activityCountEntity;
        }
        //从数据库中查询, 并保存的缓存中
        RaffleActivityCount raffleActivityCount = raffleActivityCountDao.queryRaffleActivityCountById(activityCountId);
        activityCountEntity = new ActivityCountEntity();
        BeanUtils.copyProperties(raffleActivityCount, activityCountEntity);
        redisService.setValue(cacheKey, activityCountEntity);
        return activityCountEntity;
    }

    @Override
    public void doSaveOrder(CreateOrderAggregate createOrderAggregate) {
        try {
            //订单对象
            ActivityOrderEntity activityOrderEntity = createOrderAggregate.getActivityOrderEntity();
            RaffleActivityOrder raffleActivityOrder = new RaffleActivityOrder();
            BeanUtils.copyProperties(activityOrderEntity, raffleActivityOrder);
            raffleActivityOrder.setState(activityOrderEntity.getState().getCode());
            //账户对象
            RaffleActivityAccount raffleActivityAccount = RaffleActivityAccount.builder()
                                .userId(createOrderAggregate.getUserId())
                                .activityId(createOrderAggregate.getActivityId())
                                .totalCount(createOrderAggregate.getTotalCount())
                                .totalCountSurplus(createOrderAggregate.getTotalCount())
                                .dayCount(createOrderAggregate.getDayCount())
                                .dayCountSurplus(createOrderAggregate.getDayCount())
                                .dayCountSurplus(createOrderAggregate.getDayCount())
                                .monthCount(createOrderAggregate.getMonthCount())
                                .monthCountSurplus(createOrderAggregate.getMonthCount())
                                .build();

            //以用户id为切分键，通过 dbRouter 设定路由
            dbRouter.doRouter(createOrderAggregate.getUserId());
            //编程式事务
            transactionTemplate.execute(status -> {
                try {
                    //1.写入订单
                    raffleActivityOrderDao.insert(raffleActivityOrder);
                    //2.更新账户
                    int count = raffleActivityAccountDao.updateAccountQuota(raffleActivityAccount);
                    //3.若更新返回0，则账户不存在，需要创建账户
                    if(0 == count){
                        raffleActivityAccountDao.insert(raffleActivityAccount);
                    }
                    return 1;
                } catch (Exception e) {
                    status.setRollbackOnly();
                    log.error("写入订单记录，唯一索引冲突 userId:{} activityId:{} sku:{}", activityOrderEntity.getUserId(),
                            activityOrderEntity.getActivityId(), activityOrderEntity.getSku());
                    throw new AppException(ResponseCode.INDEX_DUP.getCode());
                }
            });
        } finally {
            dbRouter.clear();
        }
    }
}
