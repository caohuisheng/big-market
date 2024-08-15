package cn.bugstack.trigger.api;

import cn.bugstack.trigger.api.dto.ActivityDrawRequestDTO;
import cn.bugstack.trigger.api.dto.ActivityDrawResponseDTO;
import cn.bugstack.types.model.Response;
import org.springframework.web.bind.annotation.RequestParam;

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

}
