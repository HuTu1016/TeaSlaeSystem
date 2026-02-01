package org.example.teasystem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Knife4j 配置
 * 按管理端、商家端、用户端分模块管理文档
 */
@Configuration
public class OpenApiConfig {

        /**
         * 全局OpenAPI配置
         */
        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("茶叶销售系统 API 文档")
                                                .description("茶叶销售系统后端接口文档，包含用户端、商家端、管理端所有接口")
                                                .version("1.0.0")
                                                .contact(new Contact()
                                                                .name("TeaSystem")
                                                                .email("support@teasystem.com"))
                                                .license(new License()
                                                                .name("Apache 2.0")
                                                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
        }

        /**
         * 管理端API分组
         * 包含所有 /admin/** 路径的接口
         */
        @Bean
        public GroupedOpenApi adminApi() {
                return GroupedOpenApi.builder()
                                .group("admin")
                                .displayName("管理端接口")
                                .pathsToMatch("/admin/**")
                                .build();
        }

        /**
         * 商家端API分组
         * 包含所有 /merchant/** 路径的接口以及商家注册接口
         */
        @Bean
        public GroupedOpenApi merchantApi() {
                return GroupedOpenApi.builder()
                                .group("merchant")
                                .displayName("商家端接口")
                                .pathsToMatch("/merchant/**", "/auth/merchant-register")
                                .build();
        }

        /**
         * 用户端API分组
         * 包含用户端相关接口（排除管理端和商家端）
         */
        @Bean
        public GroupedOpenApi userApi() {
                return GroupedOpenApi.builder()
                                .group("user")
                                .displayName("用户端接口")
                                .pathsToMatch("/**")
                                .pathsToExclude("/admin/**", "/merchant/**", "/auth/merchant-register")
                                .build();
        }
}
