package com.tourism.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 认证配置（多账号体系）
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> {
            // 用户端需要认证的接口
            SaRouter.match("/api/user/info", "/api/user/avatar", "/api/user/password")
                    .check(r -> StpUserUtil.checkLogin());
            SaRouter.match("/api/user/info").matchMethod("PUT")
                    .check(r -> StpUserUtil.checkLogin());
            SaRouter.match("/api/review/add", "/api/review/my")
                    .check(r -> StpUserUtil.checkLogin());
            SaRouter.match("/api/review/{id}").matchMethod("DELETE")
                    .check(r -> StpUserUtil.checkLogin());
            SaRouter.match("/api/order/**")
                    .check(r -> StpUserUtil.checkLogin());
            SaRouter.match("/api/favorite/**")
                    .check(r -> StpUserUtil.checkLogin());

            // 管理端所有接口需要认证（登录接口除外）
            SaRouter.match("/api/admin/**")
                    .notMatch("/api/admin/login")
                    .check(r -> StpAdminUtil.checkLogin());
        })).addPathPatterns("/api/**");
    }
}
