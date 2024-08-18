package cn.bugstack.domain.credit.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Author: chs
 * Description: 任务状态枚举
 * CreateTime: 2024-08-18
 */
@Getter
@AllArgsConstructor
public enum TaskStateVO {

    create("create", "创建"),
    complete("complete","发送完成"),
    fail("fail","发送失败"),
    ;

    private String code;
    private String desc;

}
