package cn.bugstack.domain.award.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Author: chs
 * Description: 奖品状态枚举
 * CreateTime: 2024-08-06
 */
@Getter
@AllArgsConstructor
public enum AwardStateVO {

    create("create", "创建"),
    complete("complete", "发奖完成"),
    ;

    private String code;
    private String desc;

}
