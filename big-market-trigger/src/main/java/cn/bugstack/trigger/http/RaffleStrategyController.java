package cn.bugstack.trigger.http;

import cn.bugstack.domain.activity.service.IRaffleActivityAccountQuotaService;
import cn.bugstack.domain.strategy.model.entity.RaffleAwardEntity;
import cn.bugstack.domain.strategy.model.entity.RaffleFactorEntity;
import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;
import cn.bugstack.domain.strategy.model.valobj.RuleWeightVO;
import cn.bugstack.domain.strategy.service.IRaffleAward;
import cn.bugstack.domain.strategy.service.IRaffleRule;
import cn.bugstack.domain.strategy.service.IRaffleStrategy;
import cn.bugstack.domain.strategy.service.armory.IStrategyArmory;
import cn.bugstack.trigger.api.IRaffleStrategyService;
import cn.bugstack.trigger.api.dto.*;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
import cn.bugstack.types.model.Response;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author: chs
 * Description: 营销抽奖服务
 * CreateTime: 2024-07-19
 */
@Slf4j
@RestController
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/raffle/strategy/")
public class RaffleStrategyController implements IRaffleStrategyService {

    @Resource
    private IRaffleAward raffleAward;
    @Resource
    private IRaffleStrategy raffleStrategy;
    @Resource
    private IStrategyArmory strategyArmory;
    @Resource
    private IRaffleRule raffleRule;
    @Resource
    private IRaffleActivityAccountQuotaService raffleActivityAccountQuotaService;

    @RequestMapping(value = "test", method = RequestMethod.GET)
    public Response<String> demo(){
        return Response.<String>builder().code("200").info("success").data("hello,world").build();
    }

