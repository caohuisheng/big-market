package cn.bugstack.domain.activity.service;

import cn.bugstack.domain.activity.model.entity.DeliveryOrderEntity;
import cn.bugstack.domain.activity.model.entity.SkuRechargeEntity;
import cn.bugstack.domain.activity.model.entity.UnpaidActivityOrderEntity;

/**
 * 抽奖活动订单接口
 */
public interface IRaffleActivityAccountQuotaService {

    /**
     * 创建 sku 账户充值订单，给用户增加抽奖次数
     * 1.在【打卡、签到、分享、对话、积分兑换】等行为动作下，创建出活动订单，给用户的活动账户【日、月】充值可用的抽奖次数
     * 2.对于用户可获得的抽奖次数，比如首次进来就有一次，则是依赖于运营配置的动作
     * @param skuRechargeEntity 活动商品充值实体对象
     * @return 活动ID
     */
    //String createOrder(SkuRechargeEntity skuRechargeEntity);
    UnpaidActivityOrderEntity createOrder(SkuRechargeEntity skuRechargeEntity);

    /**
     * 查询活动账户 - 日参与次数
     * @param activityId 活动id
     * @param userId 用户id
     * @return 日参与次数
     */
    Integer queryRaffleActivityAccountDayPartakeCount(String userId, Long activityId);

    /**
     * 查询活动账户 - 总参与次数
     * @param userId 用户id
     * @param activityId 活动id
     * @return 参与次数
     */
    Integer queryRaffleActivityAccountPartakeCount(String userId, Long activityId);

    /**
     * 订单出货 - 积分充值
     * @param deliveryOrderEntity 出货实体对象
     */
    void updateOrder(DeliveryOrderEntity deliveryOrderEntity);
}
