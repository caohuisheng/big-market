package cn.bugstack.trigger.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: chs
 * Description: 抽奖请求参数
 * CreateTime: 2024-07-19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaffleRequestDTO {

    // 抽奖策略id
    Long strategyId;

}
