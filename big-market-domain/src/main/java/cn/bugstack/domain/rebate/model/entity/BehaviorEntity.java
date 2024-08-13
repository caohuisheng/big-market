package cn.bugstack.domain.rebate.model.entity;

import cn.bugstack.domain.rebate.model.vo.BehaviorTypeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: chs
 * Description: 行为实体
 * CreateTime: 2024-08-13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BehaviorEntity {

    //用户id
    private String userId;
    //行为类型：sign(签到)、openai_pay(支付)
    private BehaviorTypeVO behaviorTypeVO;
    //业务ID（签到为日期字符串，支付为外部的业务ID）
    private String outBusinessNo;

}
