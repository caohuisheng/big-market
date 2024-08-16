package cn.bugstack.trigger.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: chs
 * Description: 用户活动账户响应对象
 * CreateTime: 2024-08-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityAccountResponseDTO {

    //总次数
    private Integer totalCount;
    //总次数 - 剩余
    private Integer totalCountSurplus;
    //月次数
    private Integer monthCount;
    //月次数 - 剩余
    private Integer monthCountSurplus;
    //日次数
    private Integer dayCount;
    //日次数 - 剩余
    private Integer dayCountSurplus;

}
