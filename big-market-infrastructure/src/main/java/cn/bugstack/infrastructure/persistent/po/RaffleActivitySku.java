package cn.bugstack.infrastructure.persistent.po;

import lombok.Data;
import org.omg.CORBA.LongHolder;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Author: chs
 * Description: 活动sku
 * CreateTime: 2024-07-29
 */
@Data
public class RaffleActivitySku {

    /* 自增ID */
    private Integer id;

    /* 商品sku */
    private Long sku;

    /* 活动ID */
    private Long activityId;

    /* 活动个人参与次数ID */
    private Long activityCountId;

    /* 商品库存 */
    private Integer stockCount;

    /* 剩余库存 */
    private Integer stockCountSurplus;

    /* 创建时间 */
    private Date createTime;

    /* 更新时间 */
    private Date updateTime;
}
