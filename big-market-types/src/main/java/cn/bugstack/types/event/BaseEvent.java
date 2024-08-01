package cn.bugstack.types.event;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * Author: chs
 * Description: 基础事件
 * CreateTime: 2024-07-31
 */
@Data
public abstract class BaseEvent<T> {

    public abstract EventMessage<T> buildEventMessage(T data);

    public abstract String topic();

    @Data
    @Builder
    public static class EventMessage<T>{
        private String id;
        private Date timestamp;
        private T data;
    }
}
