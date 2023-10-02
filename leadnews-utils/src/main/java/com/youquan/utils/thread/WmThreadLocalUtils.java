package com.youquan.utils.thread;

import com.youquan.model.wemedia.pojo.WmUser;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/2 0:38
 */
public class WmThreadLocalUtils {
    private final static ThreadLocal<WmUser> WM_USER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 添加用户
     *
     * @param wmUser 用户信息
     */
    public static void setUser(WmUser wmUser) {
        WM_USER_THREAD_LOCAL.set(wmUser);
    }

    /**
     * 获取用户
     */
    public static WmUser getUser() {
        return WM_USER_THREAD_LOCAL.get();
    }

    /**
     * 清理用户
     */
    public static void clear() {
        WM_USER_THREAD_LOCAL.remove();
    }
}
