package cn.bugstack.domain.rebate.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: chs
 * Description: 用户行为返利值对象
 * CreateTime: 2024-08-13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyBehaviorRebateVO {

    //行为类型
    private String behaviorType;
    //返利描述
    private String rebateDesc;
    //返利类型（sku活动库存充值奖品、integral用户活动积分）
    private String rebateType;
    //返利配置
    private String rebateConfig;

}
