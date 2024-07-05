package cn.bugstack.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

/**
 * @Author: chs
 * @CreateTime: 2024-07-05
 * @Description: 策略实体类
 * @Version: 1.0
 */
@Data
public class Strategy {

    // 自增ID
    private Long id;
    // 抽奖策略ID
    private Long strategyId;
    // 抽奖策略描述
    private String strategyDesc;
    // 创建时间
    private Date createTime;
    // 更新时间
    private Date updateTime;
}
