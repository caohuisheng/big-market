package cn.bugstack.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

/**
 * Author: chs
 * Description:
 * CreateTime: 2024-07-21
 */
public class CodeGenerator {
    public static void main(String[] args) {
        // 使用 FastAutoGenerator 快速配置代码生成器
        FastAutoGenerator.create("jdbc:mysql://127.0.0.1:3306/big_market?useUnicode=true&characterEncoding=utf8&autoReconnect=true&zeroDateTimeBehavior=convertToNull&serverTimezone=UTC&useSSL=true",
                        "root", "root")
                .globalConfig(builder -> {
                    builder.author("chs") // 设置作者
                            .outputDir("big-market-infrastructure\\src\\main\\java"); // 输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("cn.bugstack.infrastructure.persistent.generator") // 设置父包名
                            .entity("model"); // 设置实体类包名
                            //.mapper("dao") // 设置 Mapper 接口包名
                            //.service("service") // 设置 Service 接口包名
                            //.serviceImpl("service.impl") // 设置 Service 实现类包名
                            //.xml("mappers"); // 设置 Mapper XML 文件包名
                })
                .strategyConfig(builder -> {
                    builder.addInclude("raffle_activity","raffle_activity_account","raffle_activity_account_flow","raffle_activity_count","raffle_activity_order") // 设置需要生成的表名
                            .entityBuilder()
                            .enableLombok(); // 启用 Lombok
                            //.enableTableFieldAnnotation() // 启用字段注解
                            //.controllerBuilder()
                            //.enableRestStyle(); // 启用 REST 风格
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用 Freemarker 模板引擎
                .execute(); // 执行生成
    }
}
