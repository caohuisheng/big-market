package cn.bugstack.trigger.api;

import cn.bugstack.trigger.api.dto.*;
import cn.bugstack.types.model.Response;

import java.util.List;

/**
 * Author: chs
 * Description: 抽奖服务接口
 * CreateTime: 2024-07-19
 */
public interface IRaffleStrategyService {

    /**
     * 策略装配接口
     * @param strategyId 策略id
     * @return 装配结果
     */
    Response<Boolean> strategyArmory(Long strategyId);

    /**
     * 查询抽奖奖品列表
     * @param requestDTO 查询请求参数
     * @return 奖品列表
     */
    Response<List<RaffleAwardListResponseDTO>> queryRaffleAwardList(RaffleAwardListRequestDTO requestDTO);

    /**
     * 查询抽奖策略权重规则，给用户展示出抽奖N次后必中奖范围
     * @param request 请求对象
     * @return 权重奖品配置列表
     */
    Response<List<RaffleStrategyRuleWeightResponseDTO>> queryRaffleStrategyRuleWeight(RaffleStrategyRuleWeightRequestDTO request);

    /**
     * 随机抽奖接口
     * @param requestDTO 请求参数
     * @return 抽奖结果
     */
    Response<RaffleResponseDTO> randomRaffle(RaffleRequestDTO requestDTO);
}
