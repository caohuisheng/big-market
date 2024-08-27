package cn.bugstack.domain.activity.service.partake;

import cn.bugstack.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import cn.bugstack.domain.activity.model.entity.ActivityEntity;
import cn.bugstack.domain.activity.model.entity.PartakeRaffleActivityEntity;
import cn.bugstack.domain.activity.model.entity.UserRaffleOrderEntity;
import cn.bugstack.domain.activity.model.valobj.ActivityStateVO;
import cn.bugstack.domain.activity.model.valobj.UserRaffleOrderStateVO;
import cn.bugstack.domain.activity.repository.IActivityRepository;
import cn.bugstack.domain.activity.service.IRaffleActivityPartakeService;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * Author: chs
 * Description: 抽奖活动参加抽象类
 * CreateTime: 2024-08-04
 */
@Slf4j
public abstract class AbstractRaffleActivityPartake implements IRaffleActivityPartakeService {

    protected final IActivityRepository activityRepository;

    public AbstractRaffleActivityPartake(IActivityRepository activityRepository){
        this.activityRepository = activityRepository;
    }

    @Override
    public UserRaffleOrderEntity createOrder(String userId, Long activityId) {
        //0.基础信息
        Date currentDate = new Date();

        //1.活动查询
        ActivityEntity activityEntity = activityRepository.queryRaffleActivityById(activityId);

        //校验活动状态
        if(!ActivityStateVO.open.equals(activityEntity.getState())){
            throw new AppException(ResponseCode.ACTIVITY_STATE_ERROR.getCode(), ResponseCode.ACTIVITY_STATE_ERROR.getInfo());
        }
        //校验活动日期
        if(activityEntity.getBeginDateTime().after(currentDate) || activityEntity.getEndDateTime().before(currentDate)){
            throw new AppException(ResponseCode.ACTIVITY_DATE_ERROR.getCode(), ResponseCode.ACTIVITY_DATE_ERROR.getInfo());
        }

        //2.查询未被使用的活动参与订单记录
        UserRaffleOrderEntity userRaffleOrderEntity = activityRepository.queryNoUsedRaffleOrder(userId, activityId);

        if(null != userRaffleOrderEntity){
            log.info("使用未被使用的参与活动订单 userId:{} activityId:{} userRaffleOrderEntity:{}",userId, activityId, JSON.toJSONString(userRaffleOrderEntity));
            userRaffleOrderEntity.setEndDatetime(activityEntity.getEndDateTime()); //设置活动结束时间
            return userRaffleOrderEntity;
        }

        //3.额度账户过滤，返回账户构建对象
        CreatePartakeOrderAggregate createPartakeOrderAggregate = this.doFilterAccount(userId, activityId, currentDate);

        //4.构建订单
        userRaffleOrderEntity = this.buildUserRaffleOrder(userId, activityId, currentDate);
        userRaffleOrderEntity.setEndDatetime(activityEntity.getEndDateTime()); //设置活动结束时间
        createPartakeOrderAggregate.setUserRaffleOrderEntity(userRaffleOrderEntity);

        //5.保存聚合对象（一个领域内的聚合是一个事务操作）
        activityRepository.saveCreatePartakeOrderAggregate(createPartakeOrderAggregate);

        //6.返回订单信息
        return userRaffleOrderEntity;
    }

    protected abstract CreatePartakeOrderAggregate doFilterAccount(String userId, Long activityId, Date currentDate);

    protected abstract UserRaffleOrderEntity buildUserRaffleOrder(String userId, Long activityId, Date currentDate);
}
