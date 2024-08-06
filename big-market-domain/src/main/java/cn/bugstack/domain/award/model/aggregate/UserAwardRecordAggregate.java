package cn.bugstack.domain.award.model.aggregate;

import cn.bugstack.domain.award.model.entity.TaskEntity;
import cn.bugstack.domain.award.model.entity.UserAwardRecordEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: chs
 * Description: 用户中将记录聚合对象
 * CreateTime: 2024-08-06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAwardRecordAggregate {

    private UserAwardRecordEntity userAwardRecordEntity;
    private TaskEntity taskEntity;

}
