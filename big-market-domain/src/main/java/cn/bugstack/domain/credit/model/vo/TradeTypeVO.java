package cn.bugstack.domain.credit.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * Author: chs
 * Description: 交易类型枚举
 * CreateTime: 2024-08-18
 */
@Getter
@AllArgsConstructor
public enum TradeTypeVO {

    FORWARD("forward","正向交易，+积分"),
    REVERSE("reverse","逆向交易，-积分"),
    ;

    private String code;
    private String info;
}
