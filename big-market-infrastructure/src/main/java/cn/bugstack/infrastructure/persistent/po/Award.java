package cn.bugstack.infrastructure.persistent.po;

import lombok.Data;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: chs
 * @CreateTime: 2024-07-05
 * @Description: 奖品实体类
 * @Version: 1.0
 */
@Data
public class Award {

    //自增ID
    private Integer id;
    //抽奖奖品ID - 内部流转使用
    private Integer awardId;
    //奖品对接规则
    private String awardKey;
    //奖品配置信息
    private String awardConfig;
    //奖品内容描述
    private String awardDesc;
    //创建时间
    private Date createTime;
    //更新时间
    private Date updateTime;
}
