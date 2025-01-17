package cn.bugstack.trigger.http;

import cn.bugstack.domain.activity.model.entity.*;
import cn.bugstack.domain.activity.model.valobj.OrderTradeTypeVO;
import cn.bugstack.domain.activity.service.IRaffleActivityAccountQuotaService;
import cn.bugstack.domain.activity.service.IRaffleActivityPartakeService;
import cn.bugstack.domain.activity.service.IRaffleActivitySkuProductService;
import cn.bugstack.domain.activity.service.armory.IActivityArmory;
import cn.bugstack.domain.award.model.entity.UserAwardRecordEntity;
import cn.bugstack.domain.award.model.vo.AwardStateVO;
import cn.bugstack.domain.award.service.IAwardService;
import cn.bugstack.domain.credit.model.entity.CreditAccountEntity;
import cn.bugstack.domain.credit.model.entity.TradeEntity;
import cn.bugstack.domain.credit.model.vo.TradeNameVO;
import cn.bugstack.domain.credit.model.vo.TradeTypeVO;
import cn.bugstack.domain.credit.service.ICreditAdjustService;
import cn.bugstack.domain.rebate.model.entity.BehaviorEntity;
import cn.bugstack.domain.rebate.model.vo.BehaviorTypeVO;
import cn.bugstack.domain.rebate.service.IBehaviorRebateService;
import cn.bugstack.domain.strategy.model.entity.RaffleAwardEntity;
import cn.bugstack.domain.strategy.model.entity.RaffleFactorEntity;
import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;
import cn.bugstack.domain.strategy.model.valobj.RuleWeightVO;
import cn.bugstack.domain.strategy.service.IRaffleAward;
import cn.bugstack.domain.strategy.service.IRaffleRule;
import cn.bugstack.domain.strategy.service.IRaffleStrategy;
import cn.bugstack.domain.strategy.service.armory.IStrategyArmory;
import cn.bugstack.trigger.api.IRaffleActivityService;
import cn.bugstack.trigger.api.IRaffleStrategyService;
import cn.bugstack.trigger.api.dto.*;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
import cn.bugstack.types.model.Response;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Author: chs
 * Description: 营销抽奖服务
 * CreateTime: 2024-07-19
 */
@Slf4j
@RestController
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/raffle/activity/")
public class RaffleActivityController implements IRaffleActivityService {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    @Resource
    private IRaffleActivityPartakeService raffleActivityPartakeService;
    @Resource
    private IRaffleStrategy raffleStrategy;
    @Resource
    private IAwardService awardService;
    @Resource
    private IActivityArmory activityArmory;
    @Resource
    private IStrategyArmory strategyArmory;
    @Resource
    private IBehaviorRebateService behaviorRebateService;
    @Resource
    private IRaffleActivityAccountQuotaService raffleActivityAccountQuotaService;
    @Resource
    private ICreditAdjustService creditAdjustService;
    @Resource
    private IRaffleActivitySkuProductService raffleActivitySkuProductService;

