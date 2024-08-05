package cn.bugstack.domain.activity.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Author: chs
 * Description: 用户抽奖订单状态枚举
 * CreateTime: 2024-08-04
 */
@Getter
@AllArgsConstructor
public enum UserRaffleOrderStateVO {

    create("create", "创建"),
    used("used","已使用"),
    cancel("cancel","已作废"),
    ;

    private final String code;
    private final String desc;
}
