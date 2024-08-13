package cn.bugstack.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

/**
 * Author: chs
 * Description: 日常行为返利表
 * CreateTime: 2024-08-13
 */
@Data
public class DailyBehaviorRebate {

    //自增ID
    private Integer id;
    //行为类型
    private String behaviorType;
    //返利描述
    private String rebateDesc;
    //返利类型
    private String rebateType;
    //返利配置
    private String rebateConfig;
    //状态
    private String state;
    //创建时间
    private Date createTime;
    //更新时间
    private Date updateTime;

}
