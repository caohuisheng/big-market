package cn.bugstack.infrastructure.persistent.repository;

import cn.bugstack.domain.activity.event.ActivitySkuStockZeroMessageEvent;
import cn.bugstack.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import cn.bugstack.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import cn.bugstack.domain.activity.model.entity.*;
import cn.bugstack.domain.activity.model.valobj.ActivitySkuStockKeyVO;
import cn.bugstack.domain.activity.model.valobj.ActivityStateVO;
import cn.bugstack.domain.activity.model.valobj.UserRaffleOrderStateVO;
import cn.bugstack.domain.activity.repository.IActivityRepository;
import cn.bugstack.infrastructure.event.EventPublisher;
import cn.bugstack.infrastructure.persistent.dao.*;
import cn.bugstack.infrastructure.persistent.po.*;
import cn.bugstack.infrastructure.persistent.redis.IRedisService;
import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import cn.bugstack.types.common.Constants;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    private EventPublisher eventPublisher;

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
    @Resource
    private ActivitySkuStockZeroMessageEvent activitySkuStockZeroMessageEvent;
    @Resource
    private UserRaffleOrderDao userRaffleOrderDao;
    @Resource
    private RaffleActivityAccountMonthDao raffleActivityAccountMonthDao;
    @Resource
    private RaffleActivityAccountDayDao raffleActivityAccountDayDao;

    @Override
    public ActivitySkuEntity queryActivitySku(Long sku) {
        RaffleActivitySku raffleActivitySku = raffleActivitySkuDao.queryActivitySku(sku);
        ActivitySkuEntity activitySkuEntity = new ActivitySkuEntity();
        BeanUtils.copyProperties(raffleActivitySku, activitySkuEntity);
        return activitySkuEntity;
    }

    @Override
    public List<ActivitySkuEntity> queryActivitySkuByActivityId(Long activityId) {
        List<RaffleActivitySku> raffleActivitySkus = raffleActivitySkuDao.queryActivitySkuByActivityId(activityId);
        List<ActivitySkuEntity> activitySkuEntities = raffleActivitySkus.stream().map(raffleActivitySku -> {
            ActivitySkuEntity activitySkuEntity = new ActivitySkuEntity();
            BeanUtils.copyProperties(raffleActivitySku, activitySkuEntity);
            return activitySkuEntity;
        }).collect(Collectors.toList());
        return activitySkuEntities;
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
        activityEntity.setState(ActivityStateVO.valueOf(raffleActivity.getState()));
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
    public void doSaveOrder(CreateQuotaOrderAggregate createOrderAggregate) {
        try {
            String userId = createOrderAggregate.getUserId();
            Long activityId = createOrderAggregate.getActivityId();
            Integer totalCount = createOrderAggregate.getTotalCount();
            Integer monthCount = createOrderAggregate.getMonthCount();
            Integer dayCount = createOrderAggregate.getDayCount();
            //订单对象
            ActivityOrderEntity activityOrderEntity = createOrderAggregate.getActivityOrderEntity();
            RaffleActivityOrder raffleActivityOrder = new RaffleActivityOrder();
            BeanUtils.copyProperties(activityOrderEntity, raffleActivityOrder);
            raffleActivityOrder.setState(activityOrderEntity.getState().getCode());
            //账户对象 - 总
            RaffleActivityAccount raffleActivityAccount = RaffleActivityAccount.builder()
                                .userId(userId)
                                .activityId(activityId)
                                .totalCount(totalCount)
                                .totalCountSurplus(totalCount)
                                .dayCount(dayCount)
                                .dayCountSurplus(dayCount)
                                .monthCount(monthCount)
                                .monthCountSurplus(monthCount)
                                .build();

            //账户对象 - 月
            RaffleActivityAccountMonth raffleActivityAccountMonth = RaffleActivityAccountMonth.builder()
                    .userId(userId)
                    .activityId(activityId)
                    .month(RaffleActivityAccountMonth.currentMonth())
                    .monthCount(monthCount)
                    .monthCountSurplus(monthCount)
                    .build();

            //账户对象 - 日
            RaffleActivityAccountDay raffleActivityAccountDay = RaffleActivityAccountDay.builder()
                    .userId(userId)
                    .activityId(activityId)
                    .day(RaffleActivityAccountDay.currentDay())
                    .dayCount(dayCount)
                    .dayCountSurplus(dayCount)
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
                    //4.更新账户 - 月、日
                    raffleActivityAccountMonthDao.addAccountQuota(raffleActivityAccountMonth);
                    raffleActivityAccountDayDao.addAccountQuota(raffleActivityAccountDay);
                    return 1;
                } catch (Exception e) {
                    status.setRollbackOnly();
                    log.error("写入订单记录，唯一索引冲突 userId:{} activityId:{} sku:{}", activityOrderEntity.getUserId(),
                            activityOrderEntity.getActivityId(), activityOrderEntity.getSku(),e);
                    throw new AppException(ResponseCode.INDEX_DUP.getCode());
                }
            });
        } finally {
            dbRouter.clear();
        }
    }

    @Override
    public void doSaveCreditPayOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate) {
        ActivityOrderEntity activityOrderEntity = createQuotaOrderAggregate.getActivityOrderEntity();
        String userId = createQuotaOrderAggregate.getUserId();
        Long activityId = createQuotaOrderAggregate.getActivityId();
        Long sku = activityOrderEntity.getSku();
        //填充抽奖活动订单
        RaffleActivityOrder raffleActivityOrder = new RaffleActivityOrder();
        BeanUtils.copyProperties(activityOrderEntity, raffleActivityOrder);
        raffleActivityOrder.setState(activityOrderEntity.getState().getCode());

        try {
            dbRouter.doRouter(createQuotaOrderAggregate.getUserId());
            transactionTemplate.execute(status -> {
                try {
                    raffleActivityOrderDao.insert(raffleActivityOrder);
                } catch (DuplicateKeyException e) {
                    log.error("添加订单记录异常，唯一索引冲突 userId:{} activityId:{} sku:{}", userId, activityId, sku);
                    status.setRollbackOnly();
                }
                return 1;
            });
        } finally {
            dbRouter.clear();
        }
    }

    @Override
    public void doSaveNoPayOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate) {
        String userId = createQuotaOrderAggregate.getUserId();
        Long activityId = createQuotaOrderAggregate.getActivityId();
        Integer totalCount = createQuotaOrderAggregate.getTotalCount();
        Integer monthCount = createQuotaOrderAggregate.getMonthCount();
        Integer dayCount = createQuotaOrderAggregate.getDayCount();
        //活动账户锁
        RLock lock = redisService.getLock(Constants.RedisKey.ACTIVITY_ACCOUNT_LOCK + userId + Constants.UNDERLINE + activityId);
        try {
            lock.lock();
            //订单对象
            ActivityOrderEntity activityOrderEntity = createQuotaOrderAggregate.getActivityOrderEntity();
            RaffleActivityOrder raffleActivityOrder = new RaffleActivityOrder();
            BeanUtils.copyProperties(activityOrderEntity, raffleActivityOrder);
            raffleActivityOrder.setState(activityOrderEntity.getState().getCode());
            //账户对象 - 总
            RaffleActivityAccount raffleActivityAccount = RaffleActivityAccount.builder()
                    .userId(userId)
                    .activityId(activityId)
                    .totalCount(totalCount)
                    .totalCountSurplus(totalCount)
                    .dayCount(dayCount)
                    .dayCountSurplus(dayCount)
                    .monthCount(monthCount)
                    .monthCountSurplus(monthCount)
                    .build();

            //账户对象 - 月
            RaffleActivityAccountMonth raffleActivityAccountMonth = RaffleActivityAccountMonth.builder()
                    .userId(userId)
                    .activityId(activityId)
                    .month(RaffleActivityAccountMonth.currentMonth())
                    .monthCount(monthCount)
                    .monthCountSurplus(monthCount)
                    .build();

            //账户对象 - 日
            RaffleActivityAccountDay raffleActivityAccountDay = RaffleActivityAccountDay.builder()
                    .userId(userId)
                    .activityId(activityId)
                    .day(RaffleActivityAccountDay.currentDay())
                    .dayCount(dayCount)
                    .dayCountSurplus(dayCount)
                    .build();

            //以用户id为切分键，通过 dbRouter 设定路由
            dbRouter.doRouter(createQuotaOrderAggregate.getUserId());
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
                    //4.更新账户 - 月、日
                    raffleActivityAccountMonthDao.addAccountQuota(raffleActivityAccountMonth);
                    raffleActivityAccountDayDao.addAccountQuota(raffleActivityAccountDay);
                    return 1;
                } catch (Exception e) {
                    status.setRollbackOnly();
                    log.error("写入订单记录，唯一索引冲突 userId:{} activityId:{} sku:{}", activityOrderEntity.getUserId(),
                            activityOrderEntity.getActivityId(), activityOrderEntity.getSku(),e);
                    throw new AppException(ResponseCode.INDEX_DUP.getCode());
                }
            });
        } finally {
            lock.unlock();
            dbRouter.clear();
        }
    }

    @Override
    public void cacheActivitySkuStockCount(String cacheKey, Integer stockCount) {
        if(redisService.isExists(cacheKey)){
            return;
        }
        redisService.setAtomicLong(cacheKey, stockCount);
    }

    @Override
    public boolean subtractionActivitySkuStock(Long sku, String cacheKey, Date endDate) {
        //尝试扣减活动sku库存
        long surplus = redisService.decr(cacheKey);
        if(surplus == 0){
            //库存消耗完后，发送消息，通知更新数据库库存
            eventPublisher.publish(activitySkuStockZeroMessageEvent.topic(), activitySkuStockZeroMessageEvent.buildEventMessage(sku));
            return true;
        }else if(surplus < 0){
            //库存小于0，恢复为0
            redisService.setAtomicLong(cacheKey, 0);
            return false;
        }

        //加锁为了兜底，如果后续恢复库存、手动处理等，也不会超卖，因为所有可用库存key都被加锁
        //设置加锁时间为活动截止时间+延迟1天
        String lockKey = cacheKey + Constants.UNDERLINE + surplus;
        long expireMills = endDate.getTime() - System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1);
        Boolean status = redisService.setNx(lockKey, expireMills, TimeUnit.MILLISECONDS);

        return status;
    }

    @Override
    public void activitySkuStockConsumeSendQueue(ActivitySkuStockKeyVO activitySkuStockKeyVO) {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_COUNT_QUERY_KEY;
        RBlockingQueue<ActivitySkuStockKeyVO> blockingQueue = redisService.getBlockingQueue(cacheKey);
        RDelayedQueue<ActivitySkuStockKeyVO> delayedQueue = redisService.getDelayedQueue(blockingQueue);
        delayedQueue.offer(activitySkuStockKeyVO, 3, TimeUnit.SECONDS);
    }

    @Override
    public ActivitySkuStockKeyVO takeQueueValue() {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_COUNT_QUERY_KEY;
        RBlockingQueue<ActivitySkuStockKeyVO> destinationQueue = redisService.getBlockingQueue(cacheKey);
        return destinationQueue.poll();
    }

    @Override
    public void clearQueueValue() {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_COUNT_QUERY_KEY;
        RBlockingQueue<Object> destinationQueue = redisService.getBlockingQueue(cacheKey);
        destinationQueue.clear();
    }

    @Override
    public void updateActivitySkuStock(Long sku) {
        raffleActivitySkuDao.updateActivitySkuStock(sku);
    }

    @Override
    public void clearActivitySkuStock(Long sku) {
        raffleActivitySkuDao.clearActivitySkuStock(sku);
    }

    @Override
    public UserRaffleOrderEntity queryNoUsedRaffleOrder(String userId, Long activityId) {
        UserRaffleOrder userRaffleOrderReq = UserRaffleOrder.builder()
                .userId(userId)
                .activityId(activityId)
                .build();
        UserRaffleOrder userRaffleOrder = userRaffleOrderDao.queryNoUsedRaffleOrder(userRaffleOrderReq);
        //如果未空，直接返回
        if(null == userRaffleOrder){
            return null;
        }

        UserRaffleOrderEntity userRaffleOrderEntity = new UserRaffleOrderEntity();
        BeanUtils.copyProperties(userRaffleOrder, userRaffleOrderEntity);
        userRaffleOrderEntity.setOrderState(UserRaffleOrderStateVO.valueOf(userRaffleOrder.getOrderState()));
        return userRaffleOrderEntity;
    }

    @Override
    public ActivityAccountEntity queryActivityAccountByUserId(String userId, Long activityId) {
        RaffleActivityAccount raffleActivityAccountReq = RaffleActivityAccount.builder()
                .userId(userId)
                .activityId(activityId)
                .build();
        RaffleActivityAccount raffleActivityAccount = raffleActivityAccountDao.queryActivityAccountByUserId(raffleActivityAccountReq);
        if(null == raffleActivityAccount) return null;

        ActivityAccountEntity activityAccountEntity = new ActivityAccountEntity();
        BeanUtils.copyProperties(raffleActivityAccount, activityAccountEntity);
        return activityAccountEntity;
    }

    @Override
    public ActivityAccountMonthEntity queryActivityAccountMonthByUserId(String userId, Long activityId, String month) {

        RaffleActivityAccountMonth raffleActivityAccountMonth = raffleActivityAccountMonthDao.queryActivityAccountMonthByUserId(RaffleActivityAccountMonth.builder()
                        .userId(userId)
                        .activityId(activityId)
                        .month(month).build());
        if(null == raffleActivityAccountMonth) return null;

        ActivityAccountMonthEntity activityAccountMonthEntity = new ActivityAccountMonthEntity();
        BeanUtils.copyProperties(raffleActivityAccountMonth, activityAccountMonthEntity);
        return activityAccountMonthEntity;
    }

    @Override
    public ActivityAccountDayEntity queryActivityAccountDayByUserId(String userId, Long activityId, String day) {
        RaffleActivityAccountDay raffleActivityAccountDayReq = RaffleActivityAccountDay.builder()
                .userId(userId)
                .activityId(activityId)
                .day(day).build();
        RaffleActivityAccountDay raffleActivityAccountDay = raffleActivityAccountDayDao.queryActivityAccountDayByUserId(raffleActivityAccountDayReq);
        if(null == raffleActivityAccountDay) return null;

        ActivityAccountDayEntity activityAccountDayEntity = new ActivityAccountDayEntity();
        BeanUtils.copyProperties(raffleActivityAccountDay, activityAccountDayEntity);
        return activityAccountDayEntity;
    }

    @Override
    public void saveCreatePartakeOrderAggregate(CreatePartakeOrderAggregate createPartakeOrderAggregate) {
        String userId = createPartakeOrderAggregate.getUserId();
        Long activityId = createPartakeOrderAggregate.getActivityId();
        ActivityAccountEntity activityAccountEntity = createPartakeOrderAggregate.getActivityAccountEntity();
        ActivityAccountMonthEntity activityAccountMonthEntity = createPartakeOrderAggregate.getActivityAccountMonthEntity();
        ActivityAccountDayEntity activityAccountDayEntity = createPartakeOrderAggregate.getActivityAccountDayEntity();
        UserRaffleOrderEntity userRaffleOrderEntity = createPartakeOrderAggregate.getUserRaffleOrderEntity();

        try {
            dbRouter.doRouter(userId);
            transactionTemplate.execute(status -> {
                try {
                    //1.更新总账户
                    int totalCount = raffleActivityAccountDao.updateActivityAccountSubtractionQuota(
                            RaffleActivityAccount.builder()
                                    .userId(userId)
                                    .activityId(activityId)
                                    .build());
                    if(1 != totalCount){
                        status.setRollbackOnly();
                        log.warn("写入创建参与活动记录，更新总账户额度不足，异常 userId:{} activityId:{}", userId, activityId);
                        throw new AppException(ResponseCode.ACCOUNT_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_QUOTA_ERROR.getInfo());
                    }

                    //2.创建或更新月账户
                    if(createPartakeOrderAggregate.isExistAccountMonth()){
                        //存在当月的月账户，直接扣减账户
                        int updateMonthCount = raffleActivityAccountMonthDao.updateActivityAccountMonth(userId, activityId, activityAccountMonthEntity.getMonth());
                        if(1 != updateMonthCount){
                            status.setRollbackOnly();
                            log.warn("写入创建参与活动记录，更新月账户额度不足，异常 userId:{} activityId:{} month:{}", userId, activityId, activityAccountMonthEntity.getMonth());
                            throw new AppException(ResponseCode.ACCOUNT_QUOTA_MONTH_ERROR.getCode(), ResponseCode.ACCOUNT_QUOTA_MONTH_ERROR.getInfo());
                        }
                        raffleActivityAccountDao.updateActivityAccountMonthSubtractionQuota(RaffleActivityAccount.builder()
                                .userId(userId)
                                .activityId(activityId).build());
                    }else{
                        //不存在当月的月账户，则创建月账户
                        RaffleActivityAccountMonth raffleActivityAccountMonth = new RaffleActivityAccountMonth();
                        BeanUtils.copyProperties(activityAccountMonthEntity, raffleActivityAccountMonth);
                        raffleActivityAccountMonth.setMonthCountSurplus(raffleActivityAccountMonth.getMonthCountSurplus() - 1);
                        raffleActivityAccountMonthDao.insertActivityAccountMonth(raffleActivityAccountMonth);
                        //新创建月账户，则更新总账户表中月镜像额度
                        raffleActivityAccountDao.updateActivityAccountMonthSurplusImageQuota(
                                RaffleActivityAccount.builder()
                                        .userId(userId)
                                        .activityId(activityId)
                                        .monthCountSurplus(activityAccountEntity.getMonthCountSurplus())
                                        .build());
                    }

                    //3.创建或更新日账户
                    if(createPartakeOrderAggregate.isExistAccountDay()){
                        int updateDayCount = raffleActivityAccountDayDao.updateActivityAccountDay(userId, activityId, activityAccountDayEntity.getDay());
                        if(1 != updateDayCount){
                            status.setRollbackOnly();
                            log.warn("写入创建参与活动记录，更新日账户额度不足，异常 userId:{} activityId:{} day:{}", userId, activityId, activityAccountDayEntity.getDay());
                            throw new AppException(ResponseCode.ACCOUNT_QUOTA_DAY_ERROR.getCode(), ResponseCode.ACCOUNT_QUOTA_DAY_ERROR.getInfo());
                        }
                        raffleActivityAccountDao.updateActivityAccountDaySubtractionQuota(RaffleActivityAccount.builder()
                                .userId(userId)
                                .activityId(activityId).build());
                    }else{
                        RaffleActivityAccountDay raffleActivityAccountDay = new RaffleActivityAccountDay();
                        BeanUtils.copyProperties(activityAccountDayEntity, raffleActivityAccountDay);
                        raffleActivityAccountDay.setDayCountSurplus(raffleActivityAccountDay.getDayCountSurplus() - 1);
                        raffleActivityAccountDayDao.insertActivityAccountDay(raffleActivityAccountDay);
                        //新创建月账户，则更新总账户表中日镜像额度
                        raffleActivityAccountDao.updateActivityAccountDaySurplusImageQuota(
                                RaffleActivityAccount.builder()
                                        .userId(userId)
                                        .activityId(activityId)
                                        .dayCountSurplus(activityAccountEntity.getDayCountSurplus())
                                        .build()
                        );
                    }

                    //4.写入参与活动订单
                    UserRaffleOrder userRaffleOrder = new UserRaffleOrder();
                    BeanUtils.copyProperties(userRaffleOrderEntity, userRaffleOrder);
                    userRaffleOrder.setOrderState(userRaffleOrderEntity.getOrderState().getCode());
                    userRaffleOrderDao.insert(userRaffleOrder);

                    return 1;
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("写入创建参与活动记录，唯一索引冲突 userId:{} activityId:{}", userId, activityId, e);
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(), e);
                }
            });
        } finally {
            dbRouter.clear();
        }
    }

    @Override
    public void updateOrder(DeliveryOrderEntity deliveryOrderEntity) {
        String userId = deliveryOrderEntity.getUserId();
        String outBusinessNo = deliveryOrderEntity.getOutBusinessNo();
        //获取账户更新锁
        RLock lock = redisService.getLock(Constants.RedisKey.ACTIVITY_ACCOUNT_UPDATE_LOCK + userId);
        try {
            lock.lock(3, TimeUnit.SECONDS);
            //查询抽奖活动订单
            RaffleActivityOrder raffleActivityOrderReq = new RaffleActivityOrder();
            raffleActivityOrderReq.setUserId(userId);
            raffleActivityOrderReq.setOutBusinessNo(outBusinessNo);
            RaffleActivityOrder raffleActivityOrder = raffleActivityOrderDao.queryRaffleActivityOrderByUserId(raffleActivityOrderReq);
            Long activityId = raffleActivityOrder.getActivityId();
            Integer totalCount = raffleActivityOrder.getTotalCount();
            Integer monthCount = raffleActivityOrder.getMonthCount();
            Integer dayCount = raffleActivityOrder.getDayCount();

            //账户对象 - 总
            RaffleActivityAccount raffleActivityAccount = RaffleActivityAccount.builder()
                    .userId(userId)
                    .activityId(activityId)
                    .totalCount(totalCount)
                    .totalCountSurplus(totalCount)
                    .monthCount(monthCount)
                    .monthCountSurplus(monthCount)
                    .dayCount(dayCount)
                    .dayCountSurplus(dayCount)
                    .build();

            //账户对象 - 月
            RaffleActivityAccountMonth raffleActivityAccountMonth = RaffleActivityAccountMonth.builder()
                    .userId(userId)
                    .activityId(activityId)
                    .month(RaffleActivityAccountMonth.currentMonth())
                    .monthCount(monthCount)
                    .monthCountSurplus(monthCount)
                    .build();

            //账户对象 - 日
            RaffleActivityAccountDay raffleActivityAccountDay = RaffleActivityAccountDay.builder()
                    .userId(userId)
                    .day(RaffleActivityAccountDay.currentDay())
                    .dayCount(dayCount)
                    .dayCountSurplus(dayCount)
                    .build();

            dbRouter.doRouter(userId);
            //编程式事务
            transactionTemplate.execute(status -> {
                try {
                    //1.更新订单状态
                    int count = raffleActivityOrderDao.updateOrderCompleted(raffleActivityOrderReq);
                    if(1 != count){
                        status.setRollbackOnly();
                        return 1;
                    }

                    //2.更新账户 - 总
                    count = raffleActivityAccountDao.updateAccountQuota(raffleActivityAccount);
                    if(1 != count){
                        raffleActivityAccountDao.insert(raffleActivityAccount);
                    }

                    //2.更新账户 - 月
                    raffleActivityAccountMonthDao.addAccountQuota(raffleActivityAccountMonth);

                    //2.更新账户 - 日
                    raffleActivityAccountDayDao.addAccountQuota(raffleActivityAccountDay);
                    return 1;
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("更新订单记录，唯一索引冲突 userId:{} outBusinessNo:{}", userId, outBusinessNo);
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(), e);
                }
            });
        } finally {
            dbRouter.clear();
            lock.unlock();
        }
    }

    @Override
    public Integer queryRaffleActivityAccountDayPartakeCount(Long activityId, String userId) {
        RaffleActivityAccountDay raffleActivityAccountDayReq = RaffleActivityAccountDay.builder()
                .userId(userId)
                .activityId(activityId)
                .day(RaffleActivityAccountDay.currentDay()).build();
        return raffleActivityAccountDayDao.queryRaffleActivityAccountDayPartakeCount(raffleActivityAccountDayReq);
    }

    @Override
    public ActivityAccountEntity queryActivityAccountEntity(String userId, Long activityId) {
        //查询总账户
        RaffleActivityAccount raffleActivityAccount = raffleActivityAccountDao.queryActivityAccountByUserId(RaffleActivityAccount.builder()
                .userId(userId)
                .activityId(activityId)
                .build());
        if(null == raffleActivityAccount){
            return ActivityAccountEntity.builder()
                    .userId(userId)
                    .activityId(activityId)
                    .totalCount(0)
                    .totalCountSurplus(0)
                    .monthCount(0)
                    .monthCountSurplus(0)
                    .dayCount(0)
                    .dayCountSurplus(0)
                    .build();
        }

        //查询月账户
        RaffleActivityAccountMonth raffleActivityAccountMonth = raffleActivityAccountMonthDao.queryActivityAccountMonthByUserId(RaffleActivityAccountMonth.builder()
                .userId(userId)
                .activityId(activityId)
                .month(RaffleActivityAccountMonth.currentMonth())
                .build());
        //查询日账户
        RaffleActivityAccountDay raffleActivityAccountDay = raffleActivityAccountDayDao.queryActivityAccountDayByUserId(RaffleActivityAccountDay.builder()
                        .userId(userId)
                        .activityId(activityId)
                        .day(RaffleActivityAccountDay.currentDay())
                        .build());

        //组装对象
        ActivityAccountEntity activityAccountEntity = new ActivityAccountEntity();
        activityAccountEntity.setUserId(userId);
        activityAccountEntity.setActivityId(activityId);
        activityAccountEntity.setTotalCount(raffleActivityAccount.getTotalCount());
        activityAccountEntity.setTotalCountSurplus(raffleActivityAccount.getTotalCountSurplus());

        //如果没有创建月账户，直接从总账户中获取月总额度填充
        if(null == raffleActivityAccountMonth){
            activityAccountEntity.setMonthCount(raffleActivityAccount.getMonthCount());
            activityAccountEntity.setMonthCountSurplus(raffleActivityAccount.getMonthCount());
        }else{
            activityAccountEntity.setMonthCount(raffleActivityAccountMonth.getMonthCount());
            activityAccountEntity.setMonthCountSurplus(raffleActivityAccountMonth.getMonthCountSurplus());
        }

        //如果没有创建日账户，直接从总账户中获取日总额度填充
        if(null == raffleActivityAccountMonth){
            activityAccountEntity.setDayCount(raffleActivityAccount.getDayCount());
            activityAccountEntity.setDayCountSurplus(raffleActivityAccount.getDayCount());
        }else{
            activityAccountEntity.setDayCount(raffleActivityAccountDay.getDayCount());
            activityAccountEntity.setDayCountSurplus(raffleActivityAccountDay.getDayCountSurplus());
        }

        return activityAccountEntity;
    }


}
