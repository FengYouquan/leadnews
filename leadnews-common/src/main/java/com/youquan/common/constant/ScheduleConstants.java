package com.youquan.common.constant;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/5 0:53
 */
public interface ScheduleConstants {
    int SCHEDULED = 0;   // 初始化状态

    int EXECUTED = 1;       // 已执行状态

    int CANCELLED = 2;   // 已取消状态

    String FUTURE = "future_";   // 未来数据key前缀

    String TOPIC = "topic_";     // 当前数据key前缀
}
