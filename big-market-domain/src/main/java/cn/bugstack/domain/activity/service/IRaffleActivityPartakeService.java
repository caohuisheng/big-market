package cn.bugstack.domain.activity.service;

import cn.bugstack.domain.activity.model.entity.PartakeRaffleActivityEntity;
import cn.bugstack.domain.activity.model.entity.UserRaffleOrderEntity;

/**
 * Author: chs
 * Description: 抽奖活动参与服务
 * CreateTime: 2024-08-04
 */
public interface IRaffleActivityPartakeService {

    /**
     * 创建抽奖单：用于参与抽奖活动，扣减活动账户库存，产生抽奖单，入存在未被使用的抽奖单则直接返回已存在的抽奖单
     * @param partakeRaffleActivityEntity
     * @return
     */
    UserRaffleOrderEntity createOrder(PartakeRaffleActivityEntity partakeRaffleActivityEntity);
}
