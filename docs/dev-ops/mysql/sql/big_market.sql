--------------------------------
-- strategy
--------------------------------
CREATE TABLE `strategy` (
                            `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
                            `strategy_id` bigint NOT NULL COMMENT '抽奖策略ID',
                            `strategy_desc` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '抽奖策略描述',
                            `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            PRIMARY KEY (`id`),
                            KEY `idx_strategy_id` (`strategy_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--------------------------------
-- strategy_award
--------------------------------
CREATE TABLE `strategy_award` (
                                  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
                                  `strategy_id` bigint NOT NULL COMMENT '抽奖策略ID',
                                  `award_id` int NOT NULL COMMENT '抽奖奖品ID - 内部流转使用',
                                  `award_title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '抽奖奖品标题',
                                  `award_subtitle` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '抽奖奖品副标题',
                                  `award_count` int NOT NULL DEFAULT '0' COMMENT '奖品库存总量',
                                  `award_count_surplus` int NOT NULL DEFAULT '0' COMMENT '奖品库存剩余',
                                  `award_rate` decimal(6,4) NOT NULL COMMENT '奖品中奖概率',
                                  `rule_models` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '规则模型，rule配置的模型同步到此表，便于使用',
                                  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
                                  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                  PRIMARY KEY (`id`),
                                  KEY `idx_strategy_id_award_id` (`strategy_id`,`award_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--------------------------------
-- strategy_rule
--------------------------------
CREATE TABLE `strategy_rule` (
                                 `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
                                 `strategy_id` int NOT NULL COMMENT '抽奖策略ID',
                                 `award_id` int DEFAULT NULL COMMENT '抽奖奖品ID【规则类型为策略，则不需要奖品ID】',
                                 `rule_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '抽象规则类型；1-策略规则、2-奖品规则',
                                 `rule_model` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '抽奖规则类型【rule_random - 随机值计算、rule_lock - 抽奖几次后解锁、rule_luck_award - 幸运奖(兜底奖品)】',
                                 `rule_value` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '抽奖规则比值',
                                 `rule_desc` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '抽奖规则描述',
                                 `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 PRIMARY KEY (`id`),
                                 KEY `idx_strategy_id_award_id` (`strategy_id`,`award_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

自增ID
抽奖奖品ID - 内部流转使用

--------------------------------
-- award
--------------------------------
DROP TABLE IF EXISTS `award`;
CREATE TABLE `award` (
                         `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
                         `award_id` int NOT NULL COMMENT '抽奖奖品ID - 内部流转使用',
                         `award_key` varchar(32) NOT NULL COMMENT '奖品对接规则',
                         `award_config` varchar(32) NOT NULL COMMENT '奖品配置信息',
                         `award_desc` varchar(128) NOT NULL COMMENT '奖品内容描述',
                         `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
INSERT INTO `award` VALUES ('1', '101', 'user_credit_random', '1100', '用户积分', '2024-07-04 23:56:26', '2024-07-04 23:56:26');
INSERT INTO `award` VALUES ('2', '102', 'openai_use_count', '5', 'OpenAI增加使用次数', '2024-07-05 00:02:27', '2024-07-05 00:02:27');
INSERT INTO `award` VALUES ('3', '103', 'openai_use_count', '10', 'OpenAI增加使用次数', '2024-07-05 00:02:27', '2024-07-05 00:02:27');
INSERT INTO `award` VALUES ('4', '104', 'openai_use_count', '20', 'OpenAI增加使用次数', '2024-07-05 00:02:27', '2024-07-05 00:02:27');
INSERT INTO `award` VALUES ('5', '105', 'openai_model', 'gpt-4', 'OpenAI增加模型', '2024-07-05 00:02:27', '2024-07-05 00:02:27');
INSERT INTO `award` VALUES ('6', '106', 'openai_model', 'dall-e-2', 'OpenAI增加模型', '2024-07-05 00:02:27', '2024-07-05 00:02:27');
INSERT INTO `award` VALUES ('7', '107', 'openai_model', 'dall-e-3', 'OpenAI增加模型', '2024-07-05 00:02:27', '2024-07-05 00:02:27');
INSERT INTO `award` VALUES ('8', '108', 'openai_use_model', '100', 'OpenAI增加使用次数', '2024-07-05 00:02:27', '2024-07-05 00:02:27');
INSERT INTO `award` VALUES ('9', '109', 'openai_model', 'gpt-4,dall-e-2,dall-e-3', 'OpenAI增加模型', '2024-07-05 00:02:27', '2024-07-05 00:02:27');