    /**
     * 活动装配 - 数据预热 | 把活动配置的对应的sku一起预热
     * @param activityId 活动ID
     * @return
     */
    @RequestMapping(value = "armory", method = RequestMethod.GET)
    @Override
    public Response<Boolean> armory(@RequestParam Long activityId){
        try {
            log.info("活动装配，数据预热，开始 activityId:{}", activityId);
            activityArmory.assembleActivitySkuByActivityId(activityId);
            strategyArmory.assembleLotteryStrategyByActivityId(activityId);
            Response<Boolean> response = Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(true)
                    .build();
            log.info("活动装配，数据预热，完成 activityId:{}", activityId);
            return response;
        } catch (Exception e) {
            log.error("活动装配，数据预热，失败 activityId:{}", activityId, e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "draw", method = RequestMethod.POST)
    @Override
    public Response<ActivityDrawResponseDTO> draw(@RequestBody ActivityDrawRequestDTO requestDTO) {
        String userId = requestDTO.getUserId();
        Long activityId = requestDTO.getActivityId();

        try {
            log.info("活动抽奖 userId:{} activityId:{}", userId, activityId);
            //1.参数校验
            if(StringUtils.isBlank(userId) || null == activityId){
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }
            RaffleAwardEntity raffleAwardEntity = this.draw(userId, activityId);
            //5.返回结果
            return Response.<ActivityDrawResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(ActivityDrawResponseDTO.builder()
                            .awardId(raffleAwardEntity.getAwardId())
                            .awardTitle(raffleAwardEntity.getAwardTitle())
                            .awardIndex(raffleAwardEntity.getSort())
                            .build())
                    .build();
        } catch (AppException e) {
            log.error("活动抽奖失败 userId:{} activityId:{}", userId, activityId, e);
            return Response.<ActivityDrawResponseDTO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        }catch(Exception e){
            log.error("活动抽奖失败 userId:{} activityId:{}", userId, activityId, e);
            return Response.<ActivityDrawResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "continuous_draw", method = RequestMethod.POST)
    public Response<List<ActivityDrawResponseDTO>> continuousDraw(@RequestBody ActivityDrawRequestDTO requestDTO){
        String userId = requestDTO.getUserId();
        Long activityId = requestDTO.getActivityId();
        Integer raffleCount = requestDTO.getRaffleCount();

        try {
            ExecutorService executorService = Executors.newFixedThreadPool(5);
            log.info("活动连续抽奖 userId:{}, activityId:{}, raffleCount:{}", userId, activityId, raffleCount);
            List<RaffleAwardEntity> raffleAwardEntities = new ArrayList<>(raffleCount);
            CountDownLatch countDownLatch = new CountDownLatch(raffleCount);

            for (int i = 0; i < raffleCount; i++) {
                executorService.execute(() -> {
                    RaffleAwardEntity raffleAwardEntity = this.draw(userId, activityId);
                    raffleAwardEntities.add(raffleAwardEntity);
                    countDownLatch.countDown();
                });
            }

            boolean status = countDownLatch.await(5, TimeUnit.SECONDS);
            List<ActivityDrawResponseDTO> activityDrawResponseDTOS = raffleAwardEntities.stream().map(raffleAwardEntity -> ActivityDrawResponseDTO.builder()
                    .awardId(raffleAwardEntity.getAwardId())
                    .awardTitle(raffleAwardEntity.getAwardTitle())
                    .awardIndex(raffleAwardEntity.getSort())
                    .build()).collect(Collectors.toList());
            if(status){
                return Response.<List<ActivityDrawResponseDTO>>builder()
                        .code(ResponseCode.SUCCESS.getCode())
                        .info(ResponseCode.SUCCESS.getInfo())
                        .data(activityDrawResponseDTOS)
                        .build();
            }else{
                log.error("活动抽奖失败 userId:{} activityId:{} raffleCount:{}", userId, activityId, raffleCount);
                return Response.<List<ActivityDrawResponseDTO>>builder()
                        .code(ResponseCode.UN_ERROR.getCode())
                        .info(ResponseCode.UN_ERROR.getInfo())
                        .build();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return Response.<List<ActivityDrawResponseDTO>>builder()
                    .code(e.getMessage())
                    .info(e.getMessage())
                    .build();
        }
    }

    private RaffleAwardEntity draw(String userId, Long activityId){
        //2.参与活动 - 创建参与记录订单
        UserRaffleOrderEntity raffleOrderEntity = raffleActivityPartakeService.createOrder(userId, activityId);
        log.info("活动抽奖，创建订单 userId:{} activityId:{} orderId:{}", userId, activityId, raffleOrderEntity.getOrderId());
        //3.抽奖策略 - 执行抽奖
        RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(RaffleFactorEntity.builder()
                .userId(userId)
                .strategyId(raffleOrderEntity.getStrategyId())
                .endDatetime(raffleOrderEntity.getEndDatetime())
                .build());

        //4.存放结果，写入中奖记录
        UserAwardRecordEntity userAwardRecordEntity = UserAwardRecordEntity.builder()
                .userId(userId)
                .activityId(activityId)
                .strategyId(raffleOrderEntity.getStrategyId())
                .orderId(raffleOrderEntity.getOrderId())
                .awardId(raffleAwardEntity.getAwardId())
                .awardTitle(raffleAwardEntity.getAwardTitle())
                .awardTime(new Date())
                .awardState(AwardStateVO.create)
                .awardConfig(raffleAwardEntity.getAwardConfig())
                .build();
        awardService.saveUserAwardRecord(userAwardRecordEntity);

        return raffleAwardEntity;
    }

    @Override
    @RequestMapping(value = "calendar_sign_rebate", method = RequestMethod.POST)
    public Response<Boolean> calendarSignRebate(@RequestParam String userId){
        try {
            log.info("日历签到返利开始 userId:{}", userId);
            BehaviorEntity behaviorEntity = BehaviorEntity.builder()
                    .userId(userId)
                    .behaviorTypeVO(BehaviorTypeVO.SIGN)
                    .outBusinessNo(dateFormat.format(new Date()))
                    .build();
            List<String> orderIds = behaviorRebateService.createOrder(behaviorEntity);
            log.info("日历签到返利完成 userId:{} orderIds:{}", userId, orderIds);
            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(true)
                    .build();
        } catch (AppException e) {
            log.error("日历签到返利异常 userId:{}", userId);
            return Response.<Boolean>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        }catch(Exception e){
            log.error("日历签到返利异常 userId:{}", userId);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .data(false)
                    .build();
        }
    }

    @Override
    @RequestMapping(value = "is_calendar_sign_rebate", method = RequestMethod.GET)
    public Response<Boolean> isCalendarSignRebate(@RequestParam String userId) {
        try {
            log.info("查询用户是否完成日历签到返利开始 userId:{}", userId);
            Boolean isCalendarSignRebate = behaviorRebateService.isCalendarSignRebate(userId, dateFormat.format(new Date()));
            log.info("查询用户是否完成日历签到返利结束 userId:{} isCalendarSignRebate:{}", userId, isCalendarSignRebate);
            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(isCalendarSignRebate)
                    .build();
        } catch (Exception e) {
            log.info("查询用户是否完成日历签到返利失败 userId:{}", userId,e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "query_user_activity_account", method = RequestMethod.POST)
    @Override
    public Response<UserActivityAccountResponseDTO> queryUserActivityAccount(@RequestBody UserActivityAccountRequestDTO request) {
        String userId = request.getUserId();
        Long activityId = request.getActivityId();
        try {
            //参数校验
            if(StringUtils.isBlank(userId) || null == activityId) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }

            log.info("查询用户活动账户开始 userId:{} activityId:{}",userId, activityId);
            ActivityAccountEntity activityAccountEntity = behaviorRebateService.queryActivityAccountEntity(userId, activityId);
            UserActivityAccountResponseDTO userActivityAccountResponseDTO = new UserActivityAccountResponseDTO();
            BeanUtils.copyProperties(activityAccountEntity, userActivityAccountResponseDTO);
            log.info("查询用户活动账户完成 userId:{} activityId:{} dto:{}",userId, activityId, userActivityAccountResponseDTO);
            return Response.<UserActivityAccountResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(userActivityAccountResponseDTO)
                    .build();
        } catch (BeansException e) {
            log.error("查询用户活动账户失败 userId:{} activityId:{} dto:{}",userId, activityId, e);
            return Response.<UserActivityAccountResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "credit_pay_exchange_sku", method = RequestMethod.POST)
    @Override
    public Response<Boolean> creditPayExchangeSku(@RequestBody SkuProductShoppingCartRequestDTO request) {
        String userId = request.getUserId();
        Long sku = request.getSku();
        try {
            log.info("积分兑换商品开始 userId:{} sku:{}", userId, sku);
            //1.创建兑换商品sku订单
            UnpaidActivityOrderEntity unpaidActivityOrder = raffleActivityAccountQuotaService.createOrder(SkuRechargeEntity.builder()
                    .userId(userId)
                    .sku(sku)
                    .outBusinessNo(RandomStringUtils.randomNumeric(12))
                    .orderTradeType(OrderTradeTypeVO.credit_pay_trade)
                    .build());
            log.info("积分兑换商品，创建订单完成 userId:{} sku:{} outBusinessNo:{}", userId, sku, unpaidActivityOrder.getOutBusinessNo());

            //2.支付兑换商品
            String orderId = creditAdjustService.createOrder(TradeEntity.builder()
                    .userId(userId)
                    .tradeName(TradeNameVO.CONVERT_SKU)
                    .tradeType(TradeTypeVO.REVERSE)
                    .amount(unpaidActivityOrder.getPayAmount().negate())
                    .outBusinessNo(unpaidActivityOrder.getOutBusinessNo())
                    .build());
            log.info("积分兑换商品，支付订单完成 userId:{} sku:{} orderId:{}", userId, sku, orderId);

            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.info("积分兑换商品失败 userId:{} sku:{}", userId, sku, e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .data(false)
                    .build();
        }
    }

    @Override
    @RequestMapping(value = "query_sku_product_entities", method = RequestMethod.POST)
    public Response<List<SkuProductResponseDTO>> querySkuProductListByActivityId(@RequestParam Long activityId) {
        try {
            log.info("查询sku商品集合开始 activityId:{}", activityId);
            if(null == activityId){
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }

            //查询sku商品集合
            List<SkuProductEntity> skuProductEntities = raffleActivitySkuProductService.querySkuProductEntitiesByActivityId(activityId);
            //封装结果
            List<SkuProductResponseDTO> skuProductResponseDTOS = new ArrayList<>();
            for(SkuProductEntity skuProduct:skuProductEntities){
                SkuProductResponseDTO dto = new SkuProductResponseDTO();
                BeanUtils.copyProperties(skuProduct, dto);

                SkuProductResponseDTO.ActivityCount activityCount = new SkuProductResponseDTO.ActivityCount();
                BeanUtils.copyProperties(skuProduct.getActivityCount(), activityCount);
                dto.setActivityCount(activityCount);
                skuProductResponseDTOS.add(dto);
            }

            log.info("查询sku商品集合完成 activityId:{} skuProductResponseDTOS:{}", activityId, JSON.toJSONString(skuProductEntities));
            return Response.<List<SkuProductResponseDTO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(skuProductResponseDTOS)
                    .build();
        } catch (Exception e) {
            log.info("查询sku商品集合异常 activityId:{}", activityId, e);
            return Response.<List<SkuProductResponseDTO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "query_user_credit_account", method = RequestMethod.POST)
    @Override
    public Response<BigDecimal> queryUserCreditAccount(@RequestParam String userId) {
        try {
            log.info("查询用户积分值开始 userId:{}", userId);
            CreditAccountEntity creditAccountEntity = creditAdjustService.queryUserCreditAccount(userId);
            BigDecimal adjustAmount = creditAccountEntity.getAdjustAmount();
            log.info("查询用户积分值结束 userId:{} adjustAmount:{}", userId, adjustAmount);
            return Response.<BigDecimal>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(adjustAmount)
                    .build();
        } catch (Exception e) {
            log.error("查询用户积分值异常 userId:{}", userId);
            return Response.<BigDecimal>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }
}
