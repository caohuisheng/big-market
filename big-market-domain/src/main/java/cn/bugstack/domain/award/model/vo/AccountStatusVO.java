package cn.bugstack.domain.award.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Author: chs
 * Description:
 * CreateTime: 2024-08-17
 */
@Getter
@AllArgsConstructor
public enum AccountStatusVO {
    OPEN("open", "开启"),
    CLOSE("close","冻结"),
    ;

    private String code;
    private String info;
}
