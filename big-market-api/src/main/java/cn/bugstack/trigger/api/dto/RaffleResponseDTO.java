package cn.bugstack.trigger.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: chs
 * Description: 抽奖响应结果
 * CreateTime: 2024-07-19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaffleResponseDTO {

    // 奖品id
    private Integer awardId;
    // 奖品序号
    private Integer awardIndex;

}