    @Override
    @RequestMapping(value = "strategy_armory", method = RequestMethod.GET)
    public Response<Boolean> strategyArmory(@RequestParam Long strategyId){
        try{
            log.info("抽奖策略装配开始 strategyId:{}", strategyId);
            boolean armoryStatus = strategyArmory.assembleLotteryStrategy(strategyId);
            Response<Boolean> response = Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(armoryStatus)
                    .build();
            log.info("抽奖策略装配完成 strategyId:{}, response:{}", strategyId, JSON.toJSONString(response));
            return response;
        }catch(Exception e){
            log.info("抽奖策略装配失败 strategyId:{}", strategyId, e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @Override
    @RequestMapping(value = "query_raffle_award_list", method = RequestMethod.POST)
    public Response<List<RaffleAwardListResponseDTO>> queryRaffleAwardList(@RequestBody RaffleAwardListRequestDTO requestDTO) {
        String userId = requestDTO.getUserId();
        Long activityId = requestDTO.getActivityId();
        try {
            log.info("查询抽奖奖品列表开始 userId:{} activityId:{}", userId, activityId);
            //1.参数校验
            if(StringUtils.isBlank(userId) || null == activityId){
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }
            //2.查询策略奖品列表
            List<StrategyAwardEntity> strategyAwardEntities = raffleAward.queryRaffleStrategyAwardListByActivityId(activityId);
            //3.获取规则配置
            String[] treeIds = strategyAwardEntities.stream().map(StrategyAwardEntity::getRuleModels).toArray(String[]::new);
            //4.查询规则配置
            Map<String, Integer> ruleLockCountMap = raffleRule.queryAwardRuleLockCount(treeIds);
            //4.查询抽奖次数
            Integer dayPartakeCount = raffleActivityAccountQuotaService.queryRaffleActivityAccountDayPartakeCount(activityId, userId);
            //5.封装结果
            List<RaffleAwardListResponseDTO> raffleAwardListResponseDTOS = strategyAwardEntities.stream().map(strategyAward -> {
                Integer awardRuleLockCount = ruleLockCountMap.get(strategyAward.getRuleModels());
                RaffleAwardListResponseDTO raffleAwardListResponseDTO = new RaffleAwardListResponseDTO();
                BeanUtils.copyProperties(strategyAward, raffleAwardListResponseDTO);
                raffleAwardListResponseDTO.setAwardRuleLockCount(awardRuleLockCount);
                raffleAwardListResponseDTO.setIsAwardUnlock(awardRuleLockCount == null?true:(dayPartakeCount >= awardRuleLockCount));
                raffleAwardListResponseDTO.setWaitUnLockCount(awardRuleLockCount == null?null : (dayPartakeCount >= awardRuleLockCount) ? 0:(awardRuleLockCount - dayPartakeCount));
                return raffleAwardListResponseDTO;
            }).collect(Collectors.toList());

            Response<List<RaffleAwardListResponseDTO>> response = Response.<List<RaffleAwardListResponseDTO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(raffleAwardListResponseDTOS)
                    .build();
            log.info("查询抽奖奖品列表完成 userId:{} strategyId:{}, response:{}",userId, activityId, response);
            // 返回结果
            return response;
        } catch (Exception e) {
            log.error("查询抽奖奖品列表失败 userId:{} strategyId:{}", userId, activityId, e);
            return Response.<List<RaffleAwardListResponseDTO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @Override
    @RequestMapping(value = "random_raffle", method = RequestMethod.POST)
    public Response<RaffleResponseDTO> randomRaffle(@RequestBody RaffleRequestDTO requestDTO) {
        Long strategyId = requestDTO.getStrategyId();
        try {
            log.info("随机抽奖开始 strategyId:{}", strategyId);
            RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(RaffleFactorEntity.builder()
                    .userId("system")
                    .strategyId(strategyId)
                    .build());
            Response<RaffleResponseDTO> response = Response.<RaffleResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(RaffleResponseDTO.builder()
                            .awardId(raffleAwardEntity.getAwardId())
                            .awardIndex(raffleAwardEntity.getSort())
                            .build())
                    .build();
            log.info("随机抽奖完成 strategId:{} response:{}", strategyId, response);
            return response;
        } catch (AppException e) {
            log.error("随机抽奖失败 strategyId:{}", strategyId, e);
            return Response.<RaffleResponseDTO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        }catch(Exception e){
            log.error("随机抽奖失败 strategyId:{}", strategyId, e);
            return Response.<RaffleResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @Override
    @RequestMapping(value = "query_raffle_strategy_rule_weight", method = RequestMethod.POST)
    public Response<List<RaffleStrategyRuleWeightResponseDTO>> queryRaffleStrategyRuleWeight(@RequestBody RaffleStrategyRuleWeightRequestDTO request) {
        String userId = request.getUserId();
        Long activityId = request.getActivityId();
        try {
            log.info("查询抽奖策略权重配置开始 userId:{} activityId:{}", userId, activityId);
            //1.参数校验
            if(StringUtils.isBlank(userId) || null == activityId){
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }
            //2.查询抽奖总次数
            Integer userActivityAccountTotalUseCount = raffleActivityAccountQuotaService.queryRaffleActivityAccountPartakeCount(userId, activityId);
            //3.查询奖品权重配置
            List<RaffleStrategyRuleWeightResponseDTO> raffleStrategyRuleWeightResponseDTOS = new ArrayList<>();
            List<RuleWeightVO> ruleWeightVOS = raffleRule.queryAwardRuleWeightByActivityId(activityId);

            //遍历每一个规则权重并填充结果
            for (RuleWeightVO ruleWeightVO : ruleWeightVOS) {
                List<RuleWeightVO.Award> awardList = ruleWeightVO.getAwardList();
                List<RaffleStrategyRuleWeightResponseDTO.StrategyAward> strategyAwards = awardList.stream().map(award -> {
                    RaffleStrategyRuleWeightResponseDTO.StrategyAward strategyAward = new RaffleStrategyRuleWeightResponseDTO.StrategyAward();
                    strategyAward.setAwardId(award.getAwardId());
                    strategyAward.setAwardTitle(award.getAwardTitle());
                    return strategyAward;
                }).collect(Collectors.toList());

                RaffleStrategyRuleWeightResponseDTO dto = new RaffleStrategyRuleWeightResponseDTO();
                dto.setUserActivityAccountTotalUseCount(userActivityAccountTotalUseCount);
                dto.setRuleWeightCount(ruleWeightVO.getWeight());
                dto.setStrategyAwards(strategyAwards);
                raffleStrategyRuleWeightResponseDTOS.add(dto);
            }

            return Response.<List<RaffleStrategyRuleWeightResponseDTO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(raffleStrategyRuleWeightResponseDTOS)
                    .build();
        } catch(Exception e){
            log.info("查询抽奖策略权重配置异常 userId:{} activityId:{}", userId, activityId,e);
            return Response.<List<RaffleStrategyRuleWeightResponseDTO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }
}
