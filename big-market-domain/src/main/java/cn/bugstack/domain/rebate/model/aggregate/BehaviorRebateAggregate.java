package cn.bugstack.domain.rebate.model.aggregate;

import cn.bugstack.domain.rebate.model.entity.BehaviorRebateOrderEntity;
import cn.bugstack.domain.rebate.model.entity.TaskEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: chs
 * Description: 行为返利聚合对象
 * CreateTime: 2024-08-13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BehaviorRebateAggregate {

    //用户ID
    private String userId;
    //用户行为返利订单实体
    private BehaviorRebateOrderEntity behaviorRebateOrderEntity;
    //任务实体
    private TaskEntity taskEntity;

}
