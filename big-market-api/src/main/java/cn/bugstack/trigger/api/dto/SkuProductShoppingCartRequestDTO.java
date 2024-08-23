package cn.bugstack.trigger.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: chs
 * Description: 购物车商品请求对象
 * CreateTime: 2024-08-19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkuProductShoppingCartRequestDTO {

    //用户id
    private String userId;
    //商品sku
    private Long sku;

}
