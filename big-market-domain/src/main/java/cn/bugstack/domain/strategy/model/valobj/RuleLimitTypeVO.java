package cn.bugstack.domain.strategy.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Author: chs
 * Description:
 * CreateTime: 2024-07-14
 */
@Getter
@AllArgsConstructor
public enum RuleLimitTypeVO {
    EQUAL(1,"等于"),
    GT(2,"大于"),
    LT(3,"小于"),
    GE(4,"大于&等于"),
    LE(5,"小于&等于"),
    ENUM(5,"枚举"),
    ;

    private final Integer code;
    private final String info;

}
