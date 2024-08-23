package cn.bugstack.trigger.api;

import cn.bugstack.trigger.api.dto.*;
import cn.bugstack.types.model.Response;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

/**
 * Author: chs
 * Description: 抽奖活动服务
 * CreateTime: 2024-08-08
 */
public interface IRaffleActivityService {

    /**
     * 活动装配，数据预热缓存
     * @param activityId 活动ID
     * @return 装配结果
     */
    Response<Boolean> armory(Long activityId);

    /**
     * 活动抽奖
     * @param requestDTO 请求对象
     * @return 抽奖结果
     */
    Response<ActivityDrawResponseDTO> draw(ActivityDrawRequestDTO requestDTO);

    /**
     * 日历签到返利接口
     * @param userId 用户id
     * @return 签到结果
     */
    Response<Boolean> calendarSignRebate(String userId);

    /**
     * 判断是否完成日历签到返利接口
     * @param userId 用户ID
     * @return 签到结果
     */
    Response<Boolean> isCalendarSignRebate(String userId);

    /**
     * 查询用户活动账户
     */
    Response<UserActivityAccountResponseDTO> queryUserActivityAccount(UserActivityAccountRequestDTO request);

    /**
     * 积分兑换商品
     * @param request
     * @return
     */
    Response<Boolean> creditPayExchangeSku(@RequestBody SkuProductShoppingCartRequestDTO request);

    /**
     * 根据活动id查询sku商品集合
     * @param activityId
     * @return
     */
    Response<List<SkuProductResponseDTO>> querySkuProductListByActivityId(Long activityId);

    /**
     * 查询用户积分账户
     * @param userId
     * @return
     */
    Response<BigDecimal> queryUserCreditAccount(String userId);
}
