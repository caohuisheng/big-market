package cn.bugstack.domain.rebate.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Author: chs
 * Description: 任务状态值对象
 * CreateTime: 2024-08-13
 */
@Getter
@AllArgsConstructor
public enum TaskStateVO {

    create("create","创建"),
    complete("complete","发送完成"),
    fail("fail","发送失败")
    ;

    private final String code;
    private final String desc;

}
