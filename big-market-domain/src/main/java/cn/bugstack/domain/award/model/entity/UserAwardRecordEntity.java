package cn.bugstack.domain.award.model.entity;

import cn.bugstack.domain.award.model.vo.AwardStateVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Author: chs
 * Description: 用户中奖记录实体对象
 * CreateTime: 2024-08-06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAwardRecordEntity {

    //用户ID
    private String userId;
    //活动id
    private Long activityId;
    //抽奖策略id
    private Long strategyId;
    //抽奖订单id
    private String orderId;
    //奖品id
    private Integer awardId;
    //奖品标题
    private String awardTitle;
    //中奖时间
    private Date awardTime;
    //奖品状态：create-创建、completed-发奖完成
    private AwardStateVO awardState;
    //奖品配置
    private String awardConfig;

}
