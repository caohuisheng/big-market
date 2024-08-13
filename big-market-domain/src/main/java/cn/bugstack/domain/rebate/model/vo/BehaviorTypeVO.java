package cn.bugstack.domain.rebate.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Author: chs
 * Description: 行为类型值对象
 * CreateTime: 2024-08-13
 */
@Getter
@AllArgsConstructor
public enum BehaviorTypeVO {

    SIGN("sign","签到"),
    OPENAI_PAY("openai_pay","openai外部支付完成")
    ;

    private String code;
    private String info;

}
