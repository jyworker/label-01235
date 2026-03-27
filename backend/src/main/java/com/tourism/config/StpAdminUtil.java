package com.tourism.config;

import cn.dev33.satoken.stp.StpLogic;

/**
 * 管理端认证工具类（多账号体系）
 */
public class StpAdminUtil {
    public static final String TYPE = "admin";
    public static StpLogic stpLogic = new StpLogic(TYPE);

    public static String getLoginType() { return TYPE; }
    public static void login(Object id) { stpLogic.login(id); }
    public static void logout() { stpLogic.logout(); }
    public static boolean isLogin() { return stpLogic.isLogin(); }
    public static void checkLogin() { stpLogic.checkLogin(); }
    public static Object getLoginId() { return stpLogic.getLoginId(); }
    public static long getLoginIdAsLong() { return stpLogic.getLoginIdAsLong(); }
    public static String getTokenValue() { return stpLogic.getTokenValue(); }
    public static String getTokenName() { return stpLogic.getTokenName(); }
}
