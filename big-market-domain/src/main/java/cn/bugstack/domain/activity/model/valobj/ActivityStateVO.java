package cn.bugstack.domain.activity.model.valobj;

import lombok.AllArgsConstructor;

/**
 * Author: chs
 * Description: 活动状态值对象
 * CreateTime: 2024-07-30
 */
@AllArgsConstructor
public enum ActivityStateVO {

    created("create", "创建");

    private final String code;
    private final String desc;

}
