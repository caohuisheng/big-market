package cn.bugstack.infrastructure.persistent.po;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 任务表，发送MQ
 * @author chs
 * @since 2024-08-04
 */
@Data
public class Task implements Serializable {

    /**
     * 自增ID
     */
    private Integer id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 消息主题
     */
    private String topic;

    /**
     * 消息id
     */
    private String messageId;

    /**
     * 消息主体
     */
    private String message;

    /**
     * 任务状态；create-创建、completed-完成、fail-失败
     */
    private String state;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
